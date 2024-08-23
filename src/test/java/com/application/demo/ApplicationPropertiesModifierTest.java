package com.application.demo;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;

class ApplicationPropertiesModifierTest {

    @Test
    void testModifySpringApplicationName() throws IOException, URISyntaxException {
        // 要设置的新应用名称
        String newAppName = "testAppName";
        // 从资源目录加载 application.properties 文件
        Path propertiesPath = Path.of(ApplicationPropertiesModifierTest.class.getResource("/application.properties").toURI());
        // 读取原始文件内容
        List<String> originalLines = Files.readAllLines(propertiesPath);

        // 执行修改操作
        ApplicationPropertiesModifier.modifySpringApplicationName(propertiesPath.toFile(), newAppName);

        // 读取修改后的文件内容
        List<String> modifiedLines = Files.readAllLines(propertiesPath);

        // 验证 spring.application.name 是否被正确修改
        boolean isAppNameModified = modifiedLines.stream()
                .anyMatch(line -> line.equals("spring.application.name=" + newAppName));
        assertTrue(isAppNameModified, "The application name should be modified to " + newAppName);

        // 验证其他属性是否未被修改
        for (String line : originalLines) {
            if (!line.startsWith("spring.application.name")) {
                assertTrue(modifiedLines.contains(line), "The property \"" + line + "\" should remain unchanged.");
            }
        }
    }
}