package com.example.demo.data.access;


import com.example.demo.backend.custom.Dto.Book;
import com.example.demo.common.exceptions.DaoException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import java.util.ArrayList;
import java.util.List;

import static com.example.demo.DemoApplication.logger;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
class SpringBookDaoTest {

  @InjectMocks
  private static SpringBookDao sdao = mock(SpringBookDao.class);

  @Mock
  private static JdbcTemplate jdbcTemplate;

  @BeforeEach
  void reinitializeMock(){
    reset(sdao);
    reset(jdbcTemplate);
  }

  private String[] dummyBorrowers = {"08011110000"};

  private Book dummyBook1 = Book.builder()
    .id(1)
    .price(100)
    .title("Hello")
    .quantity(1)
    .url("https://example.com")
    .borrowedBy(dummyBorrowers)
    .build();

  private Book dummyBook2 = Book.builder()
    .id(1)
    .price(2000)
    .title("World")
    .quantity(3)
    .url("https://example.com")
    .build();

  private Integer dummyBookId = 1;

  @DisplayName("SpringDaoの本の全検索に関するテスト")
  @Nested
  class getAllBooks {
    @DisplayName("正しい本の全検索")
    @Test
    void getAllBooks1() throws DaoException {
      List<Book> expected = new ArrayList<>();


      expected.add(dummyBook1);
      expected.add(dummyBook2);

      when(jdbcTemplate.query(anyString(), any(RowMapper.class))).thenReturn(expected);
      when(sdao.getAllBooks()).thenCallRealMethod();
      List<Book> actual = sdao.getAllBooks();
      assertArrayEquals(expected.toArray(),actual.toArray());
      verify(sdao, times(1)).getAllBooks();
      verify(jdbcTemplate, times(1)).query(anyString(), any(RowMapper.class));
    }

    @DisplayName("正しい本の全検索 検索がヒットしない場合")
    @Test
    void getAllBooks2() throws DaoException {
      List<Book> expected = new ArrayList<>();

      when(jdbcTemplate.query(anyString(), any(RowMapper.class))).thenReturn(expected);
      when(sdao.getAllBooks()).thenCallRealMethod();
      List<Book> actual = sdao.getAllBooks();
      assertArrayEquals(expected.toArray(),actual.toArray());
      assertTrue(actual.isEmpty());
      verify(sdao, times(1)).getAllBooks();
      verify(jdbcTemplate, times(1)).query(anyString(), any(RowMapper.class));
    }

    @DisplayName("本の全検索でDataAccessExceptionが投げられる場合")
    @Test
    void getAllBooks3() throws DaoException {

      DaoException expected = new DaoException("This is fake");

      when(jdbcTemplate.query(anyString(), any(RowMapper.class))).thenThrow(new DataAccessException("..."){});
      when(sdao.getAllBooks()).thenCallRealMethod();

      Throwable e = assertThrows(expected.getClass(), ()->{
        sdao.getAllBooks();
      });
      verify(sdao, times(1)).getAllBooks();
      verify(jdbcTemplate, times(1)).query(anyString(), any(RowMapper.class));
      logger.info("Thrown error class was ",e);
    }
  }











  @DisplayName("SpringDaoの本の検索に関するテスト")
  @Nested
  class getBook {
    @DisplayName("正しい本の検索")
    @Test
    void getBook1() throws DaoException {
      List<Book> expected = new ArrayList<>();
      expected.add(dummyBook1);

      when(sdao.getBook(anyInt())).thenCallRealMethod();
      when(jdbcTemplate.query(anyString(), any(RowMapper.class),anyInt())).thenReturn(expected);
      List<Book> actual = sdao.getBook(dummyBookId);
      assertEquals(actual.size(), 1);
      assertArrayEquals(expected.toArray(),actual.toArray());
      verify(sdao, times(1)).getBook(anyInt());
      verify(jdbcTemplate, times(1)).query(anyString(), any(RowMapper.class), anyInt());
    }

    @DisplayName("正しい本の検索 検索がヒットしない場合")
    @Test
    void getBook2() throws DaoException {
      List<Book> expected = new ArrayList<>();

      when(sdao.getBook(anyInt())).thenCallRealMethod();
      when(jdbcTemplate.query(anyString(), any(RowMapper.class),anyInt())).thenReturn(expected);
      List<Book> actual = sdao.getBook(dummyBookId);
      assertEquals(actual.size(), 0);
      assertArrayEquals(expected.toArray(),actual.toArray());
      verify(sdao, times(1)).getBook(anyInt());
      verify(jdbcTemplate, times(1)).query(anyString(), any(RowMapper.class), anyInt());
    }

