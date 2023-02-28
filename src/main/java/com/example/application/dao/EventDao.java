package com.example.application.dao;

import com.example.application.model.Event;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.val;

@AllArgsConstructor
public class EventDao implements PageableDao<Event> {

  public static final String EVENT = "event:";
  private final Map<String, Object> inMemoryStorage;

  public Event create(Event event) {
    val key = getKey(event);
    inMemoryStorage.put(key, event);
    return (Event) inMemoryStorage.get(key);
  }

  private String getKey(Event event) {
    return EVENT + event.getId();
  }

  public Event update(Event event) {

    return (Event) Objects.requireNonNull(inMemoryStorage.put(getKey(event), event));
  }

  public boolean deleteById(long eventId) {
    return inMemoryStorage.remove(EVENT + eventId) != null;
  }

  public Optional<Event> getById(long eventId) {
    return Optional.ofNullable((Event) inMemoryStorage.get(EVENT + eventId));
  }

  public List<Event> getEventsByTitle(String title, int pageSize, int pageNum) {
    return getListByPage(getEventsByTitle(title), pageSize, pageNum);
  }

  private List<Event> getAllEvents() {
    return inMemoryStorage.entrySet().stream()
        .filter(entry -> entry.getKey().startsWith(EVENT) && entry.getValue() instanceof Event)
        .map(entry -> (Event) entry.getValue())
        .toList();
  }

  public List<Event> getEventsForDay(LocalDate day, int pageSize, int pageNum) {
    return getListByPage(getEventsForDay(day), pageSize, pageNum);
  }

  public List<Event> getEventsByTitle(String title) {
    return getAllEvents().stream()
        .filter(event -> event.getTitle().contains(title))
        .toList();
  }

  public List<Event> getEventsForDay(LocalDate day) {
    return getAllEvents().stream()
        .filter(event -> event.getDate().equals(day))
        .toList();
  }

  public List<Event> getByIds(Set<Long> eventIds) {
    return getAllEvents().stream()
        .filter(event -> eventIds.contains(event.getId()))
        .toList();
  }
}
