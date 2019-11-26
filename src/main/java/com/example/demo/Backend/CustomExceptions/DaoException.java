package com.example.demo.Backend.CustomExceptions;

public class DaoException extends Exception {
  public DaoException(String errorMessage) {
    super(errorMessage);
  }
}
