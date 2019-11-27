package com.example.demo.backend.custom.myexceptions;

public class DuplicateBookException extends BookException {
  public DuplicateBookException(String errorMessage) {
    super(errorMessage);
  }
}
