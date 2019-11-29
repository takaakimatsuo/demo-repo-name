package com.example.demo.backend;

import com.example.demo.backend.custom.Dto.*;
import com.example.demo.backend.custom.exceptions.*;
import com.example.demo.data.access.JdbcBookDao;
import com.example.demo.data.access.JdbcBookUserDao;
import com.example.demo.data.access.custom.enums.BookStatus;
import org.junit.Assert;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

import static com.example.demo.backend.errorcodes.SqlErrorCodes.SQL_CODE_DUPLICATE_KEY_ERROR;
import static com.example.demo.backend.messages.StaticBookMessages.*;
import static com.example.demo.backend.messages.StaticUserMessages.USER_DUPLICATE;
import static com.example.demo.backend.messages.StaticUserMessages.USER_INSERTED;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@SpringBootTest
class MockitoDemoBusinessLogicTest {


  @InjectMocks
  DemoBusinessLogic dbl;

  @Mock
  JdbcBookDao dao;
  //SpringBookDao dao;


  @Mock
  JdbcBookUserDao udao;


  /**
   * This test checks the behavior of the {@link com.example.demo.backend.DemoBusinessLogic DemoBusinessLogic} class' {@link DemoBusinessLogic#getAllBooks()} method
   *  when there is no data returned from the Dao
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


  /**
   * This test checks the behavior of the {@link com.example.demo.backend.DemoBusinessLogic DemoBusinessLogic} class' {@link DemoBusinessLogic#getAllBooks()}  method
   *  when there is at least one data returned from the Dao
   * ({@link com.example.demo.data.access.JdbcBookDao JdbcBookDao} or {@link com.example.demo.data.access.SpringBookDao SpringBookDao}).
   * @throws DaoException An exception that raises when executing an SQL query fails.
   * @throws DbException An exception that raises when the database connection/disconnection fails.
   */
  @DisplayName("全検索で本データが存在する場合")
  @Test
  void getAllBooks_OK() throws DbException, DaoException {

    ResponseBooks expected = new ResponseBooks();
    expected.getResponseHeader().setMessage(BOOK_FOUND);

    List<BookClass> fakeList = new ArrayList<>();
    fakeList.add(new BookClass("title", 1000,"https://fake.com",10));
    when(dao.getAllBooks()).thenReturn(fakeList);

    ResponseBooks actual = dbl.getAllBooks();

    Assert.assertEquals(expected.getResponseHeader().getMessage(),actual.getResponseHeader().getMessage());
  }

  /**
   * This test checks the behavior of the {@link com.example.demo.backend.DemoBusinessLogic DemoBusinessLogic} class' {@link DemoBusinessLogic#getAllBooks()} method
   *  when there is a DbException thrown from the Dao
   * ({@link com.example.demo.data.access.JdbcBookDao JdbcBookDao} or {@link com.example.demo.data.access.SpringBookDao SpringBookDao}).
   * @throws DaoException An exception that raises when executing an SQL query fails.
   * @throws DbException An exception that raises when the database connection/disconnection fails.
   */
  @DisplayName("本の全検索でDB例外が投げられる場合")
  @Test
  void getAllBooks_Db() throws DbException, DaoException {

    Throwable fakeOutput = new DbException("This is fake");
    when(dao.getAllBooks()).thenThrow(fakeOutput);

    Throwable e = assertThrows(fakeOutput.getClass(), () -> {dbl.getAllBooks();});
    System.out.println("Thrown exception was: " + e.getClass());
  }

  /**
   * This test checks the behavior of the {@link com.example.demo.backend.DemoBusinessLogic DemoBusinessLogic} class' {@link DemoBusinessLogic#getAllBooks()} method
   *  when there is a DaoException thrown from the Dao
   * ({@link com.example.demo.data.access.JdbcBookDao JdbcBookDao} or {@link com.example.demo.data.access.SpringBookDao SpringBookDao}).
   * @throws DaoException An exception that raises when executing an SQL query fails.
   * @throws DbException An exception that raises when the database connection/disconnection fails.
   */
  @DisplayName("本の全検索でDao例外が投げられる場合")
  @Test
  void getAllBooks_Dao() throws DbException, DaoException {

    Throwable fakeOutput = new DaoException("This is fake");
    when(dao.getAllBooks()).thenThrow(fakeOutput);

    Throwable e = assertThrows(fakeOutput.getClass(), () -> {dbl.getAllBooks();});
    System.out.println("Thrown exception was: " + e.getClass());
  }


