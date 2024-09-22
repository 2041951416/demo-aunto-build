package com.pomdemo;
import java.io.File;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Properties;

public class pomModifier {
    private static final Logger logger = LoggerFactory.getLogger(pomModifier.class);
    private static Properties config = new Properties();
    static {
        try (java.io.InputStream input = pomModifier.class.getClassLoader().getResourceAsStream("config.properties")) {
            config.load(input);
        } catch (IOException ex) {
            logger.error("无法加载配置文件", ex);
        }
    }

    public static void main(String[] args) throws IOException, JDOMException {
        try (Scanner scanner = new Scanner(System.in)) {
            System.out.println("请输入项目的绝对路径：");
            String projectPath = scanner.nextLine();
            processProjectPath(projectPath);
        }
    }

    public static void processProjectPath(String projectPath) throws IOException, JDOMException {
        File projectDirectory = new File(projectPath);
        if (!projectDirectory.exists() || !projectDirectory.isDirectory()) {
            logger.error("提供的项目路径不存在或不是目录");
            return;
        }
        File pomFile = new File(projectDirectory, "pom.xml");
        if (!pomFile.exists() || pomFile.length() == 0) {
            createAndInitializePomFile(pomFile);
        }
        updatePomFile(pomFile);
    }

    private static void createAndInitializePomFile(File pomFile) throws IOException {
        String initialContent = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                + "<project xmlns=\"http://maven.apache.org/POM/4.0.0\"\n"
                + "         xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n"
                + "         xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd\">\n"
                + "    <modelVersion>4.0.0</modelVersion>\n"
                + "    <groupId>com.example</groupId>\n"
                + "    <artifactId>demo-project</artifactId>\n"
                + "    <version>1.0-SNAPSHOT</version>\n"
                + "</project>";
        try (FileWriter writer = new FileWriter(pomFile)) {
            writer.write(initialContent);
            logger.info("pom.xml 文件已创建并初始化在: {}", pomFile.getAbsolutePath());
        }
    }

    private static void updatePomFile(File file) throws JDOMException, IOException {
        SAXBuilder builder = new SAXBuilder();
        Document document = builder.build(file);
        Element root = document.getRootElement();

        updateProperties(root);
        updateDependencies(root);
        updateBuild(root);

        XMLOutputter xmlOutput = new XMLOutputter();
        xmlOutput.setFormat(Format.getPrettyFormat());
        xmlOutput.output(document, new FileWriter(file));
        logger.info("POM 文件已更新: {}", file.getAbsolutePath());
    }

    private static void updateProperties(Element root) {
        Element properties = root.getChild("properties");
        if (properties == null) {
            properties = new Element("properties");
            root.addContent(properties);
        }
        updateOrAddElement(properties, "sofa.ark.version", config.getProperty("sofa.ark.version"));
        updateOrAddElement(properties, "koupleless.runtime.version", config.getProperty("koupleless.runtime.version"));
    }

    private static void updateDependencies(Element root) {
        Element dependencies = root.getChild("dependencies");
        if (dependencies == null) {
            dependencies = new Element("dependencies");
            root.addContent(dependencies);
        }
        Element dependency = new Element("dependency");
        updateOrAddElement(dependency, "groupId", config.getProperty("koupleless.groupId"));
        updateOrAddElement(dependency, "artifactId", config.getProperty("koupleless.artifactId"));
        updateOrAddElement(dependency, "version", "${koupleless.runtime.version}");
        dependencies.addContent(dependency);
    }

    private static void updateBuild(Element root) {
        Element build = root.getChild("build");
        if (build == null) {
            build = new Element("build");
            root.addContent(build);
        }
        Element plugins = build.getChild("plugins");
        if (plugins == null) {
            plugins = new Element("plugins");
            build.addContent(plugins);
        }
        addSofaArkPlugin(plugins);
        addSpringBootPlugin(plugins);
    }

    private static Element createPluginElement(String groupId, String artifactId, String version) {
        Element plugin = new Element("plugin");
        updateOrAddElement(plugin, "groupId", groupId);
        updateOrAddElement(plugin, "artifactId", artifactId);
        if (version != null) {
            updateOrAddElement(plugin, "version", version);
        }
        return plugin;
    }

    private static void addSofaArkPlugin(Element plugins) {
        Element plugin = createPluginElement(
                config.getProperty("sofa.ark.plugin.groupId"),
                config.getProperty("sofa.ark.plugin.artifactId"),
                "${sofa.ark.version}"
        );

        Element executions = new Element("executions");
        Element execution = new Element("execution");
        updateOrAddElement(execution, "id", "default-cli");

        Element goals = new Element("goals");
        goals.addContent(new Element("goal").setText("repackage"));

        execution.addContent(goals);
        executions.addContent(execution);
        plugin.addContent(executions);

        Element configuration = new Element("configuration");
        updateOrAddElement(configuration, "skipArkExecutable", "true");
        updateOrAddElement(configuration, "outputDirectory", "./target");
        updateOrAddElement(configuration, "bizName", config.getProperty("biz.name"));
        updateOrAddElement(configuration, "webContext", config.getProperty("web.context"));
        updateOrAddElement(configuration, "declareMode", "true");
        plugin.addContent(configuration);

        plugins.addContent(plugin);
    }

    private static void addSpringBootPlugin(Element plugins) {
        Element plugin = createPluginElement(
                config.getProperty("spring.boot.plugin.groupId"),
                config.getProperty("spring.boot.plugin.artifactId"),
                null
        );
        plugins.addContent(plugin);
    }

    private static void updateOrAddElement(Element parent, String childName, String childValue) {
        Element child = parent.getChild(childName);
        if (child == null) {
            child = new Element(childName);
            parent.addContent(child);
        }
        child.setText(childValue);
    }
}

