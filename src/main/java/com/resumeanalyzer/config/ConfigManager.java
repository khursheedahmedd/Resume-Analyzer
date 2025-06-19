package com.resumeanalyzer.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigManager {
    private static final String CONFIG_FILE = "config.properties";
    private static ConfigManager instance;
    private final Properties properties;

    private ConfigManager() {
        properties = new Properties();
        loadProperties();
    }

    public static ConfigManager getInstance() {
        if (instance == null) {
            instance = new ConfigManager();
        }
        return instance;
    }

    private void loadProperties() {
        try (InputStream input = getClass().getClassLoader().getResourceAsStream(CONFIG_FILE)) {
            if (input == null) {
                System.out.println("Warning: " + CONFIG_FILE + " not found in resources");
                return;
            }
            properties.load(input);
        } catch (IOException ex) {
            System.out.println("Error loading configuration: " + ex.getMessage());
        }
    }

    public String getGroqApiKey() {
        return properties.getProperty("groq.api.key");
    }
} 