  /**
   * This test checks the behavior of the {@link com.example.demo.backend.DemoBusinessLogic DemoBusinessLogic} class' {@link com.example.demo.backend.DemoBusinessLogic#removeBook(Integer)},
   *  when the specified data does not exist.
   * ({@link com.example.demo.data.access.JdbcBookDao JdbcBookDao} or {@link com.example.demo.data.access.SpringBookDao SpringBookDao}).
   * @throws DaoException An exception that raises when executing an SQL query fails.
   * @throws DbException An exception that raises when the database connection/disconnection fails.
   */
  @DisplayName("存在しない本削除申請")
  @Test
  void removeBook_WRONG() throws DbException, DaoException {
    int id = 1;
    ResponseBooks expected = new ResponseBooks();
    expected.getResponseHeader().setMessage(BOOK_NOT_EXISTING);

    when(dao.deleteBook(id)).thenReturn(0);

    ResponseBooks actual = dbl.removeBook(id);

    Assert.assertEquals(expected.getResponseHeader().getMessage(),actual.getResponseHeader().getMessage());

  }

  /**
   * This test checks the behavior of the {@link com.example.demo.backend.DemoBusinessLogic DemoBusinessLogic} class' {@link com.example.demo.backend.DemoBusinessLogic#removeBook(Integer)},
   *  when the specified data does exist.
   * ({@link com.example.demo.data.access.JdbcBookDao JdbcBookDao} or {@link com.example.demo.data.access.SpringBookDao SpringBookDao}).
   * @throws DaoException An exception that raises when executing an SQL query fails.
   * @throws DbException An exception that raises when the database connection/disconnection fails.
   */
  @DisplayName("存在する本削除申請")
  @Test
  void removeBook() throws DbException, DaoException {
    int id = 1;
    ResponseBooks expected = new ResponseBooks();
    expected.getResponseHeader().setMessage(BOOK_DELETED);

    when(dao.deleteBook(id)).thenReturn(1);

    ResponseBooks actual = dbl.removeBook(id);

    Assert.assertEquals(expected.getResponseHeader().getMessage(),actual.getResponseHeader().getMessage());

  }


  /**
   * This test checks the behavior of the {@link com.example.demo.backend.DemoBusinessLogic DemoBusinessLogic} class' {@link com.example.demo.backend.DemoBusinessLogic#removeBook(Integer)},
   *  when the method throws a DaoException.
   * ({@link com.example.demo.data.access.JdbcBookDao JdbcBookDao} or {@link com.example.demo.data.access.SpringBookDao SpringBookDao}).
   * @throws DaoException An exception that raises when executing an SQL query fails.
   * @throws DbException An exception that raises when the database connection/disconnection fails.
   */
  @DisplayName("本の削除でDao例外が投げられる場合")
  @Test
  void removeBook_Dao() throws DbException, DaoException {
    int id = 1;
    Throwable fakeOutput = new DaoException("This is fake");
    when(dao.deleteBook(id)).thenThrow(fakeOutput);

    Throwable e = assertThrows(fakeOutput.getClass(), () -> {dbl.removeBook(id);});
    System.out.println("Thrown exception was: " + e.getClass());

  }


  /**
   * This test checks the behavior of the {@link com.example.demo.backend.DemoBusinessLogic DemoBusinessLogic} class' {@link com.example.demo.backend.DemoBusinessLogic#removeBook(Integer)},
   *  when the method throws a DbException.
   * ({@link com.example.demo.data.access.JdbcBookDao JdbcBookDao} or {@link com.example.demo.data.access.SpringBookDao SpringBookDao}).
   * @throws DaoException An exception that raises when executing an SQL query fails.
   * @throws DbException An exception that raises when the database connection/disconnection fails.
   */
  @DisplayName("本の削除でDb例外が投げられる場合")
  @Test
  void removeBook_Db() throws DbException, DaoException {
    int id = 1;
    Throwable fakeOutput = new DbException("This is fake");
    when(dao.deleteBook(id)).thenThrow(fakeOutput);

    Throwable e = assertThrows(fakeOutput.getClass(), () -> {dbl.removeBook(id);});
    System.out.println("Thrown exception was: " + e.getClass());

  }


