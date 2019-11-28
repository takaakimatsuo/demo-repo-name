package com.example.demo.backend.custom.exceptions;

public class DuplicateBookException extends BookException {
  public DuplicateBookException(String errorMessage) {
    super(errorMessage);
  }
}
