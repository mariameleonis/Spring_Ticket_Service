package com.example.repository;

import static org.junit.jupiter.api.Assertions.*;

import lombok.val;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;

class TicketRepositoryTest extends AbstractRepositoryTest {

  @Autowired
  private TicketRepository ticketRepository;

  @ParameterizedTest
  @CsvSource({
      "0, 2, 4",
      "1, 1, 1",
      "0, 1, 4"
  })
  void testFindAllByUserIdOrderByEventDateDesc(int page, int size, long ticket1Id) {
    val pageable = PageRequest.of(page, size);
    val foundTickets = ticketRepository.findAllByUserIdOrderByEventDateDesc(USER_ID, pageable).getContent();

    assertEquals(size, foundTickets.size());
    assertEquals(ticket1Id, foundTickets.get(0).getId());
    if (size > 1) {
      assertTrue(foundTickets.get(0).getEvent().getDate().isAfter(foundTickets.get(1).getEvent().getDate()));
    }
  }

  @ParameterizedTest
  @CsvSource({
      "0, 2, 5",
      "1, 1, 4",
      "0, 1, 5"
  })
  void findAllByEventOrderByEventDateDesc(int page, int size, long ticket1Id) {
    val pageable = PageRequest.of(page, size);
    val foundTickets = ticketRepository.findAllByEventIdOrderByUserEmailAsc(2L, pageable).getContent();

    assertEquals(size, foundTickets.size());

    assertEquals(ticket1Id, foundTickets.get(0).getId());
    if (size > 1) {
      assertTrue(foundTickets.get(1).getUser().getEmail().compareTo(foundTickets.get(0).getUser().getEmail()) > 0);
    }

  }

  @ParameterizedTest
  @CsvSource({
      "1, 1, true",
      "1, 2, true",
      "1, 5, false",
      "6, 2, false"
  })
  void testExistsByEventIdAndPlace(long eventId, int place, boolean expected) {
    boolean result = ticketRepository.existsByEventIdAndPlace(eventId, place);
    assertEquals(expected, result);
  }

}