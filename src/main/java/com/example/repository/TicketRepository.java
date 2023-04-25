package com.example.repository;

import com.example.entity.Ticket;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

public interface TicketRepository extends CrudRepository<Ticket, Long> {

  Page<Ticket> findAllByUserIdOrderByEventDateDesc(Long userId, Pageable pageable);

  Page<Ticket> findAllByEventIdOrderByUserEmailAsc(Long eventId, Pageable pageable);

  boolean existsByEventIdAndPlace(Long eventId, int place);
}
