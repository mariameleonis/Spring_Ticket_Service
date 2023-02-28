package com.example.application.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.example.application.dao.UserDao;
import com.example.application.model.User;
import com.example.application.service.event.UserDeletedEvent;
import com.example.application.service.exception.BusinessException;
import java.util.List;
import java.util.Optional;
import lombok.val;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

  public static final String EMAIL = "test@example.com";
  @Mock
  private UserDao userDao;

  @Mock
  private ApplicationEventPublisher eventPublisher;

  @InjectMocks
  private UserService userService;

  @Test
  void getUserByEmail() throws BusinessException {
    val user = new User();

    when(userDao.getUserByEmail(EMAIL)).thenReturn(Optional.of(user));

    val result = userService.getUserByEmail(EMAIL);

    assertEquals(user, result);
    verify(userDao, times(1)).getUserByEmail(EMAIL);
  }

  @Test
  void testGetUserByEmail_NotFound() {
    when(userDao.getUserByEmail(EMAIL)).thenReturn(Optional.empty());

    assertThrows(BusinessException.class, () -> userService.getUserByEmail(EMAIL));
    verify(userDao, times(1)).getUserByEmail(EMAIL);
  }

  @Test
  void getUserById() throws BusinessException {
    long id = 123;
    val user = new User();

    when(userDao.getUserById(id)).thenReturn(Optional.of(user));

    val result = userService.getUserById(id);

    assertEquals(user, result);
    verify(userDao, times(1)).getUserById(id);
  }

  @Test
  void testGetUserById_NotFound() {
    long id = 123;

    when(userDao.getUserById(id)).thenReturn(Optional.empty());

    assertThrows(BusinessException.class, () -> userService.getUserById(id));
    verify(userDao, times(1)).getUserById(id);
  }

  @Test
  void getUsersByName() {
    val name = "test";
    int pageSize = 10;
    int pageNum = 1;
    val userList = List.of(new User(), new User());

    when(userDao.getUsersByName(name, pageSize, pageNum)).thenReturn(userList);

    val result = userService.getUsersByName(name, pageSize, pageNum);

    assertEquals(userList, result);
    verify(userDao, times(1)).getUsersByName(name, pageSize, pageNum);
  }

  @Test
  void create() {
    val user = new User();

    when(userDao.create(user)).thenReturn(user);

    val result = userService.create(user);

    assertEquals(user, result);
    verify(userDao, times(1)).create(user);
  }

  @Test
  void update() throws BusinessException {
    val user = new User();

    when(userDao.update(user)).thenReturn(Optional.of(user));

    val result = userService.update(user);

    assertEquals(user, result);
    verify(userDao, times(1)).update(user);
  }

  @Test
  void testUpdate_NotFound() {
    val user = new User();

    when(userDao.update(user)).thenReturn(Optional.empty());

    assertThrows(BusinessException.class, () -> userService.update(user));
    verify(userDao, times(1)).update(user);
  }

  @Test
  void testDeleteById_Success() {
    long userId = 1L;
    when(userDao.deleteById(userId)).thenReturn(true);

    val result = userService.deleteById(userId);

    assertTrue(result);
    verify(eventPublisher).publishEvent(any(UserDeletedEvent.class));
  }

  @Test
  void testDeleteById_Failure() {
    long userId = 1L;

    when(userDao.deleteById(userId)).thenReturn(false);

    val result = userService.deleteById(userId);

    assertFalse(result);
    verifyNoInteractions(eventPublisher);
  }
}