package com.example.demo;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

public class ApplicationPropertiesModifier {
    public static void main(String[] args) {
        // 工程的绝对路径
        String projectPath = "C:\\demo810";
        // 要设置的应用名称
        String newapplicationname = "test123451236";

        // 开始修改操作
        modifyApplicationProperties(projectPath, newapplicationname);
    }

    public static void modifyApplicationProperties(String directoryPath, String appalicationname) {
        File directory = new File(directoryPath);
        scanDirectory(directory, appalicationname);
    }

    public static void scanDirectory(File directory, String appName) {
        if (directory.isDirectory()) {
            File[] files = directory.listFiles((dir, name) -> name.equalsIgnoreCase("application.properties"));
            if (files != null) {
                for (File file : files) {
                    modifySpringApplicationName(file, appName);
                }
            }

            // 递归遍历子目录
            File[] subDirectories = directory.listFiles(File::isDirectory);
            if (subDirectories != null) {
                for (File subDir : subDirectories) {
                    scanDirectory(subDir, appName);
                }
            }
        }
    }

    private static void modifySpringApplicationName(File file, String appName) {
        Path path = file.toPath();
        try {
            // 读取文件内容
            List<String> lines = Files.readAllLines(path);
            // 定位并修改 spring.application.name 的值
            String modifiedContent = lines.stream()
                    .map(line -> line.startsWith("spring.application.name") ? "spring.application.name=" + appName : line)
                    .collect(Collectors.joining(System.lineSeparator()));

            // 将修改后的内容写回文件
            Files.write(path, modifiedContent.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}