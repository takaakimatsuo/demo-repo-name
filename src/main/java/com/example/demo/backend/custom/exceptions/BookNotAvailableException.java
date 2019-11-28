package com.example.demo.backend.custom.exceptions;

public class BookNotAvailableException extends BookException {
  public BookNotAvailableException(String errorMessage) {
    super(errorMessage);
  }
}
