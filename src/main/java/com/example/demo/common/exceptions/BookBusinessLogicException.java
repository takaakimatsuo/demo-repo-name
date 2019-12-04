package com.example.demo.common.exceptions;

import com.example.demo.common.enums.Messages;

public class BookBusinessLogicException extends Exception {

  private Messages message;

  public BookBusinessLogicException(String errorMessage) {
    super(errorMessage);
  }

  public BookBusinessLogicException(Messages enumErrorMessage) {
    super(enumErrorMessage.getMessageKey());
    message = enumErrorMessage;
  }

  public String getBookMessage() {
    return message.getMessageKey();
  }

}
