package com.example.demo.common.exceptions;

public class DaoException extends Exception {

  private String sqlCode = "";

  public DaoException(String errorMessage) {
    super(errorMessage);
  }

  public DaoException(String errorMessage, Throwable cause) {
    super(errorMessage, cause);
  }


  public DaoException(String errorMessage, Throwable cause, String sqlCode) {
    super(errorMessage, cause);
    this.sqlCode = sqlCode;
  }

  public String getSqlCode() {
    return sqlCode;
  }

  public void setSqlCode(String sqlCode) {
    this.sqlCode = sqlCode;
  }

}
