package com.example.demo.backend.custom.Dto;

public class MessageHeader {

  private String message;

  MessageHeader() {
    this.message = "Blank message";
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String msg) {
    this.message = msg;
  }

}
