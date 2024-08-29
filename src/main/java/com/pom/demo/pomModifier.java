package com.pom.demo;
import java.io.File;
import org.jdom2.Namespace;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;

public class pomModifier {
    public static void main(String[] args) throws IOException, JDOMException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("请输入项目的绝对路径：");
        String projectPath = scanner.nextLine();

        File projectDirectory = new File(projectPath);
        if (!projectDirectory.exists() || !projectDirectory.isDirectory()) {
            System.out.println("提供的项目路径不存在或不是目录");
            return;
        }
        File initialPomFile = new File(projectDirectory, "pom.xml");
        if (!initialPomFile.exists()) {
            createAndInitializePomFile(initialPomFile);
        }
        scanForFiles(projectDirectory); // 调用扫描方法，开始扫描
    }

    public static void createAndInitializePomFile(File pomFile) throws IOException {
        // 创建并初始化 pom.xml 文件
        try (FileWriter writer = new FileWriter(pomFile)) {
            String initialContent = getInitialPomContent();
            writer.write(initialContent);
            System.out.println("pom.xml 文件已创建并初始化在: " + pomFile.getAbsolutePath());
        }
    }

    private static String getInitialPomContent() {
        // 返回 pom.xml 文件的初始内容
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<project xmlns=\"http://maven.apache.org/POM/4.0.0\"\n" +
                "        xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
                "        xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd\">\n" +
                "    <modelVersion>4.0.0</modelVersion>\n" +
                "    <!-- 其他初始化内容 -->\n" +
                "</project>";
    }

    public static void scanForFiles(File projectDirectory) throws IOException, JDOMException {
        // 从项目目录开始递归扫描
        scanDirectory(projectDirectory, projectDirectory);
    }

    public static void scanDirectory(File currentDirectory, File projectDirectory) throws IOException, JDOMException {
        // 如果当前目录不是项目目录，不执行任何操作
        if (!currentDirectory.equals(projectDirectory)) {
            return;
        }

        File[] files = currentDirectory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    // 递归扫描子目录
                    scanDirectory(file, projectDirectory);
                } else if (isTargetFile(file)) {
                    // 更新找到的 pom.xml 文件
                    updatePomFile(file);
                }
            }
        }
    }

    static boolean isTargetFile(File file) {
        // 检查文件是否是 pom.xml
        return file.getName().equalsIgnoreCase("pom.xml");
    }

    static void updatePomFile(File file) throws JDOMException, IOException {
        try {
            System.out.println("File absolute path: " + file.getAbsolutePath());
            if (file.length() == 0) {
                System.out.println("The pom.xml file is empty. Initializing it with default content.");
                createAndInitializePomFile(file); // Reinitialize the empty pom.xml file

            }
            SAXBuilder builder = new SAXBuilder();
            Document document = builder.build(file);
            Element root = document.getRootElement();
            Namespace ns = root.getNamespace();
            if (root == null || !"project".equals(root.getName())) {
                String initialContent = getInitialPomContent();
                try (FileWriter writer = new FileWriter(file)) {
                    writer.write(initialContent);
                    System.out.println("pom.xml 文件为空或格式不正确，已初始化在: " + file.getAbsolutePath());
                }
            }
                document = builder.build(file);
                root = document.getRootElement();
                ns = root.getNamespace();
                //创建一个properties元素
                List<Element> propertiesList = root.getChildren("properties", ns);
                // 检查是否已经存在<properties>元素
                Element properties;
                if (propertiesList.isEmpty()) {
                    // 如果不存在则创建<properties>元素
                    properties = new Element("properties");
                    root.addContent(properties);
                } else {
                    // 如果存在，使用第一个<properties>元素
                    properties = propertiesList.get(0);

                }
                Element sofaark = new Element("sofa.ark.version").setText("2.2.12");
                Element koupleless = new Element("koupleless.runtime.version").setText("1.2.3");
                properties.addContent(sofaark);
                properties.addContent(koupleless);
                List<Element> dependenciesList = root.getChildren("dependencies", ns);
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
                Element groupId = new Element("groupId").setText("com.alipay.sofa.koupleless");
                Element artifactId = new Element("artifactId").setText("koupleless-app-starter");
                Element version = new Element("version").setText("${koupleless.runtime.version}");

                // 将groupId, artifactId, version添加到dependency元素中
                dependency.addContent(groupId);
                dependency.addContent(artifactId);
                dependency.addContent(version);

                // 将新的dependency元素添加到dependencies元素中
                dependencies.addContent(dependency);
                Element build = root.getChild("build", ns);
                if (build == null) {
                    // 如果 <build> 不存在则创建
                    build = new Element("build", ns);
                    root.addContent(build);
                }

                // 获取 <plugins> 元素
                Element plugins = build.getChild("plugins", ns);
                if (plugins == null) {
                    // 如果 <plugins> 不存在则创建
                    plugins = new Element("plugins", ns);
                    build.addContent(plugins);
                }
                // 创建并添加 <plugin> 元素
                Element plugin1 = new Element("plugin", ns);
                Element pluginGroupId = new Element("groupId", ns).setText("com.alipay.sofa");
                Element pluginArtifactId = new Element("artifactId", ns).setText("sofa-ark-maven-plugin");
                Element pluginVersion = new Element("version", ns).setText("{sofa.ark.version}");
                Element executions = new Element("executions", ns);

                // 添加子元素到 <plugin>
                plugin1.addContent(pluginGroupId);
                plugin1.addContent(pluginArtifactId);
                plugin1.addContent(pluginVersion);
                // 创建并添加 <executions> 元素
                Element execution = new Element("execution", ns);
                Element id = new Element("id", ns).setText("default-cli");
                Element goals = new Element("goals", ns);
                Element goal = new Element("goal", ns).setText("repackage");
                goals.addContent(goal);
                execution.addContent(id);
                execution.addContent(goals);
                executions.addContent(execution);
                Element configuration = new Element("configuration", ns);
                Element skipArkExectable = new Element("skipArkExecutable", ns).setText("true");
                Element outputDirectory = new Element("outputDirectory", ns).setText("./target");
                Element bizname = new Element("bizName", ns).setText("demo888.biz");
                Element webcontext = new Element("webContext", ns).setText("demo888.webcontext");
                Element declareMode = new Element("declareMode", ns).setText("true");
                configuration.addContent(skipArkExectable);
                configuration.addContent(outputDirectory);
                configuration.addContent(bizname);
                configuration.addContent(webcontext);
                configuration.addContent(declareMode);
                plugin1.addContent(executions);
                plugin1.addContent(configuration);
                Element plugin2 = new Element("plugin", ns);
                Element pluginGroupId2 = new Element("groupId", ns).setText("org.springframework.boot");
                Element pluginArtifactId2 = new Element("artifactId", ns).setText("spring-boot-maven-plugin");
                plugin2.addContent(pluginGroupId2);
                plugin2.addContent(pluginArtifactId2);
                plugins.addContent(plugin1);
                plugins.addContent(plugin2);

                // 使用XMLOutputter输出修改后的Document到文件
                XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat());
                outputter.output(document, new FileWriter(file));

                System.out.println("POM file has been updated successfully!");

            } catch(Exception e){
                e.printStackTrace();
            }
            System.out.println("POM 文件已更新: " + file.getAbsolutePath());
        }


    }

