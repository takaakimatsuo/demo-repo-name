package com.example.demo.backend;


import static com.example.demo.backend.errorcodes.SqlErrorCodes.UNIQUE_VIOLATION;




import com.example.demo.backend.dto.User;
import com.example.demo.backend.dto.ResponseUsers;
import com.example.demo.common.enums.Messages;
import com.example.demo.common.exceptions.DaoException;
import com.example.demo.common.exceptions.UserBusinessLogicException;
import com.example.demo.data.access.JdbcUserDao;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class UserBusinessLogic {
  private ResponseUsers ures;

  @Autowired
  private JdbcUserDao udao;


  public ResponseUsers getAllUsers() throws DaoException, UserBusinessLogicException {
    ures =  ResponseUsers.builder().build();
    List<User> users = udao.getAllUsers();
    if (users.isEmpty()) {
      throw new UserBusinessLogicException(Messages.USER_NOT_EXISTING);
    } else {
      ures.getMessageHeader().setMessage(Messages.USER_FOUND);
    }
    ures.setUsers(users);
    return ures;
  }


  public ResponseUsers addUser(User user) throws DaoException, UserBusinessLogicException {
    ures = ResponseUsers.builder().build();
    try {
      int updated = udao.insertBookUser(user);
      if (updated == 0) {
        throw new UserBusinessLogicException(Messages.UPDATE_FAILED_USER);
      } else {
        ures.getMessageHeader().setMessage(Messages.USER_INSERTED);
      }
      return ures;
    } catch (DaoException e) {
      if (e.getSqlCode().equals(UNIQUE_VIOLATION)) {
        throw new UserBusinessLogicException(Messages.USER_DUPLICATE);
      }
      throw e;
    }
  }


  public ResponseUsers removeUser(Integer userId) throws DaoException, UserBusinessLogicException {
    ures = ResponseUsers.builder().build();

    List<String> booksBorrowed = udao.getBorrowedBookTitles(userId);
    if(booksBorrowed.isEmpty()) {
      int update = udao.deleteBookUser(userId);
      if (update == 0) {
        throw new UserBusinessLogicException(Messages.USER_NOT_EXISTING);
      } else {
        ures.getMessageHeader().setMessage(Messages.USER_DELETED);
      }
    } else {
      throw new UserBusinessLogicException(Messages.USER_CANNOT_BE_DELETED);
    }

    return ures;
  }


}
