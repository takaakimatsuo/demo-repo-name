package com.example.demo.backend;

import static com.example.demo.DemoApplication.logger;
import static com.example.demo.backend.errorcodes.SqlErrorCodes.SQL_CODE_DUPLICATE_KEY_ERROR;
import static com.example.demo.common.messages.StaticBookMessages.BOOK_BORROWED;
import static com.example.demo.common.messages.StaticBookMessages.BOOK_CANNOT_BE_DOUBLE_BORROWED;
import static com.example.demo.common.messages.StaticBookMessages.BOOK_CANNOT_BE_LOST;
import static com.example.demo.common.messages.StaticBookMessages.BOOK_CANNOT_BE_RETURNED;
import static com.example.demo.common.messages.StaticBookMessages.BOOK_DELETED;
import static com.example.demo.common.messages.StaticBookMessages.BOOK_DUPLICATE;
import static com.example.demo.common.messages.StaticBookMessages.BOOK_FOUND;
import static com.example.demo.common.messages.StaticBookMessages.BOOK_INSERTED;
import static com.example.demo.common.messages.StaticBookMessages.BOOK_LOST;
import static com.example.demo.common.messages.StaticBookMessages.BOOK_LOST_AND_DELETED;
import static com.example.demo.common.messages.StaticBookMessages.BOOK_NOT_EXISTING;
import static com.example.demo.common.messages.StaticBookMessages.BOOK_NOT_EXISTING_OR_IS_BORROWED;
import static com.example.demo.common.messages.StaticBookMessages.BOOK_NOT_FOUND;
import static com.example.demo.common.messages.StaticBookMessages.BOOK_NO_STOCK;
import static com.example.demo.common.messages.StaticBookMessages.BOOK_RETURNED;
import static com.example.demo.common.messages.StaticBookMessages.UNEXPECTED;
import static com.example.demo.common.messages.StaticBookMessages.UPDATE_FAILED_BOOK;
import static com.example.demo.common.messages.StaticBookMessages.UPDATE_SUCCESS_BOOK;

import com.example.demo.common.enums.BookMessages;
import com.example.demo.backend.custom.Dto.Book;
import com.example.demo.backend.custom.Dto.PatchBook;
import com.example.demo.backend.custom.Dto.ResponseBooks;
import com.example.demo.common.exceptions.BookException;
import com.example.demo.common.exceptions.DaoException;
import com.example.demo.common.exceptions.DbException;
import com.example.demo.data.access.BookDao;
import com.example.demo.data.access.custom.enums.BookStatus;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;






@Component
public final class BookBusinessLogic {
  private ResponseBooks res;

  @Autowired
  @Qualifier("JdbcBookDao") //Based on standard JDBC.
  //@Qualifier("SpringBookDao") // Based on Spring JDBCtemplate.
  private BookDao dao;



  /**
   * Logic for searching all books stored in the bookshelf table.
   * @return Returns a list of Book objects, with the MessageHeader class, not {@code null}.
   * @throws DaoException if query execution fails.
   * @throws BookException if query logic fails.
  */
  public ResponseBooks getAllBooks() throws DaoException, BookException {
    res = new ResponseBooks();
    List<Book> books = dao.getAllBooks();
    if (books.isEmpty()) {
      throw new BookException(BOOK_NOT_FOUND);
    } else {
      res.getMessageHeader().setMessage(BOOK_FOUND);
    }
    res.setBooks(books);
    return res;
  }

  /**
   *  Logic for removing a specified book from the bookshelf table.
   *  @param bookId Identifier of a book, not {@code null}
   *  @return Returns an empty list of BookClass objects, with the MessageHeader class, not {@code null}.
   *  @throws DaoException if query execution fails.
   *  @throws BookException if query logic fails.
   */
  public ResponseBooks removeBook(Integer bookId) throws DaoException, BookException {
    res = new ResponseBooks();
    int update = dao.deleteBook(bookId);
    if (update == 0) {
      throw new BookException(BOOK_NOT_EXISTING);
    } else {
      res.getMessageHeader().setMessage(BOOK_DELETED);
    }
    return res;
  }

  /**
   * Logic for searching a specific book stored in the bookshelf table using its id.
   *  @param bookId Identifier of a book.
   *  @return Returns a list containing a single BookClass object, with a MessageHeader class.
   *  The list will be kept empty if no matching data has been found.
   * @throws DaoException if query execution fails.
   * @throws BookException if query logic fails.
   */
  public ResponseBooks getBook(Integer bookId) throws DaoException, BookException {
    res = new ResponseBooks();

    List<Book> books = dao.getBook(bookId);
    if (books.size() == 0) {
      throw new BookException(BOOK_NOT_EXISTING);
    } else {
      res.getMessageHeader().setMessage(BOOK_FOUND);
    }
    res.setBooks(books);

    return res;
  }

