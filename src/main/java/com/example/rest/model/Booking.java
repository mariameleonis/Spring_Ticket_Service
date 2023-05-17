package com.example.rest.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Booking implements Serializable {

  @NotNull
  private Long userId;

  @NotNull
  private Long eventId;

  @NotNull
  private Integer place;

}
