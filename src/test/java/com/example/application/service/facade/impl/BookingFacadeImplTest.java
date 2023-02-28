package com.example.application.service.facade.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import lombok.val;
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
  private RandomIdGenerator idGenerator;

  @Mock
  private TicketValidator validator;

  @InjectMocks
  private BookingFacadeImpl bookingFacade;

  @Test
  @DisplayName("Get Event by Id")
  void testGetEventById() throws BusinessException {
    long eventId = 123L;
    val event = new Event();

    when(eventService.getById(eventId)).thenReturn(event);

    val result = bookingFacade.getEventById(eventId);

    assertEquals(event, result);
  }

  @Test
  @DisplayName("Get Event by Id - Not Found")
  void testGetEventById_whenNotFound() throws BusinessException {
    long eventId = 1L;

    when(eventService.getById(eventId)).thenThrow(new BusinessException());

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

    when(idGenerator.getRandomLongId()).thenReturn(123L);
    when(eventService.create(event)).thenReturn(event);

    val result = bookingFacade.createEvent(event);

    assertEquals(event, result);
    assertEquals(123L, event.getId());
  }

  @Test
  @DisplayName("Update Event")
  void testUpdateEvent() {
    val event = new Event();

    when(eventService.updateEvent(event)).thenReturn(event);

    val result = bookingFacade.updateEvent(event);

    assertEquals(event, result);
  }

  @Test
  @DisplayName("Delete Event")
  void testDeleteEvent() {
    long eventId = 1L;
    when(eventService.deleteEvent(eventId)).thenReturn(true);

    val result = bookingFacade.deleteEvent(eventId);

    assertTrue(result);
    verify(eventService, times(1)).deleteEvent(eventId);
  }

  @Test
  @DisplayName("Get User by Id")
  void testGetUserById() throws BusinessException {
    val expectedUser = User.builder().id(1L).name(USER_NAME).email(USER_EMAIL).build();

    when(userService.getUserById(expectedUser.getId())).thenReturn(expectedUser);

    val actualUser = bookingFacade.getUserById(expectedUser.getId());

    assertEquals(expectedUser, actualUser);
  }

  @Test
  @DisplayName("Get User by Id - User Not Found")
  void testGetUserById_UserNotFound() throws BusinessException {
    long userId = 1L;

    when(userService.getUserById(userId)).thenThrow(new BusinessException());

    assertThrows(UserNotFoundException.class, () -> bookingFacade.getUserById(userId));
  }

  @Test
  @DisplayName("Get User by Email")
  void testGetUserByEmail() throws UserNotFoundException, BusinessException {
    val expectedUser = User.builder().id(1L).name("Alice").email("alice@example.com").build();

    when(userService.getUserByEmail(expectedUser.getEmail())).thenReturn(expectedUser);

    val actualUser = bookingFacade.getUserByEmail(expectedUser.getEmail());

    assertEquals(expectedUser, actualUser);
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
  void testCreateUser() {
    val user = User.builder()
        .name(USER_NAME)
        .email(USER_EMAIL)
        .build();

    when(idGenerator.getRandomLongId()).thenReturn(1L);
    when(userService.create(user)).thenReturn(user.toBuilder().id(1L).build());

    val createdUser = bookingFacade.createUser(user);

    verify(idGenerator).getRandomLongId();
    verify(userService).create(user);
    assertEquals(1L, createdUser.getId());
    assertEquals(USER_NAME, createdUser.getName());
    assertEquals(USER_EMAIL, createdUser.getEmail());
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
    assertEquals(1L, updatedUser.getId());
    assertEquals(USER_NAME, updatedUser.getName());
    assertEquals(USER_EMAIL, updatedUser.getEmail());
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

    when(userService.deleteById(userId)).thenReturn(true);

    val result = bookingFacade.deleteUser(userId);

    verify(userService).deleteById(userId);
    assertTrue(result);
  }

  @Test
  @DisplayName("Book Ticket")
  void testBookTicket() {
    long userId = 1L;
    long eventId = 2L;
    int place = 3;
    val category = Category.ECONOMY;
    val user = User.builder().id(userId).build();
    val event = Event.builder().id(eventId).build();
    val ticket = Ticket.builder()
        .id(4L)
        .userId(userId)
        .eventId(eventId)
        .place(place)
        .category(category)
        .build();

    when(idGenerator.getRandomLongId()).thenReturn(4L);
    when(ticketService.bookTicket(any(Ticket.class))).thenReturn(ticket);

    val actualTicket = bookingFacade.bookTicket(userId, eventId, place, category);

    verify(validator).validate(ticket);
    verify(ticketService).bookTicket(ticket);
    assertEquals(ticket, actualTicket);
  }

  @Test
  @DisplayName("Get Tickets By User")
  void testGetBookedTicketsByUser() {
    val user = User.builder().id(1L).build();
    val tickets = Arrays.asList(
        Ticket.builder().id(1L).userId(1L).eventId(2L).place(3).category(Category.ECONOMY).build(),
        Ticket.builder().id(2L).userId(1L).eventId(3L).place(4).category(Category.PREMIUM).build()
    );

    when(ticketService.getBookedTicketsByUser(user.getId(), 2, 1))
        .thenReturn(tickets);
    when(eventService.getByIds(anySet())).thenReturn(
        List.of(Event.builder().id(2L).date(LocalDate.now().plusDays(10)).build(),
                Event.builder().id(3L).date(LocalDate.now().plusDays(5)).build()));


    val actualTickets = bookingFacade.getBookedTickets(user, 2, 1);

    verify(eventService).getByIds(anySet());
    verify(ticketService).getBookedTicketsByUser(user.getId(), 2, 1);
    assertEquals(tickets, actualTickets);
  }

  @Test
  @DisplayName("Get Tickets By Event")
  void testGetBookedTicketsByEvent() {
    long eventId = 123;
    val event = Event.builder().id(eventId).build();

    val tickets = Arrays.asList(
        Ticket.builder().id(1L).userId(1L).eventId(eventId).place(3).category(Category.ECONOMY).build(),
        Ticket.builder().id(2L).userId(2L).eventId(eventId).place(4).category(Category.PREMIUM).build()
    );

    when(ticketService.getBookedTicketsByEvent(eventId, 2, 1))
        .thenReturn(tickets);
    when(userService.getUsersById(anySet())).thenReturn(
        List.of(User.builder().id(1L).email("aemail").build(),
            User.builder().id(2L).email("bemail").build()));


    val actualTickets = bookingFacade.getBookedTickets(event, 2, 1);

    verify(userService).getUsersById(anySet());
    verify(ticketService).getBookedTicketsByEvent(eventId, 2, 1);
    assertEquals(tickets, actualTickets);
  }

  @Test
  @DisplayName("Cancel Ticket")
  void cancelTicket() {
    bookingFacade.cancelTicket(123);

    verify(ticketService).cancelTicket(123);
  }

}