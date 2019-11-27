package com.example.demo.backend.custom.myexceptions;

public class BookUpdateException extends BookException {
  public BookUpdateException(String errorMessage) {
    super(errorMessage);
  }
}
