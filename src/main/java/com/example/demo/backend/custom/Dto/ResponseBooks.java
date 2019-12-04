package com.example.demo.backend.custom.Dto;

import java.util.ArrayList;
import java.util.List;

public class ResponseBooks {

  private List<Book> books = new ArrayList<Book>();
  private MessageHeader messageHeader = new MessageHeader();

  public ResponseBooks() {
  }

  public List<Book> getBooks() {
    return books;
  }

  public void setBooks(List<Book> b) {
    books = b;
  }

  public MessageHeader getMessageHeader() {
    return messageHeader;
  }

  public void setMessageHeader(MessageHeader header) {
    this.messageHeader = header;
  }



}
