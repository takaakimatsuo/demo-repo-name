package com.example.demo;

import com.google.gson.Gson;
import org.springframework.web.bind.annotation.*;
import com.example.demo.BookClass;
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

    @GetMapping(value = "/getAllBooks")
    public String searchAllBooks(){
        String output = "";
        List<BookClass> lb = new ArrayList<>();
        try {
            lb = new psqlApi().getAllBooks();
            for(BookClass b:lb) {
                output +=b.getTitle()+"," ;
            }
            Gson gson = new Gson();
            String json = gson.toJson(lb);
            System.out.println(json);

        }catch(SQLException e){
            output = e.toString();
            System.out.println(e);
        }
        return output;
    }

    @CrossOrigin
    @PostMapping(value = "/addBook")
    public String demo4(@RequestBody BookClass inputs){
        String output = "";
        try {
            new psqlApi().addBook(inputs);
            output = "All OK";
        }catch(SQLException e){
            output = e.toString();
        }
        //System.out.println("At least you are here. \n"+inputs.getTitle());
        return output;
    }
}