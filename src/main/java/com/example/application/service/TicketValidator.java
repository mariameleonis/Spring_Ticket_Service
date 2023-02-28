package com.example.application.service;

import com.example.application.dao.TicketDao;
import com.example.application.model.Ticket;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class TicketValidator {

  private final TicketDao ticketDao;

  public void validate(Ticket ticket) {
    validatePlace(ticket.getPlace());

  }

  private void validatePlace(int place) {
    if (ticketDao.isPlaceBooked(place)) {
      throw new IllegalStateException("The place with number " + place + " has already been booked");
    }
  }

}
