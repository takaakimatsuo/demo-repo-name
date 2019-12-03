package com.example.demo.data.access;

import com.example.demo.backend.custom.exceptions.DaoException;
import com.example.demo.backend.custom.exceptions.DbException;
import com.example.demo.backend.custom.Dto.BookClass;
import com.example.demo.data.access.custom.enums.BookStatus;
import java.util.List;

public interface BookDao {



  /**
   * Tries to get all book data from the database.
   * @return Returns a list of {@link com.example.demo.backend.custom.Dto.BookClass BookClass} objects, with a {@link com.example.demo.backend.custom.Dto.ResponseHeader ResponseHeader}.
   *         The list will be kept empty if no results were found.
   * @throws DaoException An exception that gets raised when executing an SQL query fails.
   */
  List<BookClass> getAllBooks() throws DaoException;


  /**
   * Tries to get a book data stored in the database, in a form of {@link com.example.demo.backend.custom.Dto.BookClass BookClass} object, specified with a bookId.
   * @param bookId  Unique identifier of the book data in the database.
   * @return Returns a list of {@link com.example.demo.backend.custom.Dto.BookClass BookClass} objects, with a {@link com.example.demo.backend.custom.Dto.ResponseHeader ResponseHeader}.
   *         The list will be kept empty if no results were found.
   * @throws DaoException An exception that gets raised when executing an SQL query fails.
   */
  List<BookClass> getBook(Integer bookId) throws DaoException;

  /**
   * Tries to insert a new book data to the database.
   * @param book A {@link com.example.demo.backend.custom.Dto.BookClass BookClass} object to be inserted to the database.
   * @return Returns an empty list of {@link com.example.demo.backend.custom.Dto.BookClass BookClass} objects, with a {@link com.example.demo.backend.custom.Dto.ResponseHeader ResponseHeader}.
   * @throws DaoException An exception that gets raised when executing an SQL query fails.
   */
  int insertBook(BookClass book) throws DaoException;

  /**
   * Tries to delete a book data from the database.
   * @param bookId Unique identifier for the book stored in the database.
   * @return Returns an empty list of {@link com.example.demo.backend.custom.Dto.BookClass BookClass} objects, with a {@link com.example.demo.backend.custom.Dto.ResponseHeader ResponseHeader}.
   * @throws DaoException An exception that gets raised when executing an SQL query fails.
   */
  int deleteBook(Integer bookId) throws DaoException;

  /**
   *
   * @param bookId Unique identifier for the book stored in the database.
   * @param phoneNumber Unique identifier for the user stored in the database.
   * @return Returns the {@link com.example.demo.data.access.custom.enums BookStatus}, which represents the current status of specified book.
   *  <ul>
   *  <li>BOOK_NOT_EXISTING
   *  <li>BOOK_NOT_BORROWED_BY_THIS_USER
   *  <li>BOOK_BORROWED_BY_THIS_USER
   *  </ul>
   * @throws DaoException An exception that gets raised when executing an SQL query fails.
   * @throws DbException An exception that gets raised when the database connection/disconnection fails.
   */
  BookStatus checkBookStatus(Integer bookId, String phoneNumber) throws DaoException;

  /**
   * Checks whether there is enough stocks available for a user to borrow a book.
   * @param bookId Unique identifier for the book stored in the database.
   * @return Returns a boolean representing whether the user can borrow the book in terms of quantity available.
   * @throws DaoException An exception that gets raised when executing an SQL query fails.
   */
  boolean checkBookStockAvailability(Integer bookId) throws DaoException;

  /**
   *
   * @param bookId Unique identifier for the book stored in the database.
   * @param phoneNumber Unique identifier for the user stored in the database.
   * @return Returns the number of updated rows.
   * @throws DaoException An exception that gets raised when executing an SQL query fails.
   */
  int updateBook_borrowed(Integer bookId, String phoneNumber) throws DaoException;

  /**
   *
   * @param bookId Unique identifier for the book stored in the database.
   * @param phoneNumber Unique identifier for the user stored in the database.
   * @return Returns the number of updated rows.
   * @throws DaoException An exception that gets raised when executing an SQL query fails.
   */
  int updateBook_returned(Integer bookId, String phoneNumber) throws DaoException;

  /**
   * Removes the user from the borrower
   * @param bookId Unique identifier for the book stored in the database.
   * @param phoneNumber Unique identifier for the user stored in the database.
   * @throws DaoException An exception that gets raised when executing an SQL query fails.
   */
  int updateBook_lost(Integer bookId, String phoneNumber) throws DaoException;

  /**
   * Replaces a book data to another one in the database.
   * @param bookId Unique identifier for the book stored in the database. The data with this id will be replaced with the second parameter.
   * @param book  A new {@link com.example.demo.backend.custom.Dto.BookClass BookClass} data to be replaced with another in the database.
   * @return Returns the number of updated rows.
   * @throws DaoException An exception that gets raised when executing an SQL query fails.
   */
  int updateBook_data(Integer bookId, BookClass book) throws DaoException;


  /**
   * Converts a string into an array.
   * @param str Original string to be manipulated.
   * @param splitter The specified String will be used to split the first parameter.
   * @param replacer The specified list of Strings in the this array will be removed from the first parameter.
   * @return An array of strings.
   */
  default String[] splitStringIntoArray(String str, String splitter, String[] replacer) {
    String[] adjustedToArray = new String[0];
    if (str == null) {
      return adjustedToArray;
    }
    for (final String rep: replacer) {
      str = str.replace(rep, "");
    }
    if (str.split(splitter)[0].compareTo("") != 0) {
      adjustedToArray = str.split(splitter);
    }
    return adjustedToArray;
  }

}