  /**
   * This test checks the behavior of the {@link com.example.demo.backend.DemoBusinessLogic DemoBusinessLogic} class' {@link com.example.demo.backend.DemoBusinessLogic#getBook(Integer)},
   *  when no data is found.
   * ({@link com.example.demo.data.access.JdbcBookDao JdbcBookDao} or {@link com.example.demo.data.access.SpringBookDao SpringBookDao}).
   * @throws DaoException An exception that raises when executing an SQL query fails.
   * @throws DbException An exception that raises when the database connection/disconnection fails.
   */
  @DisplayName("本の検索でデータが存在しない時")
  @Test
  void getBook_WRONG() throws DbException, DaoException, BookException {
    //TODO
    int id = 1;

    BookException expected = new BookException(BOOK_NOT_EXISTING);

    when(dao.getBook(id)).thenReturn(new ArrayList<>());


    Throwable e = assertThrows(expected.getClass(), () -> {dbl.getBook(id);});
    System.out.println("Thrown exception was: " + e.getClass());
  }


  /**
   * This test checks the behavior of the {@link com.example.demo.backend.DemoBusinessLogic DemoBusinessLogic} class' {@link com.example.demo.backend.DemoBusinessLogic#getBook(Integer)},
   *  when a data is found.
   * ({@link com.example.demo.data.access.JdbcBookDao JdbcBookDao} or {@link com.example.demo.data.access.SpringBookDao SpringBookDao}).
   * @throws DaoException An exception that raises when executing an SQL query fails.
   * @throws DbException An exception that raises when the database connection/disconnection fails.
   */
  @DisplayName("本の検索でデータが存在する時")
  @Test
  void getBook() throws DbException, DaoException, BookException {
    //TODO
    int id = 1;
    ResponseBooks expected = new ResponseBooks();
    expected.getResponseHeader().setMessage(BOOK_FOUND);

    List<BookClass> fakeList = new ArrayList<>();
    fakeList.add(new BookClass("title", 1000,"https://fake.com",10));
    when(dao.getBook(id)).thenReturn(fakeList);

    ResponseBooks actual = dbl.getBook(id);

    Assert.assertEquals(expected.getResponseHeader().getMessage(),actual.getResponseHeader().getMessage());
  }

  /**
   * This test checks the behavior of the {@link com.example.demo.backend.DemoBusinessLogic DemoBusinessLogic} class' {@link com.example.demo.backend.DemoBusinessLogic#getBook(Integer)},
   *  when the DbException is thrown.
   * ({@link com.example.demo.data.access.JdbcBookDao JdbcBookDao} or {@link com.example.demo.data.access.SpringBookDao SpringBookDao}).
   * @throws DaoException An exception that raises when executing an SQL query fails.
   * @throws DbException An exception that raises when the database connection/disconnection fails.
   */
  @DisplayName("本の検索でDb例外が投げられる場合")
  @Test
  void getBook_Db() throws DbException, DaoException {
    int id = 1;
    Throwable fakeOutput = new DbException("This is fake");
    when(dao.getBook(id)).thenThrow(fakeOutput);

    Throwable e = assertThrows(fakeOutput.getClass(), () -> {dbl.getBook(id);});
    System.out.println("Thrown exception was: " + e.getClass());
  }

  /**
   * This test checks the behavior of the {@link com.example.demo.backend.DemoBusinessLogic DemoBusinessLogic} class' {@link com.example.demo.backend.DemoBusinessLogic#getBook(Integer)},
   *  when the DaoException is thrown.
   * ({@link com.example.demo.data.access.JdbcBookDao JdbcBookDao} or {@link com.example.demo.data.access.SpringBookDao SpringBookDao}).
   * @throws DaoException An exception that raises when executing an SQL query fails.
   * @throws DbException An exception that raises when the database connection/disconnection fails.
   */
  @DisplayName("本の検索でDb例外が投げられる場合")
  @Test
  void getBook_Dao() throws DbException, DaoException {
    int id = 1;
    Throwable fakeOutput = new DaoException("This is fake");
    when(dao.getBook(id)).thenThrow(fakeOutput);

    Throwable e = assertThrows(fakeOutput.getClass(), () -> {dbl.getBook(id);});
    System.out.println("Thrown exception was: " + e.getClass());
  }


