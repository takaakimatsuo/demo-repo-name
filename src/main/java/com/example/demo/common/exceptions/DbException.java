package com.example.demo.common.exceptions;

import com.example.demo.common.exceptions.DaoException;

public class DbException extends DaoException {
  public DbException(String errorMessage) {
    super(errorMessage);
  }
}
