package com.application.demo;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class ApplicationPropertiesModifier {
    public static void main(String[] args) throws IOException {
        // 工程的绝对路径

        Scanner sc = new Scanner(System.in);
        System.out.println("请输入工程的绝对路径：");
        String projectPath = sc.nextLine();

        // 要设置的应用名称
        System.out.println("请输入要设置的应用名称：");
        String newapplicationname = sc.nextLine();
        // 开始修改操作
//        modifyApplicationProperties(projectPath, newapplicationname);
//        String projectPath = "C:\\demo810";
//        // 要设置的应用名称
//        String newapplicationname = "test123451236";

        // 开始修改操作
        modifyApplicationProperties(projectPath, newapplicationname);
    }

    public static void modifyApplicationProperties(String directoryPath, String appalicationname) throws IOException {
        File directory = new File(directoryPath);
        scanDirectory(directory, appalicationname);
    }

    public static void scanDirectory(File directory, String applicationname) throws IOException {
        if (directory.isDirectory()) {
            // 扫描当前目录下的 application.properties 文件
            File[] files = directory.listFiles((dir, name) -> name.equalsIgnoreCase("application.properties"));
            if (files !=null && files.length > 0) {
                for (File file : files) {
                    modifySpringApplicationName(file, applicationname);
                }
            } else if(directory.getName().equalsIgnoreCase("resources")) {
                // 如果不存在，创建 application.properties 文件并写入内容
                try {
                    File newPropsFile = new File(directory, "application.properties");
                    newPropsFile.createNewFile();
                    List<String> initialContent = List.of("spring.application.name=" + applicationname);
                    Files.write(newPropsFile.toPath(), initialContent);
                    System.out.println("Created and initialized application.properties in directory: " + directory.getAbsolutePath());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            // 递归遍历子目录
            File[] subDirectories = directory.listFiles(File::isDirectory);
            if (subDirectories != null) {
                for (File subDir : subDirectories) {
                    scanDirectory(subDir, applicationname);
                }
            }
        }
    }

    static void modifySpringApplicationName(File file, String applicationname) throws IOException {
        Path path = file.toPath();
        try {
            // 读取文件内容
            List<String> lines = Files.readAllLines(path);
            boolean exists = lines.stream().anyMatch(line -> line.trim().startsWith("spring.application.name"));
            if (!exists) {
                // 如果文件中没有以 spring.application.name 开头的行，则添加新行
                String newLine = "spring.application.name=" + applicationname;
                lines.add(newLine);
                // 定位并修改 spring.application.name 的值
                String modifiedContent = lines.stream()
                        .map(line -> line.startsWith("spring.application.name") ? "spring.application.name=" + applicationname : line)
                        .collect(Collectors.joining(System.lineSeparator()));

                // 将修改后的内容写回文件
                Files.write(path, modifiedContent.getBytes());
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
            // 读取文件内容
            List<String> lines = Files.readAllLines(path);
            // 定位并修改 spring.application.name 的值
            String modifiedContent = lines.stream()
                    .map(line -> line.startsWith("spring.application.name") ? "spring.application.name=" + applicationname : line)
                    .collect(Collectors.joining(System.lineSeparator()));

            // 将修改后的内容写回文件
            Files.write(path, modifiedContent.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    }