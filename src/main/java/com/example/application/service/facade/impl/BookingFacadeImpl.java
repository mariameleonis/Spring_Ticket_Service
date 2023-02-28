package com.example.application.service.facade.impl;

import com.example.application.model.Category;
import com.example.application.model.Event;
import com.example.application.model.Ticket;
import com.example.application.model.User;
import com.example.application.service.EventService;
import com.example.application.service.RandomIdGenerator;
import com.example.application.service.TicketService;
import com.example.application.service.TicketValidator;
import com.example.application.service.UserService;
import com.example.application.service.exception.BusinessException;
import com.example.application.service.exception.EventNotFoundException;
import com.example.application.service.exception.UserNotFoundException;
import com.example.application.service.facade.BookingFacade;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class BookingFacadeImpl implements BookingFacade {

  private final EventService eventService;
  private final TicketService ticketService;
  private final UserService userService;
  private final RandomIdGenerator idGenerator;
  private final TicketValidator validator;

  @Override
  public Event getEventById(long eventId) throws EventNotFoundException {
    try {
      return eventService.getById(eventId);
    } catch (BusinessException e) {
      log.error("Error retrieving event by id", e);
      throw new EventNotFoundException(eventId, e);
    }
  }

  @Override
  public List<Event> getEventsByTitle(String title, int pageSize, int pageNum) {
    return eventService.getEventsByTitle(title, pageSize, pageNum);
  }

  @Override
  public List<Event> getEventsForDay(LocalDate day, int pageSize, int pageNum) {
    return eventService.getEventsForDay(day, pageSize, pageNum);
  }

  @Override
  public Event createEvent(Event event) {
    event.setId(idGenerator.getRandomLongId());
    return eventService.create(event);
  }

  @Override
  public Event updateEvent(Event event) {
    return eventService.updateEvent(event);
  }

  @Override
  public boolean deleteEvent(long eventId) {
    return eventService.deleteEvent(eventId);
  }

  @Override
  public User getUserById(long userId) throws UserNotFoundException {
    try {
      return userService.getUserById(userId);
    } catch (BusinessException e) {
      log.error("Error retrieving user by id", e);
      throw new UserNotFoundException(userId, e);
    }
  }

  @Override
  public User getUserByEmail(String email) throws UserNotFoundException {
    try {
      return userService.getUserByEmail(email);
    } catch (BusinessException e) {
      log.error("Error retrieving user by email", e);
      throw new UserNotFoundException(email, e);
    }
  }

  @Override
  public List<User> getUsersByName(String name, int pageSize, int pageNum) {
    return userService.getUsersByName(name, pageSize, pageNum);
  }

  @Override
  public User createUser(User user) {
    user.setId(idGenerator.getRandomLongId());
    return userService.create(user);
  }

  @Override
  public User updateUser(User user) throws UserNotFoundException {
    try {
      return userService.update(user);
    } catch (BusinessException e) {
      log.error("Error updating user", e);
      throw new UserNotFoundException(user.getId(), e);
    }
  }

  @Override
  public boolean deleteUser(long userId) {
    return userService.deleteById(userId);
  }

  @Override
  public Ticket bookTicket(long userId, long eventId, int place, Category category) {
    val ticket = createTicket(userId, eventId, place, category);
    validator.validate(ticket);
    return ticketService.bookTicket(ticket);
  }

  private Ticket createTicket(long userId, long eventId, int place, Category category) {
    return Ticket.builder()
        .id(idGenerator.getRandomLongId())
        .eventId(eventId)
        .userId(userId)
        .place(place)
        .category(category)
        .build();
  }

  @Override
  public List<Ticket> getBookedTickets(User user, int pageSize, int pageNum) {
    val ticketsByUser = ticketService.getBookedTicketsByUser(user.getId(), pageSize, pageNum);

    val eventIds = ticketsByUser.stream()
        .map(Ticket::getEventId)
        .collect(Collectors.toSet());

    val eventDates = getEventDates(eventIds);

    return ticketsByUser.stream()
        .sorted(Comparator.comparing(ticket -> eventDates.get(ticket.getEventId()), Comparator.reverseOrder()))
        .toList();
  }

  @SneakyThrows
  private Map<Long, LocalDate> getEventDates(Set<Long> eventIds) {
    return eventService.getByIds(eventIds).stream()
        .collect(Collectors.toMap(Event::getId, Event::getDate));
  }

  @Override
  public List<Ticket> getBookedTickets(Event event, int pageSize, int pageNum) {
    val ticketsByEvent = ticketService.getBookedTicketsByEvent(event.getId(), pageSize, pageNum);

    val userIds = ticketsByEvent.stream()
        .map(Ticket::getUserId)
        .collect(Collectors.toSet());

    val userEmails = getUserEmail(userIds);

    return ticketsByEvent.stream()
        .sorted(Comparator.comparing(ticket -> userEmails.get(ticket.getUserId())))
        .toList();
  }

  @SneakyThrows
  private Map<Long, String> getUserEmail(Set<Long> userIds) {
    return userService.getUsersById(userIds).stream()
        .collect(Collectors.toMap(User::getId, User::getEmail));
  }

  @Override
  public boolean cancelTicket(long ticketId) {
    return ticketService.cancelTicket(ticketId);
  }
}
