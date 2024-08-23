package com.filterconfiguration;
import static org.junit.jupiter.api.Assertions.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SlimmingConfigurationTest {

    private Path testDirectory;
    private String targetDirectory;
    private String fileName = "bootstrap.properties";

    @BeforeEach
    void setUp() throws IOException {
        // 创建测试目录
        testDirectory = Files.createTempDirectory("testDirectory");
        targetDirectory = testDirectory.toString();
    }

    @AfterEach
    void tearDown() throws IOException {
        // 删除测试目录及其内容
        Files.walk(testDirectory)
                .sorted((p, n) -> -p.compareTo(n))
                .forEach(p -> {
                    try {
                        Files.delete(p);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
    }

    @Test
    void testCreateBootstrapProperties_DirectoryCreated() {
        // 调用方法创建bootstrap.properties文件
        SlimmingConfiguration.createBootstrapProperties(targetDirectory, fileName);

        // 验证目录是否被创建
        File directory = new File(targetDirectory);
        assertTrue(directory.exists(), "The target directory should be created.");
    }

    @Test
    void testCreateBootstrapProperties_FileCreatedAndWritten() throws IOException {
        // 调用方法创建bootstrap.properties文件
        SlimmingConfiguration.createBootstrapProperties(targetDirectory, fileName);

        // 验证文件是否被创建
        File propertiesFile = new File(targetDirectory, fileName);
        assertTrue(propertiesFile.exists(), "The bootstrap.properties file should be created.");

        // 验证文件内容是否正确
        String expectedContent = "excludes=org.apache.commons:commons-lang3,commons-beanutils:commons-beanutils,org.springframework.boot:spring-boot-starter-json:2.7.16\n"
                + "excludeArtifactIds=sofa-ark-spi"+"\n";
        String actualContent = new String(Files.readAllBytes(propertiesFile.toPath()));
        assertEquals(expectedContent, actualContent, "The bootstrap.properties file content should match the expected content.");
    }
}