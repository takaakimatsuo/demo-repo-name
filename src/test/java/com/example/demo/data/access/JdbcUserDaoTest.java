package com.example.demo.data.access;

import com.example.demo.backend.dto.User;
import com.example.demo.common.exceptions.DaoException;
import org.junit.jupiter.api.*;
import org.mockito.InjectMocks;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;


class JdbcUserDaoTest {

  private ResultSet mockResultSet = mock(ResultSet.class);

  private Connection mockConnection = mock(Connection.class);

  @InjectMocks
  private static JdbcUserDao udao = mock(JdbcUserDao.class);

  private int dummyUserId = 1;

  private User dummyUser1 = User.builder()
    .phoneNumber("08011110000")
    .familyName("A")
    .firstName("B")
    .build();

  private User dummyUser2 = User.builder()
    .phoneNumber("08011110001")
    .familyName("C")
    .firstName("D")
    .build();

  @BeforeEach
  void reinitializeMock(){
    reset(udao);
  }

  @Nested
  @DisplayName("Daoによるユーザの全検索に関するテスト")
  class getAllUsers {
    @Test
    @DisplayName("正しいユーザの全検索")
    void getAllUsers1() throws DaoException, SQLException {
      List<User> expected = new ArrayList<>();
      expected.add(dummyUser1);

      when(udao.getAllUsers()).thenCallRealMethod();
      when(mockResultSet.getString("firstName")).thenReturn(dummyUser1.getFamilyName());
      when(mockResultSet.getString("familyName")).thenReturn(dummyUser1.getFirstName());
      when(mockResultSet.getString("phoneNumber")).thenReturn(dummyUser1.getPhoneNumber());
      when(udao.executeQuery(anyString())).thenReturn(mockResultSet);
      when(udao.copyBookUserFromResultSet(any(ResultSet.class))).thenReturn(expected);
      List<User> actual = udao.getAllUsers();

      verify(udao, times(1)).getAllUsers();
      assertArrayEquals(expected.toArray(),actual.toArray());
    }

  }

  @Nested
  @DisplayName("Daoによるユーザの追加に関するテスト")
  class insertBookUser {
    @Test
    void insertBookUser() {
    }
  }

  @Test
  void deleteBookUser() {
  }

  @Test
  void getBorrowedBookTitles() {
  }
}