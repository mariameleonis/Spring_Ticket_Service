package com.example.application.dao;

import com.example.application.model.Ticket;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.val;
import org.springframework.stereotype.Repository;

@AllArgsConstructor
public class TicketDao implements PageableDao<Ticket> {
  public static final String TICKET = "ticket:";
  private final Map<String, Object> inMemoryStorage;

  public boolean isPlaceBooked(int place) {
    return getAllTickets().stream()
        .map(Ticket::getPlace)
        .anyMatch(p -> p == place);
  }

  public Ticket book(Ticket ticket) {
    inMemoryStorage.put(TICKET + ticket.getId(), ticket);
    return (Ticket) inMemoryStorage.get(TICKET + ticket.getId());
  }

  private List<Ticket> getAllTickets() {
    return inMemoryStorage.entrySet().stream()
        .filter(entry -> entry.getKey().startsWith(TICKET) && entry.getValue() instanceof Ticket)
        .map(entry -> (Ticket) entry.getValue())
        .toList();
  }

  public List<Ticket> getBookedTicketsByUserId(long userId, int pageSize, int pageNum) {
    return getAllTickets().stream()
        .filter(ticket -> ticket.getUserId() == userId)
        .skip((long) (pageNum - 1) * pageSize)
        .limit(pageSize)
        .toList();
  }

  public List<Ticket> getBookedTicketsByUserId(long userId) {
    return getAllTickets().stream()
        .filter(ticket -> ticket.getUserId() == userId)
        .toList();
  }

  public List<Ticket> getBookedTicketsByEventId(long eventId, int pageSize, int pageNum) {
    return getAllTickets().stream()
        .filter(ticket -> ticket.getEventId() == eventId)
        .skip((long) (pageNum - 1) * pageSize)
        .limit(pageSize)
        .toList();
  }

  public boolean deleteById(long ticketId) {
    return inMemoryStorage.remove(TICKET + ticketId) != null;
  }

  public void deleteAllByTicketId(List<Long> ticketIds) {
    val keysToDelete = ticketIds.stream()
            .map(id -> TICKET + id)
            .toList();

    inMemoryStorage.entrySet().removeIf(entry -> keysToDelete.contains(entry.getKey()));
  }

  public List<Ticket> getBookedTicketsByEventId(long eventId) {
    return getAllTickets().stream()
        .filter(ticket -> ticket.getEventId() == eventId)
        .toList();
  }
}
