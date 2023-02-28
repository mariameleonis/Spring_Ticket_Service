package com.example.application.service.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class EventDeletedEvent {
  private final long eventId;

}
