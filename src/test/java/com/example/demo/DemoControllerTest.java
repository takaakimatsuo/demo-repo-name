package com.example.demo;
import static com.example.demo.InputValidator.*;

import com.example.demo.DataAccess.BookDao;
import com.example.demo.DataAccess.BookDaoTest;
import com.example.demo.Backend.CustomENUMs.response_status;
import com.example.demo.Backend.CustomExceptions.InputFormatExeption;
import com.example.demo.Backend.CustomObjects.BookClass;
import com.example.demo.Backend.CustomObjects.BookUser;
import com.example.demo.Backend.CustomObjects.ResponseBooks;
import com.example.demo.Backend.DemoBusinessLogic;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.rules.ExpectedException;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;

@SpringBootTest
class DemoControllerTest {

    static BookDao bookDao = new BookDao();

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
            "Family, Fir,00011110000",
            "Loal, Fir,00011110001",
            "Fax, Fir,00011110002",
            "Famr, Fir,00011110003",
            "AA, Fer,00011110004",
            "BA, Air,00011110005",
            "CA, Fir,00011110006",
            "DA, Fir,00011110007",
            "松尾, 賢明,00011110008"
    })
    void insertBookUser_SUCCESS(String familyName, String firstName, String phoneNumber) throws SQLException {
        BookUser test = new BookUser(familyName, firstName, phoneNumber);
        bookDao.insertBookUser(test);
    }

    @ParameterizedTest
    @CsvSource({
            "First book ever, 100,2,https://a.example.com",
            "マイクロソフトの本, 2000,1,https://a.example.com",
            "らりるれろ, 2300,4,https://a.example.com",
            "あいうえお, 1200,1,https://a.example.com",
            "あいうえ, 1200,1,https://a.example.com",
            "あいう, 1200,1,https://a.example.com",
            "あい, 1200,1,https://a.example.com",
            "Java SE11 Silver問題集, 3400,1,https://a.example.com",
            "1231232, 1200,1,https://a.example.com",
            "1, 1200,1,\"\"",
            "\"\", 1200,10000000,\"\""
    })
    void insertBookShelf_SUCCESS(String title, int price, int quantity, String url) throws SQLException {
        BookClass test = new BookClass(title, price, url, quantity);
        bookDao.insertBook(test);
    }








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
        ResponseBooks books = new DemoController().GET_book(i);
        if(books.getBooks().size()>0)
            assertTrue((books.getBooks().get(0).getId() == Integer.parseInt(i)) == expected);
        else
            assertTrue(false == expected);
    }




    @Test
    void TEST_GET_books() throws InputFormatExeption{
        Class<? extends Exception> expectedException = null;//No error expected.
        ExpectedException thrown = ExpectedException.none();

        ResponseBooks books = new DemoController().GET_books();
        if (InputFormatExeption.class != null) {
            thrown.expect(expectedException);
        }
        for(BookClass b : books.getBooks()){
            assureBookClass_names(b);
        }
    }




    @Test
    void TEST_POST_books_EMPTY() throws InputFormatExeption, SQLException {
        BookClass test = new BookClass();
        ResponseBooks books = new DemoController().POST_book(test);
        assertTrue(books.getResponseHeader().getStatus()== response_status.ERR);
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