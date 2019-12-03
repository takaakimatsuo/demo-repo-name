package com.example.demo.backend.custom.Dto;

import java.util.ArrayList;
import java.util.List;




public class ResponseUsers {


  private List<User> users = new ArrayList<User>();
  private ResponseHeader header = new ResponseHeader();

  public ResponseUsers() {
  }

  public List<User> getUsers() {
    return users;
  }

  public void setUsers(List<User> users) {
    this.users = users;
  }

  public ResponseHeader getResponseHeader() {
    return header;
  }

  public void setResponseHeader(ResponseHeader header) {
    this.header = header;
  }


}

