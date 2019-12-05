package com.example.demo.common.exceptions;


import com.example.demo.common.enums.Messages;



public class UserBusinessLogicException extends Exception {

  private Messages message;

  public UserBusinessLogicException(String errorMessage) {
    super(errorMessage);
  }

  public UserBusinessLogicException(Messages enumErrorMessage) {
    super(enumErrorMessage.getMessageKey());
    message = enumErrorMessage;
  }

  public String getBookMessage() {
    return message.getMessageKey();
  }

}
