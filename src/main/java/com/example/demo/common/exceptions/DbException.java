package com.example.demo.common.exceptions;

public class DbException extends DaoException {

  private String sqlCode = "";

  public DbException(String errorMessage) {
    super(errorMessage);
  }

  public DbException(String errorMessage, Throwable cause) {
    super(errorMessage, cause);
  }

  public DbException(String errorMessage, Throwable cause, String sqlCode) {
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
