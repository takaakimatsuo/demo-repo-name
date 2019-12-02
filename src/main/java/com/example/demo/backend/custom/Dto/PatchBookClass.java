package com.example.demo.backend.custom.Dto;

public class PatchBookClass {
  private String borrower;
  private int status;

  public PatchBookClass(){

  }

  public PatchBookClass(String borrower, int status) {
    this.borrower = borrower;
    this.status = status;
  }

  public void setBorrower(String borrower) {
    this.borrower = borrower;
  }

  public String getBorrower() {
    return borrower;
  }

  public void setStatus(int status) {
    this.status = status;
  }

  public int getStatus() {
    return status;
  }

}
