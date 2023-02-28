package com.example.application.service;

import com.example.application.dao.UserDao;
import com.example.application.model.User;
import com.example.application.service.event.UserDeletedEvent;
import com.example.application.service.exception.BusinessException;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class UserService {

  @Autowired
  private UserDao userDao;

  @Autowired
  private ApplicationEventPublisher eventPublisher;

  public User getUserByEmail(String email) throws BusinessException {
    log.info("Getting user by email: {}", email);
    return userDao.getUserByEmail(email).orElseThrow(BusinessException::new);
  }

  public User getUserById(long id) throws BusinessException {
    log.info("Getting user by id: {}", id);
    return userDao.getUserById(id).orElseThrow(BusinessException::new);
  }

  public List<User> getUsersByName(String name, int pageSize, int pageNum) {
    log.info("Getting users by name: {}, page size: {}, page number: {}", name, pageSize, pageNum);
    return userDao.getUsersByName(name, pageSize, pageNum);
  }

  public User create(User user) {
    log.info("Creating user: {}", user);
    return userDao.create(user);
  }

  public User update(User user) throws BusinessException {
    log.info("Updating user: {}", user);
    return userDao.update(user)
        .orElseThrow(BusinessException::new);
  }

  public boolean deleteById(long userId) {
    log.info("Deleting user by id: {}", userId);
    val deleted = userDao.deleteById(userId);

    if (deleted) {
      log.info("Publishing event deleted user with id: {}", userId);
      eventPublisher.publishEvent(new UserDeletedEvent(userId));
    }

    return deleted;
  }

  public List<User> getUsersById(Set<Long> userIds) {
    log.info("Get users by ids: {}", userIds);
    return userDao.getByIds(userIds);
  }
}
