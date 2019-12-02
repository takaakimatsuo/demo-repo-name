package com.example.demo.backend;


import static com.example.demo.backend.errorcodes.SqlErrorCodes.SQL_CODE_DUPLICATE_KEY_ERROR;
import static com.example.demo.backend.messages.StaticBookMessages.BOOK_BORROWED;
import static com.example.demo.backend.messages.StaticBookMessages.BOOK_CANNOT_BE_DOUBLE_BORROWED;
import static com.example.demo.backend.messages.StaticBookMessages.BOOK_CANNOT_BE_LOST;
import static com.example.demo.backend.messages.StaticBookMessages.BOOK_CANNOT_BE_RETURNED;
import static com.example.demo.backend.messages.StaticBookMessages.BOOK_DELETED;
import static com.example.demo.backend.messages.StaticBookMessages.BOOK_DUPLICATE;
import static com.example.demo.backend.messages.StaticBookMessages.BOOK_FOUND;
import static com.example.demo.backend.messages.StaticBookMessages.BOOK_INSERTED;
import static com.example.demo.backend.messages.StaticBookMessages.BOOK_LOST;
import static com.example.demo.backend.messages.StaticBookMessages.BOOK_LOST_AND_DELETED;
import static com.example.demo.backend.messages.StaticBookMessages.BOOK_NOT_EXISTING;
import static com.example.demo.backend.messages.StaticBookMessages.BOOK_NOT_FOUND;
import static com.example.demo.backend.messages.StaticBookMessages.BOOK_NO_STOCK;
import static com.example.demo.backend.messages.StaticBookMessages.BOOK_RETURNED;
import static com.example.demo.backend.messages.StaticBookMessages.UNEXPECTED;
import static com.example.demo.backend.messages.StaticBookMessages.UPDATE_FAILED_BOOK;
import static com.example.demo.backend.messages.StaticBookMessages.UPDATE_SUCCESS_BOOK;
import static com.example.demo.backend.messages.StaticUserMessages.USER_DUPLICATE;
import static com.example.demo.backend.messages.StaticUserMessages.USER_INSERTED;

