package com.example.demo.common.enums;

public enum UserMessages {
  USER_NOT_EXISTING("User not existing."),
  USER_INSERTED("User successfully added!"),
  USER_DELETED("User successfully deleted!"),
  USER_DUPLICATE("User with the same phone number already exists!");

  private String messageKey;

  UserMessages(String msgKey) {
    messageKey = msgKey;
  }

  public String getMessageKey() {
    return messageKey;
  }
}
