package com.example.demo.Backend.CustomExceptions;

public class BookNotAvailableException extends BookException {
  public BookNotAvailableException(String errorMessage) {
    super(errorMessage);
  }
}
