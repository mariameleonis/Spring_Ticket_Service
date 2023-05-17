package com.example.service.facade.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
import com.example.service.exception.UserNotFoundException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import lombok.val;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BookingFacadeImplTest {

  public static final String USER_NAME = "John Doe";
  public static final String USER_EMAIL = "john.doe@example.com";
  @Mock
  private EventService eventService;

  @Mock
  private TicketService ticketService;

  @Mock
  private UserService userService;

  @Mock
  private UserAccountService userAccountService;

  @InjectMocks
  private BookingFacadeImpl bookingFacade;

  @Test
  @DisplayName("Get Event by Id")
  void testGetEventById() throws BusinessException {
    long eventId = 123L;
    val event = new Event();

    when(eventService.getById(eventId)).thenReturn(event);

    val result = bookingFacade.getEventById(eventId);

    Assertions.assertEquals(event, result);
  }

  @Test
  @DisplayName("Get Event by Id - Not Found")
  void testGetEventById_whenNotFound() throws BusinessException {
    long eventId = 1L;

    when(eventService.getById(eventId)).thenThrow(new EventNotFoundException(eventId));

    assertThrows(EventNotFoundException.class, () -> bookingFacade.getEventById(eventId));
    verify(eventService, times(1)).getById(eventId);
  }

  @Test
  @DisplayName("Get Events by Title")
  void testGetEventsByTitle() {
    val title = "test";
    int pageSize = 10;
    int pageNum = 1;
    val events = Arrays.asList(new Event(), new Event());

    when(eventService.getEventsByTitle(title, pageSize, pageNum)).thenReturn(events);

    val result = bookingFacade.getEventsByTitle(title, pageSize, pageNum);

    assertEquals(events, result);
  }

  @Test
  @DisplayName("Get Events for Date")
  void testGetEventsForDay() {
    val day = LocalDate.now();
    int pageSize = 10;
    int pageNum = 1;
    val events = Arrays.asList(new Event(), new Event());

    when(eventService.getEventsForDay(day, pageSize, pageNum)).thenReturn(events);

    val result = bookingFacade.getEventsForDay(day, pageSize, pageNum);

    assertEquals(events, result);
  }

  @Test
  @DisplayName("Create Event")
  void testCreateEvent() {
    val event = new Event();

    when(eventService.create(event)).thenReturn(event);

    val result = bookingFacade.createEvent(event);

    Assertions.assertEquals(event, result);
  }

  @Test
  @DisplayName("Update Event")
  void testUpdateEvent() throws BusinessException {
    val event = new Event();

    when(eventService.updateEvent(event)).thenReturn(event);

    val result = bookingFacade.updateEvent(event);

    Assertions.assertEquals(event, result);
  }

  @Test
  @DisplayName("Delete Event")
  void testDeleteEvent() {
    long eventId = 1L;

    bookingFacade.deleteEvent(eventId);

    verify(eventService, times(1)).deleteEvent(eventId);
  }

  @Test
  @DisplayName("Get User by Id")
  void testGetUserById() throws BusinessException {
    val expectedUser = User.builder().id(1L).name(USER_NAME).email(USER_EMAIL).build();

    when(userService.getUserById(expectedUser.getId())).thenReturn(expectedUser);

    val actualUser = bookingFacade.getUserById(expectedUser.getId());

    Assertions.assertEquals(expectedUser, actualUser);
  }

  @Test
  @DisplayName("Get User by Id - User Not Found")
  void testGetUserById_UserNotFound() throws BusinessException {
    long userId = 1L;

    when(userService.getUserById(userId)).thenThrow(new UserNotFoundException(userId));

    assertThrows(UserNotFoundException.class, () -> bookingFacade.getUserById(userId));
  }

  @Test
  @DisplayName("Get User by Email")
  void testGetUserByEmail() throws UserNotFoundException, BusinessException {
    val expectedUser = User.builder().id(1L).name("Alice").email("alice@example.com").build();

    when(userService.getUserByEmail(expectedUser.getEmail())).thenReturn(expectedUser);

    val actualUser = bookingFacade.getUserByEmail(expectedUser.getEmail());

    Assertions.assertEquals(expectedUser, actualUser);
  }

  @Test
  @DisplayName("Get User by Email - User Not Found")
  void testGetUserByEmail_UserNotFound() throws BusinessException {
    val email = "alice@example.com";

    when(userService.getUserByEmail(email)).thenThrow(new BusinessException());

    assertThrows(UserNotFoundException.class, () -> bookingFacade.getUserByEmail(email));
  }

  @Test
  @DisplayName("Get Users by Name")
  void testGetUsersByName() {
    val name = "Alice";
    int pageSize = 2;
    int pageNum = 1;
    val expectedUsers = new ArrayList<User>();

    expectedUsers.add(User.builder().id(1L).name("Alice").email("alice@example.com").build());
    expectedUsers.add(User.builder().id(2L).name("Alice Smith").email("asmith@example.com").build());

    when(userService.getUsersByName(name, pageSize, pageNum)).thenReturn(expectedUsers);

    val actualUsers = bookingFacade.getUsersByName(name, pageSize, pageNum);

    assertEquals(expectedUsers, actualUsers);
  }

  @Test
  @DisplayName("Create User")
  void testCreateUser() throws BusinessException {
    val user = User.builder()
        .name(USER_NAME)
        .email(USER_EMAIL)
        .build();

    when(userService.create(user)).thenReturn(user.toBuilder().id(1L).build());

    val createdUser = bookingFacade.createUser(user);

    verify(userService).create(user);
    Assertions.assertEquals(1L, createdUser.getId());
    Assertions.assertEquals(USER_NAME, createdUser.getName());
    Assertions.assertEquals(USER_EMAIL, createdUser.getEmail());
  }

  @Test
  @DisplayName("Update User")
  void testUpdateUser() throws BusinessException {
    val user = User.builder()
        .id(1L)
        .name(USER_NAME)
        .email(USER_EMAIL)
        .build();

    when(userService.update(user)).thenReturn(user);

    val updatedUser = bookingFacade.updateUser(user);

    verify(userService).update(user);
    Assertions.assertEquals(1L, updatedUser.getId());
    Assertions.assertEquals(USER_NAME, updatedUser.getName());
    Assertions.assertEquals(USER_EMAIL, updatedUser.getEmail());
  }

  @Test
  @DisplayName("Update User - User Not Found")
  void testUpdateUser_UserNotFoundException() throws BusinessException {
    val user = User.builder()
        .id(1L)
        .name(USER_NAME)
        .email(USER_EMAIL)
        .build();

    when(userService.update(user)).thenThrow(new BusinessException());

    assertThrows(UserNotFoundException.class, () -> bookingFacade.updateUser(user));
    verify(userService).update(user);
  }

  @Test
  @DisplayName("Delete User")
  void testDeleteUser() {
    long userId = 1L;

    bookingFacade.deleteUser(userId);

    verify(userService).deleteById(userId);
  }

  @Test
  @DisplayName("Book Ticket")
  void testBookTicket() throws BusinessException {
    long userId = 1L;
    long eventId = 2L;
    int place = 3;
    val user = User.builder().id(userId).build();
    val event = Event.builder().id(eventId).ticketPrice(BigDecimal.valueOf(10)).build();
    val ticket = Ticket.builder()
        .id(4L)
        .user(user)
        .event(event)
        .place(place)
        .build();


    when(ticketService.bookTicket(any(Ticket.class))).thenReturn(ticket);
    when(userAccountService.findByUserId(anyLong())).thenReturn(UserAccount.builder().money(
        BigDecimal.valueOf(1000)).build());
    when(eventService.getById(anyLong())).thenReturn(event);
    when(ticketService.isPlaceAvailableForEvent(any(Event.class), anyInt())).thenReturn(true);

    val actualTicket = bookingFacade.bookTicket(userId, eventId, place);

    verify(ticketService, times(1)).bookTicket(any(Ticket.class));
    Assertions.assertEquals(ticket, actualTicket);
  }

  @Test
  @DisplayName("Get Tickets By User")
  void testGetBookedTicketsByUser() {
    val user = User.builder().id(1L).build();
    val tickets = Arrays.asList(
        Ticket.builder().build(),
        Ticket.builder().build()
    );


    when(ticketService.getTicketsByUserSortedByEventDateDesc(user, 2, 1))
        .thenReturn(tickets);

    val actualTickets = bookingFacade.getBookedTickets(user, 2, 1);


    verify(ticketService).getTicketsByUserSortedByEventDateDesc(user, 2, 1);
    assertEquals(tickets, actualTickets);
  }

  @Test
  @DisplayName("Get Tickets By Event")
  void testGetBookedTicketsByEvent() {
    long eventId = 123;
    val event = Event.builder().id(eventId).build();

    val tickets = Arrays.asList(
        Ticket.builder().build(),
        Ticket.builder().build()
    );

    when(ticketService.getTicketsByEventSortedByEventDateDesc(event, 2, 1))
        .thenReturn(tickets);


    val actualTickets = bookingFacade.getBookedTickets(event, 2, 1);

    verify(ticketService).getTicketsByEventSortedByEventDateDesc(event, 2, 1);
    assertEquals(tickets, actualTickets);
  }

  @Test
  @DisplayName("Cancel Ticket")
  void cancelTicket() throws BusinessException {
    val ticketPrice = BigDecimal.valueOf(100);
    val initialMoney = BigDecimal.valueOf(120);
    val userId = 1L;
    val ticketId = 123L;

    val ticket = Ticket.builder()
        .user(User.builder().id(userId).build())
        .event(Event.builder().ticketPrice(ticketPrice).build())
        .build();


    val userAccount = UserAccount.builder().money(
        initialMoney).build();


    when(ticketService.findById(ticketId)).thenReturn(ticket);
    when(userAccountService.findByUserId(userId)).thenReturn(userAccount);

    bookingFacade.cancelTicket(ticketId);

    verify(ticketService).cancelTicket(ticketId);
    verify(userAccountService, times(1)).findByUserId(userId);
    assertEquals(initialMoney.add(ticketPrice), userAccount.getMoney());
  }

}