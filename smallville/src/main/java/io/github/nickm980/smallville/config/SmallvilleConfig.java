package io.github.nickm980.smallville.config;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.github.nickm980.smallville.config.prompts.Prompts;

public class SmallvilleConfig {
    private static Prompts prompts;
    private static GeneralConfig config;
    private static final Logger LOG = LoggerFactory.getLogger(SmallvilleConfig.class);

    private SmallvilleConfig() {

    }

    public static Prompts getPrompts() {
	if (prompts == null) {
	    LOG.info("Loading prompts...");
	    prompts = loadYamlFile("prompts.yaml", Prompts.class);
	}

	return prompts;
    }

    public static GeneralConfig getConfig() {
	if (config == null) {
	    LOG.info("Loading config.yaml...");
	    config = loadYamlFile("config.yaml", GeneralConfig.class);
	}

	return config;
    }

    private static InputStream loadInputStream(String path) {
	List<Path> candidates = new ArrayList<Path>();
	candidates.add(Paths.get(path));

	if (isDefaultScenarioPath(path)) {
	    candidates.add(Paths.get("scenarios", "two-house-garden-v1", path));
	    candidates.add(Paths.get("..", "scenarios", "two-house-garden-v1", path));
	}

	candidates.add(Paths.get("src", "main", "resources", path));
	candidates.add(Paths.get("smallville", "src", "main", "resources", path));

	InputStream inputStream = null;

	for (Path file : candidates) {
	    if (!Files.exists(file)) {
		continue;
	    }

	    LOG.debug("Configuration file found at {}", file.toAbsolutePath());
	    try {
		inputStream = Files.newInputStream(file);
		break;
	    } catch (IOException e) {
		e.printStackTrace();
	    }
	}

	if (inputStream == null) {
	    LOG.debug("Loading default configuration");
	    inputStream = SmallvilleConfig.class.getResourceAsStream("/" + path);
	}

	if (inputStream == null) {
	    LOG.error("No {} found. It must be either in resources folder or next to jar", path);
	}

	return inputStream;
    }

	private static boolean isDefaultScenarioPath(String path) {
	    return "config.yaml".equals(path) || "simulation.yaml".equals(path);
	}

    private static JsonNode loadJsonFile(String file) {
	ObjectMapper mapper = new ObjectMapper();
	try (InputStream stream = loadInputStream(file)) {
	    if (stream == null) {
		return null;
	    }
	    return mapper.readTree(stream);
	} catch (IOException e) {
	    e.printStackTrace();
	}

	return null;
    }

    public static <T> T loadYamlFile(String file, Class<T> clazz) {
	try (InputStream stream = loadInputStream(file)) {
	    if (stream == null) {
		return null;
	    }
	    return createYaml(clazz).loadAs(stream, clazz);
	} catch (IOException e) {
	    e.printStackTrace();
	}

	return null;
    }

    public static <T> T parseYaml(String content, Class<T> clazz) {
	if (content == null || content.isBlank()) {
	    return null;
	}

	return createYaml(clazz).loadAs(content, clazz);
    }

    private static <T> Yaml createYaml(Class<T> clazz) {
	LoaderOptions loaderOptions = new LoaderOptions();
	loaderOptions.setAllowDuplicateKeys(false);
	loaderOptions.setMaxAliasesForCollections(25);
	loaderOptions.setCodePointLimit(1_000_000);
	return new Yaml(new Constructor(clazz, loaderOptions));
    }
}
