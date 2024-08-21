package com.application.demo;
import org.jdom2.JDOMException;
import java.io.IOException;
public class MainRunner {
    public static void main(String[] args) throws IOException, JDOMException {
        // 调用 ApplicationPropertiesModifier 的方法
        com.application.demo.ApplicationPropertiesModifier.modifyApplicationProperties("D:\\demo1", "test123451236");

        // 调用 SlimmingConfiguration 的方法
        com.filterconfiguration.SlimmingConfiguration.createBootstrapProperties("D:\\demo1/conf/ark", "bootstrap.properties");

        // 调用 pomModifier 的方法
        com.pom.demo.pomModifier.scanForFiles("D:\\demo1");
    }
}