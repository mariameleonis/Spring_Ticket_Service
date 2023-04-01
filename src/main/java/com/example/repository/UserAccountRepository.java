package com.example.repository;

import com.example.entity.UserAccount;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;

public interface UserAccountRepository extends CrudRepository<UserAccount, Long> {

  Optional<UserAccount> findByUserId(long userId);

  Optional<UserAccount> findByUserEmail(String email);
}
