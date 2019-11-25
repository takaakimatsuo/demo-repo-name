package com.example.demo;

import com.example.demo.Backend.*;
import com.example.demo.Backend.CustomENUMs.response_status;
import com.example.demo.Backend.CustomExceptions.DuplicateBookException;
import com.example.demo.Backend.CustomExceptions.InputFormatExeption;
import com.example.demo.Backend.CustomObjects.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.sql.*;
import static com.example.demo.InputValidator.*;

@RestController
public class DemoController {

    @Autowired
    DemoBusinessLogic dbl;


    @CrossOrigin
    @GetMapping(value = "/books/{id}")
    public ResponseBooks GET_book(@PathVariable("id") String book_id){
        ResponseBooks response = new ResponseBooks();
        try {
            response = dbl.getBook(assureInteger(book_id));
        }catch(SQLException | InputFormatExeption e){
            //TODO
            System.out.println("Controller: "+e);
        }
        return response;
    }


    @CrossOrigin
    @GetMapping(value = "/books")
    public ResponseBooks GET_books(){
        ResponseBooks response = new ResponseBooks();
        try {
            response = dbl.getAllBooks();
        }catch(SQLException e){
            //TODO
            System.out.println(e);
        }
        return response;
    }

    @CrossOrigin
    @PostMapping(value = "/books")
    public ResponseBooks POST_book(@RequestBody BookClass inputs) {
        ResponseBooks response = new ResponseBooks();
        try {
            response = dbl.addBook(assureBookClass(inputs));
        } catch (InputFormatExeption | DuplicateBookException e) {
            //TODO
            response.setResponseHeader(new ResponseHeader(response_status.ERR,e.getMessage()));
            System.out.println(e.getMessage());
            //throw e
        }

        return response;
    }

    @CrossOrigin
    @PutMapping(value = "/books/{id}")
    public ResponseBooks PUT_book(@PathVariable("id") String id, @RequestBody BookClass inputs){
        System.out.println("IM HERE!");
        ResponseBooks response = new ResponseBooks();
        try {
            response = dbl.replaceBook(assureInteger(id),assureBookClass(inputs));
        } catch ( InputFormatExeption e ) {
            //TODO
            response.setResponseHeader(new ResponseHeader(response_status.ERR,e.getMessage()));
            System.out.println(e.getMessage());
            //throw e;
        }catch(SQLException e){
            //TODO
            response.setResponseHeader(new ResponseHeader(response_status.ERR,e.getMessage()));
            System.out.println(e.getMessage()+", "+e.getSQLState());
        }
        return response;
    }

    @CrossOrigin
    @PatchMapping(value = "/books/{id}")
    public ResponseBooks PATCH_book(@PathVariable("id") String id, @RequestBody PatchBookClass inputs){
        ResponseBooks response = new ResponseBooks();
        try {
            System.out.println(inputs.getStatus()+", by "+inputs.getBorrower());
            response = dbl.updateBook(assureInteger(id),assurePatchBookClass(inputs));
        }catch(SQLException | InputFormatExeption e){
            //TODO
            response.setResponseHeader(new ResponseHeader(response_status.ERR,e.getMessage()));
            System.out.println(e);
        }
        return response;
    }

    @CrossOrigin
    @DeleteMapping(value = "/books/{id}")
    public ResponseBooks DELETE_book(@PathVariable("id") String id){
        ResponseBooks response = new ResponseBooks();
        try {
            response = dbl.removeBook(assureInteger(id));
        }catch(SQLException | InputFormatExeption e){
            //TODO
            System.out.println(e);
        }
        return response;
    }

    //TODO
    //Proper Error handling.

    /*
    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public String handleException(){
        return "Test";
    }*/

}

