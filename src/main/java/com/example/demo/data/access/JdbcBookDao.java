package com.example.demo.data.access;

import com.example.demo.backend.custom.Dto.BookClass;
import com.example.demo.backend.custom.exceptions.DaoException;
import com.example.demo.backend.custom.exceptions.DbException;
import com.example.demo.data.access.custom.enums.BookStatus;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.stereotype.Component;



@Component("JdbcBookDao")
public class JdbcBookDao extends JdbcDao implements BookDao {


  @Override
  public BookStatus checkBookStatus(Integer bookId, String phoneNumber) throws DaoException {
    String query = "SELECT borrowedBy FROM bookshelf WHERE id = ?";
    List<Object> paramList = new ArrayList<Object>();
    paramList.add(bookId);
    BookStatus st = BookStatus.UNKNOWN;
    try {
      ResultSet check = executeQuery(query, paramList);//Execute query.
      if (!check.isBeforeFirst()) {
        //SQL returned an empty output. No data matched the condition.
        System.out.println("[INFO] Book with id=" + bookId + " doesnt exist in the table.");
        st = BookStatus.BOOK_NOT_EXISTING;
      } else {
        if (check.next()) {
          String arr = check.getString("BORROWEDBY");
          String[] customers = splitStringIntoArray(arr, ",", new String[]{"\"", "}", "{"});

          if (Arrays.asList(customers).contains(phoneNumber)) {
            //Book is borrowed by the user.
            System.out.println("[INFO] User already borrowing the book.");
            st = BookStatus.BOOK_BORROWED_BY_THIS_USER;
          } else {
            System.out.println("[INFO] User not borrowing the book yet.");
            st = BookStatus.BOOK_NOT_BORROWED_BY_THIS_USER;
          }
        }
      }
      return st;
    } catch (SQLException e) {
      throw new DaoException(e.getMessage() + ", SQL state:" + e.getSQLState());
    }
  }

  @Override
  public boolean checkBookStockAvailability(Integer bookId) throws DaoException {
    boolean available = false;
    try {
      String query = "SELECT COALESCE(array_length(borrowedBy, 1), 0) < quantity as stock_available FROM bookshelf WHERE id = ?";
      List<Object> paramList = new ArrayList<Object>();
      paramList.add(bookId);
      ResultSet check = executeQuery(query, paramList);//Execute query.
      if (check.next()) {
        available = check.getBoolean("STOCK_AVAILABLE");
      }
    } catch (SQLException e) {
      e.printStackTrace();
      throw new DaoException(e.getMessage());
    }
    return available;
  }

  @Override
  public int updateBook_borrowed(Integer bookId, String phoneNumber) throws DaoException {
    String query = "UPDATE bookshelf SET borrowedBy = array_append(borrowedBy, ?) WHERE id = ?";
    List<Object> paramList = new ArrayList<Object>();
    paramList.add(phoneNumber);
    paramList.add(bookId);
    return executeUpdate(query, paramList);
  }

  @Override
  public int updateBook_returned(Integer bookId, String phoneNumber) throws DaoException {
    String query = "UPDATE bookshelf SET borrowedBy = array_remove(borrowedBy, ?) WHERE id = ?";
    List<Object> paramList = new ArrayList<Object>();
    paramList.add(phoneNumber);
    paramList.add(bookId);
    return executeUpdate(query, paramList);
  }

  @Override
  public int updateBook_lost(Integer bookId, String phoneNumber) throws DaoException {
    String query = "UPDATE bookshelf SET borrowedBy = array_remove(borrowedBy, ?), quantity = (quantity-1) WHERE id = ?";
    List<Object> paramList = new ArrayList<Object>();
    paramList.add(phoneNumber);
    paramList.add(bookId);
    return executeUpdate(query, paramList);
  }

  @Override
  public int updateBook_data(Integer bookId, BookClass book) throws DaoException {
    String query = "UPDATE bookshelf SET title = ?, price = ?, url = ?, quantity = ? where id = ? AND borrowedBy = \'{}\'";
    List<Object> paramList = new ArrayList<Object>();
    paramList.add(book.getTitle());
    paramList.add(book.getPrice());
    paramList.add(book.getUrl());
    paramList.add(book.getQuantity());
    paramList.add(bookId);
    return executeUpdate(query, paramList);
  }

  @Override
  public int insertBook(BookClass book) throws DaoException {
    String query = "INSERT INTO bookshelf(title,price,quantity,url) values(?, ?, ?, ?)";
    List<Object> paramList = new ArrayList<Object>();
    paramList.add(book.getTitle());
    paramList.add(book.getPrice());
    paramList.add(book.getQuantity());
    paramList.add(book.getUrl());
    int updated = executeUpdate(query, paramList);
    return updated;//Also takes care the case with no data found
  }



  @Override
  public int deleteBook(Integer bookId) throws DaoException {
    String query = "DELETE FROM bookshelf WHERE id = ?";
    List<Object> paramList = new ArrayList<Object>();
    paramList.add(bookId);
    return executeUpdate(query,paramList);
  }



  @Override
  public List<BookClass> getAllBooks() throws DaoException {
    String query = "SELECT id, title, price, quantity, (SELECT ARRAY( select familyName ||' '|| firstName FROM book_user u JOIN bookshelf b ON u.phoneNumber = ANY(b.borrowedBy) WHERE b.title = OuterQuery.title)) AS \"borrowedBy\", url FROM bookshelf AS OuterQuery ORDER BY id DESC;";
    System.out.println("[INFO] Requesting query execution");
    ResultSet rs = executeQuery(query);
    System.out.println("[INFO] Done query execution");
    return mapRow(rs);
  }


  @Override
  public List<BookClass> getBook(Integer bookId) throws DaoException {
    String query = "select id, title, price, quantity, (SELECT ARRAY( select familyName ||' '|| firstName from book_user u JOIN bookshelf b ON u.phoneNumber = ANY(b.borrowedBy) WHERE b.title = OuterQuery.title)) AS \"borrowedBy\", url from bookshelf AS OuterQuery WHERE id = ?";
    List<Object> paramList = new ArrayList<Object>();
    paramList.add(bookId);
    System.out.println("[INFO] Requesting query execution");
    ResultSet rs = executeQuery(query, paramList);
    System.out.println("[INFO] Done query execution");
    return mapRow(rs);
  }


  private List<BookClass> mapRow(ResultSet rs)throws DaoException {
    try {
      List<BookClass> lb = new ArrayList<>();
      if (!rs.isBeforeFirst()) {
        return lb;
      }
      while (rs.next()) {
        BookClass book = new BookClass();
        book.setId(rs.getInt("ID"));
        book.setPrice(rs.getInt("PRICE"));
        book.setQuantity(rs.getInt("QUANTITY"));
        book.setTitle(rs.getString("TITLE"));
        book.setUrl(rs.getString("URL"));
        String arr = rs.getString("BORROWEDBY");
        String[] customers = splitStringIntoArray(arr, ",", new String[]{"\"", "}", "{"});
        book.setBorrowedBy(customers);
        lb.add(book);
      }
      return lb;
    } catch (SQLException e) {
      throw new DaoException(e.getMessage(),e.getCause(),e.getSQLState());
    }
  }

}
