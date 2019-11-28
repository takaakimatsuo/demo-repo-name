package com.example.demo.backend.custom.exceptions;

public class NegativeNumberException extends Exception {
  public NegativeNumberException(String errorMessage) {
    super(errorMessage);
  }
}
