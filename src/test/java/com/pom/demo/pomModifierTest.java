package com.pom.demo;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.io.File;

class pomModifierTest {

    @Test
    void testIsTargetFile_Positive() {
        // 创建一个模拟的File对象，命名为"pom.xml"
        File mockPomFile = new File("pom.xml");
        // 调用isTargetFile方法并断言期望的结果为true
        assertTrue(pomModifier.isTargetFile(mockPomFile), "The method should return true for 'pom.xml'");
    }

    @Test
    void testIsTargetFile_Negative() {
        // 创建一个模拟的File对象，命名为"not-pom.xml"
        File mockNotPomFile = new File("not-pom.xml");
        // 调用isTargetFile方法并断言期望的结果为false
        assertFalse(pomModifier.isTargetFile(mockNotPomFile), "The method should return false for files other than 'pom.xml'");
    }
}