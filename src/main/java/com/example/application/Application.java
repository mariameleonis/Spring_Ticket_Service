package com.example.application;

import com.example.application.config.ApplicationConfig;
import com.example.application.service.facade.impl.BookingFacadeImpl;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

@Slf4j
public class Application {

  public static void main(String[] args) {
    val context = new AnnotationConfigApplicationContext(ApplicationConfig.class);
    val bookingFacade = context.getBean(BookingFacadeImpl.class);
    log.info(bookingFacade.getUsersByName("Joh", 2, 1).toString());
    context.close();
  }

}
