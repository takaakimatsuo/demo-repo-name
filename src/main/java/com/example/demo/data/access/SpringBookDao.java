package com.example.demo.data.access;

import static com.example.demo.backend.errormessages.StaticMessages.BOOK_DUPLICATE;
import static com.example.demo.backend.errormessages.StaticMessages.BOOK_NOT_EXISTING;
import static com.example.demo.backend.errormessages.StaticMessages.BOOK_NO_STOCK;
import static com.example.demo.backend.errormessages.StaticMessages.UPDATE_FAILED_BOOK;

import com.example.demo.backend.custom.exceptions.DaoException;
import com.example.demo.backend.custom.Dto.BookClass;
import com.example.demo.backend.custom.enums.ExceptionCodes;
import com.example.demo.data.access.custom.enums.BookStatus;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;


@Component("SpringBookDao")
public class SpringBookDao implements BookDao {
  @Autowired
  private JdbcTemplate jdbcTemplate;


  /*
  * Get all books stored in the bookshelf table.
  * @param: None
  * @return:  List<BookClass> list - A list of BookClass objects with the ResponseHeader class.
  */
  @Override
  public List<BookClass> getAllBooks() throws DaoException {
    try {
      return jdbcTemplate.query("SELECT * FROM bookshelf", new RowMapper<BookClass>() {
        public BookClass mapRow(ResultSet rs, int rowNum) throws SQLException {
          BookClass book = new BookClass();
          book.setId(rs.getInt("ID"));
          book.setTitle(rs.getString("TITLE"));
          book.setPrice(rs.getInt("PRICE"));
          book.setQuantity(rs.getInt("QUANTITY"));
          book.setUrl(rs.getString("URL"));
          return book;
        }
      });
    } catch (DataAccessException e) {
      if (e.getRootCause() instanceof SQLException) {
        SQLException sqlEx = (SQLException) e.getRootCause();
        throw createDaoException(sqlEx);
      } else {
        throw new DaoException(e.getMessage(), e.getCause(),ExceptionCodes.SYSTEM_ERROR);
      }
    }
  }


  /**
   * Generates a DaoException from the SQLExcetion
   * @param sqlException Raw SQLException
   * @return DaoException
   */
  public DaoException createDaoException(SQLException sqlException) {
    return new DaoException(sqlException.getMessage(),sqlException.getCause(), ExceptionCodes.SYSTEM_ERROR);
  }


  @Override
  public List<BookClass> getBook(Integer bookId) throws DaoException {
    try {
      return jdbcTemplate.query("select * from bookshelf where id = ?", new RowMapper<BookClass>() {
        public BookClass mapRow(ResultSet rs, int rowNum) throws SQLException {
          BookClass book = new BookClass();
          book.setId(rs.getInt("ID"));
          book.setTitle(rs.getString("TITLE"));
          book.setPrice(rs.getInt("PRICE"));
          book.setQuantity(rs.getInt("QUANTITY"));
          book.setUrl(rs.getString("URL"));
          return book;
        }
      }, bookId);
    } catch (DataAccessException e) {
      if (e.getRootCause() instanceof SQLException) {
        SQLException sqlEx = (SQLException) e.getRootCause();
        throw createDaoException(sqlEx);
      } else {
        throw new DaoException(e.getMessage(), e.getCause(),ExceptionCodes.SYSTEM_ERROR);
      }
    }
  }

  @Override
  public List<BookClass> insertBook(BookClass book) throws DaoException {
    List<BookClass> list;
    try {
      list = jdbcTemplate.query("INSERT INTO bookshelf(title,price,quantity,url) values(?, ?, ?, ?) RETURNING *",
        new RowMapper<BookClass>() {
          public BookClass mapRow(ResultSet rs, int rowNum) throws SQLException {
              BookClass book = new BookClass();
              book.setId(rs.getInt("ID"));
              book.setTitle(rs.getString("TITLE"));
              book.setPrice(rs.getInt("PRICE"));
              book.setQuantity(rs.getInt("QUANTITY"));
              book.setUrl(rs.getString("URL"));
              return book;
          }
          }, book.getTitle(), book.getPrice(), book.getQuantity(), book.getUrl());
    } catch (DataAccessException e) {
      throw new DaoException(BOOK_DUPLICATE + "? : " + e.getLocalizedMessage());
    }
    return list;
  }

  @Override
  public int deleteBook(Integer bookId) throws DaoException {
    try {
      return jdbcTemplate.update("DELETE FROM bookshelf WHERE id = ?", bookId);
    } catch (DataAccessException e) {
      throw new DaoException(e.getLocalizedMessage());
    }
  }


