package com.example.service.facade.impl;

import com.example.entity.Event;
import com.example.entity.Ticket;
import com.example.entity.User;
import com.example.entity.UserAccount;
import com.example.service.EventService;
import com.example.service.TicketService;
import com.example.service.UserAccountService;
import com.example.service.UserService;
import com.example.service.exception.BusinessException;
import com.example.service.exception.EventNotFoundException;
import com.example.service.exception.TicketNotFoundException;
import com.example.service.exception.UserAccountNotFoundException;
import com.example.service.exception.UserNotFoundException;
import com.example.service.facade.BookingFacade;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
public class BookingFacadeImpl implements BookingFacade {

  public static final String ERROR_WHILE_TRYING_TO_BOOK_A_TICKET = "Error while trying to book a ticket";
  private final EventService eventService;
  private final TicketService ticketService;
  private final UserService userService;
  private final UserAccountService userAccountService;

  @Override
  public Event getEventById(long eventId) throws EventNotFoundException {
      return eventService.getById(eventId);
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
    return eventService.create(event);
  }

  @Override
  public Event updateEvent(Event event) throws BusinessException {
    return eventService.updateEvent(event);
  }

  @Override
  public void deleteEvent(long eventId) {
    eventService.deleteEvent(eventId);
  }

  @Override
  public User getUserById(long userId) throws UserNotFoundException {
      return userService.getUserById(userId);
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
  public User createUser(User user) throws BusinessException {
    val createdUser = userService.create(user);
    createUserAccount(createdUser);
    return createdUser;
  }

  private void createUserAccount(User createdUser) {
    val userAccount = UserAccount.builder()
        .user(createdUser)
        .money(BigDecimal.valueOf(0))
        .build();
    userAccountService.create(userAccount);
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
  public void deleteUser(long userId) {
    userService.deleteById(userId);
  }

  @Override
  @Transactional
  public Ticket bookTicket(long userId, long eventId, int place) throws BusinessException {
    log.debug("Start validating booking details");
    val event = validateEvent(eventId);
    val user = validateUser(userId);
    val userAccount = validateUserAccount(userId);
    validateIfEnoughMoney(event, userAccount);
    validatePlace(event, place);
    userAccount.setMoney(userAccount.getMoney().subtract(event.getTicketPrice()));
    return ticketService.bookTicket(mapToDatabase(user, event, place));
  }

  private User validateUser(long userId) throws BusinessException {
    User user = null;
    try {
      user = userService.getUserById(userId);
    } catch (UserNotFoundException e) {
      throw new BusinessException(ERROR_WHILE_TRYING_TO_BOOK_A_TICKET, e);
    }
    return user;
  }

  private UserAccount validateUserAccount(long userId) throws BusinessException {
    UserAccount userAccount = null;
    try {
      userAccount = userAccountService.findByUserId(userId);
    } catch (UserAccountNotFoundException e) {
      log.error(ERROR_WHILE_TRYING_TO_BOOK_A_TICKET, e);
      throw new BusinessException(ERROR_WHILE_TRYING_TO_BOOK_A_TICKET, e);
    }
    return userAccount;
  }

  private Event validateEvent(long eventId) throws BusinessException {
    Event event = null;
    try {
      event = eventService.getById(eventId);
    } catch (EventNotFoundException e) {
      log.error(ERROR_WHILE_TRYING_TO_BOOK_A_TICKET, e);
      throw new BusinessException(ERROR_WHILE_TRYING_TO_BOOK_A_TICKET, e);
    }
    return event;
  }

  private Ticket mapToDatabase(User user, Event event, int place) {
    return Ticket.builder()
        .event(event)
        .user(user)
        .place(place)
        .build();
  }


  private void validateIfEnoughMoney(Event event, UserAccount userAccount)
      throws BusinessException {
    if (userAccount.getMoney().compareTo(event.getTicketPrice()) < 0) {
      throw new BusinessException(
          String.format("User account with id %d does not have enough money", userAccount.getId()));
    }
  }

  private void validatePlace(Event event, int place) {
    if (!ticketService.isPlaceAvailableForEvent(event, place)) {
      throw new IllegalStateException("The place with number " + place + " has already been booked");
    }
  }

  @Override
  public List<Ticket> getBookedTickets(User user, int pageSize, int pageNum) {
    return ticketService.getTicketsByUserSortedByEventDateDesc(user, pageSize, pageNum);
  }

  @Override
  public List<Ticket> getBookedTickets(Event event, int pageSize, int pageNum) {
    return ticketService.getTicketsByEventSortedByEventDateDesc(event, pageSize, pageNum);
  }

  @Override
  @Transactional
  public void cancelTicket(Long ticketId) throws TicketNotFoundException, UserNotFoundException {
    Ticket ticketToCancel;
    try {
      ticketToCancel = ticketService.findById(ticketId);
    } catch (BusinessException e) {
      log.error("Error cancelling ticket", e);
      throw new TicketNotFoundException(ticketId, e);
    }
    refillUserAccountByUserId(ticketToCancel.getUser().getId(), ticketToCancel.getEvent()
        .getTicketPrice());
    ticketService.cancelTicket(ticketId);
  }

  @Override
  @Transactional
  public void refillUserAccountByUserId(Long userId, BigDecimal amount)
      throws UserNotFoundException {
    log.info("Refilling user account, user ID {}, amount {}", userId, amount);
    UserAccount userAccount;
    try {
      userAccount = userAccountService.findByUserId(userId);
    } catch (BusinessException e) {
      log.error("Error refilling user account by userId", e);
      throw new UserNotFoundException(userId, e);
    }
    refill(userAccount, amount);
  }

  @Override
  public UserAccount findUserAccountByUserId(Long userId) throws BusinessException {
    return userAccountService.findByUserId(userId);
  }

  private void refill(UserAccount userAccount, BigDecimal amount) {
    val newMoney = userAccount.getMoney().add(amount);
    userAccount.setMoney(newMoney);
  }
}
