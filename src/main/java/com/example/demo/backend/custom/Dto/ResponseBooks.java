package com.example.demo.backend.custom.Dto;


import java.util.ArrayList;
import java.util.List;




public class ResponseBooks {

  private List<Book> books = new ArrayList<Book>();
  private ResponseHeader header = new ResponseHeader();

  public ResponseBooks() {
  }

  public List<Book> getBooks() {
    return books;
  }

  public void setBooks(List<Book> b) {
    books = b;
  }

  public ResponseHeader getResponseHeader() {
    return header;
  }

  public void setResponseHeader(ResponseHeader header) {
    this.header = header;
  }


}
