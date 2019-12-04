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
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import java.util.ArrayList;
import java.util.List;

import static com.example.demo.DemoApplication.logger;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.anyList;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
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

  @DisplayName("SpringDaoの本の全検索に関するテスト")
  @Nested
  class getAllBooks {
    @DisplayName("正しい本の全検索")
    @Test
    void getAllBooks() throws DaoException {
      List<Book> expected = new ArrayList<>();

      String[] borrowers = {"08011110000"};
      Book book1 = Book.builder()
        .id(0)
        .price(100)
        .title("Hello")
        .quantity(1)
        .url("https://example.com")
        .borrowedBy(borrowers)
        .build();

      Book book2 = Book.builder()
        .id(1)
        .price(2000)
        .title("World")
        .quantity(3)
        .url("https://example.com")
        .build();

      expected.add(book1);
      expected.add(book2);

      when(jdbcTemplate.query(anyString(), any(RowMapper.class))).thenReturn(expected);
      when(sdao.getAllBooks()).thenCallRealMethod();
      List<Book> actual = sdao.getAllBooks();
      assertArrayEquals(expected.toArray(),actual.toArray());
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
      logger.info("Thrown error class was ",e);
    }
  }

  @DisplayName("SpringDaoの本の検索に関するテスト")
  @Nested
  class getBook {
    @DisplayName("正しい本の検索")
    @Test
    void getBook() throws DaoException {
      List<Book> expected = new ArrayList<>();

      Integer id = 1;

      String[] borrowers = {"08011110000"};
      Book book1 = Book.builder()
        .id(1)
        .price(100)
        .title("Hello")
        .quantity(1)
        .url("https://example.com")
        .borrowedBy(borrowers)
        .build();

      expected.add(book1);

      when(sdao.getBook(anyInt())).thenCallRealMethod();
      when(jdbcTemplate.query(anyString(), any(RowMapper.class),anyInt())).thenReturn(expected);
      List<Book> actual = sdao.getBook(id);
      assertTrue(actual.size() == 1);
      assertArrayEquals(expected.toArray(),actual.toArray());
      verify(sdao, times(1)).getBook(anyInt());
      verify(jdbcTemplate, times(1)).query(anyString(), any(RowMapper.class), anyInt());
    }

    @DisplayName("正しい本の検索 検索がヒットしない場合")
    @Test
    void getBook2() throws DaoException {
      List<Book> expected = new ArrayList<>();

      Integer id = 1;


      when(sdao.getBook(anyInt())).thenCallRealMethod();
      when(jdbcTemplate.query(anyString(), any(RowMapper.class),anyInt())).thenReturn(expected);
      List<Book> actual = sdao.getBook(id);
      assertTrue(actual.size() == 0);
      assertArrayEquals(expected.toArray(),actual.toArray());
      verify(sdao, times(1)).getBook(anyInt());
      verify(jdbcTemplate, times(1)).query(anyString(), any(RowMapper.class), anyInt());
    }
  }

  @Test
  void insertBook() {
  }

  @Test
  void deleteBook() {
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
}