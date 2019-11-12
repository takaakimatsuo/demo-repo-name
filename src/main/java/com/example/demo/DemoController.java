package com.example.demo;

import org.springframework.web.bind.annotation.*;

import java.sql.*;
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

    @PostMapping(value = "/addBook")
    public String demo4(@RequestBody String inputs){
        String output = "";
        /*try {
            output = new psqlApi().getBookFromID(id);
        }catch(SQLException e){
            output = e.toString();
        }*/
        System.out.println("At least you are here. \n"+inputs.toString());
        return output;
    }
}