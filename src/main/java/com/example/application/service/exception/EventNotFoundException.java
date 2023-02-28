package com.example.application.service.exception;

public class EventNotFoundException extends BusinessException{

  public static final String EVENT_NOT_FOUND_BY_ID_MESSAGE = "There is no event with ID %d.";

  public EventNotFoundException(long eventId) {
    super(String.format(EVENT_NOT_FOUND_BY_ID_MESSAGE, eventId));
  }

  public EventNotFoundException(long eventId, Throwable cause) {
    super((String.format(EVENT_NOT_FOUND_BY_ID_MESSAGE, eventId)), cause);
  }
}
