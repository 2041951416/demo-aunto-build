package com.filter.way;

import java.io.File;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class FileScanner { // 定义一个公共类 FileScanner
    public static void main(String[] args) throws IOException, JDOMException { // 程序的入口点

        String projectPath = "C:\\demo810"; // 设置项目路径变量
        scanForFiles(projectPath); // 调用扫描方法，开始扫描
    }

    public static void scanForFiles(String directoryPath) throws IOException, JDOMException { // 定义一个静态方法，用于扫描文件
        File directory = new File(directoryPath); // 创建一个 File 对象，代表项目目录
        // 递归遍历目录
        scanDirectory(directory); // 调用递归扫描方法
    }

    public static void scanDirectory(File directory) throws IOException, JDOMException { // 定义一个静态方法，用于递归扫描目录
        if (directory.isDirectory()) { // 检查传入的 File 对象是否是一个目录
            // 遍历目录中的所有文件和子目录
            File[] files = directory.listFiles(); // 获取目录中的所有文件和子目录的数组
            if (files != null) { // 检查数组是否为空
                for (File file : files) { // 遍历数组中的每个文件或目录
                    if (file.isDirectory()) { // 如果是目录，递归调用 scanDirectory 方法
                        scanDirectory(file); // 递归扫描子目录
                    } else { // 如果是文件
                        // 检查文件名是否是我们要找的文件
                        if (isTargetFile(file)) {
                            try {
                                SAXBuilder builder = new SAXBuilder();
                                Document document = builder.build(file);
                                Element root = document.getRootElement();

//                                // 检查dependencies元素是否存在，如果不存在则创建
//                                Element dependencies = root.getChild("dependencies");
//                                if (dependencies == null) {
//                                    dependencies = new Element("dependencies");
//                                    root.addContent(dependencies);
//                                }
                                List<Element> dependenciesList = root.getChildren("dependencies");
                                // 检查是否已经存在<dependencies>元素
                                Element dependencies;
                                if (dependenciesList.isEmpty()) {
                                    // 如果不存在则创建<dependencies>元素
                                    dependencies = new Element("dependencies");
                                    root.addContent(dependencies);
                                } else {
                                    // 如果存在，使用第一个<dependencies>元素
                                    dependencies = dependenciesList.get(0);
                                }

                                // 创建一个新的dependency元素
                                Element dependency = new Element("dependency");
                                Element groupId = new Element("groupId").setText("com.example");
                                Element artifactId = new Element("artifactId").setText("example-artifact");
                                Element version = new Element("version").setText("1.0.0");

                                // 将groupId, artifactId, version添加到dependency元素中
                                dependency.addContent(groupId);
                                dependency.addContent(artifactId);
                                dependency.addContent(version);

                                // 将新的dependency元素添加到dependencies元素中
                                dependencies.addContent(dependency);

                                // 使用XMLOutputter输出修改后的Document到文件
                                XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat());
                                outputter.output(document, new FileWriter(file));

                                System.out.println("POM file has been updated successfully!");

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        }
    }

    private static boolean isTargetFile(File file) { // 定义一个私有静态方法，用于检查文件是否为目标文件
        // 检查文件名是否是 application.properties 或 pom.xml
        return
                file.getName().equalsIgnoreCase("pom.xml"); // 如果文件名是 pom.xml，返回 true
    }
}