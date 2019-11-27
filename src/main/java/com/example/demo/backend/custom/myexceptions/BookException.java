package com.example.demo.backend.custom.myexceptions;

import com.example.demo.backend.custom.myenums.ExceptionCodes;

public class BookException extends Exception {
  private Integer errorCodes;

  public BookException(String errorMessage) {
    super(errorMessage);
  }

  public BookException(String errorMessage, Throwable cause) {
    super(errorMessage, cause);
  }

  public BookException(String errorMessage, Throwable cause, ExceptionCodes errorCode) {
    super(errorMessage, cause);
    this.errorCodes = errorCode.getId();
  }

  public Integer getErrorCode() {
    return errorCodes;
  }

}
