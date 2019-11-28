package com.example.demo.backend;

import static com.example.demo.backend.errorcodes.SqlErrorCodes.SQL_CODE_DUPLICATE_KEY_ERROR;
import static com.example.demo.backend.errormessages.StaticMessages.BOOK_BORROWED;
import static com.example.demo.backend.errormessages.StaticMessages.BOOK_CANNOT_BE_DOUBLE_BORROWED;
import static com.example.demo.backend.errormessages.StaticMessages.BOOK_CANNOT_BE_LOST;
import static com.example.demo.backend.errormessages.StaticMessages.BOOK_CANNOT_BE_RETURNED;
import static com.example.demo.backend.errormessages.StaticMessages.BOOK_DELETED;
import static com.example.demo.backend.errormessages.StaticMessages.BOOK_DUPLICATE;
import static com.example.demo.backend.errormessages.StaticMessages.BOOK_FOUND;
import static com.example.demo.backend.errormessages.StaticMessages.BOOK_INSERTED;
import static com.example.demo.backend.errormessages.StaticMessages.BOOK_LOST;
import static com.example.demo.backend.errormessages.StaticMessages.BOOK_NOT_EXISTING;
import static com.example.demo.backend.errormessages.StaticMessages.BOOK_NOT_FOUND;
import static com.example.demo.backend.errormessages.StaticMessages.BOOK_NO_STOCK;
import static com.example.demo.backend.errormessages.StaticMessages.BOOK_RETURNED;
import static com.example.demo.backend.errormessages.StaticMessages.INVALID_STATUS;
import static com.example.demo.backend.errormessages.StaticMessages.UNEXPECTED;
import static com.example.demo.backend.errormessages.StaticMessages.UPDATE_FAILED_NO_MATCH_BOOK;
import static com.example.demo.backend.errormessages.StaticMessages.UPDATE_SUCCESS_BOOK;

import com.example.demo.backend.custom.enums.ServiceStatus;
import com.example.demo.backend.custom.exceptions.BookException;
import com.example.demo.backend.custom.exceptions.DaoException;
import com.example.demo.backend.custom.exceptions.DbException;
import com.example.demo.backend.custom.exceptions.DuplicateBookException;
import com.example.demo.backend.custom.Dto.*;
import com.example.demo.data.access.BookDao;
import com.example.demo.data.access.JdbcBookUserDao;
import com.example.demo.data.access.custom.enums.BookStatus;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;



@Component
public final class DemoBusinessLogic {
  private ResponseBooks res;
  private ResponseUsers ures;
  @Autowired
  @Qualifier("JdbcBookDao") //Based on standard JDBC.
  //@Qualifier("SpringBookDao") // Based on Spring JDBCtemplate.
  private BookDao dao;


  @Autowired
  private JdbcBookUserDao udao;

  /**
   * Logic for searching all books stored in the bookshelf table.
   * @return Returns a list of BookClass objects, with the ResponseHeader class.
   * @throws DaoException An exception that raises when executing an SQL query fails.
   * @throws DbException An exception that raises when the database connection/disconnection fails.
  */
  public ResponseBooks getAllBooks() throws DaoException, DbException {
    res = new ResponseBooks();
    try {
      List<BookClass> books = dao.getAllBooks();
      if (books.isEmpty()) {
        res.getResponseHeader().setMessage(BOOK_NOT_FOUND);
        res.getResponseHeader().setStatus(ServiceStatus.OK);
      } else {
        res.getResponseHeader().setMessage(BOOK_FOUND);
        res.getResponseHeader().setStatus(ServiceStatus.OK);
      }
      res.setBooks(books);
      return res;
    } catch (DbException | DaoException e) {
      throw e;
    }
  }

