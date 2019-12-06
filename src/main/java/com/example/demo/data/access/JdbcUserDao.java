package com.example.demo.data.access;

import com.example.demo.backend.dto.User;
import com.example.demo.common.exceptions.DaoException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.example.demo.data.access.interfaces.UserDao;
import org.springframework.stereotype.Repository;



@Repository("JdbcUserDao")
public class JdbcUserDao extends JdbcDao implements UserDao {




  List<User> copyBookUserFromResultSet(ResultSet rs)throws DaoException {
    try {
      List<User> lu = new ArrayList<>();
      if (!rs.isBeforeFirst()) {
        return lu;
      }
      while (rs.next()) {
        User user = User.builder()
          .firstName(rs.getString("firstName"))
          .familyName(rs.getString("familyName"))
          .phoneNumber(rs.getString("phoneNumber"))
          .build();
        lu.add(user);
      }
      return lu;
    } catch (SQLException e) {
      throw new DaoException(e.getMessage(),e.getCause(), e.getSQLState());
    }
  }

  private static List<String> copyBookTitlesFromResultSet(ResultSet rs)throws DaoException {
    try {
      List<String> lu = new ArrayList<>();
      if (!rs.isBeforeFirst()) {
        return lu;
      }
      while (rs.next()) {
        lu.add(rs.getString("Title"));
      }
      return lu;
    } catch (SQLException e) {
      throw new DaoException(e.getMessage(),e.getCause(), e.getSQLState());
    }
  }

  @Override
  public List<User> getAllUsers() throws DaoException {
    String query = "SELECT * FROM book_user";
    ResultSet rs =  executeQuery(query);
    return copyBookUserFromResultSet(rs);
  }



  @Override
  public int insertBookUser(User book) throws DaoException {
    String query = "INSERT INTO book_user(familyName,firstName,phoneNumber) values(?, ?, ?)";
    List<Object> paramList = new ArrayList<>();
    paramList.add(book.getFamilyName());
    paramList.add(book.getFirstName());
    paramList.add(book.getPhoneNumber());
    return executeUpdate(query, paramList);
  }

  @Override
  public int deleteBookUser(Integer userId) throws DaoException {
    String query = "DELETE FROM book_user WHERE id = ?";
    List<Object> paramList = new ArrayList<>();
    paramList.add(userId);
    return executeUpdate(query,paramList);
  }

  @Override
  public List<String> getBorrowedBookTitles(Integer userId) throws DaoException {
    String query = "select u.id, b.title from book_user u JOIN bookshelf b ON u.phoneNumber = ANY(b.borrowedBy) where u.id = ?";
    List<Object> paramList = new ArrayList<>();
    paramList.add(userId);
    ResultSet rs =  executeQuery(query,paramList);
    return copyBookTitlesFromResultSet(rs);
  }


}
