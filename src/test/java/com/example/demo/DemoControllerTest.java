package com.example.demo;
import static com.example.demo.InputValidator.*;

import com.example.demo.Backend.CustomExceptions.DaoException;
import com.example.demo.Backend.CustomExceptions.DbException;
import com.example.demo.Backend.CustomObjects.BookUser;
import com.example.demo.DataAccess.BookDao;
import com.example.demo.DataAccess.BookUserDao;
import com.example.demo.DataAccess.BookDaoTest;
import com.example.demo.Backend.CustomENUMs.ResponseStatus;
import com.example.demo.Backend.CustomExceptions.InputFormatExeption;
import com.example.demo.Backend.CustomObjects.BookClass;
import com.example.demo.Backend.CustomObjects.ResponseBooks;
import com.example.demo.Backend.DemoBusinessLogic;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.rules.ExpectedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;

import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class DemoControllerTest {

    @Autowired
    @Qualifier("JdbcBookDao")
    BookDao bDao;

    @Autowired
    BookUserDao uDao;

    @Autowired
    DemoController controller;

    @BeforeAll
    static void initAll() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        BookDaoTest bt = new BookDaoTest();
        bt.dropBookshelf();
        bt.dropBookUser();
        bt.createBookshelf();
        bt.createBookUser();
    }


    @ParameterizedTest
    @CsvSource({
            "English, Johnny,00011110000",
            "Whatever, Whatsoever,00011110001",
            "Fax, Machine,00011110002",
            "Vending, Drink,00011110003",
            "AA, Fer,00011110004",
            "BA, Air,00011110005",
            "CA, Fir,00011110006",
            "DA, Fir,00011110007",
            "松尾, 賢明,00011110008",
            "Toto, Tata,00011110009",
            "AES, RSA,00000000000",
            "Munro, Bill,00000000001",
            "Shota, Nagayama,00000000002"
    })
    void insertBookUser_SUCCESS(String familyName, String firstName, String phoneNumber) throws DbException, DaoException {
        BookUser test = new BookUser(familyName, firstName, phoneNumber);
        uDao.insertBookUser(test);
    }


    @ParameterizedTest
    @CsvSource({
            "First book ever, 100,2,https://a.example.com",
            "マイクロソフトの本, 2000,1,https://a.example.com",
            "Java SE11 Silver 問題集, 3200,4,https://a.example.com",
            "Java SE11 Gold 問題集, 3200,1,https://a.example.com",
            "ABCDEFG, 1200,1,https://a.example.com",
            "あいうえお, 1200,1,https://a.example.com",
            "カキクケコ, 1200,1,https://a.example.com",
            "Java SE11 Silver 参考書, 3400,1,https://a.example.com",
            "1231232, 1200,1,https://a.example.com",
            "1, 1200,1,\'\'",
            "\"\", 1200,10000000,\'\'",
            "無料の本,0,1,https://cheap.example.com"
    })
    void insertBookShelf_SUCCESS(String title, int price, int quantity, String url) throws DbException, DaoException {
        BookClass test = new BookClass(title, price, url, quantity);
        bDao.insertBook(test);
    }



    /*An example of how to mix success and failure in a single parameterized test.*/
    @ParameterizedTest
    @CsvSource({
            "-2120, false",
            "0, false",
            "1, true",
            "2, true",
            "3, true",
            "4, true",
            "5, true",
            "8, true",
            "19, false",/*Not in database*/
            "18, false",
            ",false"
    })
    void TEST_GET_book(String i, boolean expected) {
        ResponseBooks books = controller.getBook(i);
        if(books.getBooks().size()>0)
            assertTrue((books.getBooks().get(0).getId() == Integer.parseInt(i)) == expected);
        else
            assertTrue(false == expected);
    }




    @Test
    void TEST_GET_books() throws InputFormatExeption{
        Class<? extends Exception> expectedException = null;//No error expected.
        ExpectedException thrown = ExpectedException.none();

        ResponseBooks books = controller.getBooks();
        if (InputFormatExeption.class != null) {
            thrown.expect(expectedException);
        }
        for(BookClass b : books.getBooks()){
            assureBookClassNames(b);
        }
    }




    @Test
    void TEST_POST_books_EMPTY() throws InputFormatExeption, SQLException {
        BookClass test = new BookClass();
        ResponseBooks books = new DemoController().postBook(test);
        assertTrue(books.getResponseHeader().getStatus()== ResponseStatus.ERR);
    }

    @ParameterizedTest
    @CsvSource({
            //"Title, 1,1,https://a.example.com, 0",
            ", 1,1,https://a.example.com",
            "AA, -1,1,https://a.example.com",
            "BB, 1,-100,https://a.example.com",
            ",1,1,",
            ",-1,-1,"
    })
    void TEST_POST_books_InputFormatExeption(String title, int price, int quantity, String url) throws InputFormatExeption, SQLException {
        Class<? extends Exception> expectedException = InputFormatExeption.class;
        assertThrows(expectedException,()->{
            BookClass test = new BookClass(title, price, url, quantity);
            ResponseBooks response = new ResponseBooks();
            response = new DemoBusinessLogic().addBook(assureBookClass(test));});
    }



    @ParameterizedTest
    @CsvSource({
            "1,Bever, 100,2,https://a.example.com",
            "1,googleの本, 2000,1,https://a.example.com",
            "2,まみむめも, 2300,4,https://a.example.com",
            "あいうえお, 1200,1,https://a.example.com",
            "あいうえ, 1200,1,https://a.example.com",
            "あいう, 1200,1,https://a.example.com",
            "あい, 1200,1,https://a.example.com",
            "Java SE11 Silver問題集, 3400,1,https://a.example.com",
            "1231232, 1200,1,https://a.example.com",
            "1, 1200,1,\"\"",
            "\"\", 1200,10000000,\"\""
    })
    void PUT_book() {
    }

    @Test
    void PATCH_book() {
    }

    @Test
    void DELETE_book() {
    }
}