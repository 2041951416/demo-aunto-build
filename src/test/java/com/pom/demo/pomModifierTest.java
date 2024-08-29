package com.pom.demo;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import static org.junit.jupiter.api.Assertions.*;

class pomModifierTest {

    private Path tempDir;
    private File pomFile;

    @BeforeEach
    void setUp() throws Exception {
        // Create a temporary directory for testing
        tempDir = Files.createTempDirectory("pomModifierTest");
        pomFile = tempDir.resolve("pom.xml").toFile();
    }

    @AfterEach
    void tearDown() throws Exception {
        // Clean up the temporary directory after tests
        Files.walk(tempDir)
                .map(Path::toFile)
                .forEach(File::delete);
    }

    @Test
    void testCreateAndInitializePomFile() throws Exception {
        // Test the creation and initialization of the pom.xml file
        pomModifier.createAndInitializePomFile(pomFile);

        // Verify the file exists and has content
        assertTrue(pomFile.exists());
        assertTrue(pomFile.length() > 0);

        // Verify the content of the file
        SAXBuilder builder = new SAXBuilder();
        Document document = builder.build(pomFile);
        Element root = document.getRootElement();
        assertEquals("project", root.getName());
    }

    @Test
    void testUpdatePomFileForEmptyPom() throws Exception {
        // Create an empty pom.xml file
        Files.write(pomFile.toPath(), new byte[0], StandardOpenOption.CREATE);

        // Test updating an empty pom.xml file
        pomModifier.updatePomFile(pomFile);

        // Verify the file has been reinitialized and updated
        SAXBuilder builder = new SAXBuilder();
        Document document = builder.build(pomFile);
        Element root = document.getRootElement();

        assertEquals("project", root.getName());
        assertNotNull(root.getChild("properties"));
        assertNotNull(root.getChild("dependencies"));
        assertNotNull(root.getChild("build"));
    }

    @Test
    void testUpdatePomFileForValidPom() throws Exception {
        // Initialize pom.xml with minimal valid content
        String initialContent = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<project xmlns=\"http://maven.apache.org/POM/4.0.0\"\n" +
                "         xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
                "         xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd\">\n" +
                "    <modelVersion>4.0.0</modelVersion>\n" +
                "</project>";

        Files.write(pomFile.toPath(), initialContent.getBytes(), StandardOpenOption.CREATE);

        // Test updating a valid pom.xml file
        pomModifier.updatePomFile(pomFile);

        // Verify the file has been updated
        SAXBuilder builder = new SAXBuilder();
        Document document = builder.build(pomFile);
        Element root = document.getRootElement();

        assertEquals("project", root.getName());
        assertNotNull(root.getChild("properties"));
        assertNotNull(root.getChild("dependencies"));
        assertNotNull(root.getChild("build"));
    }
}
