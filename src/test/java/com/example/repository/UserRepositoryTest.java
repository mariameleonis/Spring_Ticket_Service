package com.example.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import lombok.val;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;

class UserRepositoryTest extends AbstractRepositoryTest {

  @Autowired
  private UserRepository userRepository;

  @Test
  void testFindById() {
    val foundUser = userRepository.findById(USER_ID);

    assertTrue(foundUser.isPresent());
    assertEquals(USER_EMAIL, foundUser.get().getEmail());
  }

  @Test
  void testFindByEmail() {
    val foundUser = userRepository.findByEmail(USER_EMAIL);

    assertTrue(foundUser.isPresent());
    assertEquals(USER_ID, foundUser.get().getId());
  }

  @ParameterizedTest
  @CsvSource({
      "joh, 2, 0, 2",
      "JOH, 2, 0, 2",
      "JOh, 1, 1, 1",
      "JoH, 0, 1, 2"
  })
  void testFindByNameContainingIgnoreCase(String name, int expectedSize, int pageNumber, int pageSize) {
    val pageable = PageRequest.of(pageNumber, pageSize);
    val foundUsers = userRepository.findByNameContainingIgnoreCase(name, pageable).getContent();

    assertEquals(expectedSize, foundUsers.size());
  }

}