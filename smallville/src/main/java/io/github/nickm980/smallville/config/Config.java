package io.github.nickm980.smallville.config;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

public class Config {
    private static PromptsConfig prompts;
    private static GeneralConfig config;
    private static final Logger LOG = LoggerFactory.getLogger(Config.class);

    private Config() {

    }

    public static PromptsConfig getPrompts() {
	if (prompts == null) {
	    LOG.info("Loading prompts");
	    prompts = loadFile("prompts.yaml", PromptsConfig.class);
	}

	return prompts;
    }

    public static GeneralConfig getConfig() {
	if (config == null) {
	    LOG.info("Loading config.yaml");
	    config = loadFile("config.yaml", GeneralConfig.class);
	}

	return config;
    }

    private static <T> T loadFile(String file, Class<T> clazz) {
	Yaml yaml = new Yaml();

	Path configFile = Paths.get(file);
	InputStream inputStream = null;

	if (Files.exists(configFile)) {
	    LOG.debug("Configuration file found");
	    try {
		inputStream = Files.newInputStream(configFile);
	    } catch (IOException e) {
		e.printStackTrace();
	    }
	} else {
	    LOG.debug("Loading default configuration");
	    inputStream = Config.class.getResourceAsStream("/" + file);
	}

	if (inputStream == null) {
	    LOG.error("No " + file + " found. It must be either in resources folder or next to jar");
	}

	return yaml.loadAs(inputStream, clazz);
    }
}