  /**
   * This test checks the behavior of the {@link com.example.demo.backend.DemoBusinessLogic DemoBusinessLogic} class' {@link com.example.demo.backend.DemoBusinessLogic#addBook(BookClass)},
   *  when the data is correctly inserted to the database.
   * ({@link com.example.demo.data.access.JdbcBookDao JdbcBookDao} or {@link com.example.demo.data.access.SpringBookDao SpringBookDao}).
   * @throws DaoException An exception that raises when executing an SQL query fails.
   * @throws DbException An exception that raises when the database connection/disconnection fails.
   */
  @DisplayName("本の追加")
  @Test
  void addBook() throws DbException, DaoException, BookException {
    BookClass book = new BookClass("newTitle",1000,"https://fake.com",10);
    List<BookClass> fakeList = new ArrayList<>();
    fakeList.add(book);
    when(dao.insertBook(book)).thenReturn(fakeList);

    ResponseBooks actual = dbl.addBook(book);
    assertEquals(actual.getResponseHeader().getMessage(),BOOK_INSERTED);
  }


  /**
   * This test checks the behavior of the {@link com.example.demo.backend.DemoBusinessLogic DemoBusinessLogic} class' {@link com.example.demo.backend.DemoBusinessLogic#addBook(BookClass)},
   *  when the data is correctly inserted to the database.
   * ({@link com.example.demo.data.access.JdbcBookDao JdbcBookDao} or {@link com.example.demo.data.access.SpringBookDao SpringBookDao}).
   * @throws DaoException An exception that raises when executing an SQL query fails.
   * @throws DbException An exception that raises when the database connection/disconnection fails.
   */
  @DisplayName("すでに登録されている本の追加")
  @Test
  void addBook_DUPLICATE() throws DbException, DaoException {
    BookClass book = new BookClass("newTitle",1000,"https://fake.com",10);

    DaoException fakeOutput = new DaoException("This is fake");
    fakeOutput.setSqlCode(SQL_CODE_DUPLICATE_KEY_ERROR);

    when(dao.insertBook(book)).thenThrow(fakeOutput);

    BookException expected = new BookException("This is fake");

    Throwable e = assertThrows(expected.getClass(), () -> {dbl.addBook(book);});
    System.out.println("Thrown exception was: " + e.getClass());
  }


  /**
   * This test checks the behavior of the {@link com.example.demo.backend.DemoBusinessLogic DemoBusinessLogic} class' {@link com.example.demo.backend.DemoBusinessLogic#addBook(BookClass)},
   *  when the DbException is thrown.
   * ({@link com.example.demo.data.access.JdbcBookDao JdbcBookDao} or {@link com.example.demo.data.access.SpringBookDao SpringBookDao}).
   * @throws DaoException An exception that raises when executing an SQL query fails.
   * @throws DbException An exception that raises when the database connection/disconnection fails.
   */
  @DisplayName("本の追加時にDb例外")
  @Test
  void addBook_Db() throws DbException, DaoException {
    BookClass book = new BookClass("newTitle",1000,"https://fake.com",10);

    DbException fakeOutput = new DbException("This is fake");

    when(dao.insertBook(book)).thenThrow(fakeOutput);

    Throwable e = assertThrows(fakeOutput.getClass(), () -> {dbl.addBook(book);});
    System.out.println("Thrown exception was: " + e.getClass());
  }

  /**
   * This test checks the behavior of the {@link com.example.demo.backend.DemoBusinessLogic DemoBusinessLogic} class' {@link com.example.demo.backend.DemoBusinessLogic#addBook(BookClass)},
   *  when the DaoException is thrown.
   * ({@link com.example.demo.data.access.JdbcBookDao JdbcBookDao} or {@link com.example.demo.data.access.SpringBookDao SpringBookDao}).
   * @throws DaoException An exception that raises when executing an SQL query fails.
   * @throws DbException An exception that raises when the database connection/disconnection fails.
   */
  @DisplayName("本の追加時にDao例外")
  @Test
  void addBook_Dao() throws DbException, DaoException {
    BookClass book = new BookClass("newTitle",1000,"https://fake.com",10);

    DaoException fakeOutput = new DaoException("This is fake");

    when(dao.insertBook(book)).thenThrow(fakeOutput);

    Throwable e = assertThrows(fakeOutput.getClass(), () -> {dbl.addBook(book);});
    System.out.println("Thrown exception was: " + e.getClass());
  }







