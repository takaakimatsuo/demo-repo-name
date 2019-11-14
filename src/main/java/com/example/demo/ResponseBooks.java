package com.example.demo;
import java.util.ArrayList;
import java.util.List;

public class ResponseBooks {

    private List<BookClass> books = new ArrayList<>();
    private ResponseMsg msg = new ResponseMsg();

    public List<BookClass> getListBooks(){
        return books;
    }
    public void setListBooks(List<BookClass> b){
        books = b;
    }
    public ResponseMsg getResponseStatus(){
        return msg;
    }
    public void setResponseStatus(ResponseMsg m){
        this.msg = m;
    }
}
