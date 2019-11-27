package com.example.demo.backend.custom.myexceptions;

public class DataNotFoundException extends BookException {
  public DataNotFoundException(String errorMessage) {
    super(errorMessage);
  }
}