    @DisplayName("本の検索でDataAccessExceptionが投げられる場合")
    @Test
    void getBook3() throws DaoException {

      when(sdao.getBook(anyInt())).thenCallRealMethod();
      when(jdbcTemplate.query(anyString(), any(RowMapper.class), anyInt())).thenThrow(new DataAccessException("..."){});

      DaoException expected = new DaoException("This is fake");

      Throwable e = assertThrows(expected.getClass(), ()->{
        sdao.getBook(dummyBookId);
      });
      verify(sdao, times(1)).getBook(anyInt());
      verify(jdbcTemplate, times(1)).query(anyString(), any(RowMapper.class), anyInt());
      logger.info("Thrown error class was ",e);
    }
  }








  @Nested
  @DisplayName("SpringDaoの本の追加に関するテスト")
  class insertBook {
    @DisplayName("正しい本の追加")
    @Test
    void insertBook1() throws DaoException {

      int expected = 1;

      when(sdao.insertBook(any(Book.class))).thenCallRealMethod();
      when(jdbcTemplate.update(anyString(), anyString(), anyInt(), anyInt(), anyString())).thenReturn(1);

      int actual = sdao.insertBook(dummyBook1);
      assertEquals(expected, actual);
    }

    @DisplayName("本の検索でDataAccessExceptionが投げられる場合")
    @Test
    void insertBook2() throws DaoException {



      when(sdao.insertBook(any(Book.class))).thenCallRealMethod();
      when(jdbcTemplate.update(anyString(), anyString(), anyInt(), anyInt(), anyString())).thenThrow(new DataAccessException("..."){});

      DaoException expected = new DaoException("This is fake");

      Throwable e = assertThrows(expected.getClass(), ()->{
        sdao.insertBook(dummyBook1);
      });
      verify(sdao, times(1)).insertBook(any(Book.class));
      verify(jdbcTemplate, times(1)).update(anyString(), anyString(), anyInt(), anyInt(), anyString());
      logger.info("Thrown error class was ",e);
    }
  }




  @Nested
  @DisplayName("SpringDaoの本の削除に関するテスト")
  class deleteBook {
    @Test
    @DisplayName("正しい本の削除")
    void deleteBook1() throws DaoException {
      int expected = 1;

      when(sdao.deleteBook(anyInt())).thenCallRealMethod();
      when(jdbcTemplate.update(anyString(), anyInt())).thenReturn(1);
      int actual = sdao.deleteBook(dummyBookId);
      assertEquals(expected,actual);
      verify(sdao, times(1)).deleteBook(anyInt());
      verify(jdbcTemplate, times(1)).update(anyString(), anyInt());
    }

    @Test
    @DisplayName("本の削除でDataAccessExceptionが投げられる場合")
    void deleteBook2() throws DaoException {

      DaoException expected = new DaoException("This is fake.");
      when(sdao.deleteBook(anyInt())).thenCallRealMethod();
      when(jdbcTemplate.update(anyString(), anyInt())).thenThrow(new DataAccessException("..."){});

      Throwable e = assertThrows(expected.getClass(), ()->{
        sdao.deleteBook(dummyBookId);
      });
      verify(sdao, times(1)).deleteBook(anyInt());
      verify(jdbcTemplate, times(1)).update(anyString(), anyInt());
      logger.info("Thrown error was ",e);
    }


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

  @Nested
  @DisplayName("SpringDaoによる本の置換に関するテスト")
  class updateBook_data {
    @DisplayName("正しい本の置換")
    @Test
    void updateBook_data() throws DaoException {

      int expected = 1;

      when(sdao.updateBook_data(anyInt(),any(Book.class))).thenCallRealMethod();
      when(jdbcTemplate.update(anyString(), anyString(), anyInt(), anyString(), anyInt(),  anyInt())).thenReturn(1);
      int actual = sdao.updateBook_data(dummyBookId, dummyBook1);
      assertEquals(expected,actual);
      verify(sdao, times(1)).updateBook_data(anyInt(),any(Book.class));
      verify(jdbcTemplate, times(1)).update(anyString(), anyString(), anyInt(), anyString(), anyInt(),  anyInt());
    }
    @DisplayName("本の置換時にDataAccessExceptionが投げられる場合")
    @Test
    void updateBook_data2() throws DaoException {


    }
  }

}