  /**
   * This test checks the behavior of the {@link com.example.demo.backend.DemoBusinessLogic DemoBusinessLogic} class' {@link com.example.demo.backend.DemoBusinessLogic#replaceBook(Integer, BookClass)},
   *  when the data is correctly replaced.
   * ({@link com.example.demo.data.access.JdbcBookDao JdbcBookDao} or {@link com.example.demo.data.access.SpringBookDao SpringBookDao}).
   * @throws DaoException An exception that raises when executing an SQL query fails.
   * @throws DbException An exception that raises when the database connection/disconnection fails.
   */
  @DisplayName("本データの置き換え")
  @Test
  void replaceBook() throws DbException, DaoException, BookException {
    Integer bookId = 1;
    BookClass book = new BookClass("newTitle",1000,"https://fake.com",10);
    when(dao.updateBook_data(bookId,book)).thenReturn(1);
    ResponseBooks actual = dbl.replaceBook(bookId,book);
    assertEquals(actual.getResponseHeader().getMessage(), UPDATE_SUCCESS_BOOK);
  }

  @DisplayName("本データの置き換えですでに保存されている本のタイトルを指定")
  @Test
  void replaceBook_DUPLICATE() throws DbException, DaoException {
    Integer bookId = 0;
    BookClass book = new BookClass("newTitle",1000,"https://fake.com",10);

    DaoException fakeOutput = new DaoException("This is fake");
    fakeOutput.setSqlCode(SQL_CODE_DUPLICATE_KEY_ERROR);

    when(dao.updateBook_data(bookId,book)).thenThrow(fakeOutput);

    BookException expected = new BookException(BOOK_DUPLICATE);

    Throwable e = assertThrows(expected.getClass(), () -> {dbl.replaceBook(bookId,book);});
    System.out.println("Thrown exception was: " + e.getClass());
    assertEquals(e.getMessage(),expected.getMessage());
  }

  @DisplayName("存在しない本データの置き換え")
  @Test
  void replaceBook_NOT_EXISTING() throws DbException, DaoException {
    Integer bookId = 0;
    BookClass book = new BookClass("newTitle",1000,"https://fake.com",10);

    when(dao.updateBook_data(bookId,book)).thenReturn(0);

    BookException expected = new BookException(BOOK_NOT_EXISTING);

    Throwable e = assertThrows(expected.getClass(), () -> {dbl.replaceBook(bookId,book);});
    System.out.println("Thrown exception was: " + e.getClass());
  }

  @DisplayName("本データの置き換えでDb例外")
  @Test
  void replaceBook_Db() throws DbException, DaoException {
    Integer bookId = 0;
    BookClass book = new BookClass("newTitle",1000,"https://fake.com",10);

    DbException fakeOutput = new DbException("This is fake");

    when(dao.updateBook_data(bookId,book)).thenThrow(fakeOutput);

    Throwable e = assertThrows(fakeOutput.getClass(), () -> {dbl.replaceBook(bookId,book);});
    System.out.println("Thrown exception was: " + e.getClass());
  }

  @DisplayName("本データの置き換えでDao例外")
  @Test
  void replaceBook_Dao() throws DbException, DaoException {
    Integer bookId = 0;
    BookClass book = new BookClass("newTitle",1000,"https://fake.com",10);

    DaoException fakeOutput = new DaoException("This is fake");

    when(dao.updateBook_data(bookId,book)).thenThrow(fakeOutput);

    Throwable e = assertThrows(fakeOutput.getClass(), () -> {dbl.replaceBook(bookId,book);});
    System.out.println("Thrown exception was: " + e.getClass());
  }






