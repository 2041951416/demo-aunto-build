package com.filterconfiguration;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class SlimmingConfiguration {

    public static void main(String[] args) {
        // 设定目标路径
        String targetDirectory = "C:\\demo810/conf/ark";
        String fileName = "bootstrap.properties";

        // 调用方法创建bootstrap.properties文件并写入内容
        createBootstrapProperties(targetDirectory, fileName);
    }

    /**
     * 创建并写入bootstrap.properties文件的方法
     * @param targetDirectory 目标目录路径
     * @param fileName 文件名
     */
    public static void createBootstrapProperties(String targetDirectory, String fileName) {
        // 创建目标目录
        File directory = new File(targetDirectory);
        if (!directory.exists()) {
            if (directory.mkdirs()) {
                System.out.println("Directories created: " + targetDirectory);
            } else {
                System.out.println("Failed to create directories: " + targetDirectory);
                return;
            }
        }

        // 创建bootstrap.properties文件
        File propertiesFile = new File(directory, fileName);
        try {
            if (propertiesFile.createNewFile()) {
                System.out.println(fileName + " file created successfully!");
            } else {
                System.out.println(fileName + " file already exists.");
            }

            // 写入内容到bootstrap.properties文件
            try (FileWriter writer = new FileWriter(propertiesFile)) {
                writer.write("excludes=org.apache.commons:commons-lang3,commons-beanutils:commons-beanutils,org.springframework.boot:spring-boot-starter-json:2.7.16\n");
                writer.write("excludeArtifactIds=sofa-ark-spi\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}