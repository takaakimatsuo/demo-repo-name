package com.example.demo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.bind.annotation.*;
import com.example.demo.response_status;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import DatabaseQueryApplier.psqlApi;

@RestController
public class DemoController {



    @GetMapping(value = "/hello")
    public String demo() {
        return "Hello, World!";
    }

    @GetMapping(value = "/hi")
    public String demo2() {
        return "Hi, World!";
    }

    @GetMapping(value = "/testing{ti}")
    public String demo3(@RequestParam("ti") String ti){
        String output = "";
        try {
            output = new psqlApi().getBookFromTitle(ti);
        }catch(SQLException e){
            output = e.toString();
        }
        return output;
    }


    @GetMapping(value = "/getBookFromID{id}")
    public String demo3(@RequestParam("id") int id){
        String output = "";
        try {
            output = new psqlApi().getBookFromID(id);
        }catch(SQLException e){
            output = e.toString();
        }
        return output;
    }

    @CrossOrigin
    @GetMapping(value = "/getAllBooks")
    //Return with Class!
    public ResponseBooks searchAllBooks(){
        String output = "";
        ResponseBooks lb = new ResponseBooks();
        try {
            lb = new psqlApi().getAllBooks();
            /*for(BookClass b:lb.getListBooks()) {
                output +=b.getTitle()+"," ;
                lb.add(b);
            }*/
            //Gson gson = new Gson();
            //String json = gson.toJson(lb);
            //System.out.println(json);

        }catch(SQLException e){
            output = e.toString();
            System.out.println(e);
        }
        //return output;
        return lb;
    }

    @CrossOrigin
    @PostMapping(value = "/addBook")
    public BookClass demo4(@RequestBody BookClass inputs) throws JsonProcessingException {
        ResponseMsg response = new ResponseMsg();
        System.out.println("URL is "+ inputs.getUrl());
        if(inputs.getTitle()==""){
            inputs.setResponseStatus(new ResponseMsg(response_status.ERR,"User input with no title forbidden."));
        }else if(inputs.getQuantity()<=0 || inputs.getPrice()<0){
            inputs.setResponseStatus(new ResponseMsg(response_status.ERR,"Quantity must be more than 1, and Price must be more than 0."));
        } else {
            try {
                 response = new psqlApi().addBook(inputs);
                 inputs.setResponseStatus(response);
            } catch (SQLException | JsonProcessingException e) {
                 inputs.setResponseStatus(new ResponseMsg(response_status.ERR,e.toString()));
            }
        }
        return inputs;
    }
}