  /**
   * Logic for removing a specified book from the bookshelf table.
   *  This method does not check whether the book exists in the database beforehand,
   *  but executes the query anyway and checks how many rows in the database has been updated afterwards.
   *  @param bookId Identifier of a book, not {@code null}
   *  @return An empty list of BookClass objects, with the ResponseHeader class.
   *  @throws DaoException An exception that raises when executing an SQL query fails.
   *  @throws DbException An exception that raises when the database connection/disconnection fails.
   */
  public ResponseBooks removeBook(Integer bookId) throws DaoException, DbException {
    res = new ResponseBooks();
    try {
      int update = dao.deleteBook(bookId);
      if (update == 0) {
        res.getResponseHeader().setMessage(BOOK_NOT_EXISTING);
        res.getResponseHeader().setStatus(ServiceStatus.ERR);
      } else {
        res.getResponseHeader().setMessage(BOOK_DELETED);
        res.getResponseHeader().setStatus(ServiceStatus.OK);
      }
    } catch (DaoException | DbException e) {
      //TODO
      res.getResponseHeader().setMessage(e.toString());
      res.getResponseHeader().setStatus(ServiceStatus.ERR);
      //throw new SQLException("SQL query failure: ", e);
    }
    return res;
  }

  /**
   * Logic for searching a specific book stored in the bookshelf table using its id.
   *  @param bookId Identifier of a book.
   *  @return Returns a list containing a single BookClass object, with a ResponseHeader class.
   *  The list will be kept empty if no matching data has been found.
   *  @throws DaoException An exception that raises when executing an SQL query fails.
   *  @throws DbException An exception that raises when the database connection/disconnection fails.
   */
  public ResponseBooks getBook(Integer bookId) throws DaoException, DbException {
    res = new ResponseBooks();
    try {
      List<BookClass> books = dao.getBook(bookId);
      if (books.size() == 0) {
        res.getResponseHeader().setMessage(BOOK_NOT_EXISTING);
        res.getResponseHeader().setStatus(ServiceStatus.ERR);
      } else {
        res.getResponseHeader().setMessage(BOOK_FOUND);
        res.getResponseHeader().setStatus(ServiceStatus.OK);
      }
      res.setBooks(books);
    } catch (DaoException | DbException e) {
      throw e;
    }
    return res;
  }

