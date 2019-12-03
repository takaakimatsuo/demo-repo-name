package com.example.demo.backend.custom.exceptions;

public class DbException extends DaoException {
  public DbException(String errorMessage) {
    super(errorMessage);
  }
}
