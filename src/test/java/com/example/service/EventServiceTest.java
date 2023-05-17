package com.example.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.example.repository.EventRepository;
import com.example.entity.Event;
import com.example.service.exception.BusinessException;
import com.example.service.exception.EventNotFoundException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Optional;
import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class EventServiceTest {

  @Mock
  private EventRepository eventRepository;

  @InjectMocks
  private EventService eventService;

  private Event testEvent;

  @BeforeEach
  public void setup() {
    testEvent = new Event();
    testEvent.setId(1L);
    testEvent.setTitle("Test Event");
    testEvent.setDate(LocalDate.now());
  }

  @Test
  void testGetById_Success() throws BusinessException {
    long eventId = testEvent.getId();
    when(eventRepository.findById(eventId)).thenReturn(Optional.of(testEvent));

    Event result = eventService.getById(eventId);

    assertEquals(testEvent, result);
  }

  @Test
  void testGetById_NotFound() {
    long eventId = testEvent.getId();

    when(eventRepository.findById(eventId)).thenReturn(Optional.empty());

    assertThrows(EventNotFoundException.class, () -> eventService.getById(eventId));
  }

  @Test
  void testGetEventsByTitle() {
    val title = "Test Event";
    int pageSize = 10;
    int pageNum = 1;
    val eventPage = new PageImpl<>(Arrays.asList(testEvent));
    when(eventRepository.findByTitleIsContainingIgnoreCase(eq(title), any(Pageable.class)))
        .thenReturn(eventPage);

    val result = eventService.getEventsByTitle(title, pageSize, pageNum);

    assertEquals(1, result.size());
    assertEquals(testEvent, result.get(0));
  }

  @Test
  void testGetEventsForDay() {
    val day = LocalDate.now();
    int pageSize = 10;
    int pageNum = 1;
    val eventPage = new PageImpl<>(Arrays.asList(testEvent));

    when(eventRepository.findByDate(eq(day), any(Pageable.class)))
        .thenReturn(eventPage);

    val result = eventService.getEventsForDay(day, pageSize, pageNum);

    assertEquals(1, result.size());
    assertEquals(testEvent, result.get(0));
  }

  @Test
  void testCreate() {
    val newEvent = new Event();

    when(eventRepository.save(newEvent)).thenReturn(testEvent);

    val result = eventService.create(newEvent);

    assertEquals(testEvent, result);
  }

  @Test
  void testUpdateEvent() throws EventNotFoundException {
    val existingEvent = Event.builder()
        .id(testEvent.getId())
        .title("Existing Event")
        .build();

    when(eventRepository.findById(testEvent.getId())).thenReturn(Optional.of(existingEvent));
    when(eventRepository.save(testEvent)).thenReturn(testEvent);

    val result = eventService.updateEvent(testEvent);

    assertEquals(testEvent, result);
  }

  @Test
  void testDeleteEvent() {
    long eventId = testEvent.getId();

    eventService.deleteEvent(eventId);

    verify(eventRepository, times(1)).deleteById(eventId);
  }
}