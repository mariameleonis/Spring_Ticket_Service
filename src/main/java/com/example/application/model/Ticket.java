package com.example.application.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Ticket {
  private long id;
  private long eventId;
  private long userId;
  private int place;
}
