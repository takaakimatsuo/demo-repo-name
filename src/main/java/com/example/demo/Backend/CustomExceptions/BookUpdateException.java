package com.example.demo.Backend.CustomExceptions;

public class BookUpdateException extends BookException {
  public BookUpdateException(String errorMessage) {
    super(errorMessage);
  }
}
