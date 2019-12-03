package com.example.demo.backend;


import com.example.demo.backend.custom.Dto.Book;
import com.example.demo.backend.custom.Dto.PatchBook;
import com.example.demo.backend.custom.Dto.ResponseBooks;
import com.example.demo.common.exceptions.BookException;
import com.example.demo.common.exceptions.DaoException;
import com.example.demo.common.exceptions.DbException;
import com.example.demo.data.access.JdbcBookDao;
import com.example.demo.data.access.JdbcDao;
import com.example.demo.data.access.custom.enums.BookStatus;
import org.junit.Assert;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import java.util.ArrayList;
import java.util.List;

import static com.example.demo.backend.errorcodes.SqlErrorCodes.SQL_CODE_DUPLICATE_KEY_ERROR;
import static com.example.demo.common.messages.StaticBookMessages.BOOK_BORROWED;
import static com.example.demo.common.messages.StaticBookMessages.BOOK_CANNOT_BE_DOUBLE_BORROWED;
import static com.example.demo.common.messages.StaticBookMessages.BOOK_CANNOT_BE_LOST;
import static com.example.demo.common.messages.StaticBookMessages.BOOK_CANNOT_BE_RETURNED;
import static com.example.demo.common.messages.StaticBookMessages.BOOK_DELETED;
import static com.example.demo.common.messages.StaticBookMessages.BOOK_DUPLICATE;
import static com.example.demo.common.messages.StaticBookMessages.BOOK_FOUND;
import static com.example.demo.common.messages.StaticBookMessages.BOOK_INSERTED;
import static com.example.demo.common.messages.StaticBookMessages.BOOK_LOST_AND_DELETED;
import static com.example.demo.common.messages.StaticBookMessages.BOOK_NOT_EXISTING;
import static com.example.demo.common.messages.StaticBookMessages.BOOK_NOT_FOUND;
import static com.example.demo.common.messages.StaticBookMessages.BOOK_NO_STOCK;
import static com.example.demo.common.messages.StaticBookMessages.BOOK_RETURNED;
import static com.example.demo.common.messages.StaticBookMessages.UPDATE_SUCCESS_BOOK;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.isA;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
class MockitoBookBusinessLogicTest {

  @InjectMocks
  BookBusinessLogic dbl;

  @Mock
  JdbcBookDao dao;
  //SpringBookDao dao;

  @Mock
  JdbcDao jdao;

  @DisplayName("本の全検索に関するテスト")
  @Nested
  class test1{
    /**
     * This test checks the behavior of the {@link com.example.demo.backend.BookBusinessLogic DemoBusinessLogic} class' {@link BookBusinessLogic#getAllBooks()} method
     *  when there is no data returned from the Dao
     * ({@link com.example.demo.data.access.JdbcBookDao JdbcBookDao} or {@link com.example.demo.data.access.SpringBookDao SpringBookDao}).
     * @throws DaoException An exception that raises when executing an SQL query fails.
     * @throws DbException An exception that raises when the database connection/disconnection fails.
     */
    @DisplayName("全検索で本データが一つも存在しない場合")
    @Test
    void getAllBooks_EMPTY() throws DbException, DaoException, BookException {


      BookException expected = new BookException(BOOK_NOT_FOUND);

      when(dao.getAllBooks()).thenReturn(new ArrayList<Book>());

      assertThrows(expected.getClass(), ()->{
        dbl.getAllBooks();
        verify(dao, times(1)).getAllBooks();
      });
      //Assert.assertArrayEquals(expected, actual);
    }


