package com.filter.way;
import java.io.File;
public class FileScanner { // 定义一个公共类 FileScanner
    public static void main(String[] args) { // 程序的入口点

        String projectPath = "C:\\demo810"; // 设置项目路径变量
        scanForFiles(projectPath); // 调用扫描方法，开始扫描
    }

    public static void scanForFiles(String directoryPath) { // 定义一个静态方法，用于扫描文件
        File directory = new File(directoryPath); // 创建一个 File 对象，代表项目目录
        // 递归遍历目录
        scanDirectory(directory); // 调用递归扫描方法
    }

    public static void scanDirectory(File directory) { // 定义一个静态方法，用于递归扫描目录
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
                            //这里可以处理过滤的file   、、、、、、     ;
                        }
                    }
                }
            }
        }
    }

    private static boolean isTargetFile(File file) { // 定义一个私有静态方法，用于检查文件是否为目标文件
        // 检查文件名是否是 application.properties 或 pom.xml
        return file.getName().equalsIgnoreCase("application.properties") || // 如果文件名是 application.properties，返回 true
                file.getName().equalsIgnoreCase("pom.xml"); // 如果文件名是 pom.xml，返回 true
    }
}