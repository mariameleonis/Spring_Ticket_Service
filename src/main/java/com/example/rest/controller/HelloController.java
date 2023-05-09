package com.example.rest.controller;

import org.springframework.web.bind.annotation.GetMapping;
import static org.springframework.util.MimeTypeUtils.TEXT_PLAIN_VALUE;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

  @GetMapping(value = "/hello", produces = TEXT_PLAIN_VALUE)
  public String hello() {
    return "Hello World!";
  }

}
