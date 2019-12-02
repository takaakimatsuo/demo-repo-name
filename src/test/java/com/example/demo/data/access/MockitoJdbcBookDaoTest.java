package com.example.demo.data.access;

import com.example.demo.backend.custom.Dto.BookClass;
import com.example.demo.backend.custom.exceptions.DaoException;
import com.example.demo.backend.custom.exceptions.DbException;
import org.junit.Ignore;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class JdbcBookDaoTest {


  //@Mock private Connection mockConnection = mock(Connection.class);
  //@Mock private PreparedStatement mockStatement = mock(PreparedStatement.class);
  //@Mock private ResultSet resultSet = mock(ResultSet.class);
  //@InjectMocks

  @Mock
  private PreparedStatement pstmt = mock(PreparedStatement.class);

  @Mock
  ResultSet mockResultSet = mock(ResultSet.class);

  @Mock
  Connection mockConnection = mock(Connection.class);

  @InjectMocks
  private static JdbcBookDao dao = mock(JdbcBookDao.class);






  @Test
  void checkBookStatus() {
  }

  @Test
  void checkBookStockAvailability() {
  }

  @Test
  void updateBook_borrowed() {
  }

  @Test
  void updateBook_returned() {
  }

  @Test
  void updateBook_lost() {
  }

  @Test
  void updateBook_data() {
  }

  @Test
  void insertBook() {
  }

  @Test
  void deleteBook() {
  }

  @DisplayName("Daoによる本の全検索に関するテスト")
  @Nested
  class getAllBooks {
    @Test
    @DisplayName("SQL実行の失敗")
    void getAllBooks1() throws DbException, DaoException{
      DaoException expected = new DaoException("This is fake");

      reset(dao);
      when(dao.getAllBooks()).thenCallRealMethod();
      when(dao.connectToDB()).thenCallRealMethod();
      when(dao.executeQuery(anyString(), anyList())).thenThrow(expected);

      Throwable e = assertThrows(expected.getClass(), () -> {
        dao.getBook(0);
        verify(dao, times(1)).getAllBooks();
        verify(dao, times(1)).connectToDB();
        verify(dao, times(1)).executeQuery(anyString(),anyList());
        verify(dao, times(1)).closeDB(isA(Connection.class));
      });
    }
  }

  @DisplayName("Daoによる本の検索に関するテスト")
  @Nested
  class getBook {
    @DisplayName("SQL実行の失敗")
    @Test
    public void getBook1() throws DbException, DaoException {
      DaoException expected = new DaoException("This is fake");

      reset(dao);

      when(dao.getBook(anyInt())).thenCallRealMethod();
      when(dao.connectToDB()).thenCallRealMethod();
      when(dao.executeQuery(anyString(), anyList())).thenThrow(expected);


      Throwable e = assertThrows(expected.getClass(), () -> {
        dao.getBook(0);
        verify(dao, times(1)).getBook(anyInt());
        verify(dao, times(1)).connectToDB();
        verify(dao, times(1)).executeQuery(anyString(),anyList());
        verify(dao, times(1)).closeDB(isA(Connection.class));
      });
    }

    @Ignore
    @DisplayName("データベースへの接続失敗")
    @Test
    public void getBook2() throws SQLException, DbException, DaoException {
      DbException expected = new DbException("This is fake");

      reset(dao);

      when(dao.connectToDB()).thenThrow(new DbException("This is fake"));
      when(dao.executeQuery(anyString(), anyList())).thenCallRealMethod();
      when(dao.getBook(anyInt())).thenCallRealMethod();

      Throwable e = assertThrows(expected.getClass(), () -> {
        dao.getBook(0);
        verify(dao, times(1)).getBook(anyInt());
        verify(dao, times(1)).executeQuery(anyString(),anyList());
        verify(dao, times(1)).connectToDB();
        verify(dao, times(1)).closeDB(isA(Connection.class));
      });
    }

    @Ignore
    @DisplayName("正しい本へのアクセス")
    @Test
    public void getBook3() throws SQLException, DbException, DaoException {


      reset(dao);
      when(dao.connectToDB()).thenCallRealMethod();
      when(dao.getBook(anyInt())).thenCallRealMethod();
      when(mockResultSet.getString("ID")).thenReturn("1");
      when(mockResultSet.getString("TITLE")).thenReturn("MOCK");
      when(mockResultSet.getString("QUANTITY")).thenReturn("1");
      when(mockResultSet.getString("PRICE")).thenReturn("1");
      when(mockResultSet.getString("URL")).thenReturn("https://mock.com");
      when(mockResultSet.getString("BORROWEDBY")).thenReturn("{\"08011110000\",\"08011110001\"}");
      when(dao.executeQuery(anyString(),anyList())).thenReturn(mockResultSet);

      dao.getBook(1);
      verify(dao, times(1)).getBook(anyInt());
      verify(dao, times(1)).executeQuery(anyString(),anyList());
      //verify(dao, times(1)).connectToDB(); //Mocked, thus skipped
      //verify(dao, times(1)).closeDB(isA(Connection.class)); //Mocked, thus skipped
    }
  }




}