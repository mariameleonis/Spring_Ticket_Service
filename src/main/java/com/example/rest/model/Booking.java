package com.example.rest.model;

import lombok.Data;

@Data
public class Booking {
  private long userId;
  private long eventId;
  private int place;

}
