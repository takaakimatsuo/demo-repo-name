package com.example.demo.common.exceptions;

import com.example.demo.common.enums.Messages;

public class InputFormatException extends Exception {
  private Messages message;

  public InputFormatException(String errorMessage) {
    super(errorMessage);
  }

  public InputFormatException(Messages enumErrorMessage) {
    super(enumErrorMessage.getMessageKey());
    message = enumErrorMessage;
  }

  public String getBookMessage() {
    return message.getMessageKey();
  }
}
