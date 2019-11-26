package com.example.demo.Backend.CustomExceptions;

public class DbException extends Exception {
  public DbException(String errorMessage) {
    super(errorMessage);
  }
}
