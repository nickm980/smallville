package io.github.nickm980.smallville.memory;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.zip.GZIPOutputStream;

import com.fasterxml.jackson.databind.ObjectMapper;

public class MemoryLedgerStore {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private final Path root;

    public MemoryLedgerStore(Path root) {
	this.root = root;
    }

    public Path getRoot() {
	return root;
    }

    public MemoryLedger load(String agentName) {
	Path indexPath = indexPath(agentName);
	if (!Files.exists(indexPath)) {
	    return null;
	}

	try {
	    MemoryLedger ledger = MAPPER.readValue(Files.readString(indexPath, StandardCharsets.UTF_8), MemoryLedger.class);
	    if (ledger != null) {
		ledger.rebuildSummary();
		ledger.rebuildSeenMemoryKeys();
	    }
	    return ledger;
	} catch (IOException e) {
	    throw new IllegalStateException("Could not load memory ledger for " + agentName, e);
	}
    }

    public void save(MemoryLedger ledger) {
	if (ledger == null || ledger.getAgent() == null || ledger.getAgent().isBlank()) {
	    return;
	}

	try {
	    Path agentDir = agentDirectory(ledger.getAgent());
	    Path dreamPackDir = dreamPackDirectory(ledger.getAgent());
	    Files.createDirectories(agentDir);
	    Files.createDirectories(dreamPackDir);
	    ledger.rebuildSummary();
	    ledger.setUpdatedAtUtc(Instant.now().toString());

	    String json = MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(ledger);
	    Files.writeString(indexPath(ledger.getAgent()), json, StandardCharsets.UTF_8);

	    for (MemoryLedger.DreamPack dreamPack : ledger.getDreamPacks()) {
		if (dreamPack == null || dreamPack.getId() == null || dreamPack.getId().isBlank()) {
		    continue;
		}
		writeDreamPack(ledger.getAgent(), dreamPack);
	    }
	} catch (IOException e) {
	    throw new IllegalStateException("Could not save memory ledger for " + ledger.getAgent(), e);
	}
    }

    public void appendEvent(String agentName, String type, Map<String, Object> detail) {
	if (agentName == null || agentName.isBlank()) {
	    return;
	}

	try {
	    Files.createDirectories(agentDirectory(agentName));
	    Map<String, Object> event = new LinkedHashMap<String, Object>();
	    event.put("atUtc", Instant.now().toString());
	    event.put("type", type);
	    if (detail != null) {
		event.putAll(detail);
	    }

	    try (BufferedWriter writer = Files.newBufferedWriter(
		eventsPath(agentName),
		StandardCharsets.UTF_8,
		java.nio.file.StandardOpenOption.CREATE,
		java.nio.file.StandardOpenOption.WRITE,
		java.nio.file.StandardOpenOption.APPEND)) {
		writer.write(MAPPER.writeValueAsString(event));
		writer.newLine();
	    }
	} catch (IOException e) {
	    throw new IllegalStateException("Could not append memory ledger event for " + agentName, e);
	}
    }

    public Path indexPath(String agentName) {
	return agentDirectory(agentName).resolve("index.json");
    }

    public Path eventsPath(String agentName) {
	return agentDirectory(agentName).resolve("events.ndjson");
    }

    public Path dreamPackPath(String agentName, String packId) {
	return dreamPackDirectory(agentName).resolve(packId + ".json.gz");
    }

    private Path agentDirectory(String agentName) {
	return root.resolve("agents").resolve(sanitizeAgentName(agentName));
    }

    private Path dreamPackDirectory(String agentName) {
	return agentDirectory(agentName).resolve("dream-packs");
    }

    private void writeDreamPack(String agentName, MemoryLedger.DreamPack dreamPack) throws IOException {
	Path path = dreamPackPath(agentName, dreamPack.getId());
	try (GZIPOutputStream gzipStream = new GZIPOutputStream(Files.newOutputStream(path));
	     OutputStreamWriter outputStreamWriter = new OutputStreamWriter(gzipStream, StandardCharsets.UTF_8);
	     BufferedWriter writer = new BufferedWriter(outputStreamWriter)) {
	    writer.write(MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(dreamPack));
	}
    }

    public static String sanitizeAgentName(String agentName) {
	return agentName == null ? "unknown-agent" : agentName.trim().replaceAll("[^a-zA-Z0-9._-]+", "_");
    }
}
