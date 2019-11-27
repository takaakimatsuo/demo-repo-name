package com.example.demo.backend.custom.myexceptions;

public class DbException extends Exception {
  public DbException(String errorMessage) {
    super(errorMessage);
  }
}
