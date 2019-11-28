package com.example.demo.backend.custom.objects;

import com.example.demo.backend.custom.myenums.ServiceStatus;


public class ResponseHeader {
  private ServiceStatus status;
  private String message;

  public ResponseHeader(ServiceStatus rs, String msg) {
    this.status = rs;
    this.message = msg;
  }

  ResponseHeader() {
    this.status = ServiceStatus.OK;
    this.message = "Blank message";
  }

  public ServiceStatus getStatus() {
    return status;
  }

  public void setStatus(ServiceStatus rs) {
    this.status = rs;
  }

  public String getMessage() {
    return message;
  }


  public void setMessage(String msg) {
    this.message = msg;
  }

}