  /** Validates the relation between what the user wants to do and what the system knows.
   *  An example that will throw an exception would be when a user tries to return a book that has never been borrowed.
   *  @param currentStatus enum representing the current book status. It is either not existing, already borrowed by the same user, or available.
   *  @param requestedStatus Users requested status {0:borrow, 1:return, 2:report lost
   *  @return Returns the inputted requestedStatus directly.
   *  @throws BookException An exception that gets raised when there is a contradiction between the user's action and the state recognized by the system.
   */
  private int validateUserBookRelation(BookStatus currentStatus, int requestedStatus) throws BookException {
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


  /**
   *
   * @param bookId Identifier of a book.
   * @param updStatus Describes the user, and the user's action
   * @return An empty list of BookClass objects, with a ResponseHeader class.
   * @throws DaoException An exception that gets raised when executing an SQL query fails.
   * @throws DbException An exception that gets raised when the database connection/disconnection fails.
   */
  public ResponseBooks updateBook(Integer bookId, PatchBookClass updStatus) throws DaoException, DbException {
    res = new ResponseBooks();
    System.out.println(bookId + ", " + updStatus.getBorrower() + ", " + updStatus.getStatus());
    int action = updStatus.getStatus();//0 = Borrow, 1 = Return, 2 = Lost.
    try {
      BookStatus currentStatus = dao.checkBookStatus(bookId, updStatus.getBorrower());
      //Check inconsistency. e.g. User trying to return a book that has not been borrowed.
      switch (validateUserBookRelation(currentStatus, action)) {
        case 0: {
          //Borrow!
          if (dao.checkBookStockAvailability(bookId)) {
            dao.updateBook_borrowed(bookId, updStatus.getBorrower());
          } else {
          throw new BookException(BOOK_NO_STOCK);
        }
          res.getResponseHeader().setMessage(BOOK_BORROWED);
          res.getResponseHeader().setStatus(ServiceStatus.OK);
          break;
        }
        case 1: {
          dao.updateBook_returned(bookId, updStatus.getBorrower());
          res.getResponseHeader().setMessage(BOOK_RETURNED);
          res.getResponseHeader().setStatus(ServiceStatus.OK);
          break;
        }
        case 2: {
          dao.updateBook_lost(bookId,updStatus.getBorrower());
          List<BookClass> book = dao.getBook(bookId);
          if (book.get(0).getQuantity() <= 0) {
            dao.deleteBook(bookId);//Simply remove the book from the bookshelf
          }
          res.getResponseHeader().setMessage(BOOK_LOST);
          res.getResponseHeader().setStatus(ServiceStatus.OK);
          break;
        } default: {
          res.getResponseHeader().setMessage(INVALID_STATUS);
          res.getResponseHeader().setStatus(ServiceStatus.ERR);
          break;
        }
      }
    } catch (BookException | DbException | DaoException e) {
      //TODO
      res.getResponseHeader().setStatus(ServiceStatus.ERR);
      res.getResponseHeader().setMessage(e.getMessage());
    }
    return res;
  }


  /**
   * Logic for adding a new book data to the database.
   * @param book {@link com.example.demo.backend.custom.Dto.BookClass BookClass} to be added.
   * @return Returns a list containing a single BookClass object that has been inserted, with a ResponseHeader class.
   * @throws DaoException An exception that gets raised when executing an SQL query fails.
   * @throws DbException An exception that gets raised when the database connection/disconnection fails.
   */
  public ResponseBooks addBook(BookClass book) throws DaoException, DbException {
    res = new ResponseBooks();
    try {
      List<BookClass> books = dao.insertBook(book);
      res.setBooks(books);
      res.getResponseHeader().setMessage(BOOK_INSERTED);
      res.getResponseHeader().setStatus(ServiceStatus.OK);
      return res;
    } catch (DaoException | DbException e) {
      //TODO
      if (e instanceof  DaoException) {
        if (((DaoException) e).getSqlCode().equals(SQL_CODE_DUPLICATE_KEY_ERROR)) {
          res.getResponseHeader().setMessage(BOOK_DUPLICATE);
          res.getResponseHeader().setStatus(ServiceStatus.ERR);
          return res;
        }
      }
      throw e;
    }
  }

  public ResponseBooks replaceBook(Integer bookId, BookClass book) throws DuplicateBookException {
    res = new ResponseBooks();
    try {
      int updated = dao.updateBook_data(bookId, book);
      if (updated == 0) {
        res.getResponseHeader().setMessage(UPDATE_FAILED_NO_MATCH_BOOK);
        res.getResponseHeader().setStatus(ServiceStatus.ERR);
      } else {
        res.getResponseHeader().setMessage(UPDATE_SUCCESS_BOOK);
        res.getResponseHeader().setStatus(ServiceStatus.OK);
      }
    } catch (DaoException | DbException e) {
      //TODO
      System.out.println(e.getMessage());
      res.getResponseHeader().setMessage(BOOK_DUPLICATE);
      res.getResponseHeader().setStatus(ServiceStatus.ERR);
      return res;
    }
    return res;
  }








  public ResponseUsers addUser(BookUser user) throws DaoException, DbException {
    ures = new ResponseUsers();
    try {
      List<BookUser> users = udao.insertBookUser(user);
      ures.setUsers(users);
      ures.getResponseHeader().setMessage(BOOK_INSERTED);
      ures.getResponseHeader().setStatus(ServiceStatus.OK);
      return ures;
    } catch (DaoException | DbException e) {
      //TODO
      if (e instanceof  DaoException) {
        if (((DaoException) e).getSqlCode().equals(SQL_CODE_DUPLICATE_KEY_ERROR)) {
          ures.getResponseHeader().setMessage(BOOK_DUPLICATE);
          ures.getResponseHeader().setStatus(ServiceStatus.ERR);
          return ures;
        }
      }
      throw e;
    }
  }






}


