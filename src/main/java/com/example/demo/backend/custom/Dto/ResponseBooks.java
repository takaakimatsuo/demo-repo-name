package com.example.demo.backend.custom.Dto;


import java.util.ArrayList;
import java.util.List;




public class ResponseBooks {

  private List<BookClass> books = new ArrayList<BookClass>();
  private ResponseHeader header = new ResponseHeader();

  public ResponseBooks() {
  }

  public List<BookClass> getBooks() {
    return books;
  }

  public void setBooks(List<BookClass> b) {
    books = b;
  }

  public ResponseHeader getResponseHeader() {
    return header;
  }


}
