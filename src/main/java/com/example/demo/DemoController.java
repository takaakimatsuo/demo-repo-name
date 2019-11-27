package com.example.demo;

import static com.example.demo.InputValidator.assureBookClass;
import static com.example.demo.InputValidator.assureInteger;
import static com.example.demo.InputValidator.assurePatchBookClass;

import com.example.demo.backend.DemoBusinessLogic;
import com.example.demo.backend.custom.myenums.ResponseStatus;
import com.example.demo.backend.custom.myexceptions.DaoException;
import com.example.demo.backend.custom.myexceptions.DbException;
import com.example.demo.backend.custom.myexceptions.DuplicateBookException;
import com.example.demo.backend.custom.myexceptions.InputFormatExeption;
import com.example.demo.backend.custom.objects.BookClass;
import com.example.demo.backend.custom.objects.PatchBookClass;
import com.example.demo.backend.custom.objects.ResponseBooks;
import com.example.demo.backend.custom.objects.ResponseHeader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class DemoController {

  @Autowired
  DemoBusinessLogic dbl;

  /**
   * Receiver for the Get request, with a single integer path-variable.
   * This is used for acquiring a single book data from the database.
   * @param bookId Unique identifier for the book stored in the database.
   * @return A list of BookClass objects, with a ResponseHeader class. The list will be kept empty if no results were found.
   */
  @CrossOrigin
  @GetMapping(value = "/books/{id}")
  public ResponseBooks getBook(@PathVariable("id") String bookId) {
    ResponseBooks response = new ResponseBooks();
    try {
      response = dbl.getBook(assureInteger(bookId));
    } catch (DaoException | InputFormatExeption | DbException e) {
      //TODO
      System.out.println("Controller: " + e);
    }
    return response;
  }


  /**
   * Receiver for the Get request with no path-variable.
   * This is used for acquiring the whole book data from the database.
   * @return A list of BookClass objects, with a ResponseHeader class. The list will be kept empty if no results were found.
   */
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

  /**
  * Receiver for the Post request with no path-variable.
  * This is used for inserting a new book data to the database.
  * @param book A BookClass object to be inserted.
  * @return A list of BookClass objects, with a ResponseHeader class.
  * The list will hold the data of inserted book.
  * Otherwise it is empty.
  */
  @CrossOrigin
  @PostMapping(value = "/books")
  public ResponseBooks postBook(@RequestBody BookClass book) {
    ResponseBooks response = new ResponseBooks();
    try {
      response = dbl.addBook(assureBookClass(book));
    } catch (InputFormatExeption | DaoException | DbException e) {
      //TODO
      response.setResponseHeader(new ResponseHeader(ResponseStatus.ERR,e.getMessage()));
      System.out.println(e.getMessage());
    }
    return response;
  }

  /**
   * Receiver for the Put request with a single integer path-variable.
   * This is used for replacing the book data to a new one, followed by an identifier.
   * @param bookId Unique identifier for the book stored in the database.
   * @param newBookData A BookClass that is ought to be replaced with another data inside the database.
   * @return A list of BookClass objects, with a ResponseHeader class. The list will hold the data of replaced book.
   * Otherwise it is empty.
   */
  @CrossOrigin
  @PutMapping(value = "/books/{id}")
  public ResponseBooks putBook(@PathVariable("id") String bookId, @RequestBody BookClass newBookData) {
    System.out.println("IM HERE!");
    ResponseBooks response = new ResponseBooks();
    try {
      response = dbl.replaceBook(assureInteger(bookId),assureBookClass(newBookData));
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

  /**
   * Receiver for the Patch request with a single integer path-variable.
   * This is used for replacing the book data to a new one, specified with an id.
   * The book title must also be unique.
   * @param bookId Unique identifier for the book stored in the database.
   * @param patchData Update data for the book.
   * @return An empty list of BookClass objects, with a ResponseHeader class.
   */
  @CrossOrigin
  @PatchMapping(value = "/books/{id}")
  public ResponseBooks patchBook(@PathVariable("id") String bookId, @RequestBody PatchBookClass patchData) {
    ResponseBooks response = new ResponseBooks();
    try {
      response = dbl.updateBook(assureInteger(bookId),assurePatchBookClass(patchData));
    } catch (InputFormatExeption | DaoException | DbException e) {
      //TODO
      e.printStackTrace();
      response.setResponseHeader(new ResponseHeader(ResponseStatus.ERR,e.getMessage()));
    }
    return response;
  }

  /**
   * Receiver for the Delete request with a single integer path-variable.
   * This is used for deleting a book data from the database.
   * @param bookId Unique identifier for the book stored in the database.
   * @return An empty list of BookClass objects, with a ResponseHeader class.
   */
  @CrossOrigin
  @DeleteMapping(value = "/books/{id}")
  public ResponseBooks deleteBook(@PathVariable("id") String bookId) {
    ResponseBooks response = new ResponseBooks();
    try {
      response = dbl.removeBook(assureInteger(bookId));
    } catch (DaoException | InputFormatExeption | DbException e) {
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

