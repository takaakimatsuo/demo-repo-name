package com.example.demo.common.exceptions;


public class UserException extends Exception {

  private Integer errorCodes;

  public UserException(String errorMessage) {
    super(errorMessage);
  }

}
