package com.application.demo;

import com.filterconfiguration.SlimmingConfiguration;
import org.jdom2.JDOMException;
import com.pomdemo.pomModifier;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class MainRunner {

    public static void main(String[] args) {
        try {
            Scanner sc = new Scanner(System.in);
            System.out.println("请输入工程的绝对路径：");
            String projectPath = sc.nextLine();
            // 要设置的应用名称
            System.out.println("请输入要设置的应用名称：");
            String applicationName = sc.nextLine();
            // 调用 ApplicationPropertiesModifier 的 main 方法
            ApplicationPropertiesModifier.modifyApplicationProperties(projectPath, applicationName);

            SlimmingConfiguration.createBootstrapProperties(projectPath + "/conf/ark", "bootstrap.properties");
            // 调用 SlimmingConfiguration 的 main 方法
//            com.filterconfiguration.SlimmingConfiguration.main(args);
            pomModifier.processProjectPath(projectPath);
            // 调用 pomModifier 的 main 方法
//            com.pom.demo.pomModifier.main(args);

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("发生错误，请检查输入的路径是否正确，以及是否有足够的权限。");
        } catch (JDOMException e) {
            throw new RuntimeException(e);
        }
    }
}