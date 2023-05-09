package com.example.rest.model;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Booking implements Serializable {
  private long userId;
  private long eventId;
  private int place;

}
