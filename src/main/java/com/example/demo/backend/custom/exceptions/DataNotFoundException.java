package com.example.demo.backend.custom.exceptions;

public class DataNotFoundException extends BookException {
  public DataNotFoundException(String errorMessage) {
    super(errorMessage);
  }
}
