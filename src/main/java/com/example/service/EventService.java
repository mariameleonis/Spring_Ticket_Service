package com.example.service;

import com.example.repository.EventRepository;
import com.example.entity.Event;
import com.example.service.exception.BusinessException;
import com.example.service.exception.EventNotFoundException;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class EventService {

  private final EventRepository eventRepository;

  public Event getById(long eventId) throws EventNotFoundException {
    log.info("Getting event by ID: {}", eventId);
    return eventRepository.findById(eventId).orElseThrow(() -> new EventNotFoundException(eventId));
  }

  public List<Event> getEventsByTitle(String title, int pageSize, int pageNum) {
    log.info("Getting events by title: {}, pageSize: {}, pageNum: {}", title, pageSize, pageNum);
    val pageable = PageRequest.of(pageNum, pageSize);
    return eventRepository.findByTitleIsContainingIgnoreCase(title, pageable).getContent();
  }

  public List<Event> getEventsForDay(LocalDate day, int pageSize, int pageNum) {
    log.info("Getting events for day: {}, pageSize: {}, pageNum: {}", day, pageSize, pageNum);
    val pageable = PageRequest.of(pageNum, pageSize);
    return eventRepository.findByDate(day, pageable).getContent();
  }

  public Event create(Event event) {
    log.info("Creating event: {}", event);
    return eventRepository.save(event);
  }

  public Event updateEvent(Event event) throws EventNotFoundException {
    log.info("Updating event: {}", event);
    val existingEvent = eventRepository.findById(event.getId()).orElseThrow(() -> new EventNotFoundException(event.getId()));
    return eventRepository.save(map(event, existingEvent));
  }

  private Event map(Event source, Event target) {
    target.setDate(source.getDate());
    target.setTitle(source.getTitle());
    target.setTicketPrice(source.getTicketPrice());
    return target;
  }

  public void deleteEvent(long eventId) {
    log.info("Deleting event with ID: {}", eventId);
    eventRepository.deleteById(eventId);
  }

}
