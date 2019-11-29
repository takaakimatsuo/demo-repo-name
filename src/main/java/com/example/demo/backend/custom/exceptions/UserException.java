package com.example.demo.backend.custom.exceptions;


public class UserException extends Exception {

  private Integer errorCodes;

  public UserException(String errorMessage) {
    super(errorMessage);
  }

}
