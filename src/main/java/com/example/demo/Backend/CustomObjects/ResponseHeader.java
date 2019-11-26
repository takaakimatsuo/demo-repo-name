package com.example.demo.Backend.CustomObjects;

import com.example.demo.Backend.CustomENUMs.ResponseStatus;


public class ResponseHeader {
  private ResponseStatus status;
  private String message;

  public ResponseHeader(ResponseStatus rs, String msg) {
    this.status = rs;
    this.message = msg;
  }

  ResponseHeader() {
    this.status = ResponseStatus.OK;
    this.message = "Blank message";
  }

  public ResponseStatus getStatus() {
    return status;
  }

  public void setStatus(ResponseStatus rs) {
    this.status = rs;
  }

  public String getMessage() {
    return message;
  }


  public void setMessage(String msg) {
    this.message = msg;
  }

}
