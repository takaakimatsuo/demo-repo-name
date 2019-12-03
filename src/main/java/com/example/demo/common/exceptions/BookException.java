package com.example.demo.common.exceptions;

public class BookException extends Exception {
  private Integer errorCodes;

  public BookException(String errorMessage) {
    super(errorMessage);
  }

}
