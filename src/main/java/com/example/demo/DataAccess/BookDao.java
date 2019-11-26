package com.example.demo.DataAccess;

import com.example.demo.Backend.CustomExceptions.*;
import com.example.demo.Backend.CustomObjects.BookClass;
import com.example.demo.DataAccess.CustomENUMs.BookStatus;
import java.util.List;


public interface BookDao {

  List<BookClass> getAllBooks() throws DaoException, DbException;

  List<BookClass> getBook(Integer bookId) throws DaoException, DbException;

  List<BookClass> insertBook(BookClass book) throws DaoException, DbException;

  int deleteBook(Integer bookId) throws DaoException, DbException;

  BookStatus checkBookStatus(Integer bookId, String phoneNumber) throws DaoException, DbException;

  boolean checkBookStockAvailability(Integer bookId) throws DaoException, DbException;

  void updateBook_borrowed(Integer bookId, String phoneNumber) throws DaoException, DbException;

  void updateBook_returned(Integer bookId, String phoneNumber) throws DaoException, DbException;

  void updateBook_lost(Integer bookId, String phoneNumber) throws DaoException, DbException;

  int updateBook_data(Integer bookId, BookClass book) throws DaoException, DbException;


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
