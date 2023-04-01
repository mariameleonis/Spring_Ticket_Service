package com.example.service;

import com.example.entity.UserAccount;
import com.example.repository.UserAccountRepository;
import com.example.service.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserAccountService {

  private final UserAccountRepository repository;

  public UserAccount create(UserAccount userAccount)  {
    return repository.save(userAccount);
  }

  public UserAccount findByUserId(long userId) throws BusinessException {
    return repository.findByUserId(userId).orElseThrow(BusinessException::new);
  }

  public UserAccount findByUserEmail(String email) throws BusinessException {
    return repository.findByUserEmail(email).orElseThrow(BusinessException::new);
  }
}
