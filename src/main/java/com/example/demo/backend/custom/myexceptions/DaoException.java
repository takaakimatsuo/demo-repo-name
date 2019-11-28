package com.example.demo.backend.custom.myexceptions;

import com.example.demo.backend.custom.myenums.ExceptionCodes;

public class DaoException extends Exception {

  private Integer errorCodes;
  private String sqlCode;

  public DaoException(String errorMessage) {
    super(errorMessage);
  }

  public DaoException(String errorMessage, Throwable cause) {
    super(errorMessage, cause);
  }


  public DaoException(String errorMessage, Throwable cause, ExceptionCodes errorCode) {
    super(errorMessage, cause);
    this.errorCodes = errorCode.getId();
  }

  public DaoException(String errorMessage, Throwable cause, String sqlCode, ExceptionCodes errorCode) {
    super(errorMessage, cause);
    this.errorCodes = errorCode.getId();
    this.sqlCode = sqlCode;
  }

  public Integer getErrorCode() {
    return errorCodes;
  }

  public String getSqlCode() {
    return sqlCode;
  }

}