  /**
   * This test checks the behavior of the {@link com.example.demo.backend.DemoBusinessLogic DemoBusinessLogic} class' {@link com.example.demo.backend.DemoBusinessLogic#updateBook(Integer, PatchBookClass)},
   *  when the data is correctly patched as borrowed.
   * ({@link com.example.demo.data.access.JdbcBookDao JdbcBookDao} or {@link com.example.demo.data.access.SpringBookDao SpringBookDao}).
   * @throws DaoException An exception that raises when executing an SQL query fails.
   * @throws DbException An exception that raises when the database connection/disconnection fails.
   */
  @Test
  @DisplayName("本を正しく借りることができた場合")
  void updateBook_BORROWED() throws DbException, DaoException, BookException {
    //TODO
    PatchBookClass book = new PatchBookClass();
    String borrower = "08011110000";
    book.setBorrower(borrower);
    book.setStatus(0);//Borrow
    Integer bookId = 1;
    when(dao.checkBookStatus(bookId,borrower)).thenReturn(BookStatus.BOOK_NOT_BORROWED_BY_THIS_USER);
    when(dao.checkBookStockAvailability(bookId)).thenReturn(true);

    ResponseBooks actual = dbl.updateBook(bookId,book);
    assertEquals(actual.getResponseHeader().getMessage(), BOOK_BORROWED);
  }


  /**
   * This test checks the behavior of the {@link com.example.demo.backend.DemoBusinessLogic DemoBusinessLogic} class' {@link com.example.demo.backend.DemoBusinessLogic#updateBook(Integer, PatchBookClass)},
   *  when the same book is already borrowed.
   * ({@link com.example.demo.data.access.JdbcBookDao JdbcBookDao} or {@link com.example.demo.data.access.SpringBookDao SpringBookDao}).
   * @throws DaoException An exception that raises when executing an SQL query fails.
   * @throws DbException An exception that raises when the database connection/disconnection fails.
   */
  @Test
  @DisplayName("同じ本を既に借りている時に、また借りようとした場合")
  void updateBook_BORROWED_AGAIN() throws DbException, DaoException, BookException {
    //TODO
    PatchBookClass book = new PatchBookClass();
    String borrower = "08011110000";
    book.setBorrower(borrower);
    book.setStatus(0);//Borrow
    Integer bookId = 1;
    when(dao.checkBookStatus(bookId,borrower)).thenReturn(BookStatus.BOOK_BORROWED_BY_THIS_USER);
    when(dao.checkBookStockAvailability(bookId)).thenReturn(true);

    BookException expected = new BookException(BOOK_CANNOT_BE_DOUBLE_BORROWED);
    Throwable actual = assertThrows(expected.getClass(),()->{dbl.updateBook(bookId,book);});
    assertEquals(expected.getMessage(), actual.getMessage());
  }



  /**
   * This test checks the behavior of the {@link com.example.demo.backend.DemoBusinessLogic DemoBusinessLogic} class' {@link com.example.demo.backend.DemoBusinessLogic#updateBook(Integer, PatchBookClass)},
   *  when the book has no stock left.
   * ({@link com.example.demo.data.access.JdbcBookDao JdbcBookDao} or {@link com.example.demo.data.access.SpringBookDao SpringBookDao}).
   * @throws DaoException An exception that raises when executing an SQL query fails.
   * @throws DbException An exception that raises when the database connection/disconnection fails.
   */
  @Test
  @DisplayName("本の借り出しを行うための在庫がない場合")
  void updateBook_NO_STOCK() throws DbException, DaoException, BookException {
    PatchBookClass book = new PatchBookClass();
    String borrower = "08011110000";
    book.setBorrower(borrower);
    book.setStatus(0);//Borrow
    Integer bookId = 1;
    when(dao.checkBookStatus(bookId,borrower)).thenReturn(BookStatus.BOOK_NOT_BORROWED_BY_THIS_USER);
    when(dao.checkBookStockAvailability(bookId)).thenReturn(false);

    BookException expected = new BookException(BOOK_NO_STOCK);
    Throwable actual = assertThrows(expected.getClass(),()->{dbl.updateBook(bookId,book);});
    assertEquals(expected.getMessage(), actual.getMessage());
  }


