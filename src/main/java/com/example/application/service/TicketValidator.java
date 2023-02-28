package com.example.application.service;

import com.example.application.dao.TicketDao;
import com.example.application.model.Ticket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TicketValidator {

  private TicketDao ticketDao;

  @Autowired
  public void setTicketDao(TicketDao ticketDao) {
    this.ticketDao = ticketDao;
  }

  public void validate(Ticket ticket) {
    validatePlace(ticket.getPlace());

  }

  private void validatePlace(int place) {
    if (ticketDao.isPlaceBooked(place)) {
      throw new IllegalStateException("The place with number " + place + " has already been booked");
    }
  }

}