import com.example.demo.backend.custom.Dto.BookClass;
import com.example.demo.backend.custom.Dto.BookUser;
import com.example.demo.backend.custom.Dto.PatchBookClass;
import com.example.demo.backend.custom.Dto.ResponseBooks;
import com.example.demo.backend.custom.Dto.ResponseUsers;
import com.example.demo.backend.custom.exceptions.BookException;
import com.example.demo.backend.custom.exceptions.DaoException;
import com.example.demo.backend.custom.exceptions.DbException;
import com.example.demo.backend.custom.exceptions.UserException;
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
   * @throws BookException An exception that gets raised mainly due to logic error.
  */
  public ResponseBooks getAllBooks() throws DaoException, DbException {
    res = new ResponseBooks();
    try {
      List<BookClass> books = dao.getAllBooks();
      if (books.isEmpty()) {
        res.getResponseHeader().setMessage(BOOK_NOT_FOUND);
      } else {
        res.getResponseHeader().setMessage(BOOK_FOUND);
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
   *  @return Returns an empty list of BookClass objects, with the ResponseHeader class.
   *  @throws DaoException An exception that raises when executing an SQL query fails.
   *  @throws DbException An exception that raises when the database connection/disconnection fails.
   *  @throws BookException An exception that gets raised mainly due to logic error.
   */
  public ResponseBooks removeBook(Integer bookId) throws DaoException, DbException, BookException {
    res = new ResponseBooks();
    int update = dao.deleteBook(bookId);
    if (update == 0) {
      throw new BookException(BOOK_NOT_EXISTING);
    } else {
      res.getResponseHeader().setMessage(BOOK_DELETED);
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
   *  @throws BookException An exception that gets raised mainly due to logic error.
   */
  public ResponseBooks getBook(Integer bookId) throws DaoException, DbException, BookException {
    res = new ResponseBooks();
    try {
      List<BookClass> books = dao.getBook(bookId);
      if (books.size() == 0) {
        throw new BookException(BOOK_NOT_EXISTING);
      } else {
        res.getResponseHeader().setMessage(BOOK_FOUND);
      }
      res.setBooks(books);
    } catch (DaoException | DbException | BookException e) {
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
   * @param bookId Identifier of a book.
   * @param updStatus Describes the user, and the user's action
   * @return An empty list of BookClass objects, with a ResponseHeader class.
   * @throws DaoException An exception that gets raised when executing an SQL query fails.
   * @throws DbException An exception that gets raised when the database connection/disconnection fails.
   * @throws BookException An exception that gets raised mainly due to logic error.
   */
  public ResponseBooks updateBook(Integer bookId, PatchBookClass updStatus) throws DaoException, DbException, BookException {
    res = new ResponseBooks();
    System.out.println(bookId + ", " + updStatus.getBorrower() + ", " + updStatus.getStatus());
    int action = updStatus.getStatus();//0 = Borrow, 1 = Return, 2 = Lost.
    BookStatus currentStatus = dao.checkBookStatus(bookId, updStatus.getBorrower());

    switch (validateUserBookRelation(currentStatus, action)) {
      case 0: {
        //Borrow!
        if (dao.checkBookStockAvailability(bookId)) {
          dao.updateBook_borrowed(bookId, updStatus.getBorrower());
          res.getResponseHeader().setMessage(BOOK_BORROWED);
        } else {
          throw new BookException(BOOK_NO_STOCK);
        }
        break;
      }
      case 1: {
        //Return
        dao.updateBook_returned(bookId, updStatus.getBorrower());
        res.getResponseHeader().setMessage(BOOK_RETURNED);
        break;
      }
      case 2: {
        //Lost
        dao.updateBook_lost(bookId,updStatus.getBorrower());
        List<BookClass> book = dao.getBook(bookId);
        if (book.get(0).getQuantity() <= 0) {
          dao.deleteBook(bookId);//Simply remove the book from the bookshelf
          res.getResponseHeader().setMessage(BOOK_LOST_AND_DELETED);
        }else {
          res.getResponseHeader().setMessage(BOOK_LOST);
        }
        break;
      }
    }
    return res;
  }


  /**
   * Logic for adding a new book data to the database.
   * @param book {@link com.example.demo.backend.custom.Dto.BookClass BookClass} to be added.
   * @return Returns an empty list of BookClass objects, with the ResponseHeader class.
   * @throws DaoException An exception that gets raised when executing an SQL query fails.
   * @throws DbException An exception that gets raised when the database connection/disconnection fails.
   * @throws BookException An exception that gets raised mainly due to logic error.
   */
  public ResponseBooks addBook(BookClass book) throws DaoException, DbException, BookException {
    res = new ResponseBooks();
    try {
      int updated = dao.insertBook(book);
      if (updated == 0) {
        throw new BookException(UPDATE_FAILED_BOOK);
      } else {
        res.getResponseHeader().setMessage(BOOK_INSERTED);
        return res;
      }

    } catch (DaoException | DbException e) {
      if (e instanceof DaoException && ((DaoException) e).getSqlCode().equals(SQL_CODE_DUPLICATE_KEY_ERROR)) {
        throw new BookException(BOOK_DUPLICATE);
      }
      throw e;
    }
  }

  public ResponseBooks replaceBook(Integer bookId, BookClass book) throws DbException, DaoException, BookException {
    res = new ResponseBooks();
    try {
      int updated = dao.updateBook_data(bookId, book);
      if (updated == 0) {
        throw new BookException(BOOK_NOT_EXISTING);
      } else {
        res.getResponseHeader().setMessage(UPDATE_SUCCESS_BOOK);
      }
    } catch (DaoException e) {
      if (e.getSqlCode().equals(SQL_CODE_DUPLICATE_KEY_ERROR)) {
        throw new BookException(BOOK_DUPLICATE);
      }
      throw e;
    }
    return res;
  }








  public ResponseUsers addUser(BookUser user) throws DaoException, DbException, UserException {
    ures = new ResponseUsers();
    try {
      List<BookUser> users = udao.insertBookUser(user);
      ures.setUsers(users);
      ures.getResponseHeader().setMessage(USER_INSERTED);
      return ures;
    } catch (DaoException | DbException e) {
      if (e instanceof DaoException && ((DaoException) e).getSqlCode().equals(SQL_CODE_DUPLICATE_KEY_ERROR)) {
        throw new UserException(USER_DUPLICATE);
      }
      throw e;
    }
  }
}


