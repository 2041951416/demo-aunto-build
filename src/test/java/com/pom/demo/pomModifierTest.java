package com.pom.demo;

import org.jdom2.Document;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.XMLOutputter;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static org.junit.Assert.*;

public class pomModifierTest {

    private File testDirectory;
    private File testPomFile;

    @Before
    public void setUp() throws IOException {
        // 创建临时目录但不创建pom.xml文件
        testDirectory = Files.createTempDirectory("test_pom_dir").toFile();
        testPomFile = new File(testDirectory, "pom.xml");
        // 确保pom.xml文件在开始测试之前不存在
        if (testPomFile.exists()) {
            testPomFile.delete();
        }
        assertFalse(testPomFile.exists());
    }

    @After
    public void tearDown() {
        // 删除临时目录和文件
        if (testPomFile.exists()) {
            testPomFile.delete();
        }
        testDirectory.delete();
    }

    @Test
    public void testCreatePomFileWhenNotExist() throws IOException {
        // 调用createAndInitializePomFile方法
        pomModifier.createAndInitializePomFile(testPomFile);

        // 验证pom.xml文件是否被创建
        assertTrue(testPomFile.exists());

        // 验证文件内容是否符合预期
        String content = new String(Files.readAllBytes(testPomFile.toPath()));
        assertTrue(content.contains("<modelVersion>4.0.0</modelVersion>"));
    }
}