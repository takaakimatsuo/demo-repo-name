package com.example.demo.backend.custom.exceptions;

public class BookException extends Exception {
  private Integer errorCodes;

  public BookException(String errorMessage) {
    super(errorMessage);
  }

}
