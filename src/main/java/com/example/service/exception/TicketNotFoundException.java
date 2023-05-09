package com.example.service.exception;

public class TicketNotFoundException extends BusinessException {

  public static final String TICKET_NOT_FOUND_BY_ID_MESSAGE = "There is no ticket with ID %d.";

  public TicketNotFoundException(long ticketId, Throwable cause) {
    super((String.format(TICKET_NOT_FOUND_BY_ID_MESSAGE, ticketId)), cause);
  }

}