    /**
     * This test checks the behavior of the {@link com.example.demo.backend.BookBusinessLogic DemoBusinessLogic} class' {@link BookBusinessLogic#getAllBooks()}  method
     *  when there is at least one data returned from the Dao
     * ({@link com.example.demo.data.access.JdbcBookDao JdbcBookDao} or {@link com.example.demo.data.access.SpringBookDao SpringBookDao}).
     * @throws DaoException An exception that raises when executing an SQL query fails.
     * @throws DbException An exception that raises when the database connection/disconnection fails.
     */
    @DisplayName("全検索で本データが存在する場合")
    @Test
    void getAllBooks_OK() throws DbException, DaoException, BookException {

      ResponseBooks expected = new ResponseBooks();
      expected.getMessageHeader().setMessage(BOOK_FOUND);

      List<Book> fakeList = new ArrayList<>();
      fakeList.add(new Book("title", 1000,"https://fake.com",10));
      when(dao.getAllBooks()).thenReturn(fakeList);

      ResponseBooks actual = dbl.getAllBooks();
      verify(dao, times(1)).getAllBooks();
      Assert.assertEquals(expected.getMessageHeader().getMessage(),actual.getMessageHeader().getMessage());
    }

    /**
     * This test checks the behavior of the {@link com.example.demo.backend.BookBusinessLogic DemoBusinessLogic} class' {@link BookBusinessLogic#getAllBooks()} method
     *  when there is a DbException thrown from the Dao
     * ({@link com.example.demo.data.access.JdbcBookDao JdbcBookDao} or {@link com.example.demo.data.access.SpringBookDao SpringBookDao}).
     * @throws DaoException An exception that raises when executing an SQL query fails.
     */
    @DisplayName("本の全検索でDB例外が投げられる場合")
    @Test
    void getAllBooks_Db() throws DaoException {

      Throwable fakeOutput = new DaoException("This is fake");
      when(dao.getAllBooks()).thenThrow(fakeOutput);
      Throwable e = assertThrows(fakeOutput.getClass(), () -> {dbl.getAllBooks();});
      verify(dao, times(1)).getAllBooks();
    }

    /**
     * This test checks the behavior of the {@link com.example.demo.backend.BookBusinessLogic DemoBusinessLogic} class' {@link BookBusinessLogic#getAllBooks()} method
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
      verify(dao, times(1)).getAllBooks();
    }

  }


  @DisplayName("本の削除に関するテスト")
  @Nested
  class test2{
    /**
     * This test checks the behavior of the {@link com.example.demo.backend.BookBusinessLogic DemoBusinessLogic} class' {@link com.example.demo.backend.BookBusinessLogic#removeBook(Integer)},
     *  when the specified data does not exist.
     * ({@link com.example.demo.data.access.JdbcBookDao JdbcBookDao} or {@link com.example.demo.data.access.SpringBookDao SpringBookDao}).
     * @throws DaoException An exception that raises when executing an SQL query fails.
     * @throws DbException An exception that raises when the database connection/disconnection fails.
     */
    @DisplayName("存在しない本削除申請")
    @Test
    void removeBook_WRONG() throws DbException, DaoException, BookException {
      int id = 1;

      when(dao.deleteBook(id)).thenReturn(0);

      BookException expected = new BookException(BOOK_NOT_EXISTING);
      Throwable actual = assertThrows(expected.getClass(), () -> {dbl.removeBook(id);});

      verify(dao, times(1)).deleteBook(anyInt());
      assertEquals(actual.getMessage(),expected.getMessage());

    }

    /**
     * This test checks the behavior of the {@link com.example.demo.backend.BookBusinessLogic DemoBusinessLogic} class' {@link com.example.demo.backend.BookBusinessLogic#removeBook(Integer)},
     *  when the specified data does exist.
     * ({@link com.example.demo.data.access.JdbcBookDao JdbcBookDao} or {@link com.example.demo.data.access.SpringBookDao SpringBookDao}).
     * @throws DaoException An exception that raises when executing an SQL query fails.
     * @throws DbException An exception that raises when the database connection/disconnection fails.
     */
    @DisplayName("存在する本削除申請")
    @Test
    void removeBook() throws DbException, DaoException, BookException {
      int id = 1;
      ResponseBooks expected = new ResponseBooks();
      expected.getMessageHeader().setMessage(BOOK_DELETED);

      when(dao.deleteBook(id)).thenReturn(1);

      ResponseBooks actual = dbl.removeBook(id);

      verify(dao, times(1)).deleteBook(anyInt());
      Assert.assertEquals(expected.getMessageHeader().getMessage(),actual.getMessageHeader().getMessage());

    }

