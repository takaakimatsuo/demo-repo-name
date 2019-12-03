package com.example.demo.backend;

import com.example.demo.backend.custom.Dto.User;
import com.example.demo.backend.custom.Dto.ResponseUsers;
import com.example.demo.common.exceptions.DaoException;
import com.example.demo.common.exceptions.DbException;
import com.example.demo.common.exceptions.UserException;
import com.example.demo.data.access.JdbcUserDao;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

import static com.example.demo.backend.errorcodes.SqlErrorCodes.SQL_CODE_DUPLICATE_KEY_ERROR;
import static com.example.demo.common.messages.StaticUserMessages.USER_DUPLICATE;
import static com.example.demo.common.messages.StaticUserMessages.USER_INSERTED;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@SpringBootTest
public class MockitoUserBusinessLogicTest {

  @InjectMocks
  UserBusinessLogic dbl;

  @Mock
  JdbcUserDao udao;


  @DisplayName("ユーザの追加に関するテスト")
  @Nested
  class test9 {
    @DisplayName("ユーザの追加")
    @Test
    void addUser() throws DbException, DaoException, UserException {

      List<User> list = new ArrayList<>();
      User user = new User("Family","First","08011110000");
      list.add(user);

      when(udao.insertBookUser(user)).thenReturn(list);

      ResponseUsers actual = dbl.addUser(user);
      assertEquals(actual.getMessageHeader().getMessage(),USER_INSERTED);

    }


    @DisplayName("すでに登録されている電話番号を使い新たなユーザを追加")
    @Test
    void addUser_DUPLICATE() throws DbException, DaoException {

      List<User> list = new ArrayList<>();
      User user = new User("Family","First","08011110000");
      list.add(user);

      DaoException fakeOutput = new DaoException("This is fake");
      fakeOutput.setSqlCode(SQL_CODE_DUPLICATE_KEY_ERROR);

      UserException expected = new UserException(USER_DUPLICATE);

      when(udao.insertBookUser(user)).thenThrow(fakeOutput);

      Throwable e = assertThrows(expected.getClass(), () -> {dbl.addUser(user);});

    }


    @DisplayName("ユーザの追加時にDb例外")
    @Test
    void addUser_Dp() throws DbException, DaoException {

      List<User> list = new ArrayList<>();
      User user = new User("Family","First","08011110000");
      list.add(user);

      DbException fakeOutput = new DbException("This is fake");
      when(udao.insertBookUser(user)).thenThrow(fakeOutput);
      Throwable e = assertThrows(fakeOutput.getClass(), () -> {dbl.addUser(user);});

    }



    @DisplayName("ユーザの追加時にDao例外")
    @Test
    void addUser_Dao() throws DbException, DaoException {

      List<User> list = new ArrayList<>();
      User user = new User("Family","First","08011110000");
      list.add(user);

      DaoException fakeOutput = new DaoException("This is fake");
      when(udao.insertBookUser(user)).thenThrow(fakeOutput);
      Throwable e = assertThrows(fakeOutput.getClass(), () -> {dbl.addUser(user);});

    }
  }

}
