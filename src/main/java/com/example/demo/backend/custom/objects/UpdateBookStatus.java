package com.example.demo.backend.custom.objects;

public class UpdateBookStatus {

  private int bookId;
  private int status;
  private String phoneNumber;

  public void setBook_id(int bookId) {
    this.bookId = bookId;
  }

  public int getBook_id() {
    return bookId;
  }

  public void setStatus(int status) {
    this.status = status;
  }

  public int getStatus() {
    return status;
  }

  public String getPhoneNumber() {
    return phoneNumber;
  }

  public void setPhoneNumber(String phoneNumber) {
    this.phoneNumber = phoneNumber;
  }


}
