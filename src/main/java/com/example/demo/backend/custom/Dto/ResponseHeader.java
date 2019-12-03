package com.example.demo.backend.custom.Dto;

public class ResponseHeader {

  private String message;

  ResponseHeader() {
    this.message = "Blank message";
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String msg) {
    this.message = msg;
  }

}
