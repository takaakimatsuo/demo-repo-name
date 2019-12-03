package com.example.demo.backend.custom.Dto;

import java.util.ArrayList;
import java.util.List;




public class ResponseUsers {


  private List<User> users = new ArrayList<User>();
  private MessageHeader header = new MessageHeader();

  public ResponseUsers() {
  }

  public List<User> getUsers() {
    return users;
  }

  public void setUsers(List<User> users) {
    this.users = users;
  }

  public MessageHeader getMessageHeader() {
    return header;
  }

  public void setMessageHeader(MessageHeader header) {
    this.header = header;
  }


}

