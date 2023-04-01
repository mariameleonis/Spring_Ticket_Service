package com.example.repository;

import com.example.entity.Event;
import java.time.LocalDate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventRepository extends JpaRepository<Event, Long> {

  Page<Event> findByTitleIsContainingIgnoreCase(String title, Pageable pageable);

  Page<Event> findByDate(LocalDate date, Pageable pageable);
}
