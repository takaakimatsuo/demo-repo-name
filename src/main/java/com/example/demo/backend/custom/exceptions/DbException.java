package com.example.demo.backend.custom.exceptions;

public class DbException extends Exception {
  public DbException(String errorMessage) {
    super(errorMessage);
  }
}
