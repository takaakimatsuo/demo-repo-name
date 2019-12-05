package com.example.demo.backend;

import com.example.demo.backend.custom.Dto.User;
import com.example.demo.backend.custom.Dto.ResponseUsers;
import com.example.demo.common.enums.Messages;
import com.example.demo.common.exceptions.DaoException;
import com.example.demo.common.exceptions.DbException;
import com.example.demo.common.exceptions.UserBusinessLogicException;
import com.example.demo.data.access.JdbcUserDao;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import java.util.ArrayList;
import java.util.List;

import static com.example.demo.backend.errorcodes.SqlErrorCodes.UNIQUE_VIOLATION;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@SpringBootTest
public class MockitoUserBusinessLogicTest {

  @InjectMocks
  UserBusinessLogic dbl;

  @Mock
  JdbcUserDao udao;

  User dummyUser = User.builder()
    .firstName("First")
    .familyName("Family")
    .phoneNumber("08011110000")
    .build();


  @DisplayName("ユーザの追加に関するテスト")
  @Nested
  class test9 {
    @DisplayName("ユーザの追加")
    @Test
    void addUser() throws DaoException, UserBusinessLogicException {

      when(udao.insertBookUser(dummyUser)).thenReturn(1);

      ResponseUsers actual = dbl.addUser(dummyUser);
      assertEquals(actual.getMessageHeader().getMessage(), Messages.USER_INSERTED);

    }


    @DisplayName("すでに登録されている電話番号を使って新たなユーザを追加")
    @Test
    void addUser_DUPLICATE() throws DbException, DaoException {


      DaoException fakeOutput = new DaoException("This is fake");
      fakeOutput.setSqlCode(UNIQUE_VIOLATION);

      UserBusinessLogicException expected = new UserBusinessLogicException(Messages.USER_DUPLICATE);

      when(udao.insertBookUser(dummyUser)).thenThrow(fakeOutput);

      Throwable e = assertThrows(expected.getClass(), () -> {dbl.addUser(dummyUser);});

    }


    @DisplayName("ユーザの追加時にDb例外")
    @Test
    void addUser_Dp() throws DaoException {

      DbException fakeOutput = new DbException("This is fake");
      when(udao.insertBookUser(dummyUser)).thenThrow(fakeOutput);
      Throwable e = assertThrows(fakeOutput.getClass(), () -> {dbl.addUser(dummyUser);});

    }



    @DisplayName("ユーザの追加時にDao例外")
    @Test
    void addUser_Dao() throws DaoException {

      DaoException fakeOutput = new DaoException("This is fake");
      when(udao.insertBookUser(dummyUser)).thenThrow(fakeOutput);
      Throwable e = assertThrows(fakeOutput.getClass(), () -> {dbl.addUser(dummyUser);});

    }
  }

}
