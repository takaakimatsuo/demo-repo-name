package com.example.demo.common.exceptions;

import com.example.demo.common.enums.BookMessages;

public class BookException extends Exception {

  private BookMessages message;

  public BookException(String errorMessage) {
    super(errorMessage);
  }

  public BookException(BookMessages enumErrorMessage) {
    super(enumErrorMessage.getMessageKey());
    message = enumErrorMessage;
  }

  public String getEnumErrorMessage() {
    return message.getMessageKey();
  }

}
