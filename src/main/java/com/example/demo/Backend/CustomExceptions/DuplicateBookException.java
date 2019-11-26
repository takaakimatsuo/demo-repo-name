package com.example.demo.Backend.CustomExceptions;

public class DuplicateBookException extends BookException {
  public DuplicateBookException(String errorMessage) {
    super(errorMessage);
  }
}
