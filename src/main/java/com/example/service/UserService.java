package com.example.service;

import com.example.entity.User;
import com.example.repository.UserRepository;
import com.example.service.exception.BusinessException;
import com.example.service.exception.UserNotFoundException;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

  public static final String EMAIL_ADDRESS_ALREADY_EXISTS = "Email address already exists";
  private final UserRepository userRepository;

  public User getUserByEmail(String email) throws BusinessException {
    log.info("Getting user by email: {}", email);
    return userRepository.findByEmail(email).orElseThrow(BusinessException::new);
  }

  public User getUserById(long id) throws UserNotFoundException {
    log.info("Getting user by id: {}", id);
    return userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
  }

  public List<User> getUsersByName(String name, int pageSize, int pageNum) {
    log.info("Getting users by name: {}, page size: {}, page number: {}", name, pageSize, pageNum);
    val pageable = PageRequest.of(pageNum, pageSize);
    return userRepository.findByNameContainingIgnoreCase(name, pageable).getContent();
  }

  public User create(User user) throws BusinessException {
    log.info("Creating user: {}", user);
    return saveOrUpdate(user);
  }

  public User update(User user) throws BusinessException {
    log.info("Updating user: {}", user);
    val existingUser = userRepository.findById(user.getId()).orElseThrow(BusinessException::new);
    return saveOrUpdate(map(user, existingUser));

  }

  private User saveOrUpdate(User user) throws BusinessException {
    try {
      return userRepository.save(user);
    } catch (DataIntegrityViolationException e) {
      throw new BusinessException(
          String.format("%s (%s)", EMAIL_ADDRESS_ALREADY_EXISTS, user.getEmail()));
    }
  }

  private User map(User source, User target) {
    target.setName(source.getName());
    target.setEmail(source.getEmail());
    return target;
  }

  public void deleteById(long userId) {
    log.info("Deleting user by id: {}", userId);
    userRepository.deleteById(userId);
  }

}
