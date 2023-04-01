package com.example.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.repository.UserRepository;
import com.example.entity.User;
import com.example.service.exception.BusinessException;
import java.util.List;
import java.util.Optional;
import lombok.val;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

  public static final String EMAIL = "test@example.com";
  @Mock
  private UserRepository userRepository;

  @InjectMocks
  private UserService userService;

  @Test
  void getUserByEmail() throws BusinessException {
    val user = new User();

    when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(user));

    val result = userService.getUserByEmail(EMAIL);

    assertEquals(user, result);
    verify(userRepository, times(1)).findByEmail(EMAIL);
  }

  @Test
  void testGetUserByEmail_NotFound() {
    when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.empty());

    assertThrows(BusinessException.class, () -> userService.getUserByEmail(EMAIL));
    verify(userRepository, times(1)).findByEmail(EMAIL);
  }

  @Test
  void getUserById() throws BusinessException {
    long id = 123;
    val user = new User();

    when(userRepository.findById(id)).thenReturn(Optional.of(user));

    val result = userService.getUserById(id);

    assertEquals(user, result);
    verify(userRepository, times(1)).findById(id);
  }

  @Test
  void testGetUserById_NotFound() {
    long id = 123;

    when(userRepository.findById(id)).thenReturn(Optional.empty());

    assertThrows(BusinessException.class, () -> userService.getUserById(id));
    verify(userRepository, times(1)).findById(id);
  }

  @Test
  void getUsersByName() {
    val name = "test";
    int pageSize = 10;
    int pageNum = 1;
    val userList = List.of(new User(), new User());
    val userPage = new PageImpl<>(userList);

    when(userRepository.findByNameContainingIgnoreCase(eq(name), any(Pageable.class))).thenReturn(userPage);

    val result = userService.getUsersByName(name, pageSize, pageNum);

    assertEquals(userList, result);
    verify(userRepository, times(1)).findByNameContainingIgnoreCase(eq(name), any(Pageable.class));
  }

  @Test
  void create() throws BusinessException {
    val user = new User();

    when(userRepository.save(user)).thenReturn(user);

    val result = userService.create(user);

    assertEquals(user, result);
    verify(userRepository, times(1)).save(user);
  }

  @Test
  void update() throws BusinessException {
    val user = new User();

    when(userRepository.save(user)).thenReturn(user);

    val result = userService.update(user);

    assertEquals(user, result);
    verify(userRepository, times(1)).save(user);
  }

  @Test
  void testUpdate_NotFound() {
    val user = new User();

    when(userRepository.save(user)).thenThrow(new DataIntegrityViolationException("message"));

    assertThrows(BusinessException.class, () -> userService.update(user));
    verify(userRepository, times(1)).save(user);
  }

  @Test
  void testDeleteById() {
    long userId = 1L;

    userService.deleteById(userId);

    verify(userRepository, times(1)).deleteById(userId);
  }
}