package com.example.application.service;

import com.example.application.dao.EventDao;
import com.example.application.model.Event;
import com.example.application.service.event.EventDeletedEvent;
import com.example.application.service.exception.BusinessException;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.context.ApplicationEventPublisher;

@RequiredArgsConstructor
@Slf4j
public class EventService {

  private final EventDao eventDao;

  private final ApplicationEventPublisher eventPublisher;

  public Event getById(long eventId) throws BusinessException {
    log.debug("Getting event by ID: {}", eventId);
    return eventDao.getById(eventId).orElseThrow(BusinessException::new);
  }

  public List<Event> getEventsByTitle(String title, int pageSize, int pageNum) {
    log.debug("Getting events by title: {}, pageSize: {}, pageNum: {}", title, pageSize, pageNum);
    return eventDao.getEventsByTitle(title, pageSize, pageNum);
  }

  public List<Event> getEventsForDay(LocalDate day, int pageSize, int pageNum) {
    log.debug("Getting events for day: {}, pageSize: {}, pageNum: {}", day, pageSize, pageNum);
    return eventDao.getEventsForDay(day, pageSize, pageNum);
  }

  public Event create(Event event) {
    log.debug("Creating event: {}", event);
    return eventDao.create(event);
  }

  public Event updateEvent(Event event) {
    log.debug("Updating event: {}", event);
    return eventDao.update(event);

  }

  public boolean deleteEvent(long eventId) {
    log.debug("Deleting event with ID: {}", eventId);
    val deleted = eventDao.deleteById(eventId);

    if (deleted) {
      log.debug("Publishing event deleted event with ID: {}", eventId);
      eventPublisher.publishEvent(new EventDeletedEvent(eventId));
    }

    return deleted;
  }

  public List<Event> getByIds(Set<Long> eventIds) {
    return eventDao.getByIds(eventIds);
  }
}