  /**
   * This test checks the behavior of the {@link com.example.demo.backend.DemoBusinessLogic DemoBusinessLogic} class' {@link com.example.demo.backend.DemoBusinessLogic#updateBook(Integer, PatchBookClass)},
   *  when the book doesn't exist.
   * ({@link com.example.demo.data.access.JdbcBookDao JdbcBookDao} or {@link com.example.demo.data.access.SpringBookDao SpringBookDao}).
   * @throws DaoException An exception that raises when executing an SQL query fails.
   * @throws DbException An exception that raises when the database connection/disconnection fails.
   */
  @Test
  @DisplayName("存在しない本を借りようとする場合")
  void updateBook_NO_() throws DbException, DaoException, BookException {
    PatchBookClass book = new PatchBookClass();
    String borrower = "08011110000";
    book.setBorrower(borrower);
    book.setStatus(0);//Borrow
    Integer bookId = 1;
    when(dao.checkBookStatus(bookId,borrower)).thenReturn(BookStatus.BOOK_NOT_EXISTING);
    when(dao.checkBookStockAvailability(bookId)).thenReturn(true);

    BookException expected = new BookException(BOOK_NOT_EXISTING);
    Throwable actual = assertThrows(expected.getClass(),()->{dbl.updateBook(bookId,book);});
    assertEquals(expected.getMessage(), actual.getMessage());
  }

  /**
   * This test checks the behavior of the {@link com.example.demo.backend.DemoBusinessLogic DemoBusinessLogic} class' {@link com.example.demo.backend.DemoBusinessLogic#updateBook(Integer, PatchBookClass)},
   *  when the book is returned correclty
   * ({@link com.example.demo.data.access.JdbcBookDao JdbcBookDao} or {@link com.example.demo.data.access.SpringBookDao SpringBookDao}).
   * @throws DaoException An exception that raises when executing an SQL query fails.
   * @throws DbException An exception that raises when the database connection/disconnection fails.
   */
  @Test
  @DisplayName("本の正しい返却")
  void updateBook_RETURN() throws DbException, DaoException, BookException {
    PatchBookClass book = new PatchBookClass();
    String borrower = "08011110000";
    book.setBorrower(borrower);
    book.setStatus(1);//Return
    Integer bookId = 1;
    when(dao.checkBookStatus(bookId,borrower)).thenReturn(BookStatus.BOOK_BORROWED_BY_THIS_USER);


    ResponseBooks actual = dbl.updateBook(bookId, book);
    assertEquals(actual.getResponseHeader().getMessage(), BOOK_RETURNED);
  }


  /**
   * This test checks the behavior of the {@link com.example.demo.backend.DemoBusinessLogic DemoBusinessLogic} class' {@link com.example.demo.backend.DemoBusinessLogic#updateBook(Integer, PatchBookClass)},
   *  when the book being return but is not borrowed first of all.
   * ({@link com.example.demo.data.access.JdbcBookDao JdbcBookDao} or {@link com.example.demo.data.access.SpringBookDao SpringBookDao}).
   * @throws DaoException An exception that raises when executing an SQL query fails.
   * @throws DbException An exception that raises when the database connection/disconnection fails.
   */
  @Test
  @DisplayName("借りていない本の返却")
  void updateBook_RETURN_NOT_BORROWED() throws DbException, DaoException, BookException {
    PatchBookClass book = new PatchBookClass();
    String borrower = "08011110000";
    book.setBorrower(borrower);
    book.setStatus(1);//Return
    Integer bookId = 1;
    when(dao.checkBookStatus(bookId,borrower)).thenReturn(BookStatus.BOOK_NOT_BORROWED_BY_THIS_USER);

    BookException expected = new BookException(BOOK_CANNOT_BE_RETURNED);
    Throwable actual = assertThrows(expected.getClass(),()->{dbl.updateBook(bookId,book);});
    assertEquals(expected.getMessage(), actual.getMessage());

  }


  /**
   * This test checks the behavior of the {@link com.example.demo.backend.DemoBusinessLogic DemoBusinessLogic} class' {@link com.example.demo.backend.DemoBusinessLogic#updateBook(Integer, PatchBookClass)},
   *  when the book being lost but is not borrowed first of all.
   * ({@link com.example.demo.data.access.JdbcBookDao JdbcBookDao} or {@link com.example.demo.data.access.SpringBookDao SpringBookDao}).
   * @throws DaoException An exception that raises when executing an SQL query fails.
   * @throws DbException An exception that raises when the database connection/disconnection fails.
   */
  @Test
  @DisplayName("借りていない本の紛失")
  void updateBook_LOST_NOT_BORROWED() throws DbException, DaoException, BookException {
    PatchBookClass book = new PatchBookClass();
    String borrower = "08011110000";
    book.setBorrower(borrower);
    book.setStatus(2);//LOST
    Integer bookId = 1;
    when(dao.checkBookStatus(bookId,book.getBorrower())).thenReturn(BookStatus.BOOK_NOT_BORROWED_BY_THIS_USER);


    BookException expected = new BookException(BOOK_CANNOT_BE_LOST);
    Throwable actual = assertThrows(expected.getClass(),()->{dbl.updateBook(bookId,book);});
    assertEquals(expected.getMessage(), actual.getMessage());
  }


