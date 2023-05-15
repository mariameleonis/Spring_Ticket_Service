package com.example.config;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletRegistration;
import lombok.val;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

public class WebAppInitializer implements WebApplicationInitializer {

  @Override
  public void onStartup(ServletContext servletContext) {
    servletContext.setInitParameter("log4jConfiguration", "classpath:log4j2.xml");

    val context = new AnnotationConfigWebApplicationContext();
    context.setConfigLocation("com.example.config");

    val dispatcherServlet = new DispatcherServlet(context);

    val dispatcher = servletContext.addServlet("dispatcher", dispatcherServlet);
    dispatcher.setLoadOnStartup(1);
    dispatcher.addMapping("/");
  }
}