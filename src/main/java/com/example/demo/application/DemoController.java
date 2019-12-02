package com.example.demo.application;

import static com.example.demo.application.InputValidator.assureBookClass;
import static com.example.demo.application.InputValidator.assureBookUser;
import static com.example.demo.application.InputValidator.assureInteger;
import static com.example.demo.application.InputValidator.assurePatchBookClass;
import static com.example.demo.application.InputValidator.assurePositive;

import com.example.demo.backend.DemoBusinessLogic;
import com.example.demo.backend.custom.Dto.BookClass;
import com.example.demo.backend.custom.Dto.BookUser;
import com.example.demo.backend.custom.Dto.PatchBookClass;
import com.example.demo.backend.custom.Dto.ResponseBooks;
import com.example.demo.backend.custom.Dto.ResponseHeader;
import com.example.demo.backend.custom.Dto.ResponseUsers;
import com.example.demo.backend.custom.exceptions.BookException;
import com.example.demo.backend.custom.exceptions.DaoException;
import com.example.demo.backend.custom.exceptions.DbException;
import com.example.demo.backend.custom.exceptions.InputFormatException;
import com.example.demo.backend.custom.exceptions.UserException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class DemoController {

  @Autowired
  DemoBusinessLogic dbl;

  /**
   * Takes care of Get requests with a single integer path-variable.
   * This is used for acquiring a single book data from the database.
   * @param bookId Unique identifier for the book stored in the database.
   * @return A list of BookClass objects, with a ResponseHeader class. The list will be kept empty if no results were found.
   */
  @CrossOrigin
  @GetMapping(value = "/books/{id}")
  public ResponseBooks getBook(@PathVariable("id") String bookId) throws InputFormatException, DbException, DaoException, BookException {
    ResponseBooks response;
    try {
      response = dbl.getBook(assurePositive(assureInteger(bookId)));
    } catch (DaoException | InputFormatException | DbException | BookException e) {
      e.printStackTrace();
      throw e;
    }
    return response;
  }


  /**
   * Takes care of Get requests with no path-variable.
   * This is used for acquiring the whole book data from the database.
   * @return A list of BookClass objects, with a ResponseHeader class. The list will be kept empty if no results were found.
   */
  @CrossOrigin
  @GetMapping(value = "/books")
  public ResponseBooks getBooks() throws DbException, DaoException {
    try {
      return dbl.getAllBooks();
    } catch (DaoException | DbException e) {
      e.printStackTrace();
      throw e;
    }
  }

  /**
  * Takes care of Post requests with no path-variable.
  * This is used for inserting a new book data to the database.
  * @param book A BookClass object to be inserted.
  * @return A list of BookClass objects, with a ResponseHeader class.
  * The list will hold the data of inserted book.
  * Otherwise it is empty.
  */
  @CrossOrigin
  @PostMapping(value = "/books")
  public ResponseBooks postBook(@RequestBody BookClass book) throws DaoException, InputFormatException, DbException, BookException {
    ResponseBooks response = new ResponseBooks();
    try {
      response = dbl.addBook(assureBookClass(book));
    } catch (InputFormatException | DaoException | DbException | BookException e) {
      e.printStackTrace();
      throw e;
    }
    return response;
  }

  /**
   * Takes care of Put requests with a single integer path-variable.
   * This is used for replacing the book data to a new one, followed by an identifier.
   * @param bookId Unique identifier for the book stored in the database.
   * @param newBookData A BookClass that is ought to be replaced with another data inside the database.
   * @return A list of BookClass objects, with a ResponseHeader class. The list will hold the data of replaced book.
   * Otherwise it is empty.
   */
  @CrossOrigin
  @PutMapping(value = "/books/{id}")
  public ResponseBooks putBook(@PathVariable("id") String bookId, @RequestBody BookClass newBookData) throws InputFormatException, DaoException, BookException, DbException {
    ResponseBooks response;
    try {
      response = dbl.replaceBook(assurePositive(assureInteger(bookId)),assureBookClass(newBookData));
    } catch (InputFormatException |  DbException | DaoException | BookException e) {
      e.printStackTrace();
      throw e;
    }
    return response;
  }

  /**
   * Takes care of Patch requests with a single integer path-variable.
   * This is used for replacing the book data to a new one, specified with an id.
   * The book title must also be unique.
   * @param bookId Unique identifier for the book stored in the database.
   * @param patchData Update data for the book.
   * @return An empty list of BookClass objects, with a ResponseHeader class.
   */
  @CrossOrigin
  @PatchMapping(value = "/books/{id}")
  public ResponseBooks patchBook(@PathVariable("id") String bookId, @RequestBody PatchBookClass patchData) throws InputFormatException, BookException, DaoException, DbException {
    ResponseBooks response;
    try {
      response = dbl.updateBook(assurePositive(assureInteger(bookId)),assurePatchBookClass(patchData));
    } catch (InputFormatException | DaoException | DbException | BookException e) {
      //TODO
      e.printStackTrace();
      throw e;
    }
    return response;
  }

  /**
   * Takes care of Delete requests with a single integer path-variable.
   * This is used for deleting a book data from the database.
   * @param bookId Unique identifier for the book stored in the database.
   * @return An empty list of BookClass objects, with a ResponseHeader class.
   */
  @CrossOrigin
  @DeleteMapping(value = "/books/{id}")
  public ResponseBooks deleteBook(@PathVariable("id") String bookId) throws DaoException, InputFormatException, DbException, BookException {
    ResponseBooks response;
    try {
      response = dbl.removeBook(assurePositive(assureInteger(bookId)));
    } catch (DaoException | InputFormatException | DbException | BookException e) {
      throw e;
    }
    return response;
  }




  @ExceptionHandler({DbException.class})
  @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
  @ResponseBody
  public String handleException(DbException e) {
    return e.getMessage();
  }

  @ExceptionHandler({DaoException.class})
  @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
  @ResponseBody
  public String handleException(DaoException e) {
    return e.getMessage();
  }



  @ExceptionHandler({InputFormatException.class})
  @ResponseStatus(value = HttpStatus.BAD_REQUEST)
  @ResponseBody
  public String handleException(InputFormatException e) {
    return e.getMessage();
  }


  @ExceptionHandler({BookException.class})
  @ResponseStatus(value = HttpStatus.BAD_REQUEST)
  @ResponseBody
  public String handleException(BookException e) {
    return e.getMessage();
  }







  /**
   * Receiver for the Post request with no path-variable.
   * This is used for inserting a new user data to the database.
   * @param user A UserClass object to be inserted.
   * @return A list of UserClass objects, with a ResponseHeader class.
   * The list will hold the data of inserted user.
   * Otherwise it is empty.
   */
  @CrossOrigin
  @PostMapping(value = "/users")
  public ResponseUsers postUser(@RequestBody BookUser user) throws DaoException, InputFormatException, DbException, UserException {
    ResponseUsers response = new ResponseUsers();
    try {
      response = dbl.addUser(assureBookUser(user));
    } catch (InputFormatException | DaoException | DbException | UserException e) {
      //TODO
      response.setResponseHeader(new ResponseHeader(e.getMessage()));
      System.out.println(e.getMessage());
      throw e;
    }
    return response;
  }



}

