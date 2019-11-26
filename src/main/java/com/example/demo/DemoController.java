package com.example.demo;


import static com.example.demo.InputValidator.*;

import com.example.demo.Backend.*;
import com.example.demo.Backend.CustomENUMs.ResponseStatus;
import com.example.demo.Backend.CustomExceptions.DaoException;
import com.example.demo.Backend.CustomExceptions.DbException;
import com.example.demo.Backend.CustomExceptions.DuplicateBookException;
import com.example.demo.Backend.CustomExceptions.InputFormatExeption;
import com.example.demo.Backend.CustomObjects.*;
import java.sql.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController
public class DemoController {

  @Autowired
  DemoBusinessLogic dbl;

  @CrossOrigin
  @GetMapping(value = "/books/{id}")
  public ResponseBooks getBook(@PathVariable("id") String bookId) {
    ResponseBooks response = new ResponseBooks();
    try {
      response = dbl.getBook(assureInteger(bookId));
    } catch (SQLException | InputFormatExeption e) {
      //TODO
      System.out.println("Controller: " + e);
    }
    return response;
  }


  @CrossOrigin
  @GetMapping(value = "/books")
  public ResponseBooks getBooks() {

    ResponseBooks response = new ResponseBooks();
    try {
      return dbl.getAllBooks();
    } catch (DbException | DaoException e) {
      //TODO
      System.out.println(e);
      response.getResponseHeader().setMessage(e.getMessage());
      return response;
    }
  }

  @CrossOrigin
  @PostMapping(value = "/books")
  public ResponseBooks postBook(@RequestBody BookClass inputs) {
    ResponseBooks response = new ResponseBooks();
    try {
      response = dbl.addBook(assureBookClass(inputs));
    } catch (InputFormatExeption | DaoException | DbException e) {
      //TODO
      response.setResponseHeader(new ResponseHeader(ResponseStatus.ERR,e.getMessage()));
      System.out.println(e.getMessage());
    }
    return response;
  }

  @CrossOrigin
  @PutMapping(value = "/books/{id}")
  public ResponseBooks putBook(@PathVariable("id") String id, @RequestBody BookClass inputs) {
    System.out.println("IM HERE!");
    ResponseBooks response = new ResponseBooks();
    try {
      response = dbl.replaceBook(assureInteger(id),assureBookClass(inputs));
    } catch (InputFormatExeption e) {
      //TODO
      response.setResponseHeader(new ResponseHeader(ResponseStatus.ERR,e.getMessage()));
      System.out.println(e.getMessage());
      //throw e;
    } catch (DuplicateBookException e) {
      e.printStackTrace();
      response.setResponseHeader(new ResponseHeader(ResponseStatus.ERR,e.getMessage()));
    }
    return response;
  }

  @CrossOrigin
  @PatchMapping(value = "/books/{id}")
  public ResponseBooks patchBook(@PathVariable("id") String id, @RequestBody PatchBookClass inputs) {
    ResponseBooks response = new ResponseBooks();
    try {
      response = dbl.updateBook(assureInteger(id),assurePatchBookClass(inputs));
    } catch (InputFormatExeption | DaoException | DbException e) {
      //TODO
      e.printStackTrace();
      response.setResponseHeader(new ResponseHeader(ResponseStatus.ERR,e.getMessage()));
    }
    return response;
  }

  @CrossOrigin
  @DeleteMapping(value = "/books/{id}")
  public ResponseBooks deleteBook(@PathVariable("id") String id) {
    ResponseBooks response = new ResponseBooks();
    try {
      response = dbl.removeBook(assureInteger(id));
    } catch (SQLException | InputFormatExeption e) {
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

