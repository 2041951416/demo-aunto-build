package com.filterconfiguration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class SlimmingConfiguration {
    private static final Logger logger = LoggerFactory.getLogger(SlimmingConfiguration.class);

    public static void createBootstrapProperties(String targetDirectoryPath, String fileName) {
        Map<String, String> completeConfigs = new LinkedHashMap<>();
        completeConfigs.put("excludeGroupIds", "org.springframework,aopalliance*");
        completeConfigs.put("excludes", "org.apache.commons:commons-lang3,commons-beanutils:commons-beanutils,org.springframework.boot:spring-boot-starter-json:2.7.16");
        completeConfigs.put("excludeArtifactIds", "sofa-ark-spi,commons-lang");

        Path directory = Paths.get(targetDirectoryPath);
        Path propertiesFile = directory.resolve(fileName);

        try {
            Files.createDirectories(directory);
            logger.info("目录已创建: {}", targetDirectoryPath);

            List<String> existingLines = Files.exists(propertiesFile) ? Files.readAllLines(propertiesFile) : new ArrayList<>();
            Map<String, String> existingConfigs = parseExistingConfigs(existingLines);

            List<String> updatedLines = updateConfigs(existingConfigs, completeConfigs);

            Files.write(propertiesFile, updatedLines);
            logger.info("配置文件已更新: {}", propertiesFile);
        } catch (IOException e) {
            logger.error("创建或更新配置文件时发生错误", e);
        }
    }

    private static Map<String, String> parseExistingConfigs(List<String> lines) {
        Map<String, String> configs = new LinkedHashMap<>();
        for (String line : lines) {
            if (line.contains("=")) {
                String[] parts = line.split("=", 2);
                String key = parts[0].trim();
                String value = parts.length > 1 ? parts[1].trim() : "";
                configs.put(key, value);
            }
        }
        return configs;
    }

    private static String mergeConfigValues(String existingValue, String completeValue) {
        Set<String> items = new LinkedHashSet<>();
        if (!existingValue.isEmpty()) {
            items.addAll(Arrays.asList(existingValue.split(",")));
        }
        items.addAll(Arrays.asList(completeValue.split(",")));
        return String.join(",", items);
    }

    private static List<String> updateConfigs(Map<String, String> existingConfigs, Map<String, String> completeConfigs) {
        List<String> updatedLines = new ArrayList<>();
        for (Map.Entry<String, String> entry : completeConfigs.entrySet()) {
            String key = entry.getKey();
            String completeValue = entry.getValue();
            String existingValue = existingConfigs.getOrDefault(key, "");

            String updatedValue = mergeConfigValues(existingValue, completeValue);
            updatedLines.add(key + "=" + updatedValue);
        }
        return updatedLines;
    }
}