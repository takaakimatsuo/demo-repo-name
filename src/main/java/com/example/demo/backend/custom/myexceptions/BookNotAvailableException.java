package com.example.demo.backend.custom.myexceptions;

public class BookNotAvailableException extends BookException {
  public BookNotAvailableException(String errorMessage) {
    super(errorMessage);
  }
}
