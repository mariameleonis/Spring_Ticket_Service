package com.example.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import lombok.val;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;

class EventRepositoryTest extends AbstractRepositoryTest {

  @Autowired
  private EventRepository eventRepository;

  @ParameterizedTest
  @CsvSource({
      "Co, 0, 1, 1",
      "CO, 0, 2, 2",
      "co, 1, 1, 1",
      "cO, 1, 2, 0"
  })
  void findByTitleIsContainingIgnoreCase(String title, int page, int size, int expectedSize) {
    val pageable = PageRequest.of(page, size);

    val foundEvents = eventRepository.findByTitleIsContainingIgnoreCase(title, pageable).getContent();

    assertEquals(expectedSize, foundEvents.size());
  }

  @ParameterizedTest
  @CsvSource({
      "2022-07-20, 0, 1, 1",
      "2022-07-20, 0, 2, 2",
      "2022-07-20, 1, 1, 1",
      "2022-07-20, 1, 2, 0"
  })
  void findByDate(String date, int page, int size, int expectedSize) {
    val pageable = PageRequest.of(page, size);

    val foundEvents = eventRepository.findByDate(LocalDate.parse(date), pageable).getContent();

    assertEquals(expectedSize, foundEvents.size());
  }
}