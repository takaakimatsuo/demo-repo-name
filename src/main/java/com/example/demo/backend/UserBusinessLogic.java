package com.example.demo.backend;


import static com.example.demo.backend.errorcodes.SqlErrorCodes.SQL_CODE_DUPLICATE_KEY_ERROR;
import static com.example.demo.backend.messages.StaticUserMessages.USER_NOT_EXISTING;
import static com.example.demo.backend.messages.StaticUserMessages.USER_DELETED;
import static com.example.demo.backend.messages.StaticUserMessages.USER_DUPLICATE;
import static com.example.demo.backend.messages.StaticUserMessages.USER_INSERTED;



import com.example.demo.backend.custom.Dto.User;
import com.example.demo.backend.custom.Dto.ResponseUsers;
import com.example.demo.backend.custom.exceptions.DaoException;
import com.example.demo.backend.custom.exceptions.DbException;
import com.example.demo.backend.custom.exceptions.UserException;
import com.example.demo.data.access.JdbcUserDao;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class UserBusinessLogic {


  @Autowired
  private JdbcUserDao udao;
  private ResponseUsers ures;


  public ResponseUsers addUser(User user) throws DaoException, DbException, UserException {
    ures = new ResponseUsers();
    try {
      List<User> users = udao.insertBookUser(user);
      ures.setUsers(users);
      ures.getResponseHeader().setMessage(USER_INSERTED);
      return ures;
    } catch (DaoException e) {
      if (e.getSqlCode().equals(SQL_CODE_DUPLICATE_KEY_ERROR)) {
        throw new UserException(USER_DUPLICATE);
      }
      throw e;
    }
  }


  public ResponseUsers removeUser(Integer userId) throws DaoException, DbException, UserException {
    ures = new ResponseUsers();

    //TODO
    //Must check whether the user is not borrowing any book.

    int update = udao.deleteBookUser(userId);
    if (update == 0) {
      throw new UserException(USER_NOT_EXISTING);
    } else {
      ures.getResponseHeader().setMessage(USER_DELETED);
    }
    return ures;
  }


}
