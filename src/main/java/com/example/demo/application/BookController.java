package com.example.demo.application;

import static com.example.demo.DemoApplication.logger;
import static com.example.demo.application.InputValidator.assureBookClass;
import static com.example.demo.application.InputValidator.assureInteger;
import static com.example.demo.application.InputValidator.assurePatchBookClass;
import static com.example.demo.application.InputValidator.assurePositive;

import com.example.demo.backend.BookBusinessLogic;
import com.example.demo.backend.custom.Dto.Book;
import com.example.demo.backend.custom.Dto.PatchBook;
import com.example.demo.backend.custom.Dto.ResponseBooks;
import com.example.demo.backend.custom.exceptions.BookException;
import com.example.demo.backend.custom.exceptions.DaoException;
import com.example.demo.backend.custom.exceptions.InputFormatException;
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
public class BookController {

  @Autowired
  BookBusinessLogic dbl;

  /**
   * Used for acquiring a single book data from the database, specified with an ID.
   * @param bookId Unique identifier for the book to be searched, not {@code null}.
   * @return A list of searched {@link com.example.demo.backend.custom.Dto.Book Book} objects,
   * with a ResponseHeader class, not {@code null}.
   * @throws DaoException if query execution fails.
   * @throws InputFormatException if user input is wrong.
   * @throws BookException if logic fails.
   */
  @CrossOrigin
  @GetMapping(value = "/books/{id}")
  public ResponseBooks getBook(@PathVariable("id") String bookId) throws InputFormatException, DaoException, BookException {
    ResponseBooks response;
    try {
      response = dbl.getBook(assurePositive(assureInteger(bookId)));
    } catch (DaoException | InputFormatException | BookException e) {
      logger.error("Error in getBook() in BookController.java: ",e);
      throw e;
    }
    return response;
  }


  /**
   * Used for acquiring the entire book data from the database.
   * @return A list of searched {@link com.example.demo.backend.custom.Dto.Book Book} objects,
   * with a ResponseHeader class, not {@code null}.
   * @throws DaoException if query execution fails.
   * @throws InputFormatException if user input is wrong.
   * @throws BookException if logic fails.
   */
  @CrossOrigin
  @GetMapping(value = "/books")
  public ResponseBooks getBooks() throws DaoException, BookException {
    try {
      return dbl.getAllBooks();
    } catch (DaoException | BookException e) {
      logger.error("Error in getBook() in BookController.java: ",e);
      throw e;
    }
  }

  /**
  * Used for inserting a new book data to the database.
  * @param book The {@link com.example.demo.backend.custom.Dto.Book Book} object to be inserted, not {@code null}.
  * @return An empty list of {@link com.example.demo.backend.custom.Dto.Book Book} objects,
  * with a ResponseHeader class, not {@code null}.
  * @throws DaoException if query execution fails.
  * @throws InputFormatException if user input is wrong.
  * @throws BookException if logic fails.
  */
  @CrossOrigin
  @PostMapping(value = "/books")
  public ResponseBooks postBook(@RequestBody Book book) throws DaoException, InputFormatException, BookException {
    ResponseBooks response = new ResponseBooks();
    try {
      response = dbl.addBook(assureBookClass(book));
    } catch (InputFormatException | DaoException | BookException e) {
      logger.error("Error in postBook() in BookController.java: ",e);
      throw e;
    }
    return response;
  }

  /**
   * Used for replacing a book data to a new one, specified by an ID.
   * @param bookId Unique identifier for the book stored in the database, not {@code null}.
   * @param newBookData A new {@link com.example.demo.backend.custom.Dto.Book Book} object.
   * @return An empty list of {@link com.example.demo.backend.custom.Dto.Book Book} objects,
   * with a ResponseHeader class, not {@code null}.
   * @throws DaoException if query execution fails.
   * @throws InputFormatException if user input is wrong.
   * @throws BookException if logic fails.
   */
  @CrossOrigin
  @PutMapping(value = "/books/{id}")
  public ResponseBooks putBook(@PathVariable("id") String bookId, @RequestBody Book newBookData) throws InputFormatException, DaoException, BookException {
    ResponseBooks response;
    try {
      response = dbl.replaceBook(assurePositive(assureInteger(bookId)),assureBookClass(newBookData));
    } catch (InputFormatException | DaoException | BookException e) {
      logger.error("Error in putBook() in BookController.java: ",e);
      throw e;
    }
    return response;
  }

  /**
   * Used for updating the status of the book.
   * @param bookId Unique identifier for the book stored in the database, not {@code null}.
   * @param patchData Update data for the book, not {@code null}.
   * @return An empty list of {@link com.example.demo.backend.custom.Dto.Book Book} objects,
   * with a ResponseHeader class, not {@code null}.
   * @throws DaoException if query execution fails.
   * @throws InputFormatException if user input is wrong.
   * @throws BookException if logic fails.
   */
  @CrossOrigin
  @PatchMapping(value = "/books/{id}")
  public ResponseBooks patchBook(@PathVariable("id") String bookId, @RequestBody PatchBook patchData) throws InputFormatException, BookException, DaoException {
    ResponseBooks response;
    try {
      response = dbl.updateBook(assurePositive(assureInteger(bookId)),assurePatchBookClass(patchData));
    } catch (InputFormatException | DaoException | BookException e) {
      logger.error("Error in patchBook() in BookController.java: ",e);
      throw e;
    }
    return response;
  }

  /**
   * Used for deleting a book data from the database.
   * @param bookId Unique identifier for the book stored in the database, not {@code null}.
   * @return An empty list of {@link com.example.demo.backend.custom.Dto.Book Book} objects,
   * with a ResponseHeader class, not {@code null}.
   * @throws DaoException if query execution fails.
   * @throws InputFormatException if user input is wrong.
   * @throws BookException if logic fails.
   */
  @CrossOrigin
  @DeleteMapping(value = "/books/{id}")
  public ResponseBooks deleteBook(@PathVariable("id") String bookId) throws DaoException, InputFormatException, BookException {
    ResponseBooks response;
    try {
      response = dbl.removeBook(assurePositive(assureInteger(bookId)));
    } catch (DaoException | InputFormatException | BookException e) {
      logger.error("Error in deleteBook() in BookController.java: ",e);
      throw e;
    }
    return response;
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





}