    /**
     * This test checks the behavior of the {@link com.example.demo.backend.BookBusinessLogic DemoBusinessLogic} class' {@link com.example.demo.backend.BookBusinessLogic#removeBook(Integer)},
     *  when the method throws a DaoException.
     * ({@link com.example.demo.data.access.JdbcBookDao JdbcBookDao} or {@link com.example.demo.data.access.SpringBookDao SpringBookDao}).
     * @throws DaoException An exception that raises when executing an SQL query fails.
     * @throws DbException An exception that raises when the database connection/disconnection fails.
     */
    @DisplayName("本の削除でDao例外が投げられる場合")
    @Test
    void removeBook_Dao() throws DbException, DaoException {
      int id = 1;
      Throwable expected = new DaoException("This is fake");
      when(dao.deleteBook(id)).thenThrow(expected);

      Throwable actual = assertThrows(expected.getClass(), () -> {dbl.removeBook(id);});
      verify(dao, times(1)).deleteBook(anyInt());
    }

    /**
     * This test checks the behavior of the {@link com.example.demo.backend.BookBusinessLogic DemoBusinessLogic} class' {@link com.example.demo.backend.BookBusinessLogic#removeBook(Integer)},
     *  when the method throws a DbException.
     * ({@link com.example.demo.data.access.JdbcBookDao JdbcBookDao} or {@link com.example.demo.data.access.SpringBookDao SpringBookDao}).
     * @throws DaoException An exception that raises when executing an SQL query fails.
     */
    @DisplayName("本の削除でDb例外が投げられる場合")
    @Test
    void removeBook_Db() throws DaoException {
      int id = 1;
      Throwable fakeOutput = new DaoException("This is fake");
      when(dao.deleteBook(id)).thenThrow(fakeOutput);

      Throwable e = assertThrows(fakeOutput.getClass(), () -> {dbl.removeBook(id);});
      verify(dao, times(1)).deleteBook(anyInt());
    }
  }




  @DisplayName("本の検索に関するテスト")
  @Nested
  class test3{
    /**
     * This test checks the behavior of the {@link com.example.demo.backend.BookBusinessLogic DemoBusinessLogic} class' {@link com.example.demo.backend.BookBusinessLogic#getBook(Integer)},
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
      verify(dao, times(1)).getBook(anyInt());
    }

    /**
     * This test checks the behavior of the {@link com.example.demo.backend.BookBusinessLogic DemoBusinessLogic} class' {@link com.example.demo.backend.BookBusinessLogic#getBook(Integer)},
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
      expected.getMessageHeader().setMessage(BOOK_FOUND);

      List<Book> fakeList = new ArrayList<>();
      fakeList.add(new Book("title", 1000,"https://fake.com",10));
      when(dao.getBook(id)).thenReturn(fakeList);

      ResponseBooks actual = dbl.getBook(id);

      verify(dao, times(1)).getBook(anyInt());
      Assert.assertEquals(expected.getMessageHeader().getMessage(),actual.getMessageHeader().getMessage());
    }

    /**
     * This test checks the behavior of the {@link com.example.demo.backend.BookBusinessLogic DemoBusinessLogic} class' {@link com.example.demo.backend.BookBusinessLogic#getBook(Integer)},
     *  when the DbException is thrown.
     * ({@link com.example.demo.data.access.JdbcBookDao JdbcBookDao} or {@link com.example.demo.data.access.SpringBookDao SpringBookDao}).
     * @throws DaoException An exception that raises when executing an SQL query fails.
     */
    @DisplayName("本の検索でDb例外が投げられる場合")
    @Test
    void getBook_Db() throws DaoException {
      int id = 1;
      Throwable fakeOutput = new DaoException("This is fake");
      when(dao.getBook(id)).thenThrow(fakeOutput);

      Throwable e = assertThrows(fakeOutput.getClass(), () -> {dbl.getBook(id);});
      verify(dao, times(1)).getBook(anyInt());
    }

