package com.filterconfiguration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class SlimmingConfigurationTest {

    private File mockDirectory;
    private File mockFile;

    @BeforeEach
    void setUp() {
        mockDirectory = mock(File.class);
        mockFile = mock(File.class);
    }

    @Test
    void testCreateBootstrapProperties() throws IOException {
        String targetDirectory = "C:\\demo810/conf/ark";
        String fileName = "bootstrap.properties";

        try (MockedStatic<File> mockedFile = Mockito.mockStatic(File.class);
             MockedStatic<FileWriter> mockedFileWriter = Mockito.mockStatic(FileWriter.class)) {

            // 模拟目录不存在并创建成功
            mockedFile.when(() -> new File(targetDirectory)).thenReturn(mockDirectory);
            when(mockDirectory.exists()).thenReturn(false);
            when(mockDirectory.mkdirs()).thenReturn(true);

            // 模拟文件不存在并创建成功
            mockedFile.when(() -> new File(mockDirectory, fileName)).thenReturn(mockFile);
            when(mockFile.createNewFile()).thenReturn(true);

            // 捕获写入内容
            ArgumentCaptor<String> contentCaptor = ArgumentCaptor.forClass(String.class);
            FileWriter mockWriter = mock(FileWriter.class);
            mockedFileWriter.when(() -> new FileWriter(mockFile)).thenReturn(mockWriter);

            doNothing().when(mockWriter).write(contentCaptor.capture());

            // 调用待测试方法
            SlimmingConfiguration.createBootstrapProperties(targetDirectory, fileName);

            // 验证目录是否创建
            verify(mockDirectory).mkdirs();

            // 验证写入的内容
            String expectedContent = "excludes=org.apache.commons:commons-lang3,commons-beanutils:commons-beanutils,org.springframework.boot:spring-boot-starter-json:2.7.16\n" +
                    "excludeArtifactIds=sofa-ark-spi\n";
            assertEquals(expectedContent, String.join("", contentCaptor.getAllValues()));
        }
    }
}
