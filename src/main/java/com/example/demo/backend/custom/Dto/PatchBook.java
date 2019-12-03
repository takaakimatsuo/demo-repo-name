package com.example.demo.backend.custom.Dto;

public class PatchBook {
  private String borrower;
  private int status;

  public PatchBook(){

  }

  public PatchBook(String borrower, int status) {
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
