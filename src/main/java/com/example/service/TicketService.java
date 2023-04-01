package com.example.service;

import com.example.entity.Event;
import com.example.entity.User;
import com.example.repository.TicketRepository;
import com.example.entity.Ticket;
import com.example.service.exception.BusinessException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class TicketService {

  private final TicketRepository ticketRepository;

  public Ticket findById(Long id) throws BusinessException {
    log.info("Getting user by id {}", id);
    return ticketRepository.findById(id).orElseThrow(BusinessException::new);
  }

  public Ticket bookTicket(Ticket ticket) {
    log.info("Booking ticket: {}", ticket);
    return ticketRepository.save(ticket);

  }

  public boolean isPlaceAvailableForEvent(Event event, int place) {
    log.info("Checking if place {} available for event id {}", place, event.getId());
    return !ticketRepository.existsByEventIdAndPlace(event.getId(), place);
  }

  public List<Ticket> getTicketsByUserSortedByEventDateDesc(User user, int pageSize, int pageNum) {
    log.info("Getting booked tickets for user with id {} (pageSize={}, pageNum={})", user.getId(), pageSize, pageNum);
    val pageable = PageRequest.of(pageNum, pageSize);
    return ticketRepository.findAllByUserIdOrderByEventDateDesc(user.getId(), pageable).getContent();
  }

  public List<Ticket> getTicketsByEventSortedByEventDateDesc(Event event, int pageSize, int pageNum) {
    log.info("Getting booked tickets for event with id {} (pageSize={}, pageNum={})", event.getId(), pageSize, pageNum);
    val pageable = PageRequest.of(pageNum, pageSize);
    return ticketRepository.findAllByEventIdOrderByUserEmailAsc(event.getId(), pageable).getContent();

  }

  public void cancelTicket(Long ticketId) {
    log.info("Canceling ticket with id {}", ticketId);
    ticketRepository.deleteById(ticketId);
    log.info("Canceled ticket with id {}", ticketId);
  }

}
