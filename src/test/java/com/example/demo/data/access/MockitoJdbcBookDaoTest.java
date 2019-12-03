package com.example.demo.data.access;

import com.example.demo.common.exceptions.DaoException;
import com.example.demo.common.exceptions.DbException;
import org.junit.Ignore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.anyList;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class JdbcBookDaoTest {


  private PreparedStatement pstmt = mock(PreparedStatement.class);

  ResultSet mockResultSet = mock(ResultSet.class);

  Connection mockConnection = mock(Connection.class);

  @InjectMocks
  private static JdbcBookDao dao = mock(JdbcBookDao.class);

  @BeforeEach
  void reinitializeMock(){
    reset(dao);
  }



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

  @DisplayName("Daoによる本の削除に関するテスト")
  @Nested
  class insertBook {
    @Test
    void insertBook1() {
    }
  }

  @DisplayName("Daoによる本の削除に関するテスト")
  @Nested
  class deleteBook1 {
    @Test
    @DisplayName("SQL実行の失敗")
    void deleteBook1() throws DbException, DaoException {
      int bookId = 1;
      DaoException expected = new DaoException("This is fake");

      when(dao.deleteBook(anyInt())).thenCallRealMethod();
      when(dao.connectToDB()).thenReturn(mockConnection);
      when(dao.executeUpdate(anyString(),anyList())).thenThrow(expected);
      doNothing().when(dao).closeDB(isA(Connection.class));

      Throwable e = assertThrows(expected.getClass(), () -> {
        dao.deleteBook(bookId);
        verify(dao, times(1)).deleteBook(anyInt());
        verify(dao, times(1)).connectToDB();
        verify(dao, times(1)).executeUpdate(anyString(),anyList());
        verify(dao, times(1)).closeDB(isA(Connection.class));
      });
    }

    @Test
    @DisplayName("データベースへの接続失敗")
    void deleteBook2() throws DaoException {
      Integer bookId = 1;
      DaoException expected = new DbException("This is fake");

      when(dao.deleteBook(anyInt())).thenCallRealMethod();
      when(dao.connectToDB()).thenThrow(expected);
      when(dao.executeUpdate(anyString(),anyList())).thenCallRealMethod();
      doNothing().when(dao).closeDB(isA(Connection.class));

      Throwable e = assertThrows(expected.getClass(), () -> {
        dao.deleteBook(bookId);
        verify(dao, times(1)).deleteBook(anyInt());
        verify(dao, times(1)).connectToDB();
        verify(dao, times(1)).executeUpdate(anyString(),anyList());
        verify(dao, times(1)).closeDB(isA(Connection.class));
      });

    }


    @Test
    @DisplayName("正しい本の削除")
    void deleteBook3() throws DaoException {
      Integer bookId = 1;
      DaoException expected = new DbException("This is fake");

      when(dao.deleteBook(anyInt())).thenCallRealMethod();
      when(dao.connectToDB()).thenThrow(expected);
      when(dao.executeUpdate(anyString(),anyList())).thenCallRealMethod();
      doNothing().when(dao).closeDB(isA(Connection.class));

      Throwable e = assertThrows(expected.getClass(), () -> {
        dao.deleteBook(bookId);
        verify(dao, times(1)).deleteBook(anyInt());
        verify(dao, times(1)).connectToDB();
        verify(dao, times(1)).executeUpdate(anyString(),anyList());
        verify(dao, times(1)).closeDB(isA(Connection.class));
      });

    }
  }





  @DisplayName("Daoによる本の全検索に関するテスト")
  @Nested
  class getAllBooks {
    @Test
    @DisplayName("SQL実行の失敗")
    void getAllBooks1() throws DbException, DaoException {
      DaoException expected = new DaoException("This is fake");

      when(dao.getAllBooks()).thenCallRealMethod();
      when(dao.connectToDB()).thenCallRealMethod();
      when(dao.executeQuery(anyString())).thenThrow(expected);

      Throwable e = assertThrows(expected.getClass(), () -> {
        dao.getAllBooks();
        verify(dao, times(1)).getAllBooks();
        verify(dao, times(1)).connectToDB();
        verify(dao, times(1)).executeQuery(anyString());
        verify(dao, times(1)).closeDB(isA(Connection.class));
      });
    }

    @Test
    @DisplayName("データベースへの接続失敗")
    void getAllBooks2() throws DbException, DaoException{
      DbException expected = new DbException("This is fake");

      when(dao.getAllBooks()).thenCallRealMethod();
      when(dao.connectToDB()).thenThrow(expected);
      when(dao.executeQuery(anyString())).thenCallRealMethod();

      Throwable e = assertThrows(expected.getClass(), () -> {
        dao.getAllBooks();
        verify(dao, times(1)).getAllBooks();
        verify(dao, times(1)).connectToDB();
        verify(dao, times(1)).executeQuery(anyString());
        verify(dao, times(1)).closeDB(isA(Connection.class));
      });
    }

    @Ignore
    @DisplayName("正しい本へのアクセス")
    @Test
    public void getAllBooks3() throws SQLException, DbException, DaoException {

      when(dao.connectToDB()).thenCallRealMethod();
      when(dao.getAllBooks()).thenCallRealMethod();
      when(mockResultSet.getString("ID")).thenReturn("1");
      when(mockResultSet.getString("TITLE")).thenReturn("MOCK");
      when(mockResultSet.getString("QUANTITY")).thenReturn("1");
      when(mockResultSet.getString("PRICE")).thenReturn("1");
      when(mockResultSet.getString("URL")).thenReturn("https://mock.com");
      when(mockResultSet.getString("BORROWEDBY")).thenReturn("{\"08011110000\",\"08011110001\"}");
      when(dao.executeQuery(anyString())).thenReturn(mockResultSet);

      dao.getAllBooks();
      verify(dao, times(1)).getAllBooks();
      verify(dao, times(1)).executeQuery(anyString());
     }
  }











  @DisplayName("Daoによる本の検索に関するテスト")
  @Nested
  class getBook {
    @DisplayName("SQL実行の失敗")
    @Test
    public void getBook1() throws DbException, DaoException {
      DaoException expected = new DaoException("This is fake");

      when(dao.getBook(anyInt())).thenCallRealMethod();
      when(dao.connectToDB()).thenReturn(mockConnection);
      when(dao.executeQuery(anyString(), anyList())).thenThrow(expected);
      doNothing().when(dao).closeDB(isA(Connection.class));

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

      when(dao.connectToDB()).thenReturn(mockConnection);
      when(dao.getBook(anyInt())).thenCallRealMethod();
      when(mockResultSet.getString("ID")).thenReturn("1");
      when(mockResultSet.getString("TITLE")).thenReturn("MOCK");
      when(mockResultSet.getString("QUANTITY")).thenReturn("1");
      when(mockResultSet.getString("PRICE")).thenReturn("1");
      when(mockResultSet.getString("URL")).thenReturn("https://mock.com");
      when(mockResultSet.getString("BORROWEDBY")).thenReturn("{\"08011110000\",\"08011110001\"}");
      when(dao.executeQuery(anyString(),anyList())).thenReturn(mockResultSet);
      doNothing().when(dao).closeDB(isA(Connection.class));

      dao.getBook(1);
      verify(dao, times(1)).getBook(anyInt());
      verify(dao, times(1)).executeQuery(anyString(),anyList());
    }
  }




}