    /**
     * This test checks the behavior of the {@link com.example.demo.backend.BookBusinessLogic DemoBusinessLogic} class' {@link com.example.demo.backend.BookBusinessLogic#getBook(Integer)},
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
      verify(dao, times(1)).getBook(anyInt());
    }
  }


  @DisplayName("本の追加に関するテスト")
  @Nested
  class test4{

    /**
     * This test checks the behavior of the {@link com.example.demo.backend.BookBusinessLogic DemoBusinessLogic} class' {@link com.example.demo.backend.BookBusinessLogic#addBook(Book)},
     *  when the data is correctly inserted to the database.
     * ({@link com.example.demo.data.access.JdbcBookDao JdbcBookDao} or {@link com.example.demo.data.access.SpringBookDao SpringBookDao}).
     * @throws DaoException An exception that raises when executing an SQL query fails.
     * @throws DbException An exception that raises when the database connection/disconnection fails.
     */
    @DisplayName("本の追加")
    @Test
    void addBook() throws DbException, DaoException, BookException {
      Book book = new Book("newTitle",1000,"https://fake.com",10);
      when(dao.insertBook(book)).thenReturn(1);

      ResponseBooks actual = dbl.addBook(book);
      verify(dao, times(1)).insertBook(isA(Book.class));
      assertEquals(actual.getMessageHeader().getMessage(),BOOK_INSERTED);
    }


    /**
     * This test checks the behavior of the {@link com.example.demo.backend.BookBusinessLogic DemoBusinessLogic} class' {@link com.example.demo.backend.BookBusinessLogic#addBook(Book)},
     *  when the data is correctly inserted to the database.
     * ({@link com.example.demo.data.access.JdbcBookDao JdbcBookDao} or {@link com.example.demo.data.access.SpringBookDao SpringBookDao}).
     * @throws DaoException An exception that raises when executing an SQL query fails.
     * @throws DbException An exception that raises when the database connection/disconnection fails.
     */
    @DisplayName("すでに登録されている本の追加")
    @Test
    void addBook_DUPLICATE() throws DbException, DaoException {
      Book book = new Book("newTitle",1000,"https://fake.com",10);

      DaoException fakeOutput = new DaoException("This is fake");
      fakeOutput.setSqlCode(SQL_CODE_DUPLICATE_KEY_ERROR);

      when(dao.insertBook(book)).thenThrow(fakeOutput);

      BookException expected = new BookException("This is fake");

      Throwable e = assertThrows(expected.getClass(), () -> {dbl.addBook(book);});
      verify(dao, times(1)).insertBook(isA(Book.class));
    }


    /**
     * This test checks the behavior of the {@link com.example.demo.backend.BookBusinessLogic DemoBusinessLogic} class' {@link com.example.demo.backend.BookBusinessLogic#addBook(Book)},
     *  when the DbException is thrown.
     * ({@link com.example.demo.data.access.JdbcBookDao JdbcBookDao} or {@link com.example.demo.data.access.SpringBookDao SpringBookDao}).
     * @throws DaoException An exception that raises when executing an SQL query fails.
     */
    @DisplayName("本の追加時にDb例外")
    @Test
    void addBook_Db() throws DaoException {
      Book book = new Book("newTitle",1000,"https://fake.com",10);

      DaoException fakeOutput = new DaoException("This is fake");

      when(dao.insertBook(book)).thenThrow(fakeOutput);

      Throwable e = assertThrows(fakeOutput.getClass(), () -> {dbl.addBook(book);});
      verify(dao, times(1)).insertBook(isA(Book.class));
    }

    /**
     * This test checks the behavior of the {@link com.example.demo.backend.BookBusinessLogic DemoBusinessLogic} class' {@link com.example.demo.backend.BookBusinessLogic#addBook(Book)},
     *  when the DaoException is thrown.
     * ({@link com.example.demo.data.access.JdbcBookDao JdbcBookDao} or {@link com.example.demo.data.access.SpringBookDao SpringBookDao}).
     * @throws DaoException An exception that raises when executing an SQL query fails.
     * @throws DbException An exception that raises when the database connection/disconnection fails.
     */
    @DisplayName("本の追加時にDao例外")
    @Test
    void addBook_Dao() throws DbException, DaoException {
      Book book = new Book("newTitle",1000,"https://fake.com",10);

      DaoException fakeOutput = new DaoException("This is fake");

      when(dao.insertBook(book)).thenThrow(fakeOutput);

      Throwable e = assertThrows(fakeOutput.getClass(), () -> {dbl.addBook(book);});
      verify(dao, times(1)).insertBook(isA(Book.class));
    }

  }



