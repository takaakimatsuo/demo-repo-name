package com.example.demo.backend;

import com.example.demo.backend.custom.myexceptions.DaoException;
import com.example.demo.backend.custom.myexceptions.DbException;
import com.example.demo.backend.custom.Dto.BookClass;
import com.example.demo.backend.custom.Dto.ResponseBooks;
import com.example.demo.data.access.BookDaoTest;
import org.junit.FixMethodOrder;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.lang.reflect.InvocationTargetException;

import static com.example.demo.backend.errormessages.StaticMessages.BOOK_DUPLICATE;
import static org.junit.jupiter.api.Assertions.assertEquals;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@ExtendWith(SpringExtension.class)
@SpringBootTest
class DemoBusinessLogicTest {

  @BeforeAll
  static void initAll() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, DbException, DaoException {
    BookDaoTest.dropBookshelf();
    BookDaoTest.dropBookUser();
    BookDaoTest.createBookshelf();
    BookDaoTest.createBookUser();
    BookDaoTest.fillInBookUser();
    BookDaoTest.fillInBooks();

  }

  @Autowired
  DemoBusinessLogic dbl;


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
  @DisplayName("本の重複追加: addBook(BookClass book)")
  void test1(String title, int price, int quantity, String url) throws DbException, DaoException {
    BookClass test = new BookClass(title, price, url, quantity);
    ResponseBooks books = dbl.addBook(test);
    assertEquals(BOOK_DUPLICATE,books.getResponseHeader().getMessage());
  }
}