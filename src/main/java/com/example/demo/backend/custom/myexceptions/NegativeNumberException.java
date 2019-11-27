package com.example.demo.backend.custom.myexceptions;

public class NegativeNumberException extends Exception {
  public NegativeNumberException(String errorMessage) {
    super(errorMessage);
  }
}
