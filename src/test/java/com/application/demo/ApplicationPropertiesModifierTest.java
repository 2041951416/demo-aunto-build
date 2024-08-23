package com.application.demo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class ApplicationPropertiesModifierTest {

    private ApplicationPropertiesModifier modifier;
    private File mockDirectory;
    private File mockFile;
    private File mockSubDirectory;

    @BeforeEach
    void setUp() {
        modifier = new ApplicationPropertiesModifier();
        mockDirectory = mock(File.class);
        mockFile = mock(File.class);
        mockSubDirectory = mock(File.class);
    }

    @Test
    void testScanDirectory_ShouldRecurseIntoSubDirectories() {
        ApplicationPropertiesModifier spyModifier = spy(new ApplicationPropertiesModifier());

        File[] subDirectories = new File[]{mockSubDirectory};
        when(mockDirectory.isDirectory()).thenReturn(true);
        when(mockDirectory.listFiles((file, name) -> name.equalsIgnoreCase("application.properties"))).thenReturn(new File[]{});
        when(mockDirectory.listFiles(File::isDirectory)).thenReturn(subDirectories);
        when(mockSubDirectory.isDirectory()).thenReturn(true);

        // 使用 spy 对象调用方法
        spyModifier.scanDirectory(mockDirectory, "testAppName");

        // 验证 spy 对象是否递归调用了子目录的 scanDirectory 方法
        verify(spyModifier).scanDirectory(mockSubDirectory, "testAppName");
    }

    @Test
    void testModifySpringApplicationName_ShouldUpdateAppName() throws IOException {
        Path mockPath = createTempFileWithContent("spring.application.name=oldName", "some.other.property=value");
        String newAppName = "testAppName";

        ApplicationPropertiesModifier.modifySpringApplicationName(mockPath.toFile(), newAppName);

        // 验证文件内容是否被正确修改
        String expectedContent = "spring.application.name=" + newAppName + System.lineSeparator() + "some.other.property=value";
        List<String> allLines = Files.readAllLines(mockPath);
        assertEquals(expectedContent, String.join(System.lineSeparator(), allLines));
    }

    private Path createTempFileWithContent(String... lines) throws IOException {
        Path tempFile = Files.createTempFile("temp", ".properties");
        Files.write(tempFile, Arrays.asList(lines));
        return tempFile;
    }
}