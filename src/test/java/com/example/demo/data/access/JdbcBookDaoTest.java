package com.example.demo.data.access;


import com.example.demo.backend.dto.Book;
import com.example.demo.common.exceptions.DaoException;
import com.example.demo.common.exceptions.DbException;
import org.junit.Ignore;
import org.junit.jupiter.api.*;
import org.mockito.InjectMocks;


import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;


import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.anyList;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class JdbcBookDaoTest {

  private ResultSet mockResultSet = mock(ResultSet.class);

  private Connection mockConnection = mock(Connection.class);

  @InjectMocks
  private static JdbcBookDao dao = mock(JdbcBookDao.class);

  private int dummyBookId = 1;
  private String dummyPhoneNumber = "08000001111";
  Book dummyBook1 = Book.builder()
    .title("Title")
    .price(1)
    .url("https://hi.com")
    .quantity(1)
    .build();

  @BeforeEach
  void reinitializeMock(){
    reset(dao);
  }


  @DisplayName("Daoによる本の貸し出しに関するテスト")
  @Nested
  class updateBook_borrowed {
    @DisplayName("SQL実行の失敗")
    @Test
    void updateBook_borrowed() throws DaoException {
      
      DaoException expected = new DaoException("This is fake");
      when(dao.updateBookBorrowed(anyInt(),anyString())).thenCallRealMethod();
      when(dao.executeUpdate(anyString(),anyList())).thenThrow(expected);

      Throwable e = assertThrows(expected.getClass(), () -> {
        dao.updateBookBorrowed(dummyBookId, dummyPhoneNumber);
      });
      verify(dao, times(1)).updateBookBorrowed(anyInt(),anyString());
      verify(dao, times(1)).executeUpdate(anyString(),anyList());
    }
    @DisplayName("データベースへの接続失敗")
    @Test
    void updateBook_borrowed2() throws DaoException {
      
      DaoException expected = new DbException("This is fake");
      when(dao.updateBookBorrowed(anyInt(),anyString())).thenCallRealMethod();
      when(dao.connectToDB()).thenThrow(expected);
      when(dao.executeUpdate(anyString(),anyList())).thenCallRealMethod();

      Throwable e = assertThrows(expected.getClass(), () -> {
        dao.updateBookBorrowed(dummyBookId, dummyPhoneNumber);
      });
      verify(dao, times(1)).updateBookBorrowed(anyInt(),anyString());
      verify(dao, times(1)).executeUpdate(anyString(),anyList());
    }

    @DisplayName("正しい本の置き換え")
    @Test
    void updateBook_borrowed3() throws DaoException {
      
      DaoException expected = new DbException("This is fake");
      when(dao.updateBookBorrowed(anyInt(),anyString())).thenCallRealMethod();
      when(dao.executeUpdate(anyString(),anyList())).thenReturn(1);

      dao.updateBookBorrowed(dummyBookId, dummyPhoneNumber);
      verify(dao, times(1)).updateBookBorrowed(anyInt(),anyString());
      verify(dao, times(1)).executeUpdate(anyString(),anyList());
    }
  }

  @DisplayName("Daoによる本の返却に関するテスト")
  @Nested
  class updateBook_returned {
    @DisplayName("SQL実行の失敗")
    @Test
    void updateBook_returned1() throws DaoException {
      
      DaoException expected = new DaoException("This is fake");
      when(dao.updateBookReturned(anyInt(),anyString())).thenCallRealMethod();
      when(dao.executeUpdate(anyString(),anyList())).thenThrow(expected);

      Throwable e = assertThrows(expected.getClass(), () -> {
        dao.updateBookReturned(dummyBookId, dummyPhoneNumber);
      });
      verify(dao, times(1)).updateBookReturned(anyInt(),anyString());
      verify(dao, times(1)).executeUpdate(anyString(),anyList());
    }
    @DisplayName("データベースへの接続失敗")
    @Test
    void updateBook_returned2() throws DaoException {
      
      DaoException expected = new DbException("This is fake");
      when(dao.updateBookReturned(anyInt(),anyString())).thenCallRealMethod();
      when(dao.connectToDB()).thenThrow(expected);
      when(dao.executeUpdate(anyString(),anyList())).thenCallRealMethod();

      Throwable e = assertThrows(expected.getClass(), () -> {
        dao.updateBookReturned(dummyBookId, dummyPhoneNumber);
      });
      verify(dao, times(1)).updateBookReturned(anyInt(),anyString());
      verify(dao, times(1)).executeUpdate(anyString(),anyList());
    }

    @DisplayName("正しい本の置き換え")
    @Test
    void updateBook_returned3() throws DaoException {

      when(dao.updateBookReturned(anyInt(),anyString())).thenCallRealMethod();
      when(dao.executeUpdate(anyString(),anyList())).thenReturn(1);

      dao.updateBookReturned(dummyBookId, dummyPhoneNumber);
      verify(dao, times(1)).updateBookReturned(anyInt(),anyString());
      verify(dao, times(1)).executeUpdate(anyString(),anyList());
    }
  }



  @DisplayName("Daoによる本の紛失に関するテスト")
  @Nested
  class updateBook_lost {
    @DisplayName("SQL実行の失敗")
    @Test
    void updateBook_lost1() throws DaoException {
      
      DaoException expected = new DaoException("This is fake");
      when(dao.updateBookLost(anyInt(),anyString())).thenCallRealMethod();
      when(dao.executeUpdate(anyString(),anyList())).thenThrow(expected);

      Throwable e = assertThrows(expected.getClass(), () -> {
        dao.updateBookLost(dummyBookId, dummyPhoneNumber);
      });
      verify(dao, times(1)).updateBookLost(anyInt(),anyString());
      verify(dao, times(1)).executeUpdate(anyString(),anyList());
    }
    @DisplayName("データベースへの接続失敗")
    @Test
    void updateBook_lost2() throws DaoException {
      
      DaoException expected = new DbException("This is fake");
      when(dao.updateBookLost(anyInt(),anyString())).thenCallRealMethod();
      when(dao.connectToDB()).thenThrow(expected);
      when(dao.executeUpdate(anyString(),anyList())).thenCallRealMethod();

      Throwable e = assertThrows(expected.getClass(), () -> {
        dao.updateBookLost(dummyBookId, dummyPhoneNumber);
      });
      verify(dao, times(1)).updateBookLost(anyInt(),anyString());
      verify(dao, times(1)).executeUpdate(anyString(),anyList());
    }

    @DisplayName("正しい本の置き換え")
    @Test
    void updateBook_lost3() throws DaoException {

      when(dao.updateBookLost(anyInt(),anyString())).thenCallRealMethod();
      when(dao.executeUpdate(anyString(),anyList())).thenReturn(1);

      dao.updateBookLost(dummyBookId, dummyPhoneNumber);
      verify(dao, times(1)).updateBookLost(anyInt(),anyString());
      verify(dao, times(1)).executeUpdate(anyString(),anyList());
    }
  }

  @DisplayName("Daoによる本の置き換えに関するテスト")
  @Nested
  class updateBook_data {
    @DisplayName("SQL実行の失敗")
    @Test
    void updateBook_data1() throws DaoException {

      
      DaoException expected = new DaoException("This is fake");
      when(dao.replaceBook(anyInt(),any(Book.class))).thenCallRealMethod();
      when(dao.executeUpdate(anyString(),anyList())).thenThrow(expected);

      Throwable e = assertThrows(expected.getClass(), () -> {
        dao.replaceBook(dummyBookId, dummyBook1);
      });
      verify(dao, times(1)).replaceBook(anyInt(),any(Book.class));
      verify(dao, times(1)).executeUpdate(anyString(),anyList());
    }
    @DisplayName("データベースへの接続失敗")
    @Test
    void updateBook_data2() throws DaoException {

      
      DaoException expected = new DbException("This is fake");
      when(dao.replaceBook(anyInt(),any(Book.class))).thenCallRealMethod();
      when(dao.connectToDB()).thenThrow(expected);
      when(dao.executeUpdate(anyString(),anyList())).thenCallRealMethod();

      Throwable e = assertThrows(expected.getClass(), () -> {
        dao.replaceBook(dummyBookId, dummyBook1);
      });
      verify(dao, times(1)).replaceBook(anyInt(),any(Book.class));
      verify(dao, times(1)).executeUpdate(anyString(),anyList());
    }

    @DisplayName("正しい本の置き換え")
    @Test
    void updateBook_data3() throws DaoException {

      
      DaoException expected = new DbException("This is fake");
      when(dao.replaceBook(anyInt(),any(Book.class))).thenCallRealMethod();
      when(dao.executeUpdate(anyString(),anyList())).thenReturn(1);

      dao.replaceBook(dummyBookId, dummyBook1);
      verify(dao, times(1)).replaceBook(anyInt(),any(Book.class));
      verify(dao, times(1)).executeUpdate(anyString(),anyList());
    }
  }







  @DisplayName("Daoによる本の追加に関するテスト")
  @Nested
  class insertBook {
    @Test
    @DisplayName("SQL実行の失敗")
    void insertBook1() throws DaoException {
      

      DaoException expected = new DaoException("This is fake");
      when(dao.insertBook(any(Book.class))).thenCallRealMethod();
      when(dao.executeUpdate(anyString(),anyList())).thenThrow(expected);

      Throwable e = assertThrows(expected.getClass(), () -> {
        dao.insertBook(dummyBook1);
      });
      verify(dao, times(1)).insertBook(any(Book.class));
      verify(dao, times(1)).executeUpdate(anyString(),anyList());
    }

    @Test
    @DisplayName("データベースへの接続失敗")
    void insertBook2() throws DaoException {
      

      DaoException expected = new DbException("This is fake");
      when(dao.insertBook(any(Book.class))).thenCallRealMethod();
      when(dao.connectToDB()).thenThrow(expected);
      when(dao.executeUpdate(anyString(),anyList())).thenCallRealMethod();

      Throwable e = assertThrows(expected.getClass(), () -> {
        dao.insertBook(dummyBook1);
      });
      verify(dao, times(1)).insertBook(any(Book.class));
      verify(dao, times(1)).connectToDB();
      verify(dao, times(1)).executeUpdate(anyString(),anyList());
    }

    @Test
    @DisplayName("正しい本の追加")
    void insertBook3() throws DaoException {

      when(dao.insertBook(any(Book.class))).thenCallRealMethod();
      when(dao.executeUpdate(anyString(),anyList())).thenReturn(1);

      dao.insertBook(dummyBook1);
      verify(dao, times(1)).insertBook(any(Book.class));
      verify(dao, times(1)).executeUpdate(anyString(),anyList());
    }
  }

  @DisplayName("Daoによる本の削除に関するテスト")
  @Nested
  class deleteBook1 {
    @Test
    @DisplayName("SQL実行の失敗")
    void deleteBook1() throws DaoException {

      DaoException expected = new DaoException("This is fake");

      when(dao.deleteBook(anyInt())).thenCallRealMethod();
      when(dao.executeUpdate(anyString(),anyList())).thenThrow(expected);

      Throwable e = assertThrows(expected.getClass(), () -> {
        dao.deleteBook(dummyBookId);
      });
      verify(dao, times(1)).deleteBook(anyInt());
      verify(dao, times(1)).executeUpdate(anyString(),anyList());
    }

    @Test
    @DisplayName("データベースへの接続失敗")
    void deleteBook2() throws DaoException {

      DaoException expected = new DbException("This is fake");

      when(dao.deleteBook(anyInt())).thenCallRealMethod();
      when(dao.connectToDB()).thenThrow(expected);
      when(dao.executeUpdate(anyString(),anyList())).thenCallRealMethod();

      Throwable e = assertThrows(expected.getClass(), () -> {
        dao.deleteBook(dummyBookId);
      });
      verify(dao, times(1)).deleteBook(anyInt());
      verify(dao, times(1)).connectToDB();
      verify(dao, times(1)).executeUpdate(anyString(),anyList());
    }


    @Test
    @DisplayName("正しい本の削除")
    void deleteBook3() throws DaoException {

      when(dao.deleteBook(anyInt())).thenCallRealMethod();
      when(dao.executeUpdate(anyString(),anyList())).thenReturn(1);

      dao.deleteBook(dummyBookId);
      verify(dao, times(1)).deleteBook(anyInt());
      verify(dao, times(1)).executeUpdate(anyString(),anyList());
    }
  }





  @DisplayName("Daoによる本の全検索に関するテスト")
  @Nested
  class getAllBooks {
    @Test
    @DisplayName("SQL実行の失敗")
    void getAllBooks1() throws DaoException {
      DaoException expected = new DaoException("This is fake");

      when(dao.getAllBooks()).thenCallRealMethod();
      when(dao.executeQuery(anyString())).thenThrow(expected);

      Throwable e = assertThrows(expected.getClass(), () -> {
        dao.getAllBooks();
      });
      verify(dao, times(1)).getAllBooks();
      verify(dao, times(1)).executeQuery(anyString());
    }

    @Test
    @DisplayName("データベースへの接続失敗")
    void getAllBooks2() throws DaoException{
      DbException expected = new DbException("This is fake");

      when(dao.getAllBooks()).thenCallRealMethod();
      when(dao.connectToDB()).thenThrow(expected);
      when(dao.executeQuery(anyString())).thenCallRealMethod();

      Throwable e = assertThrows(expected.getClass(), () -> {
        dao.getAllBooks();
      });
      verify(dao, times(1)).getAllBooks();
      verify(dao, times(1)).connectToDB();
      verify(dao, times(1)).executeQuery(anyString());
    }

    @Ignore
    @DisplayName("正しい本へのアクセス")
    @Test
    public void getAllBooks3() throws SQLException, DaoException {

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
    public void getBook1() throws DaoException {
      DaoException expected = new DaoException("This is fake");

      when(dao.getBook(anyInt())).thenCallRealMethod();
      when(dao.executeQuery(anyString(), anyList())).thenThrow(expected);

      Throwable e = assertThrows(expected.getClass(), () -> {
        dao.getBook(0);
      });
      verify(dao, times(1)).getBook(anyInt());
      verify(dao, times(1)).executeQuery(anyString(),anyList());
    }

    @Ignore
    @DisplayName("データベースへの接続失敗")
    @Test
    public void getBook2() throws DaoException {
      DbException expected = new DbException("This is fake");

      when(dao.connectToDB()).thenThrow(new DbException("This is fake"));
      when(dao.executeQuery(anyString(), anyList())).thenCallRealMethod();
      when(dao.getBook(anyInt())).thenCallRealMethod();


      Throwable e = assertThrows(expected.getClass(), () -> {
        dao.getBook(0);
      });
      verify(dao, times(1)).getBook(anyInt());
      verify(dao, times(1)).executeQuery(anyString(),anyList());
      verify(dao, times(1)).connectToDB();
    }

    @Ignore
    @DisplayName("正しい本へのアクセス")
    @Test
    public void getBook3() throws SQLException, DaoException {

      when(dao.connectToDB()).thenReturn(mockConnection);
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
    }
  }




}