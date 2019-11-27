package com.example.demo.Backend;

import static com.example.demo.Backend.staticErrorMessages.StaticMessages.*;

import com.example.demo.Backend.CustomENUMs.ResponseStatus;
import com.example.demo.Backend.CustomExceptions.BookException;
import com.example.demo.Backend.CustomExceptions.DaoException;
import com.example.demo.Backend.CustomExceptions.DbException;
import com.example.demo.Backend.CustomExceptions.DuplicateBookException;
import com.example.demo.Backend.CustomObjects.*;
import com.example.demo.DataAccess.BookDao;
import com.example.demo.DataAccess.CustomENUMs.BookStatus;
import java.sql.SQLException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;



@Component
public final class DemoBusinessLogic {
  private ResponseBooks res;
  @Autowired
  @Qualifier("JdbcBookDao") //Based on standard JDBC.
  //@Qualifier("SpringBookDao") // Based on Spring JDBCtemplate.
  private BookDao dao;

  /* Simply return all books stored in the bookshelf table.
  *  @param: None
  *  @return: ResponseBooks res - A list of BookClass objects, with the ResponseHeader class.
  */
  public ResponseBooks getAllBooks() throws DaoException, DbException {
    res = new ResponseBooks();
    try {
      List<BookClass> books = dao.getAllBooks();
      res.getResponseHeader().setMessage(BOOK_FOUND);
      res.getResponseHeader().setStatus(ResponseStatus.OK);
      res.setBooks(books);
    } catch (DaoException | DbException e) {
      //TODO
      //res.getResponseHeader().setMessage(e.toString());
      //res.getResponseHeader().setStatus(ResponseStatus.ERR);
      //System.out.println("[ERROR] SQL failure" + e.getMessage());
      //throw new DaoException("SQL query failure: ", e);
      throw e;
    }
    return res;
  }

  /* Remove specified book from the bookshelf table.
   *  @param: Integer bookId - Identifier of a book.
   *  @return: ResponseBooks res - An empty list of BookClass objects, with the ResponseHeader class.
   */
  public ResponseBooks removeBook(Integer bookId) throws DaoException, DbException {
    res = new ResponseBooks();
    try {
      int update = dao.deleteBook(bookId);
      if (update == 0) {
        res.getResponseHeader().setMessage(BOOK_NOT_EXISTING);
        res.getResponseHeader().setStatus(ResponseStatus.ERR);
      } else {
        res.getResponseHeader().setMessage(BOOK_DELETED);
        res.getResponseHeader().setStatus(ResponseStatus.OK);
      }
    } catch (DaoException | DbException e) {
      //TODO
      res.getResponseHeader().setMessage(e.toString());
      res.getResponseHeader().setStatus(ResponseStatus.ERR);
      //throw new SQLException("SQL query failure: ", e);
    }
    return res;
  }


  /* Get a specified book stored in the bookshelf table.
   *  @param: Integer bookId - Identifier of a book.
   *  @return: ResponseBooks res - An list containing a single BookClass object, with a ResponseHeader class.
   */
  public ResponseBooks getBook(Integer bookId) throws DaoException, DbException {
    res = new ResponseBooks();
    try {
      List<BookClass> books = dao.getBook(bookId);
      if (books.size() == 0) {
        res.getResponseHeader().setMessage(BOOK_NOT_EXISTING);
        res.getResponseHeader().setStatus(ResponseStatus.ERR);
      } else {
        res.getResponseHeader().setMessage(BOOK_FOUND);
        res.getResponseHeader().setStatus(ResponseStatus.OK);
      }
      res.setBooks(books);
    } catch (DaoException | DbException e) {
      //TODO
      res.getResponseHeader().setMessage(e.toString());
      res.getResponseHeader().setStatus(ResponseStatus.ERR);
      //throw new SQLException("SQL query failure: ", e);
    }
    return res;
  }


