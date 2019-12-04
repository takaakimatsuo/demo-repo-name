package com.example.demo.backend;


import static com.example.demo.backend.errorcodes.SqlErrorCodes.SQL_CODE_DUPLICATE_KEY_ERROR;




import com.example.demo.backend.custom.Dto.User;
import com.example.demo.backend.custom.Dto.ResponseUsers;
import com.example.demo.common.enums.Messages;
import com.example.demo.common.exceptions.DaoException;
import com.example.demo.common.exceptions.UserBusinessLogicException;
import com.example.demo.data.access.JdbcUserDao;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;


@Component
public class UserBusinessLogic {


  @Autowired
  private JdbcUserDao udao;
  private ResponseUsers ures;


  public ResponseUsers addUser(User user) throws DaoException, UserBusinessLogicException {
    ures = ResponseUsers.builder().build();
    try {
      List<User> users = udao.insertBookUser(user);
      ures.setUsers(users);
      ures.getMessageHeader().setMessage(Messages.USER_INSERTED);
      return ures;
    } catch (DaoException e) {
      if (e.getSqlCode().equals(SQL_CODE_DUPLICATE_KEY_ERROR)) {
        throw new UserBusinessLogicException(Messages.USER_DUPLICATE);
      }
      throw e;
    }
  }


  public ResponseUsers removeUser(Integer userId) throws DaoException, UserBusinessLogicException {
    ures = ResponseUsers.builder().build();
    //Even better, must check whether the user is not borrowing any book.

    int update = udao.deleteBookUser(userId);
    if (update == 0) {
      throw new UserBusinessLogicException(Messages.USER_NOT_EXISTING);
    } else {
      ures.getMessageHeader().setMessage(Messages.USER_DELETED);
    }
    return ures;
  }


}
