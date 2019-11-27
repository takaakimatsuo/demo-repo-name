package com.example.demo.backend.custom.myexceptions;

public class InputFormatExeption extends Exception {
  public InputFormatExeption(String errorMessage) {
    super(errorMessage);
  }
}
