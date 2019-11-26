package com.example.demo.Backend.CustomObjects;

public class PatchBookClass {
  private String borrower;
  private int status;

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
