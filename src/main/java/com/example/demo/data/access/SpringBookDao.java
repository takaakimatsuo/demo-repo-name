package com.example.demo.data.access;

import static com.example.demo.common.messages.StaticBookMessages.BOOK_DUPLICATE;
import static com.example.demo.common.messages.StaticBookMessages.BOOK_NO_STOCK;
import static com.example.demo.common.messages.StaticBookMessages.UPDATE_FAILED_BOOK;
import static com.example.demo.DemoApplication.logger;

import com.example.demo.backend.custom.Dto.Book;
import com.example.demo.common.exceptions.DaoException;
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


  @Override
  public List<Book> getAllBooks() throws DaoException {
    try {
      return jdbcTemplate.query("SELECT * FROM bookshelf", new RowMapper<Book>() {
        public Book mapRow(ResultSet rs, int rowNum) throws SQLException {
          Book book = new Book();
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
        throw new DaoException(e.getMessage(), e.getCause());
      }
    }
  }


  private DaoException createDaoException(SQLException sqlExc) {
    return new DaoException(sqlExc.getMessage(),sqlExc.getCause(),sqlExc.getSQLState());
  }

  @Override
  public List<Book> getBook(Integer bookId) throws DaoException {
    try {
      return jdbcTemplate.query("select * from bookshelf where id = ?", new RowMapper<Book>() {
        public Book mapRow(ResultSet rs, int rowNum) throws SQLException {
          Book book = new Book();
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
        throw new DaoException(e.getMessage(), e.getCause());
      }
    }
  }

  @Override
  public int insertBook(Book book) throws DaoException {
    int updated;
    try {
      updated = jdbcTemplate.update("INSERT INTO bookshelf(title,price,quantity,url) values(?, ?, ?, ?)"
        , book.getTitle(), book.getPrice(), book.getQuantity(), book.getUrl());
    } catch (DataAccessException e) {
      if(e.getRootCause() instanceof SQLException) {
        SQLException sqlEx = (SQLException) e.getRootCause();
        throw new DaoException(e.getMessage(),sqlEx.getCause(), sqlEx.getSQLState());
      }
      throw new DaoException(e.getMessage(),e.getCause());
    }
    return updated;
  }

  @Override
  public int deleteBook(Integer bookId) throws DaoException {
    try {
      return jdbcTemplate.update("DELETE FROM bookshelf WHERE id = ?", bookId);
    } catch (DataAccessException e) {
      if (e.getRootCause() instanceof SQLException) {
        SQLException sqlEx = (SQLException) e.getRootCause();
        throw createDaoException(sqlEx);
      } else {
        throw new DaoException(e.getMessage(), e.getCause());
      }
    }
  }


  @Override
  public BookStatus checkBookStatus(Integer bookId, String phoneNumber) throws DaoException {
    BookStatus st = BookStatus.UNKNOWN;
    List<Book> customers = jdbcTemplate.query("select borrowedby from bookshelf where id = ?", new RowMapper<Book>() {
      public Book mapRow(ResultSet rs, int rowNum) throws SQLException {
        Book book = new Book();
        book.setBorrowedBy(splitStringIntoArray(rs.getString("BORROWEDBY"), ",", new String[]{"\"", "}", "{"}));
        return book;
      }
      },bookId);
    if (customers.size() == 0) {
      st = BookStatus.BOOK_NOT_EXISTING;
    } else {
      if (Arrays.asList(customers.get(0).getBorrowedBy()).contains(phoneNumber)) {
        //Book is borrowed by the user.
        logger.info("User already borrowing the book.");
        st = BookStatus.BOOK_BORROWED_BY_THIS_USER;
      } else {
        logger.info("User not borrowing the book yet.");
        st =  BookStatus.BOOK_NOT_BORROWED_BY_THIS_USER;
      }
    }
    return st;
  }



  @Override
  public boolean checkBookStockAvailability(Integer bookId) throws DaoException {
    Boolean available = false;
    try {
      available = jdbcTemplate.queryForObject("SELECT COALESCE(array_length(borrowedBy, 1), 0) < quantity as stock_available FROM bookshelf WHERE id = ?", new RowMapper<Boolean>() {
          public Boolean mapRow(ResultSet rs, int rowNum) throws SQLException {
            return rs.getBoolean("STOCK_AVAILABLE");
          }
        }, bookId);
    } catch (DataAccessException e) {
      throw new DaoException(e.getMessage(),e.getCause());
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
      if (e.getRootCause() instanceof SQLException) {
        SQLException sqlEx = (SQLException) e.getRootCause();
        throw new DaoException(UPDATE_FAILED_BOOK,sqlEx.getCause(), sqlEx.getSQLState());
      }
      throw new DaoException(e.getMessage(),e.getCause());
    }
  }

  @Override
  public int updateBook_lost(Integer bookId, String phoneNumber) throws DaoException {
    try {
      return jdbcTemplate.update("UPDATE bookshelf SET borrowedBy = array_remove(borrowedBy, ?), quantity = (quantity-1) WHERE id = ?", phoneNumber, bookId);
    } catch (DataAccessException e) {
      if(e.getRootCause() instanceof SQLException) {
        SQLException sqlEx = (SQLException) e.getRootCause();
        throw new DaoException(UPDATE_FAILED_BOOK,sqlEx.getCause(), sqlEx.getSQLState());
      }
      throw new DaoException(e.getMessage(),e.getCause());
    }
  }

  @Override
  public int updateBook_data(Integer bookId, Book book) throws DaoException {
    try {
      return jdbcTemplate.update("UPDATE bookshelf SET title = ?, price = ?, url = ?, quantity = ? where id = ? AND borrowedBy = \'{}\'", book.getTitle(), book.getPrice(), book.getUrl(), book.getQuantity(), bookId);
    } catch (DataAccessException e) {
      if(e.getRootCause() instanceof SQLException) {
        SQLException sqlEx = (SQLException) e.getRootCause();
        throw new DaoException(BOOK_DUPLICATE,sqlEx.getCause(), sqlEx.getSQLState());
      }
      throw new DaoException(e.getMessage(),e.getCause());
    }
  }
}
