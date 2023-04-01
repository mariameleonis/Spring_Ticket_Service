package com.example;

import com.example.config.AppConfig;
import com.example.service.facade.BookingFacade;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

@Slf4j
public class Application {

  public static void main(String[] args) {
    val context = new AnnotationConfigApplicationContext(AppConfig.class);
    val bookingFacade = context.getBean("bookingFacadeImpl", BookingFacade.class);
    log.info(bookingFacade.getEventsByTitle("Co", 2, 0).toString());
    context.close();
  }

}
