package com.example.demo.application;

import com.example.demo.backend.custom.exceptions.DaoException;
import com.example.demo.backend.custom.exceptions.DbException;
import com.example.demo.backend.custom.Dto.BookUser;
import com.example.demo.backend.custom.Dto.PatchBookClass;
import com.example.demo.data.access.BookDaoTest;
import com.example.demo.backend.custom.exceptions.InputFormatExeption;
import com.example.demo.backend.custom.Dto.BookClass;
import com.example.demo.backend.custom.Dto.ResponseBooks;
import org.junit.FixMethodOrder;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import java.lang.reflect.InvocationTargetException;


import static com.example.demo.application.InputValidator.assureBookClassNames;
import static com.example.demo.backend.errormessages.StaticMessages.BOOK_BORROWED;
import static com.example.demo.backend.errormessages.StaticMessages.UPDATE_SUCCESS_BOOK;
import static com.example.demo.backend.errormessages.StaticMessages.BOOK_RETURNED;
import static com.example.demo.backend.errormessages.StaticMessages.BOOK_LOST;
import static com.example.demo.backend.errormessages.StaticMessages.BOOK_DELETED;
import static com.example.demo.backend.errormessages.StaticMessages.BOOK_INSERTED;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;



@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@ExtendWith(SpringExtension.class)
@SpringBootTest
public class DemoControllerSuccessTest {



  @Autowired
  DemoController controller;



  @BeforeAll
  static void initAll() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, DbException, DaoException {
    BookDaoTest.dropBookshelf();
    BookDaoTest.dropBookUser();
    BookDaoTest.createBookshelf();
    BookDaoTest.createBookUser();
    //BookDaoTest.fillInBookUser();
  }


  @ParameterizedTest
  @CsvSource({
    "English, Johnny,00011110000",
    "Whatever, Whatsoever,00011110001",
    "Fax, Machine,00011110002",
    "Vending, Machine,00011110003",
    "Goda, Takeshi,00011110004",
    "のび, のび太,00011110005",
    "Family, First,00011110006",
    "白い, 犬,00011110007",
    "茶色い, 猫,12345678910",
    "松尾, 賢明,00011110008",
    "Toto, Tata,00011110009",
    "AES, RSA,00000000000",
    "Munro, Bill,00000000001",
    "Shota, Nagayama,00000000002"
  })
  @DisplayName("ユーザの追加")
  void test0(String familyName, String firstName, String phoneNumber) throws DbException, DaoException, InputFormatExeption {
    BookUser test = new BookUser(familyName, firstName, phoneNumber);
    controller.postUser(test);
  }


  @ParameterizedTest
  @CsvSource({
    "First book ever, 100,2,https://a.example.com",
    "マイクロソフトの本, 2000,1,https://a.example.com",
    "Java SE11 Silver 問題集, 3200,4,https://a.example.com",
    "Java SE11 Gold 問題集, 3200,1,https://a.example.com",
    "ABCDEFG, 1200,1,https://a.example.com",
    "あいうえお, 1200,1,https://a.example.com",
    "白い犬の本, 1200,1,https://a.example.com",
    "猫の写真集, 2200,1,https://a.example.com",
    "Java SE11 Silver 参考書, 3400,1,https://a.example.com",
    "1231232, 1200,1,https://a.example.com",
    "1, 1200,1,\'\'",
    "\"\", 1200,10000000,\'\'",
    "無料の本,0,1,https://cheap.example.com"
  })
  @DisplayName("本の追加")
  void test1(String title, int price, int quantity, String url) throws DbException, DaoException, InputFormatExeption {
    BookClass test = new BookClass(title, price, url, quantity);
    ResponseBooks books = controller.postBook(test);
    assertEquals(BOOK_INSERTED,books.getResponseHeader().getMessage());
  }