  /**
   * This test checks the behavior of the {@link com.example.demo.backend.DemoBusinessLogic DemoBusinessLogic} class' {@link com.example.demo.backend.DemoBusinessLogic#updateBook(Integer, PatchBookClass)},
   *  when the book being lost but is not borrowed first of all.
   * ({@link com.example.demo.data.access.JdbcBookDao JdbcBookDao} or {@link com.example.demo.data.access.SpringBookDao SpringBookDao}).
   * @throws DaoException An exception that raises when executing an SQL query fails.
   * @throws DbException An exception that raises when the database connection/disconnection fails.
   */
  @Test
  @DisplayName("正しい本の紛失")
  void updateBook_LOST() throws DbException, DaoException, BookException {
    PatchBookClass book = new PatchBookClass();
    String borrower = "08011110000";
    book.setBorrower(borrower);
    book.setStatus(2);//LOST
    Integer bookId = 1;
    when(dao.checkBookStatus(bookId,borrower)).thenReturn(BookStatus.BOOK_BORROWED_BY_THIS_USER);
    List<BookClass> fakeList = new ArrayList<>();
    fakeList.add(new BookClass("title", 1000,"https://fake.com",0));
    when(dao.getBook(bookId)).thenReturn(fakeList);
    ResponseBooks actual = dbl.updateBook(bookId, book);
    assertEquals(BOOK_LOST, actual.getResponseHeader().getMessage());
  }



  
  
  @DisplayName("ユーザの追加")
  @Test
  void addUser() throws DbException, DaoException, UserException {

    List<BookUser> list = new ArrayList<>();
    BookUser user = new BookUser("Family","First","08011110000");
    list.add(user);

    when(udao.insertBookUser(user)).thenReturn(list);

    ResponseUsers actual = dbl.addUser(user);
    assertEquals(actual.getResponseHeader().getMessage(),USER_INSERTED);

  }


  @DisplayName("すでに登録されている電話番号を使い新たなユーザを追加")
  @Test
  void addUser_DUPLICATE() throws DbException, DaoException {

    List<BookUser> list = new ArrayList<>();
    BookUser user = new BookUser("Family","First","08011110000");
    list.add(user);

    DaoException fakeOutput = new DaoException("This is fake");
    fakeOutput.setSqlCode(SQL_CODE_DUPLICATE_KEY_ERROR);

    UserException expected = new UserException(USER_DUPLICATE);

    when(udao.insertBookUser(user)).thenThrow(fakeOutput);

    Throwable e = assertThrows(expected.getClass(), () -> {dbl.addUser(user);});
    System.out.println("Thrown exception was: " + e.getClass());

  }


  @DisplayName("ユーザの追加時にDb例外")
  @Test
  void addUser_Dp() throws DbException, DaoException {

    List<BookUser> list = new ArrayList<>();
    BookUser user = new BookUser("Family","First","08011110000");
    list.add(user);

    DbException fakeOutput = new DbException("This is fake");

    when(udao.insertBookUser(user)).thenThrow(fakeOutput);

    Throwable e = assertThrows(fakeOutput.getClass(), () -> {dbl.addUser(user);});
    System.out.println("Thrown exception was: " + e.getClass());

  }



  @DisplayName("ユーザの追加時にDao例外")
  @Test
  void addUser_Dao() throws DbException, DaoException {

    List<BookUser> list = new ArrayList<>();
    BookUser user = new BookUser("Family","First","08011110000");
    list.add(user);

    DaoException fakeOutput = new DaoException("This is fake");

    when(udao.insertBookUser(user)).thenThrow(fakeOutput);


    Throwable e = assertThrows(fakeOutput.getClass(), () -> {dbl.addUser(user);});

  }




}