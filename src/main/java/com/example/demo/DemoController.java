package com.example.demo;

import DemoBackend.*;
import DemoBackend.CustomENUMs.response_status;
import DemoBackend.CustomExceptions.InputFormatExeption;
import DemoBackend.CustomObjects.*;
import org.springframework.web.bind.annotation.*;
import java.sql.*;
import static com.example.demo.InputResolver.*;

@RestController
public class DemoController {


    /*
    @CrossOrigin
    @GetMapping(value = "/getBook")
    public ResponseBooks demo5(@RequestParam("id") String id){
        ResponseBooks lb = new ResponseBooks();
        try {
            lb = new DemoDomain().getBook(Integer.parseInt(id));
        }catch(SQLException e){
            System.out.println(e);
        }
        return lb;
    }*/


    /*
    @CrossOrigin
    @GetMapping(value = "/getAllBooks")
    //Return with Class!
    public ResponseBooks searchAllBooks(){
        ResponseBooks lb = new ResponseBooks();
        try {
            lb = new DemoDomain().getAllBooks();
        }catch(SQLException e){
            System.out.println(e);
        }
        return lb;
    }*/

/*
    @CrossOrigin
    @PutMapping(value = "/addBook")
    public ResponseBooks demo4(@RequestBody BookClass inputs) {
        ResponseBooks response = new ResponseBooks();
        System.out.println("URL is "+ inputs.getUrl());
        if(inputs.getTitle()==""){
            System.out.println("[ERROR] Empty title input from user.");
            response.setResponseStatus(new ResponseMsg(response_status.ERR,"User input with no title forbidden."));
        }else if(inputs.getQuantity()<=0 || inputs.getPrice()<0){
            response.setResponseStatus(new ResponseMsg(response_status.ERR,"Quantity must be more than 1, and Price must be more than 0."));
        } else {
            try {
                 response = new DemoDomain().addBook(inputs);
            } catch (SQLException e) {
                response.setResponseStatus(new ResponseMsg(response_status.ERR,e.toString()));
                System.out.println(e);
            }
        }
        return response;
    }


    @CrossOrigin
    @GetMapping(value = "/deleteBook")
    public ResponseBooks demo6(@RequestParam("id") String id){
        ResponseBooks lb = new ResponseBooks();
        try {
            lb = new DemoDomain().removeBook(Integer.parseInt(id));
        }catch(SQLException e){
            System.out.println(e);
        }
        return lb;
    }


    @CrossOrigin
    @PatchMapping(value = "/updateBookStatus")
    public ResponseBooks demo7(@RequestBody UpdateBookStatus upd_status){
        ResponseBooks lb = new ResponseBooks();
        System.out.println(upd_status.getBook_id()+","+upd_status.getPhone_number()+","+upd_status.getStatus());
        try {
            lb = new DemoDomain().updateBookStatus(upd_status);
        }catch(SQLException e){
            System.out.println(e);
        }
        return lb;
    }

*/



    @CrossOrigin
    @GetMapping(value = "/books/{id}")
    public ResponseBooks GET_book(@PathVariable("id") String book_id){

        ResponseBooks lb = new ResponseBooks();
        try {
            lb = new DemoBusinessLogic().getBook(assureInteger(book_id));
        }catch(SQLException e){
            System.out.println(e);
        }catch(InputFormatExeption e){
            System.out.println(e);
        }
        return lb;
    }


    @CrossOrigin
    @GetMapping(value = "/books")
    public ResponseBooks GET_books(){
        ResponseBooks lb = new ResponseBooks();
        try {
            lb = new DemoBusinessLogic().getAllBooks();
        }catch(SQLException e){
            System.out.println(e);
        }
        return lb;
    }

    @CrossOrigin
    @PostMapping(value = "/books")
    public ResponseBooks POST_books(@RequestBody BookClass inputs){
        ResponseBooks response = new ResponseBooks();
        try {
            response = new DemoBusinessLogic().addBook(assureBookClassList(inputs));
        } catch (SQLException | InputFormatExeption e ) {
            response.setResponseHeader(new ResponseHeader(response_status.ERR,e.getMessage()));
            System.out.println(e.getMessage());
        }

        return response;
    }

    //TODO
    @CrossOrigin
    @PutMapping(value = "/books/{id}")
    public ResponseBooks PUT_book(@PathVariable("id") String id, @RequestBody BookClass inputs){
        ResponseBooks lb = new ResponseBooks();
        //checkInteger(book_id)

        //System.out.println("Here I am PUT[id]"+id+", "+inputs.getTitle());
        return lb;
    }

    @CrossOrigin
    @PatchMapping(value = "/books/{id}")
    public ResponseBooks PATCH_book(@PathVariable("id") String id, @RequestBody PatchBookClass inputs){
        ResponseBooks lb = new ResponseBooks();
        try {
            lb = new DemoBusinessLogic().updateBookStatus(assureInteger(id),assurePatchBookClass(inputs));
        }catch(SQLException e){
            System.out.println(e);
        }catch(InputFormatExeption e){
            System.out.println(e);
        }
        return lb;
    }

    @CrossOrigin
    @DeleteMapping(value = "/books/{id}")
    public ResponseBooks DELETE_book(@PathVariable("id") String id){
        ResponseBooks lb = new ResponseBooks();
        try {
            lb = new DemoBusinessLogic().removeBook(assureInteger(id));
        }catch(SQLException e){
            System.out.println(e);
        }catch(InputFormatExeption e){
            System.out.println(e);
        }
        return lb;
    }


}