  /*An example of how to mix success and failure in a single parameterized test.*/
  @ParameterizedTest
  @CsvSource({
    "1, true",
    "2, true",
    "3, true",
    "4, true",
    "5, true",
    "6, true",
    "7, true",
    "8, true",
    "9, true",
    "10, true",
    "11, true",
    "12, true",
    "13, true"
  })
  @DisplayName("本の検索")
  void test2(String i, boolean expected) throws InputFormatExeption, DbException, DaoException {
    ResponseBooks books = controller.getBook(i);
    if(books.getBooks().size()>0)
      assertTrue((books.getBooks().get(0).getId() == Integer.parseInt(i)) == expected);
    else
      assertTrue(false == expected);
  }


  @Test
  @DisplayName("本の全検索")
  void test3() throws InputFormatExeption, DbException{
    ResponseBooks books = controller.getBooks();
    for(BookClass b : books.getBooks()){
      assureBookClassNames(b);
    }
  }


  @ParameterizedTest
  @CsvSource({
    "5,Beaver book, 100,2,https://a.example.com",
    "6,googleの本, 2000,1,https://a.example.com",
    "10,まみむめも, 2300,4,https://a.example.com",
  })
  @DisplayName("本データの変更")
  void test4(String bookId, String title, int price, int quantity, String url) throws InputFormatExeption {
    BookClass test = new BookClass(title, price, url, quantity);
    ResponseBooks books = controller.putBook(bookId, test);
    assertEquals(UPDATE_SUCCESS_BOOK, books.getResponseHeader().getMessage());
    for(BookClass b : books.getBooks()){
      assureBookClassNames(b);
    }
  }

  @ParameterizedTest
  @CsvSource({
    "1, 00011110007, 0",
    "1, 00011110008, 0",
    "4, 00011110007, 0",
    "3, 00011110008, 0",
    "12, 00000000000, 0",
    "11, 00000000001, 0",
    "10, 00000000001, 0",
    "9, 00000000001, 0",
    "8, 00000000001, 0",
    "7, 00000000001, 0"
  })
  @DisplayName("本の貸し出し")
  void test5(String bookId, String phoneNumber, int action) {
    PatchBookClass pb = new PatchBookClass();
    pb.setBorrower(phoneNumber);
    pb.setStatus(action);
    ResponseBooks books = controller.patchBook(bookId, pb);
    assertEquals(BOOK_BORROWED, books.getResponseHeader().getMessage());
  }

  @ParameterizedTest
  @CsvSource({
    "1, 00011110007, 1",
    "1, 00011110008, 1",
    "4, 00011110007, 1",
    "3, 00011110008, 1",
    "12, 00000000000, 1"
  })
  @DisplayName("本の返却")
  void test6(String bookId, String phoneNumber, int action) {
    PatchBookClass pb = new PatchBookClass();
    pb.setBorrower(phoneNumber);
    pb.setStatus(action);
    ResponseBooks books = controller.patchBook(bookId, pb);
    assertEquals(BOOK_RETURNED, books.getResponseHeader().getMessage());
  }

  @ParameterizedTest
  @CsvSource({
    "11, 00000000001, 2",
    "10, 00000000001, 2",
    "9, 00000000001, 2",
    "8, 00000000001, 2",
    "7, 00000000001, 2"
  })
  @DisplayName("本の紛失")
  void test7(String bookId, String phoneNumber, int action) {
    PatchBookClass pb = new PatchBookClass();
    pb.setBorrower(phoneNumber);
    pb.setStatus(action);
    ResponseBooks books = controller.patchBook(bookId, pb);
    assertEquals(BOOK_LOST, books.getResponseHeader().getMessage());
  }


  @ParameterizedTest
  @CsvSource({
    "1",
    "2",
    "3"
  })
  @DisplayName("本の削除")
  void test8(String bookId) {
    ResponseBooks books = controller.deleteBook(bookId);
    assertEquals(BOOK_DELETED, books.getResponseHeader().getMessage());
  }




}