package com.example.service.exception;

public class UserNotFoundException extends BusinessException {

  public static final String USER_NOT_FOUND_BY_ID_MESSAGE = "There is no user with ID %d.";

  public static final String USER_NOT_FOUND_BY_EMAIL_MESSAGE = "There is no user with email %s.";

  public UserNotFoundException(long userId) {
    super(String.format(USER_NOT_FOUND_BY_ID_MESSAGE, userId));
  }

  public UserNotFoundException(long userId, Throwable cause) {
    super((String.format(USER_NOT_FOUND_BY_ID_MESSAGE, userId)), cause);
  }

  public UserNotFoundException(String email, Throwable cause) {
    super((String.format(USER_NOT_FOUND_BY_EMAIL_MESSAGE, email)), cause);
  }
}