  @DisplayName("本データの置き換えに関するテスト")
  @Nested
  class test5{

    /**
     * This test checks the behavior of the {@link com.example.demo.backend.BookBusinessLogic DemoBusinessLogic} class' {@link com.example.demo.backend.BookBusinessLogic#replaceBook(Integer, Book)},
     *  when the data is correctly replaced.
     * ({@link com.example.demo.data.access.JdbcBookDao JdbcBookDao} or {@link com.example.demo.data.access.SpringBookDao SpringBookDao}).
     * @throws DaoException An exception that raises when executing an SQL query fails.
     * @throws DbException An exception that raises when the database connection/disconnection fails.
     */
    @DisplayName("本データの置き換え")
    @Test
    void replaceBook() throws DbException, DaoException, BookException {
      Integer bookId = 1;
      Book book = new Book("newTitle",1000,"https://fake.com",10);
      when(dao.updateBook_data(bookId,book)).thenReturn(1);
      ResponseBooks actual = dbl.replaceBook(bookId,book);
      verify(dao, times(1)).updateBook_data(anyInt(),isA(Book.class));
      assertEquals(actual.getMessageHeader().getMessage(), UPDATE_SUCCESS_BOOK);
    }

    @DisplayName("本データの置き換えですでに保存されている本のタイトルを指定")
    @Test
    void replaceBook_DUPLICATE() throws DbException, DaoException {
      Integer bookId = 0;
      Book book = new Book("newTitle",1000,"https://fake.com",10);

      DaoException fakeOutput = new DaoException("This is fake");
      fakeOutput.setSqlCode(SQL_CODE_DUPLICATE_KEY_ERROR);

      when(dao.updateBook_data(bookId,book)).thenThrow(fakeOutput);

      BookException expected = new BookException(BOOK_DUPLICATE);

      Throwable e = assertThrows(expected.getClass(), () -> {dbl.replaceBook(bookId,book);});
      verify(dao, times(1)).updateBook_data(anyInt(),isA(Book.class));
      assertEquals(e.getMessage(),expected.getMessage());
    }

    @DisplayName("存在しない本データの置き換え")
    @Test
    void replaceBook_NOT_EXISTING() throws DbException, DaoException {
      Integer bookId = 0;
      Book book = new Book("newTitle",1000,"https://fake.com",10);

      when(dao.updateBook_data(bookId,book)).thenReturn(0);

      BookException expected = new BookException(BOOK_NOT_EXISTING);

      Throwable e = assertThrows(expected.getClass(), () -> {dbl.replaceBook(bookId,book);});
      verify(dao, times(1)).updateBook_data(anyInt(),isA(Book.class));
    }



    @DisplayName("本データの置き換えでDb例外")
    @Test
    void replaceBook_Db() throws DaoException {
      Integer bookId = 0;
      Book book = new Book("newTitle",1000,"https://fake.com",10);

      DaoException fakeOutput = new DaoException("This is fake");

      when(dao.updateBook_data(bookId,book)).thenThrow(fakeOutput);

      Throwable e = assertThrows(fakeOutput.getClass(), () -> {dbl.replaceBook(bookId,book);});
      verify(dao, times(1)).updateBook_data(anyInt(),isA(Book.class));
    }

    @DisplayName("本データの置き換えでDao例外")
    @Test
    void replaceBook_Dao() throws DaoException {
      Integer bookId = 0;
      Book book = new Book("newTitle",1000,"https://fake.com",10);

      DaoException fakeOutput = new DaoException("This is fake");

      when(dao.updateBook_data(bookId,book)).thenThrow(fakeOutput);

      Throwable e = assertThrows(fakeOutput.getClass(), () -> {dbl.replaceBook(bookId,book);});
      verify(dao, times(1)).updateBook_data(anyInt(),isA(Book.class));
    }

  }





