package com.example.service.exception;

import com.example.entity.UserAccount;

public class UserAccountNotFoundException extends BusinessException {

  public static final String USER_ACCOUNT_NOT_FOUND_BY_USER_ID_MESSAGE = "There is no user account for user with ID %d.";

  public UserAccountNotFoundException(long userId) {
    super((String.format(USER_ACCOUNT_NOT_FOUND_BY_USER_ID_MESSAGE, userId)));
  }

}
