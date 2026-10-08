package io.github.nickm980.smallville.memory.ledger;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

/**
 * NDJSON journal persistence for the memory ledger.
 * <p>
 * File layout under {@code <rootPath>/agents/<agent-name>/}:
 * <ul>
 *   <li>{@code events.ndjson} — append-only ledger entries</li>
 *   <li>{@code index.json} — MemoryLedgerIndex</li>
 *   <li>{@code dream-packs/<pack-id>.json.gz} — compressed dream packs</li>
 * </ul>
 * Thread-safe: synchronized per agent name.
 */
public class MemoryJournalWriter {

    private static final Logger LOG = LoggerFactory.getLogger(MemoryJournalWriter.class);

    private final Path rootPath;
    private final ObjectMapper mapper;

    public MemoryJournalWriter(String rootDir) {
	this.rootPath = Paths.get(rootDir);
	this.mapper = new ObjectMapper();
	this.mapper.registerModule(new JavaTimeModule());
	this.mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    public MemoryJournalWriter(Path rootPath) {
	this.rootPath = rootPath;
	this.mapper = new ObjectMapper();
	this.mapper.registerModule(new JavaTimeModule());
	this.mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    // --- Agent directory ---

    private Path agentDir(String agentName) {
	return rootPath.resolve("agents").resolve(sanitizeName(agentName));
    }

    private Path eventsFile(String agentName) {
	return agentDir(agentName).resolve("events.ndjson");
    }

    private Path indexFile(String agentName) {
	return agentDir(agentName).resolve("index.json");
    }

    private Path dreamPacksDir(String agentName) {
	return agentDir(agentName).resolve("dream-packs");
    }

    // --- Write operations (synchronized per agent) ---

    public synchronized void appendEntry(String agentName, MemoryLedgerEntry entry) {
	try {
	    Path dir = agentDir(agentName);
	    Files.createDirectories(dir);

	    String json = mapper.writeValueAsString(entry);
	    Path file = eventsFile(agentName);
	    Files.write(file, (json + System.lineSeparator()).getBytes(StandardCharsets.UTF_8),
		StandardOpenOption.CREATE, StandardOpenOption.APPEND);
	} catch (IOException e) {
	    LOG.error("Failed to append ledger entry for {}", agentName, e);
	}
    }

    public synchronized void writeIndex(String agentName, MemoryLedgerIndex index) {
	try {
	    Path dir = agentDir(agentName);
	    Files.createDirectories(dir);

	    String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(index);
	    Files.writeString(indexFile(agentName), json, StandardCharsets.UTF_8,
		StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
	} catch (IOException e) {
	    LOG.error("Failed to write index for {}", agentName, e);
	}
    }

    public synchronized void writeDreamPack(String agentName, DreamPack pack, boolean compress) {
	try {
	    Path dir = dreamPacksDir(agentName);
	    Files.createDirectories(dir);

	    if (compress) {
		Path gzFile = dir.resolve(pack.getPackId() + ".json.gz");
		try (OutputStream fos = Files.newOutputStream(gzFile);
		     GZIPOutputStream gzos = new GZIPOutputStream(fos);
		     OutputStreamWriter writer = new OutputStreamWriter(gzos, StandardCharsets.UTF_8)) {
		    mapper.writeValue(writer, pack);
		}
	    } else {
		Path jsonFile = dir.resolve(pack.getPackId() + ".json");
		String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(pack);
		Files.writeString(jsonFile, json, StandardCharsets.UTF_8);
	    }
	} catch (IOException e) {
	    LOG.error("Failed to write dream pack {} for {}", pack.getPackId(), agentName, e);
	}
    }

    // --- Read operations ---

    public MemoryLedgerIndex loadOrCreateIndex(String agentName) {
	Path file = indexFile(agentName);
	if (Files.exists(file)) {
	    try {
		return mapper.readValue(file.toFile(), MemoryLedgerIndex.class);
	    } catch (IOException e) {
		LOG.warn("Failed to load index for {}, creating new", agentName, e);
	    }
	}
	return new MemoryLedgerIndex(agentName);
    }

    public List<MemoryLedgerEntry> loadEntries(String agentName) {
	List<MemoryLedgerEntry> entries = new ArrayList<>();
	Path file = eventsFile(agentName);
	if (!Files.exists(file)) {
	    return entries;
	}

	try (BufferedReader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
	    String line;
	    while ((line = reader.readLine()) != null) {
		line = line.trim();
		if (!line.isEmpty()) {
		    entries.add(mapper.readValue(line, MemoryLedgerEntry.class));
		}
	    }
	} catch (IOException e) {
	    LOG.error("Failed to load entries for {}", agentName, e);
	}
	return entries;
    }

    public List<DreamPack> loadDreamPacks(String agentName) {
	List<DreamPack> packs = new ArrayList<>();
	Path dir = dreamPacksDir(agentName);
	if (!Files.exists(dir)) {
	    return packs;
	}

	try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir)) {
	    for (Path entry : stream) {
		String name = entry.getFileName().toString();
		try {
		    if (name.endsWith(".json.gz")) {
			try (InputStream fis = Files.newInputStream(entry);
			     GZIPInputStream gzis = new GZIPInputStream(fis);
			     InputStreamReader reader = new InputStreamReader(gzis, StandardCharsets.UTF_8)) {
			    packs.add(mapper.readValue(reader, DreamPack.class));
			}
		    } else if (name.endsWith(".json")) {
			packs.add(mapper.readValue(entry.toFile(), DreamPack.class));
		    }
		} catch (IOException e) {
		    LOG.warn("Failed to load dream pack {}", entry, e);
		}
	    }
	} catch (IOException e) {
	    LOG.error("Failed to list dream packs for {}", agentName, e);
	}

	return packs;
    }

    /**
     * Check if persisted memory data exists for an agent (for restart reload).
     */
    public boolean hasPersistedData(String agentName) {
	return Files.exists(indexFile(agentName));
    }

    /**
     * Load recent entries of a specific type (for restart reload of hot memories).
     */
    public List<MemoryLedgerEntry> loadRecentEntriesByType(String agentName, LedgerEntryType type, int limit) {
	List<MemoryLedgerEntry> all = loadEntries(agentName);
	List<MemoryLedgerEntry> filtered = new ArrayList<>();
	// Walk backwards for most recent
	for (int i = all.size() - 1; i >= 0 && filtered.size() < limit; i--) {
	    if (type == all.get(i).getType()) {
		filtered.add(all.get(i));
	    }
	}
	return filtered;
    }

    private String sanitizeName(String name) {
	// Replace characters that are invalid in file paths
	return name.replaceAll("[^a-zA-Z0-9._-]", "_");
    }
}