  @DisplayName("本の貸し出し情報更新に関するテスト")
  @Nested
  class test6{

    /**
     * This test checks the behavior of the {@link com.example.demo.backend.BookBusinessLogic DemoBusinessLogic} class' {@link com.example.demo.backend.BookBusinessLogic#updateBook(Integer, PatchBook)},
     *  when the data is correctly patched as borrowed.
     * ({@link com.example.demo.data.access.JdbcBookDao JdbcBookDao} or {@link com.example.demo.data.access.SpringBookDao SpringBookDao}).
     * @throws DaoException An exception that raises when executing an SQL query fails.
     * @throws DbException An exception that raises when the database connection/disconnection fails.
     */
    @Test
    @DisplayName("本を正しく借りることができた場合")
    void updateBook_BORROWED() throws DbException, DaoException, BookException {
      //TODO
      PatchBook book = new PatchBook();
      String borrower = "08011110000";
      book.setBorrower(borrower);
      book.setStatus(0);//Borrow
      Integer bookId = 1;
      when(dao.checkBookStatus(bookId,borrower)).thenReturn(BookStatus.BOOK_NOT_BORROWED_BY_THIS_USER);
      when(dao.checkBookStockAvailability(bookId)).thenReturn(true);

      ResponseBooks actual = dbl.updateBook(bookId,book);
      verify(dao, times(1)).checkBookStatus(anyInt(),anyString());
      verify(dao, times(1)).checkBookStockAvailability(anyInt());
      verify(dao, times(1)).updateBook_borrowed(anyInt(), anyString());
      assertEquals(actual.getMessageHeader().getMessage(), BOOK_BORROWED);
    }


    /**
     * This test checks the behavior of the {@link com.example.demo.backend.BookBusinessLogic DemoBusinessLogic} class' {@link com.example.demo.backend.BookBusinessLogic#updateBook(Integer, PatchBook)},
     *  when the same book is already borrowed.
     * ({@link com.example.demo.data.access.JdbcBookDao JdbcBookDao} or {@link com.example.demo.data.access.SpringBookDao SpringBookDao}).
     * @throws DaoException An exception that raises when executing an SQL query fails.
     * @throws DbException An exception that raises when the database connection/disconnection fails.
     */
    @Test
    @DisplayName("同じ本を既に借りている時に、また借りようとした場合")
    void updateBook_BORROWED_AGAIN() throws DbException, DaoException, BookException {
      //TODO
      PatchBook book = new PatchBook();
      String borrower = "08011110000";
      book.setBorrower(borrower);
      book.setStatus(0);//Borrow
      Integer bookId = 1;
      when(dao.checkBookStatus(bookId,borrower)).thenReturn(BookStatus.BOOK_BORROWED_BY_THIS_USER);
      when(dao.checkBookStockAvailability(bookId)).thenReturn(true);

      BookException expected = new BookException(BOOK_CANNOT_BE_DOUBLE_BORROWED);
      Throwable actual = assertThrows(expected.getClass(),()->{dbl.updateBook(bookId,book);});
      verify(dao, times(1)).checkBookStatus(anyInt(),anyString());
      assertEquals(expected.getMessage(), actual.getMessage());
    }



    /**
     * This test checks the behavior of the {@link com.example.demo.backend.BookBusinessLogic DemoBusinessLogic} class' {@link com.example.demo.backend.BookBusinessLogic#updateBook(Integer, PatchBook)},
     *  when the book has no stock left.
     * ({@link com.example.demo.data.access.JdbcBookDao JdbcBookDao} or {@link com.example.demo.data.access.SpringBookDao SpringBookDao}).
     * @throws DaoException An exception that raises when executing an SQL query fails.
     * @throws DbException An exception that raises when the database connection/disconnection fails.
     */
    @Test
    @DisplayName("本の借り出しを行うための在庫がない場合")
    void updateBook_NO_STOCK() throws DbException, DaoException, BookException {
      PatchBook book = new PatchBook();
      String borrower = "08011110000";
      book.setBorrower(borrower);
      book.setStatus(0);//Borrow
      Integer bookId = 1;
      when(dao.checkBookStatus(bookId,borrower)).thenReturn(BookStatus.BOOK_NOT_BORROWED_BY_THIS_USER);
      when(dao.checkBookStockAvailability(bookId)).thenReturn(false);

      BookException expected = new BookException(BOOK_NO_STOCK);
      Throwable actual = assertThrows(expected.getClass(),()->{dbl.updateBook(bookId,book);});
      verify(dao, times(1)).checkBookStatus(anyInt(),anyString());
      verify(dao, times(1)).checkBookStockAvailability(anyInt());
      assertEquals(expected.getMessage(), actual.getMessage());
    }


