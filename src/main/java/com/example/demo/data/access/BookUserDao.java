package com.example.demo.data.access;

import com.example.demo.backend.custom.myexceptions.DaoException;
import com.example.demo.backend.custom.myexceptions.DbException;
import com.example.demo.backend.custom.objects.BookUser;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


@Component
public class BookUserDao implements JdbcDao {




  private static List<BookUser> copyBookUserFromResultSet(ResultSet rs)throws SQLException {
    List<BookUser> lu = new ArrayList<>();
    if (!rs.isBeforeFirst()) {
      return lu;
    }
    while (rs.next()) {
      BookUser user = new BookUser();
      user.setFamilyName(rs.getString("familyName"));
      user.setFirstName(rs.getString("firstName"));
      user.setFamilyName(rs.getString("phoneNumber"));
      lu.add(user);
    }
    return lu;
  }

  public List<BookUser> insertBookUser(BookUser book) throws DaoException, DbException {
    String query = "INSERT INTO book_user(familyName,firstName,phoneNumber) values(?, ?, ?) RETURNING *";
    List<Object> paramList = new ArrayList<Object>();
    paramList.add(book.getFamilyName());
    paramList.add(book.getFirstName());
    paramList.add(book.getPhoneNumber());
    try {
      ResultSet rs = executeQuery(query, paramList);
      return copyBookUserFromResultSet(rs);//Also takes care the case with no data found
    } catch (SQLException e) {
      throw new DaoException(e.getMessage());
    }
  }


}
