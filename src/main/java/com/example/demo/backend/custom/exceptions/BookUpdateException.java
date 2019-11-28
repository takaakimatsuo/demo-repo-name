package com.example.demo.backend.custom.exceptions;

public class BookUpdateException extends BookException {
  public BookUpdateException(String errorMessage) {
    super(errorMessage);
  }
}
