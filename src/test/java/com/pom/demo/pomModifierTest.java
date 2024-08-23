package com.pom.demo;
import static org.junit.Assert.*;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class pomModifierTest {

    private File testDirectory;
    private File testPomFile;

    @Before
    public void setUp() throws IOException {
        // 创建一个临时目录和文件用于测试
        testDirectory = new File("tempTestDir");
        testDirectory.mkdir();
        testPomFile = new File(testDirectory, "pom.xml");
        try (PrintWriter writer = new PrintWriter(testPomFile)) {
            writer.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
            writer.println("<project>");
            writer.println("</project>");
        }
    }

    @After
    public void tearDown() {
        // 删除测试目录和文件
        testPomFile.delete();
        testDirectory.delete();
    }

    @Test
    public void testIsTargetFile() {
        assertTrue(pomModifier.isTargetFile(testPomFile));
        File notPom = new File(testDirectory, "notpom.txt");
        assertFalse(pomModifier.isTargetFile(notPom));
    }

    @Test
    public void testScanDirectory() throws IOException, JDOMException {
        // 调用scanDirectory方法，期望testPomFile被处理
        pomModifier.scanDirectory(testDirectory);

        // 验证testPomFile内容是否被修改
        Document document = new SAXBuilder().build(testPomFile);
        Element root = document.getRootElement();
        assertNotNull(root.getChild("properties"));
        // 这里可以添加更多的断言来检查XML结构是否符合预期
    }
    // 其他测试方法..
}