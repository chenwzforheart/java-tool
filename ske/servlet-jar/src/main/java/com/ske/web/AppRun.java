package com.ske.web;

import org.apache.catalina.LifecycleException;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.startup.Tomcat;

/**
 * @author csh9016
 * @date 2020/5/19
 */
public class AppRun {

    public static void main(String[] args) throws LifecycleException {
        // 创建Tomcat应用对象
        Tomcat tomcat = new Tomcat();
        // 设置Tomcat的端口号
        tomcat.setPort(8080);
        // 是否设置Tomcat自动部署
        tomcat.getHost().setAutoDeploy(false);
        // 创建上下文
        StandardContext standardContext = new StandardContext();
        // 设置项目名
        standardContext.setPath("/app");
        // 监听上下文
        standardContext.addLifecycleListener(new Tomcat.FixContextListener());
        // 向tomcat容器对象添加上下文配置
        tomcat.getHost().addChild(standardContext);
        // 创建Servlet
        tomcat.addServlet("/app", "helloword", new HelloServlet());
        // Servlet映射
        standardContext.addServletMapping("/hello", "helloword");
        // context load WEB-INF/web.xml from classpath
        standardContext.addLifecycleListener(new WebXmlMountListener());
        //启动tomcat容器
        tomcat.start();
        //等待
        tomcat.getServer().await();
    }
}