    /**
     * This test checks the behavior of the {@link com.example.demo.backend.BookBusinessLogic DemoBusinessLogic} class' {@link com.example.demo.backend.BookBusinessLogic#updateBook(Integer, PatchBook)},
     *  when the book doesn't exist.
     * ({@link com.example.demo.data.access.JdbcBookDao JdbcBookDao} or {@link com.example.demo.data.access.SpringBookDao SpringBookDao}).
     * @throws DaoException An exception that raises when executing an SQL query fails.
     * @throws DbException An exception that raises when the database connection/disconnection fails.
     */
    @Test
    @DisplayName("存在しない本を借りようとする場合")
    void updateBook_NO_() throws DbException, DaoException, BookException {
      PatchBook book = new PatchBook();
      String borrower = "08011110000";
      book.setBorrower(borrower);
      book.setStatus(0);//Borrow
      Integer bookId = 1;
      when(dao.checkBookStatus(bookId,borrower)).thenReturn(BookStatus.BOOK_NOT_EXISTING);
      when(dao.checkBookStockAvailability(bookId)).thenReturn(true);

      BookException expected = new BookException(BOOK_NOT_EXISTING);
      Throwable actual = assertThrows(expected.getClass(),()->{dbl.updateBook(bookId,book);});
      verify(dao, times(1)).checkBookStatus(anyInt(),anyString());
      assertEquals(expected.getMessage(), actual.getMessage());
    }
  }


  @DisplayName("本の返却に関するテスト")
  @Nested
  class test7{
    /**
     * This test checks the behavior of the {@link com.example.demo.backend.BookBusinessLogic DemoBusinessLogic} class' {@link com.example.demo.backend.BookBusinessLogic#updateBook(Integer, PatchBook)},
     *  when the book is returned correclty
     * ({@link com.example.demo.data.access.JdbcBookDao JdbcBookDao} or {@link com.example.demo.data.access.SpringBookDao SpringBookDao}).
     * @throws DaoException An exception that raises when executing an SQL query fails.
     * @throws DbException An exception that raises when the database connection/disconnection fails.
     */
    @Test
    @DisplayName("本の正しい返却")
    void updateBook_RETURN() throws DbException, DaoException, BookException {
      PatchBook book = new PatchBook();
      String borrower = "08011110000";
      book.setBorrower(borrower);
      book.setStatus(1);//Return
      Integer bookId = 1;
      when(dao.checkBookStatus(bookId,borrower)).thenReturn(BookStatus.BOOK_BORROWED_BY_THIS_USER);

      ResponseBooks actual = dbl.updateBook(bookId, book);
      verify(dao, times(1)).checkBookStatus(anyInt(),anyString());
      verify(dao, times(1)).updateBook_returned(anyInt(),anyString());
      assertEquals(actual.getMessageHeader().getMessage(), BOOK_RETURNED);
    }


