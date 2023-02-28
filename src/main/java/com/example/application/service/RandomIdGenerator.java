package com.example.application.service;

import java.util.Random;

public class RandomIdGenerator {

  public long getRandomLongId() {
    return getRandom().nextLong();
  }

  private Random getRandom() {
    return new Random();
  }

}
