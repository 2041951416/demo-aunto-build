package com.filterconfiguration;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

public class SlimmingConfiguration {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("请输入项目的绝对路径：");
        String projectPath = scanner.nextLine();

        // 构造目标路径
        String targetDirectoryPath = projectPath + "/conf/ark";
        String fileName = "bootstrap.properties";

        // 调用方法处理bootstrap.properties文件
        createBootstrapProperties(targetDirectoryPath, fileName);
    }

    public static void createBootstrapProperties(String targetDirectoryPath, String fileName) {
        Map<String, String> completeConfigs = new HashMap<>();
        completeConfigs.put("excludes", "org.apache.commons:commons-lang3,commons-beanutils:commons-beanutils,org.springframework.boot:spring-boot-starter-json:2.7.16");
        completeConfigs.put("excludeGroupIds", "org.springframework,aopalliance*");
        completeConfigs.put("excludeArtifactIds", "sofa-ark-spi,commons-lang");

        File directory = new File(targetDirectoryPath);
        if (!directory.exists()) {
            if (directory.mkdirs()) {
                System.out.println("Directories created: " + targetDirectoryPath);
            } else {
                System.out.println("Failed to create directories: " + targetDirectoryPath);
                return;
            }
        }

        File propertiesFile = new File(directory, fileName);
        List<String> lines = new ArrayList<>();
        Map<String, String> existingConfigs = new HashMap<>();

        if (propertiesFile.exists()) {
            try {
                lines = Files.readAllLines(propertiesFile.toPath());
                for (String line : lines) {
                    String trimmedLine = line.trim();
                    if (!trimmedLine.isEmpty() && trimmedLine.contains("=")) {
                        existingConfigs.put(trimmedLine.split("=")[0], trimmedLine.split("=")[1]);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        for (Map.Entry<String, String> entry : completeConfigs.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            // 只有当配置项不存在或值为空时，才添加或更新配置项
            if (!existingConfigs.containsKey(key) || existingConfigs.get(key).isEmpty()) {
                // 添加新的配置项或更新现有配置项
                if (value.isEmpty()) {
                    lines.add(key + "=");
                } else {
                    String[] items = value.split(",");
                    for (String item : items) {
                        // 确保配置项不重复添加
                        if (!existingConfigs.containsValue(key + "=" + item)) {
                            boolean added = false;
                            for (int i = 0; i < lines.size(); i++) {
                                String existingLine = lines.get(i).trim();
                                if (existingLine.startsWith(key + "=")) {
                                    String existingValue = existingLine.substring(key.length() + 1);
                                    if (!existingValue.contains(item)) {
                                        lines.set(i, existingLine + "," + item);
                                        added = true;
                                        break;
                                    }
                                }
                            }
                            if (!added) {
                                lines.add(key + "=" + item);
                            }
                        }
                    }
                }
            }
        }

        // 写回修改后的配置到文件
        try (FileWriter writer = new FileWriter(propertiesFile)) {
            for (String line : lines) {
                writer.write(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}