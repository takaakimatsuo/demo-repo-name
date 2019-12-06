package com.example.demo.backend;

import static com.example.demo.backend.errorcodes.SqlErrorCodes.UNIQUE_VIOLATION;

import com.example.demo.backend.custom.Dto.Book;
import com.example.demo.backend.custom.Dto.PatchBook;
import com.example.demo.backend.custom.Dto.ResponseBooks;
import com.example.demo.common.enums.Messages;
import com.example.demo.common.exceptions.BookBusinessLogicException;
import com.example.demo.common.exceptions.DaoException;
import com.example.demo.data.access.BookDao;
import com.example.demo.data.access.custom.enums.BookStatus;
import java.util.List;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class BookBusinessLogic {
  private ResponseBooks res;

  @Autowired
  @Qualifier("JdbcBookDao") //Based on standard Jdbc.
  //@Qualifier("SpringBookDao") // Based on Spring JdbcTemplate.
  public BookDao dao;


  /**
   * Logic for searching all books stored in the bookshelf table.
   * @return Returns a list of Book objects, with the MessageHeader class, not {@code null}.
   * @throws DaoException if query execution fails.
   * @throws BookBusinessLogicException if query logic fails.
  */
  public ResponseBooks getAllBooks() throws DaoException, BookBusinessLogicException {
    res =  ResponseBooks.builder().build();
    List<Book> books = dao.getAllBooks();
    if (books.isEmpty()) {
      throw new BookBusinessLogicException(Messages.BOOK_NOT_EXISTING);
    } else {
      res.getMessageHeader().setMessage(Messages.BOOK_FOUND);
    }
    res.setBooks(books);
    return res;
  }

  /**
   *  Logic for removing a specified book from the bookshelf table.
   *  @param bookId Identifier of a book, not {@code null}
   *  @return Returns an empty list of BookClass objects, with the MessageHeader class, not {@code null}.
   *  @throws DaoException if query execution fails.
   *  @throws BookBusinessLogicException if query logic fails.
   */
  public ResponseBooks removeBook(Integer bookId) throws DaoException, BookBusinessLogicException {
    res =  ResponseBooks.builder().build();
    int updated = dao.deleteBook(bookId);
    if (updated == 0) {
      throw new BookBusinessLogicException(Messages.BOOK_NOT_EXISTING);
    } else {
      res.getMessageHeader().setMessage(Messages.BOOK_DELETED);
    }
    return res;
  }

  /**
  * Logic for searching a specific book stored in the bookshelf table using its id.
  *  @param bookId Identifier of a book.
  *  @return Returns a list containing a single BookClass object, with a MessageHeader class.
  *  The list will be kept empty if no matching data has been found.
  * @throws DaoException if query execution fails.
  * @throws BookBusinessLogicException if query logic fails.
  */
  public ResponseBooks getBook(Integer bookId) throws DaoException, BookBusinessLogicException {
    res =  ResponseBooks.builder().build();

    List<Book> books = dao.getBook(bookId);
    if (books.size() == 0) {
      throw new BookBusinessLogicException(Messages.BOOK_NOT_EXISTING);
    } else {
      res.getMessageHeader().setMessage(Messages.BOOK_FOUND);
    }
    res.setBooks(books);

    return res;
  }

  /** Validates the relation between what the user wants to do and what the system knows.
   *  @param currentStatus enum representing the current book status.
   *  @param requestedStatus Users requested status {0:borrow, 1:return, 2:report lost
   *  @return The same inputted requestedStatus.
   * @throws BookBusinessLogicException if query logic fails.
   */
  private int validateUserBookRelation(BookStatus currentStatus, int requestedStatus) throws BookBusinessLogicException {
    if (currentStatus == BookStatus.UNKNOWN) {
      log.error("Unexpected output in validateUserBookRelation() in BookBusinessLogic. This should not happen.");
      throw new BookBusinessLogicException(Messages.UNEXPECTED);
    } else if (currentStatus == BookStatus.BOOK_NOT_EXISTING) {
      log.info("Book does not exist.");
      throw new BookBusinessLogicException(Messages.BOOK_NOT_EXISTING);
    } else if (currentStatus == BookStatus.BOOK_BORROWED_BY_THIS_USER && requestedStatus == 0) {
      log.info("Book already borrowed by the same user.");
      throw new BookBusinessLogicException(Messages.BOOK_CANNOT_BE_DOUBLE_BORROWED);
    } else if (currentStatus == BookStatus.BOOK_NOT_BORROWED_BY_THIS_USER && requestedStatus == 1) {
      log.info("Trying to return a book that has not been borrowed by the user.");
      throw new BookBusinessLogicException(Messages.BOOK_CANNOT_BE_RETURNED);
    } else if (currentStatus == BookStatus.BOOK_NOT_BORROWED_BY_THIS_USER && requestedStatus == 2) {
      log.info("Trying to report a book as lost, which has not been borrowed by the user.");
      throw new BookBusinessLogicException(Messages.BOOK_CANNOT_BE_LOST);
    }
    return requestedStatus;
  }


  /**
   * Logic for updating the book status
   * @param bookId Identifier of a book.
   * @param updStatus Describes the user, and the user's action
   * @return An empty list of BookClass objects, with a MessageHeader class.
   * @throws DaoException if query execution fails.
   * @throws BookBusinessLogicException if query logic fails.
   */
  public ResponseBooks updateBook(Integer bookId, PatchBook updStatus) throws DaoException, BookBusinessLogicException {
    res = ResponseBooks.builder().build();
    int action = updStatus.getStatus();//0 = Borrow, 1 = Return, 2 = Lost.
    BookStatus currentStatus = dao.checkBookStatus(bookId, updStatus.getBorrower());

    switch (validateUserBookRelation(currentStatus, action)) {
      case 0: {
        //Borrow!
        if (dao.checkBookStockAvailability(bookId)) {
          dao.updateBookBorrowed(bookId, updStatus.getBorrower());
          res.getMessageHeader().setMessage(Messages.BOOK_BORROWED);
        } else {
          throw new BookBusinessLogicException(Messages.BOOK_NO_STOCK);
        }
        break;
      }
      case 1: {
        //Return
        dao.updateBookReturned(bookId, updStatus.getBorrower());
        res.getMessageHeader().setMessage(Messages.BOOK_RETURNED);
        break;
      }
      case 2: {
        //Lost
        dao.updateBookLost(bookId,updStatus.getBorrower());
        List<Book> book = dao.getBook(bookId);
        if (book.get(0).getQuantity() <= 0) {
          dao.deleteBook(bookId);//Simply remove the book from the bookshelf
          res.getMessageHeader().setMessage(Messages.BOOK_LOST_AND_DELETED);
        } else {
          res.getMessageHeader().setMessage(Messages.BOOK_LOST);
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
   * @throws BookBusinessLogicException if query logic fails.
   */
  public ResponseBooks addBook(Book book) throws DaoException, BookBusinessLogicException {
    res =  ResponseBooks.builder().build();
    try {
      int updated = dao.insertBook(book);
      if (updated == 0) {
        throw new BookBusinessLogicException(Messages.UPDATE_FAILED_BOOK);
      } else {
        res.getMessageHeader().setMessage(Messages.BOOK_INSERTED);
        return res;
      }

    } catch (DaoException e) {
      if (e.getSqlCode().equals(UNIQUE_VIOLATION)) {
        throw new BookBusinessLogicException(Messages.BOOK_DUPLICATE);
      }
      throw e;
    }
  }

  public ResponseBooks replaceBook(Integer bookId, Book book) throws DaoException, BookBusinessLogicException {
    res =  ResponseBooks.builder().build();
    try {
      int updated = dao.replaceBook(bookId, book);
      if (updated == 0) {
        throw new BookBusinessLogicException(Messages.BOOK_NOT_EXISTING_OR_IS_BORROWED);
      } else {
        res.getMessageHeader().setMessage(Messages.UPDATE_SUCCESS_BOOK);
      }
    } catch (DaoException e) {
      if (e.getSqlCode().equals(UNIQUE_VIOLATION)) {
        throw new BookBusinessLogicException(Messages.BOOK_DUPLICATE);
      }
      throw e;
    }
    return res;
  }



}


