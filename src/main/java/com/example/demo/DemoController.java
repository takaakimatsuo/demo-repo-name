package com.example.demo;

import DemoBackend.*;
import DemoBackend.CustomENUMs.response_status;
import DemoBackend.CustomExceptions.InputFormatExeption;
import DemoBackend.CustomObjects.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.sound.midi.Soundbank;
import java.sql.*;
import static com.example.demo.InputValidator.*;

@RestController
public class DemoController {


    @CrossOrigin
    @GetMapping(value = "/books/{id}")
    public ResponseBooks GET_book(@PathVariable("id") String book_id){

        ResponseBooks lb = new ResponseBooks();
        try {
            lb = new DemoBusinessLogic().getBook(assureInteger(book_id));
        }catch(SQLException | InputFormatExeption e){
            System.out.println("Controller: "+e);
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
    public ResponseBooks POST_book(@RequestBody BookClass inputs) {
        ResponseBooks response = new ResponseBooks();
        try {
            response = new DemoBusinessLogic().addBook(assureBookClass(inputs));
        } catch (InputFormatExeption | SQLException e) {
            response.setResponseHeader(new ResponseHeader(response_status.ERR,e.getMessage()));
            System.out.println(e.getMessage());
        }

        return response;
    }

    //TODO
    @CrossOrigin
    @PutMapping(value = "/books/{id}")
    public ResponseBooks PUT_book(@PathVariable("id") String id, @RequestBody BookClass inputs){
        System.out.println("IM HERE!");
        ResponseBooks response = new ResponseBooks();
        try {
            response = new DemoBusinessLogic().replaceBook(assureInteger(id),assureBookClass(inputs));
        } catch ( InputFormatExeption e ) {
            response.setResponseHeader(new ResponseHeader(response_status.ERR,e.getMessage()));
            System.out.println(e.getMessage());
            //throw e;
        }catch(SQLException e){
            response.setResponseHeader(new ResponseHeader(response_status.ERR,e.getMessage()));
            System.out.println(e.getMessage()+", "+e.getSQLState());
        }
        return response;
    }

    @CrossOrigin
    @PatchMapping(value = "/books/{id}")
    public ResponseBooks PATCH_book(@PathVariable("id") String id, @RequestBody PatchBookClass inputs){
        ResponseBooks lb = new ResponseBooks();
        try {
            System.out.println(inputs.getStatus()+", by "+inputs.getBorrower());
            lb = new DemoBusinessLogic().updateBook(assureInteger(id),assurePatchBookClass(inputs));
        }catch(SQLException | InputFormatExeption e){
            lb.setResponseHeader(new ResponseHeader(response_status.ERR,e.getMessage()));
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
        }catch(SQLException | InputFormatExeption e){
            System.out.println(e);
        }
        return lb;
    }

    /*
    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public String handleException(){
        return "Test";
    }*/

}

