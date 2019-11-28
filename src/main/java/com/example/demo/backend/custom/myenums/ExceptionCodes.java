package com.example.demo.backend.custom.myenums;

import com.example.demo.backend.custom.myenums.myinterface.ExceptionCodeConstant;

public enum ExceptionCodes implements ExceptionCodeConstant {

  All_OK(CODE_200, "All OK."),
  REQUEST_ERROR(CODE_400, "The request is invalid."),
  SYSTEM_ERROR(CODE_500, "Error in the system occurred.");

  private final int id;
  private final String msg;

  ExceptionCodes(int id, String msg) {
    this.id = id;
    this.msg = msg;
  }

  public int getId() {
    return this.id;
  }

  public String getMsg() {
    return this.msg;
  }

}