  /* Checks contradictions of what the user wants to do, and what the system recognize. e.g. User trying to return a book that has never been borrowed.
   *  @param: BookStatus currentStatus - enum representing the current status
   *          int requestedStatus - 0:borrow, 1:return, 2:report lost
   *  @return: ResponseBooks res - An list containing a single BookClass object, with a ResponseHeader class.
   */
  private int assureUserBookRelation(BookStatus currentStatus, int requestedStatus) throws BookException {
    System.out.println("Current Status = " + currentStatus.toString());
    if (currentStatus == BookStatus.UNKNOWN) {
      System.out.println("[Error] Unexpected output. This should not happen.");
      throw new BookException(UNEXPECTED);
    } else if (currentStatus == BookStatus.BOOK_NOT_EXISTING) {
      System.out.println("[Error] Book does not exist.");
      throw new BookException(BOOK_NOT_EXISTING);
    } else if (currentStatus == BookStatus.BOOK_BORROWED_BY_THIS_USER && requestedStatus == 0) {
      System.out.println("[Error] Book already borrowed by the same user.");
      throw new BookException(BOOK_CANNOT_BE_DOUBLE_BORROWED);
    } else if (currentStatus == BookStatus.BOOK_NOT_BORROWED_BY_THIS_USER && requestedStatus == 1) {
      System.out.println("[Error] Trying to return a book that has not been borrowed by the user.");
      throw new BookException(BOOK_CANNOT_BE_RETURNED);
    } else if (currentStatus == BookStatus.BOOK_NOT_BORROWED_BY_THIS_USER && requestedStatus == 2) {
      System.out.println("[Error] Trying to report a book as lost, which has not been borrowed by the user.");
      throw new BookException(BOOK_CANNOT_BE_LOST);
    }
    return requestedStatus;
  }




  public ResponseBooks updateBook(Integer bookId, PatchBookClass updStatus) throws DaoException, DbException {
    res = new ResponseBooks();
    System.out.println(bookId + ", " + updStatus.getBorrower() + ", " + updStatus.getStatus());
    int action = updStatus.getStatus();//0 = Borrow, 1 = Return, 2 = Lost.
    try {
      BookStatus currentStatus = dao.checkBookStatus(bookId, updStatus.getBorrower());
      //Check inconsistency. e.g. User trying to return a book that has not been borrowed.
      switch (assureUserBookRelation(currentStatus, action)) {
        case 0: {
          //Borrow!
          dao.updateBook_borrowed(bookId, updStatus.getBorrower());
          res.getResponseHeader().setMessage(BOOK_BORROWED);
          res.getResponseHeader().setStatus(ResponseStatus.OK);
          break;
        }
        case 1: {
          dao.updateBook_returned(bookId, updStatus.getBorrower());
          res.getResponseHeader().setMessage(BOOK_RETURNED);
          res.getResponseHeader().setStatus(ResponseStatus.OK);
          break;
        }
        case 2: {
          dao.updateBook_lost(bookId,updStatus.getBorrower());
          res.getResponseHeader().setMessage(BOOK_LOST);
          res.getResponseHeader().setStatus(ResponseStatus.OK);
          break;
        } default: {
          res.getResponseHeader().setMessage(INVALID_STATUS);
          res.getResponseHeader().setStatus(ResponseStatus.ERR);
          break;
        }
      }
    } catch (BookException e) {
      //TODO
      res.getResponseHeader().setStatus(ResponseStatus.ERR);
      res.getResponseHeader().setMessage(e.getMessage());
    }
    return res;
  }



  public ResponseBooks addBook(BookClass book) throws DaoException, DbException {
    res = new ResponseBooks();
    try {
      List<BookClass> books = dao.insertBook(book);
      res.setBooks(books);
      res.getResponseHeader().setMessage(BOOK_INSERTED);
      res.getResponseHeader().setStatus(ResponseStatus.OK);
      return res;
    } catch (DaoException | DbException e) {
      //TODO
      res.getResponseHeader().setMessage(BOOK_DUPLICATE);
      res.getResponseHeader().setStatus(ResponseStatus.ERR);
      return res;
    }
  }

  public ResponseBooks replaceBook(Integer bookId, BookClass book) throws DuplicateBookException {
    res = new ResponseBooks();
    try {
      int updated = dao.updateBook_data(bookId, book);
      if (updated == 0) {
        res.getResponseHeader().setMessage(UPDATE_FAILED_NO_MATCH_BOOK);
        res.getResponseHeader().setStatus(ResponseStatus.ERR);
      } else {
        res.getResponseHeader().setMessage(UPDATE_SUCCESS_BOOK);
        res.getResponseHeader().setStatus(ResponseStatus.OK);
      }
    } catch (DaoException | DbException e) {
      //TODO
      System.out.println(e.getMessage());
      res.getResponseHeader().setMessage(BOOK_DUPLICATE);
      res.getResponseHeader().setStatus(ResponseStatus.ERR);
      return res;
    }
    return res;
  }


}


