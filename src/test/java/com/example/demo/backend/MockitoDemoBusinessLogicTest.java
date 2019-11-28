package com.example.demo.backend;

import com.example.demo.backend.custom.Dto.BookClass;
import com.example.demo.backend.custom.Dto.ResponseBooks;
import com.example.demo.backend.custom.Dto.ResponseHeader;
import com.example.demo.backend.custom.myexceptions.DaoException;
import com.example.demo.backend.custom.myexceptions.DbException;
import com.example.demo.backend.custom.myexceptions.InputFormatExeption;
import com.example.demo.data.access.BookDao;
import org.junit.Assert;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.rules.ExpectedException;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static com.example.demo.backend.errormessages.StaticMessages.BOOK_FOUND;
import static com.example.demo.backend.errormessages.StaticMessages.BOOK_NOT_FOUND;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest
class MockitoDemoBusinessLogicTest {


  @InjectMocks
  DemoBusinessLogic dbl;

  @Mock
  BookDao dao;


  /**
   * This test checks the behavior of the {@link com.example.demo.backend.DemoBusinessLogic DemoBusinessLogic} class,
   *  when there is at least one data returned from the Dao
   * ({@link com.example.demo.data.access.JdbcBookDao JdbcBookDao} or {@link com.example.demo.data.access.SpringBookDao SpringBookDao}).
   * @throws DaoException An exception that raises when executing an SQL query fails.
   * @throws DbException An exception that raises when the database connection/disconnection fails.
   */
  @DisplayName("全検索で本データが一つも存在しない場合")
  @Test
  void getAllBooks_EMPTY() throws DbException, DaoException {

    ResponseBooks expected = new ResponseBooks();
    expected.getResponseHeader().setMessage(BOOK_NOT_FOUND);

    when(dao.getAllBooks()).thenReturn(new ArrayList<BookClass>());

    ResponseBooks actual = dbl.getAllBooks();

    Assert.assertEquals(expected.getResponseHeader().getMessage(),actual.getResponseHeader().getMessage());
    //Assert.assertArrayEquals(expected, actual);
  }

  @DisplayName("全検索で本データが存在する場合")
  @Test
  void getAllBooks_OK() throws DbException, DaoException {

    ResponseBooks expected2 = new ResponseBooks();
    expected2.getResponseHeader().setMessage(BOOK_FOUND);

    List<BookClass> fakeList = new ArrayList<>();
    fakeList.add(new BookClass("title", 1000,"https://fake.com",10));
    when(dao.getAllBooks()).thenReturn(fakeList);

    ResponseBooks actual2 = dbl.getAllBooks();

    Assert.assertEquals(expected2.getResponseHeader().getMessage(),actual2.getResponseHeader().getMessage());
  }

  @DisplayName("本の全検索でDB例外が投げられる場合")
  @Test
  void getAllBooks_Db() throws DbException, DaoException {

    Throwable fakeOutput = new DbException("This is fake");
    when(dao.getAllBooks()).thenThrow(fakeOutput);

    Throwable e = assertThrows(fakeOutput.getClass(), () -> {dbl.getAllBooks();});
    System.out.println("Throwed exception was: " + e.getMessage());
  }

  @DisplayName("本の全検索でDao例外が投げられる場合")
  @Test
  void getAllBooks_Dao() throws DbException, DaoException {

    Throwable fakeOutput = new DaoException("This is fake");
    when(dao.getAllBooks()).thenThrow(fakeOutput);

    Throwable e = assertThrows(fakeOutput.getClass(), () -> {dbl.getAllBooks();});
    System.out.println("Throwed exception was: " + e.getMessage());
  }

  @Test
  void removeBook() {
  }

  @Test
  void getBook() {
  }

  @Test
  void updateBook() {
  }

  @Test
  void addBook() {
  }

  @Test
  void replaceBook() {
  }

  @Test
  void addUser() {
  }
}