  @Override
  public BookStatus checkBookStatus(Integer bookId, String phoneNumber) throws DaoException {
    BookStatus st = BookStatus.UNKNOWN;
    List<BookClass> customers = jdbcTemplate.query("select borrowedby from bookshelf where id = ?", new RowMapper<BookClass>() {
      public BookClass mapRow(ResultSet rs, int rowNum) throws SQLException {
        BookClass book = new BookClass();
        book.setBorrowedBy(splitStringIntoArray(rs.getString("BORROWEDBY"), ",", new String[]{"\"", "}", "{"}));
        return book;
      }
      },bookId);
    if (customers.size() == 0) {
      System.out.println("list =  null ");
      st = BookStatus.BOOK_NOT_EXISTING;
    } else {
      if (Arrays.asList(customers.get(0).getBorrowedBy()).contains(phoneNumber)) {
        //Book is borrowed by the user.
        System.out.println("[INFO] User already borrowing the book.");
        st = BookStatus.BOOK_BORROWED_BY_THIS_USER;
      } else {
        System.out.println("[INFO] User not borrowing the book yet.");
        st =  BookStatus.BOOK_NOT_BORROWED_BY_THIS_USER;
      }
    }
    return st;
  }



  @Override
  public boolean checkBookStockAvailability(Integer bookId) throws DaoException {
    boolean available = false;
    try {
      available = jdbcTemplate.queryForObject("SELECT COALESCE(array_length(borrowedBy, 1), 0) < quantity as stock_available FROM bookshelf WHERE id = ?", new RowMapper<Boolean>() {
        public Boolean mapRow(ResultSet rs, int rowNum) throws SQLException {
          return rs.getBoolean("STOCK_AVAILABLE");
        }
        }, bookId);
    } catch (DataAccessException e) {
     /* if (e.getRootCause() instanceof SQLException) {
        SQLException sqlEx = (SQLException) e.getRootCause();
        int sqlErrorCode = sqlEx.getErrorCode();
        // do further things from here on...
      } else {*/
        throw new DaoException(BOOK_NOT_EXISTING + "? : " + e.getLocalizedMessage());
      //}
    }
    return available;
  }

  @Override
  public int updateBook_borrowed(Integer bookId, String phoneNumber) throws DaoException {
    if (checkBookStockAvailability(bookId)) {
      return jdbcTemplate.update("UPDATE bookshelf SET borrowedBy = array_append(borrowedBy, ?) WHERE id = ?", phoneNumber, bookId);
    } else {
      throw new DaoException(BOOK_NO_STOCK);
    }
  }

  @Override
  public int updateBook_returned(Integer bookId, String phoneNumber) throws DaoException {
    try {
      return jdbcTemplate.update("UPDATE bookshelf SET borrowedBy = array_remove(borrowedBy, ?) WHERE id = ?", phoneNumber, bookId);
    } catch (DataAccessException e) {
      throw new DaoException(UPDATE_FAILED_BOOK + "? : " + e.getLocalizedMessage());
    }
  }

  @Override
  public int updateBook_lost(Integer bookId, String phoneNumber) throws DaoException {
    try {
      return jdbcTemplate.update("UPDATE bookshelf SET borrowedBy = array_remove(borrowedBy, ?), quantity = (quantity-1) WHERE id = ?", phoneNumber, bookId);
    } catch (DataAccessException e) {
      throw new DaoException(UPDATE_FAILED_BOOK + "? : " + e.getLocalizedMessage());
    }
  }

//
//  @Override
//  public void updateBook_lost(Integer bookId, String phoneNumber) throws DaoException {
//    try {
//      Integer remainingQuantity = jdbcTemplate.queryForObject("UPDATE bookshelf SET borrowedBy = array_remove(borrowedBy, ?), quantity = (quantity-1) WHERE id = ? RETURNING quantity", new RowMapper<Integer>() {
//        public Integer mapRow(ResultSet rs, int rowNum) throws SQLException {
//          return rs.getInt("QUANTITY");
//        }
//        }, phoneNumber, bookId);
//      if (remainingQuantity <= 0) {
//        //If quantity is less than 0
//        deleteBook(bookId);//Simply remove the book from the bookshelf
//      }
//    } catch (DataAccessException e) {
//      throw new DaoException(UPDATE_FAILED_BOOK + "? : " + e.getLocalizedMessage());
//    }
//  }

  @Override
  public int updateBook_data(Integer bookId, BookClass book) throws DaoException {
    try {
      return jdbcTemplate.update("UPDATE bookshelf SET title = ?, price = ?, url = ?, quantity = ? where id = ? AND borrowedBy = \'{}\'", book.getTitle(), book.getPrice(), book.getUrl(), book.getQuantity(), bookId);
    } catch (DataAccessException e) {
      throw new DaoException(BOOK_DUPLICATE + "? : " + e.getLocalizedMessage());
    }
  }
}
