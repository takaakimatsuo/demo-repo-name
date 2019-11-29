package com.example.demo.backend.custom.exceptions;

public class InputFormatException extends Exception {
  public InputFormatException(String errorMessage) {
    super(errorMessage);
  }
}
