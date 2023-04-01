package com.example.repository;

import com.example.entity.User;
import java.util.Optional;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, Long> {

  Optional<User> findByEmail(String email);

  @Cacheable(value = "myCache", key = "#id")
  Optional<User> findById(long id);

  Page<User> findByNameContainingIgnoreCase(String name, Pageable pageable);

}
