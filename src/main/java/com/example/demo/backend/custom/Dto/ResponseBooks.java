package com.example.demo.backend.custom.Dto;

import com.example.demo.backend.custom.myenums.ServiceStatus;

import java.util.ArrayList;
import java.util.List;




public class ResponseBooks {


  private List<BookClass> books = new ArrayList<BookClass>();
  private ResponseHeader header = new ResponseHeader();


  public ResponseBooks(List<BookClass> books,ResponseHeader header) {
    this.books = books;
    this.header = header;
  }

  public ResponseBooks(List<BookClass> books) {
    this.books = books;
  }

  public ResponseBooks(ResponseHeader header) {
    this.header = header;
  }

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

  public void setResponseHeader(ResponseHeader header) {
    this.header = header;
  }

  public void ResponseBooksMsg(String header) {
    this.header.setMessage(header);
  }

  public void ResponseBooksStatus(ServiceStatus status) {
    this.header.setStatus(status);
  }

}