    /**
     * This test checks the behavior of the {@link com.example.demo.backend.BookBusinessLogic DemoBusinessLogic} class' {@link com.example.demo.backend.BookBusinessLogic#updateBook(Integer, PatchBook)},
     *  when the book being return but is not borrowed first of all.
     * ({@link com.example.demo.data.access.JdbcBookDao JdbcBookDao} or {@link com.example.demo.data.access.SpringBookDao SpringBookDao}).
     * @throws DaoException An exception that raises when executing an SQL query fails.
     * @throws DbException An exception that raises when the database connection/disconnection fails.
     */
    @Test
    @DisplayName("借りていない本の返却")
    void updateBook_RETURN_NOT_BORROWED() throws DbException, DaoException, BookException {
      PatchBook book = new PatchBook();
      String borrower = "08011110000";
      book.setBorrower(borrower);
      book.setStatus(1);//Return
      Integer bookId = 1;
      when(dao.checkBookStatus(bookId,borrower)).thenReturn(BookStatus.BOOK_NOT_BORROWED_BY_THIS_USER);

      BookException expected = new BookException(BOOK_CANNOT_BE_RETURNED);
      Throwable actual = assertThrows(expected.getClass(),()->{dbl.updateBook(bookId,book);});
      verify(dao, times(1)).checkBookStatus(anyInt(),anyString());
      verify(dao, times(0)).updateBook_returned(anyInt(),anyString());
      assertEquals(expected.getMessage(), actual.getMessage());

    }

  }


  @DisplayName("本の紛失に関するテスト")
  @Nested
  class test8 {

    /**
     * This test checks the behavior of the {@link com.example.demo.backend.BookBusinessLogic DemoBusinessLogic} class' {@link com.example.demo.backend.BookBusinessLogic#updateBook(Integer, PatchBook)},
     *  when the book being lost but is not borrowed first of all.
     * ({@link com.example.demo.data.access.JdbcBookDao JdbcBookDao} or {@link com.example.demo.data.access.SpringBookDao SpringBookDao}).
     * @throws DaoException An exception that raises when executing an SQL query fails.
     * @throws DbException An exception that raises when the database connection/disconnection fails.
     */
    @Test
    @DisplayName("借りていない本の紛失")
    void updateBook_LOST_NOT_BORROWED() throws DbException, DaoException, BookException {
      PatchBook book = new PatchBook();
      String borrower = "08011110000";
      book.setBorrower(borrower);
      book.setStatus(2);//LOST
      Integer bookId = 1;
      when(dao.checkBookStatus(bookId,book.getBorrower())).thenReturn(BookStatus.BOOK_NOT_BORROWED_BY_THIS_USER);


      BookException expected = new BookException(BOOK_CANNOT_BE_LOST);
      Throwable actual = assertThrows(expected.getClass(),()->{dbl.updateBook(bookId,book);});
      verify(dao, times(1)).checkBookStatus(anyInt(),anyString());
      verify(dao, times(0)).updateBook_lost(anyInt(),anyString());
      assertEquals(expected.getMessage(), actual.getMessage());
    }


    /**
     * This test checks the behavior of the {@link com.example.demo.backend.BookBusinessLogic DemoBusinessLogic} class' {@link com.example.demo.backend.BookBusinessLogic#updateBook(Integer, PatchBook)},
     *  when the book being lost but is not borrowed first of all.
     * ({@link com.example.demo.data.access.JdbcBookDao JdbcBookDao} or {@link com.example.demo.data.access.SpringBookDao SpringBookDao}).
     * @throws DaoException An exception that raises when executing an SQL query fails.
     * @throws DbException An exception that raises when the database connection/disconnection fails.
     */
    @Test
    @DisplayName("正しい本の紛失、および削除")
    void updateBook_LOST() throws DbException, DaoException, BookException {
      PatchBook book = new PatchBook();
      String borrower = "08011110000";
      book.setBorrower(borrower);
      book.setStatus(2);//LOST
      Integer bookId = 1;
      when(dao.checkBookStatus(bookId,borrower)).thenReturn(BookStatus.BOOK_BORROWED_BY_THIS_USER);
      List<Book> fakeList = new ArrayList<>();
      fakeList.add(new Book("title", 1000,"https://fake.com",0));
      when(dao.getBook(bookId)).thenReturn(fakeList);
      ResponseBooks actual = dbl.updateBook(bookId, book);
      verify(dao, times(1)).checkBookStatus(anyInt(),anyString());
      verify(dao, times(1)).updateBook_lost(anyInt(),anyString());
      verify(dao, times(1)).deleteBook(anyInt());
      assertEquals(BOOK_LOST_AND_DELETED, actual.getMessageHeader().getMessage());
    }
  }








}