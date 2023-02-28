package com.example.application.dao;

import com.example.application.model.User;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class UserDao implements PageableDao<User> {

  public static final String USER = "user:";
  private final Map<String, Object> inMemoryStorage;

  public User create(User user) {
    inMemoryStorage.put(USER + user.getId(), user);
    return (User) inMemoryStorage.get(USER + user.getId());
  }

  public Optional<User> getUserByEmail(String email) {
    return getAllUsers().stream()
        .filter(user -> user.getEmail().equals(email))
        .findFirst();
  }

  public Optional<User> getUserById(long id) {
    return Optional.ofNullable((User) inMemoryStorage.get(USER + id));
  }

  private List<User> getAllUsers() {
    return inMemoryStorage.entrySet().stream()
        .filter(entry -> entry.getKey().startsWith(USER) && entry.getValue() instanceof User)
        .map(entry -> (User) entry.getValue())
        .toList();
  }

  public List<User> getUsersByName(String name, int pageSize, int pageNum) {
    return getListByPage(getUsersByName(name), pageSize, pageNum);
  }

  public Optional<User> update(User user) {
    return Optional.ofNullable((User) inMemoryStorage.put(USER + user.getId(), user));
  }

  public boolean deleteById(long userId) {
    return inMemoryStorage.remove(USER + userId) != null;
  }

  public List<User> getUsersByName(String name) {
    return getAllUsers().stream()
        .filter(user -> user.getName().contains(name))
        .toList();
  }

  public List<User> getByIds(Set<Long> userIds) {
    return getAllUsers().stream()
        .filter(user -> userIds.contains(user.getId()))
        .toList();
  }
}
