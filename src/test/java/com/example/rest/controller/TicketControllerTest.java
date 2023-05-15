package com.example.rest.controller;

import static com.example.rest.controller.TicketController.BOOKING_REQUEST_SENT;
import static com.example.rest.controller.TicketController.TICKET_CANCELLED;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.entity.Event;
import com.example.entity.Ticket;
import com.example.entity.User;
import com.example.rest.model.Booking;
import com.example.service.facade.BookingFacade;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
class TicketControllerTest {

  public static final String BOOKING_EXCHANGE = "booking-exchange";
  public static final String MY_ROUTING_KEY = "my-routing-key";
  public static final String BASE_URL = "/api/tickets";
  private MockMvc mockMvc;

  @Mock
  private BookingFacade bookingFacade;

  @Mock
  private RabbitTemplate rabbitTemplate;

  @InjectMocks
  private TicketController ticketController;

  @BeforeEach
  public void setUp() {
    ReflectionTestUtils.setField(ticketController, "bookingExchange", BOOKING_EXCHANGE);
    ReflectionTestUtils.setField(ticketController, "routingKey", MY_ROUTING_KEY);

    mockMvc = MockMvcBuilders.standaloneSetup(ticketController).build();
  }

  @Test
  void testBookTicket() throws Exception {
    val booking = new Booking(1L, 1L, 1);

    mockMvc.perform(post(BASE_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .content(asJsonString(booking)))
        .andExpect(status().isOk())
        .andExpect(content().string(BOOKING_REQUEST_SENT));

    verify(rabbitTemplate).convertAndSend(BOOKING_EXCHANGE, MY_ROUTING_KEY, booking);
  }

  @Test
  void testGetTicketsByUser() throws Exception {
    val userId = 1L;
    int pageSize = 10;
    int pageNum = 0;
    val user = User.builder().build();
    val ticket1 = getTicket(1L);
    val ticket2 = getTicket(2L);
    val tickets = List.of(ticket1, ticket2);

    when(bookingFacade.getUserById(userId)).thenReturn(user);
    when(bookingFacade.getBookedTickets(user, pageSize, pageNum)).thenReturn(tickets);

    mockMvc.perform(get(BASE_URL + "/user/{userId}", userId)
            .param("pageSize", String.valueOf(pageSize))
            .param("pageNum", String.valueOf(pageNum))
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(tickets.size())))
        .andExpect(jsonPath("$[0].id").value(String.valueOf(ticket1.getId())))
        .andExpect(jsonPath("$[1].id").value(String.valueOf(ticket2.getId())));
  }

  @Test
  void testGetTicketsByEvent() throws Exception {
    val eventId = 1L;
    int pageSize = 10;
    int pageNum = 0;
    val event = Event.builder().build();
    val ticket1 = getTicket(1L);
    val ticket2 = getTicket(2L);
    val tickets = List.of(ticket1, ticket2);

    when(bookingFacade.getEventById(eventId)).thenReturn(event);
    when(bookingFacade.getBookedTickets(event, pageSize, pageNum)).thenReturn(tickets);

    mockMvc.perform(get(BASE_URL + "/event/{eventId}", eventId)
            .param("pageSize", String.valueOf(pageSize))
            .param("pageNum", String.valueOf(pageNum))
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(tickets.size())))
        .andExpect(jsonPath("$[0].id").value(String.valueOf(ticket1.getId())))
        .andExpect(jsonPath("$[1].id").value(String.valueOf(ticket2.getId())));
  }

  @Test
  void testCancelTicket() throws Exception {
    val ticketId = 123L;

    mockMvc.perform(delete(BASE_URL + "/" + ticketId))
        .andExpect(status().isOk())
        .andExpect(content().string(String.format(TICKET_CANCELLED, ticketId)));

    verify(bookingFacade).cancelTicket(ticketId);
  }

  private Ticket getTicket(long ticketId) {
    return Ticket.builder()
        .id(ticketId)
        .build();
  }

  private String asJsonString(final Object obj) {
    try {
      return new ObjectMapper().writeValueAsString(obj);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}