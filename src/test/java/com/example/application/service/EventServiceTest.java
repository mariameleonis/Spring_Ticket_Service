package com.example.application.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.example.application.dao.EventDao;
import com.example.application.model.Event;
import com.example.application.service.event.EventDeletedEvent;
import com.example.application.service.exception.BusinessException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;
import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

@ExtendWith(MockitoExtension.class)
class EventServiceTest {

  @Mock
  private EventDao eventDao;

  @Mock
  private ApplicationEventPublisher eventPublisher;

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
    when(eventDao.getById(eventId)).thenReturn(Optional.of(testEvent));

    Event result = eventService.getById(eventId);

    assertEquals(testEvent, result);
  }

  @Test
  void testGetById_NotFound() {
    long eventId = testEvent.getId();

    when(eventDao.getById(eventId)).thenReturn(Optional.empty());

    assertThrows(BusinessException.class, () -> eventService.getById(eventId));
  }

  @Test
  void testGetEventsByTitle() {
    val title = "Test Event";
    int pageSize = 10;
    int pageNum = 1;

    when(eventDao.getEventsByTitle(title, pageSize, pageNum)).thenReturn(Arrays.asList(testEvent));

    val result = eventService.getEventsByTitle(title, pageSize, pageNum);

    assertEquals(1, result.size());
    assertEquals(testEvent, result.get(0));
  }

  @Test
  void testGetEventsForDay() {
    val day = LocalDate.now();
    int pageSize = 10;
    int pageNum = 1;

    when(eventDao.getEventsForDay(day, pageSize, pageNum)).thenReturn(
        Collections.singletonList(testEvent));

    val result = eventService.getEventsForDay(day, pageSize, pageNum);

    assertEquals(1, result.size());
    assertEquals(testEvent, result.get(0));
  }

  @Test
  void testCreate() {
    val newEvent = new Event();

    when(eventDao.create(newEvent)).thenReturn(testEvent);

    val result = eventService.create(newEvent);

    assertEquals(testEvent, result);
  }

  @Test
  void testUpdateEvent() {
    when(eventDao.update(testEvent)).thenReturn(testEvent);

    val result = eventService.updateEvent(testEvent);

    assertEquals(testEvent, result);
  }

  @Test
  void testDeleteEvent_Success() {
    long eventId = testEvent.getId();

    when(eventDao.deleteById(eventId)).thenReturn(true);

    val result = eventService.deleteEvent(eventId);

    assertTrue(result);
    verify(eventPublisher).publishEvent(any(EventDeletedEvent.class));
  }

  @Test
  void testDeleteEvent_Failure() {
    long eventId = testEvent.getId();

    when(eventDao.deleteById(eventId)).thenReturn(false);

    val result = eventService.deleteEvent(eventId);

    assertFalse(result);
    verifyNoInteractions(eventPublisher);
  }
}