  /** Validates the relation between what the user wants to do and what the system knows.
   *  @param currentStatus enum representing the current book status.
   *  @param requestedStatus Users requested status {0:borrow, 1:return, 2:report lost
   *  @return The same inputted requestedStatus.
   * @throws BookException if query logic fails.
   */
  private int validateUserBookRelation(BookStatus currentStatus, int requestedStatus) throws BookException {
    if (currentStatus == BookStatus.UNKNOWN) {
      logger.error("Unexpected output in validateUserBookRelation() in BookBusinessLogic. This should not happen.");
      throw new BookException(UNEXPECTED);
    } else if (currentStatus == BookStatus.BOOK_NOT_EXISTING) {
      logger.info("Book does not exist.");
      throw new BookException(BOOK_NOT_EXISTING);
    } else if (currentStatus == BookStatus.BOOK_BORROWED_BY_THIS_USER && requestedStatus == 0) {
      logger.info("Book already borrowed by the same user.");
      throw new BookException(BOOK_CANNOT_BE_DOUBLE_BORROWED);
    } else if (currentStatus == BookStatus.BOOK_NOT_BORROWED_BY_THIS_USER && requestedStatus == 1) {
      logger.info("Trying to return a book that has not been borrowed by the user.");
      throw new BookException(BOOK_CANNOT_BE_RETURNED);
    } else if (currentStatus == BookStatus.BOOK_NOT_BORROWED_BY_THIS_USER && requestedStatus == 2) {
      logger.info("Trying to report a book as lost, which has not been borrowed by the user.");
      throw new BookException(BOOK_CANNOT_BE_LOST);
    }
    return requestedStatus;
  }


  /**
   * @param bookId Identifier of a book.
   * @param updStatus Describes the user, and the user's action
   * @return An empty list of BookClass objects, with a MessageHeader class.
   * @throws DaoException if query execution fails.
   * @throws BookException if query logic fails.
   */
  public ResponseBooks updateBook(Integer bookId, PatchBook updStatus) throws DaoException, BookException {
    res = new ResponseBooks();
    int action = updStatus.getStatus();//0 = Borrow, 1 = Return, 2 = Lost.
    BookStatus currentStatus = dao.checkBookStatus(bookId, updStatus.getBorrower());

    switch (validateUserBookRelation(currentStatus, action)) {
      case 0: {
        //Borrow!
        if (dao.checkBookStockAvailability(bookId)) {
          dao.updateBook_borrowed(bookId, updStatus.getBorrower());
          res.getMessageHeader().setMessage(BOOK_BORROWED);
        } else {
          throw new BookException(BOOK_NO_STOCK);
        }
        break;
      }
      case 1: {
        //Return
        dao.updateBook_returned(bookId, updStatus.getBorrower());
        res.getMessageHeader().setMessage(BOOK_RETURNED);
        break;
      }
      case 2: {
        //Lost
        dao.updateBook_lost(bookId,updStatus.getBorrower());
        List<Book> book = dao.getBook(bookId);
        if (book.get(0).getQuantity() <= 0) {
          dao.deleteBook(bookId);//Simply remove the book from the bookshelf
          res.getMessageHeader().setMessage(BOOK_LOST_AND_DELETED);
        }else {
          res.getMessageHeader().setMessage(BOOK_LOST);
        }
        break;
      }
    }
    return res;
  }


  /**
   * Logic for adding a new book data to the database.
   * @param book {@link com.example.demo.backend.custom.Dto.Book BookClass} to be added.
   * @return Returns an empty list of BookClass objects, with the MessageHeader class.
   * @throws DaoException if query execution fails.
   * @throws BookException if query logic fails.
   */
  public ResponseBooks addBook(Book book) throws DaoException, BookException {
    res = new ResponseBooks();
    try {
      int updated = dao.insertBook(book);
      if (updated == 0) {
        throw new BookException(UPDATE_FAILED_BOOK);
      } else {
        res.getMessageHeader().setMessage(BOOK_INSERTED);
        return res;
      }

    } catch (DaoException e) {
      if (e.getSqlCode().equals(SQL_CODE_DUPLICATE_KEY_ERROR)) {
        throw new BookException(BOOK_DUPLICATE);
      }
      throw e;
    }
  }

  public ResponseBooks replaceBook(Integer bookId, Book book) throws DaoException, BookException {
    res = new ResponseBooks();
    try {
      int updated = dao.updateBook_data(bookId, book);
      if (updated == 0) {
        throw new BookException(BOOK_NOT_EXISTING_OR_IS_BORROWED);
      } else {
        res.getMessageHeader().setMessage(UPDATE_SUCCESS_BOOK);
      }
    } catch (DaoException e) {
      if (e.getSqlCode().equals(SQL_CODE_DUPLICATE_KEY_ERROR)) {
        throw new BookException(BOOK_DUPLICATE);
      }
      throw e;
    }
    return res;
  }



}


