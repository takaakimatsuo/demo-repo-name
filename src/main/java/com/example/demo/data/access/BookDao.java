package com.example.demo.data.access;

import com.example.demo.backend.custom.Dto.MessageHeader;
import com.example.demo.common.exceptions.DaoException;
import com.example.demo.backend.custom.Dto.Book;
import com.example.demo.data.access.custom.enums.BookStatus;
import java.util.List;

public interface BookDao {



  /**
   * Searches all book data from the database.
   * @return Returns a list of searched {@link com.example.demo.backend.custom.Dto.Book Book} objects,
   * with a {@link MessageHeader MessageHeader}, not {@code null}..
   * @throws DaoException if query execution fails.
   */
  List<Book> getAllBooks() throws DaoException;

  /**
   * Searches a book data stored in the database with an ID.
   * @param bookId  Unique identifier of the book data in the database, not {@code null}.
   * @return Returns a list of searched {@link com.example.demo.backend.custom.Dto.Book Book} objects,
   * with a {@link MessageHeader MessageHeader}, not {@code null}..
   * @throws DaoException if query execution fails.
   */
  List<Book> getBook(Integer bookId) throws DaoException;

  /**
   * Inserts a new book data to the database.
   * @param book A {@link com.example.demo.backend.custom.Dto.Book Book} object to be inserted to the database , not {@code null}..
   * @return Returns an empty list of {@link com.example.demo.backend.custom.Dto.Book Book} objects,
   * with a {@link MessageHeader MessageHeader}, not {@code null}..
   * @throws DaoException if query execution fails.
   */
  int insertBook(Book book) throws DaoException;

  /**
   * Deletes a book data from the database.
   * @param bookId Unique identifier for the book stored in the database, not {@code null}.
   * @return Returns an empty list of {@link com.example.demo.backend.custom.Dto.Book Book} objects,
   * with a {@link MessageHeader MessageHeader}, not {@code null}.
   * @throws DaoException if query execution fails.
   */
  int deleteBook(Integer bookId) throws DaoException;

  /**
   * Checks the current status of the specified book.
   * @param bookId Unique identifier for the book stored in the database, not {@code null}.
   * @param phoneNumber Unique identifier for the user stored in the database, not {@code null}.
   * @return Returns the {@link com.example.demo.data.access.custom.enums BookStatus},
   * which represents the current status of specified book, not {@code null}.
   *  <ul>
   *  <li>BOOK_NOT_EXISTING
   *  <li>BOOK_NOT_BORROWED_BY_THIS_USER
   *  <li>BOOK_BORROWED_BY_THIS_USER
   *  </ul>
   * @throws DaoException if query execution fails.
   */
  BookStatus checkBookStatus(Integer bookId, String phoneNumber) throws DaoException;

  /**
   * Checks whether there is enough stocks available for a user to borrow a book.
   * @param bookId Unique identifier for the book stored in the database , not {@code null}.
   * @return Returns a boolean representing whether the user can borrow the book in terms of quantity available.
   * @throws DaoException if query execution fails.
   */
  boolean checkBookStockAvailability(Integer bookId) throws DaoException;

  /**
   * Updates book as borrowed.
   * @param bookId Unique identifier for the book stored in the database, not {@code null}.
   * @param phoneNumber Unique identifier for the user stored in the database, not {@code null}.
   * @return Returns the number of updated rows.
   * @throws DaoException if query execution fails.
   */
  int updateBookBorrowed(Integer bookId, String phoneNumber) throws DaoException;

  /**
   * Updates book as returned.
   * @param bookId Unique identifier for the book stored in the database, not {@code null}.
   * @param phoneNumber Unique identifier for the user stored in the database, not {@code null}.
   * @return Returns the number of updated rows.
   * @throws DaoException if query execution fails.
   */
  int updateBookReturned(Integer bookId, String phoneNumber) throws DaoException;

  /**
   * Updates book as lost.
   * @param bookId Unique identifier for the book stored in the database, not {@code null}.
   * @param phoneNumber Unique identifier for the user stored in the database, not {@code null}.
   * @throws DaoException if query execution fails.
   */
  int updateBookLost(Integer bookId, String phoneNumber) throws DaoException;

  /**
   * Replaces a book data to another one in the database.
   * @param bookId Unique identifier for the book stored in the database, not {@code null}.
   * @param book  A new {@link com.example.demo.backend.custom.Dto.Book Book} data to be replaced with another in the database, not {@code null}..
   * @return Returns the number of updated rows.
   * @throws DaoException if query execution fails.
   */
  int replaceBook(Integer bookId, Book book) throws DaoException;


  /**
   * Converts a JSON-like String into an array.
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
