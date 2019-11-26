package com.example.demo.DataAccess;

import static com.example.demo.Backend.staticErrorMessages.StaticMessages.*;

import com.example.demo.Backend.CustomExceptions.*;
import com.example.demo.Backend.CustomObjects.BookClass;
import com.example.demo.DataAccess.CustomENUMs.BookStatus;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.stereotype.Component;



@SuppressWarnings("checkstyle:CommentsIndentation")
@Component("JdbcBookDao")
public final class JdbcBookDao implements BookDao, JdbcDao {


  @Override
  // Outputs:
  //      BOOK_NOT_EXISTING - Book not existing.
  //      BOOK_NOT_BORROWED_BY_THIS_USER - Book currently not borrowed by the user. The users action must be "borrow".
  //      BOOK_BORROWED_BY_THIS_USER - Book currently borrowed by the user. The users action must be either "return", or "lost".
  public BookStatus checkBookStatus(Integer bookId, String phoneNumber) throws DaoException, DbException {
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
  public boolean checkBookStockAvailability(Integer bookId) throws DaoException, DbException {
    boolean available = false;
    try {
      String query = "SELECT COALESCE(array_length(borrowedBy, 1), 0) < quantity as stock_available FROM bookshelf WHERE id = ?";
      List<Object> paramList = new ArrayList<Object>();
      paramList.add(bookId);
      ResultSet check = executeQuery(query, paramList);//Execute query.
      if (check.next()) {
        available = check.getBoolean("STOCK_AVAILABLE");
      }
    } catch (SQLException e ) {
      e.printStackTrace();
      throw new DaoException(e.getMessage());
    }
    return available;
  }




  @Override
  public void updateBook_borrowed(Integer bookId, String phoneNumber) throws DaoException, DbException {
    if (checkBookStockAvailability(bookId)) {
      String query = "UPDATE bookshelf SET borrowedBy = array_append(borrowedBy, ?) WHERE id = ?";
      List<Object> paramList = new ArrayList<Object>();
      paramList.add(phoneNumber);
      paramList.add(bookId);
      executeUpdate(query, paramList);
    } else {
      throw new DaoException(BOOK_NO_STOCK);
    }
  }




  @Override
  public void updateBook_returned(Integer bookId, String phoneNumber) throws DaoException, DbException {
    String query = "UPDATE bookshelf SET borrowedBy = array_remove(borrowedBy, ?) WHERE id = ?";
    List<Object> paramList = new ArrayList<Object>();
    paramList.add(phoneNumber);
    paramList.add(bookId);
    executeUpdate(query, paramList);
  }


  @Override
  //Reports a book as lost. This will decrease the book's quantity by 1, and delete the entire data from the bookshelf table when the quantity becomes less than 0.
  public void updateBook_lost(Integer bookId, String phoneNumber) throws DbException, DaoException {
    try {
      String query = "UPDATE bookshelf SET borrowedBy = array_remove(borrowedBy, ?), quantity = (quantity-1) WHERE id = ? RETURNING quantity";
      List<Object> paramList = new ArrayList<Object>();
      paramList.add(phoneNumber);
      paramList.add(bookId);
      ResultSet rs = executeQuery(query, paramList);
      if (rs.next()) {
        int quantity = rs.getInt("QUANTITY");
        if (quantity <= 0) {
          deleteBook(bookId);//Simply remove the book from the bookshelf
        }
      }
    } catch (SQLException e) {
      throw new DaoException(e.getMessage());
    }
  }

  @Override
  public int updateBook_data(Integer bookId, BookClass book) throws DaoException, DbException {
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
  public List<BookClass> insertBook(BookClass book) throws DaoException, DbException {
    String query = "INSERT INTO bookshelf(title,price,quantity,url) values(?, ?, ?, ?) RETURNING *";
    List<Object> paramList = new ArrayList<Object>();
    paramList.add(book.getTitle());
    paramList.add(book.getPrice());
    paramList.add(book.getQuantity());
    paramList.add(book.getUrl());
    ResultSet rs = executeQuery(query, paramList);
    return mapRow(rs);//Also takes care the case with no data found
  }



  @Override
  public int deleteBook(Integer bookId) throws DaoException,DbException {
    String query = "DELETE FROM bookshelf WHERE id = ?";
    List<Object> paramList = new ArrayList<Object>();
    paramList.add(bookId);
    return executeUpdate(query,paramList);
  }



  @Override
  public List<BookClass> getAllBooks() throws DaoException,DbException {
    String query = "SELECT id, title, price, quantity, (SELECT ARRAY( select familyName ||' '|| firstName FROM book_user u JOIN bookshelf b ON u.phoneNumber = ANY(b.borrowedBy) WHERE b.title = OuterQuery.title)) AS \"borrowedBy\", url FROM bookshelf AS OuterQuery ORDER BY id DESC;";
    System.out.println("[INFO] Requesting query execution");
    ResultSet rs = executeQuery(query);
    System.out.println("[INFO] Done query execution");
    return mapRow(rs);
  }


  public List<BookClass> getBook(Integer bookId) throws DaoException, DbException {
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
      throw new DaoException(e.getMessage());
    }
  }











//
//
//
//    private static String[] splitStringIntoArray(String str, String splitter, String[] replacer){
//        String[] adjusted_to_array = new String[0];
//        if(str==null){
//            return adjusted_to_array;
//        }
//        for(final String rep: replacer) {
//            str = str.replace(rep, "");
//        }
//        if(str.split(splitter)[0].compareTo("")!=0){
//            adjusted_to_array=str.split(splitter);
//        }
//        return adjusted_to_array;
//    }




}
