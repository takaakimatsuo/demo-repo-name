package com.example.demo.backend.custom.Dto;

import java.util.ArrayList;
import java.util.List;




public class ResponseUsers {


  private List<BookUser> users = new ArrayList<BookUser>();
  private ResponseHeader header = new ResponseHeader();


  public ResponseUsers(List<BookUser> users,ResponseHeader header) {
    this.users = users;
    this.header = header;
  }

  public ResponseUsers(List<BookUser> users) {
    this.users = users;
  }

  public ResponseUsers(ResponseHeader header) {
    this.header = header;
  }

  public ResponseUsers() {
  }


  public List<BookUser> getUsers() {
    return users;
  }


  public void setUsers(List<BookUser> users) {
    this.users = users;
  }


  public ResponseHeader getResponseHeader() {
    return header;
  }

  public void setResponseHeader(ResponseHeader header) {
    this.header = header;
  }


}

