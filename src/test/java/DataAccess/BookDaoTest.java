package DataAccess;

import DemoBackend.CustomExceptions.InputFormatExeption;
import DemoBackend.CustomObjects.BookClass;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import DataAccess.BookDao;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;
import static DemoBackend.staticErrorCodes.SQLErrorCodes.*;

class BookDaoTest {



    BookDao bookDao = new BookDao();

    @Test
    void checkBookStatus() {
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

    @ParameterizedTest
    @CsvSource({
            "AXCWWV, 1,1,https://a.example.com, 0",
            "XVWVWREDXC, 1,1,https://a.example.com, 0",
            "ABCAFWVW, 1,1,https://a.example.com, 0",
            "ABCAAAFWVW, 1,1,https://a.example.com, 0"
    })
    void insertBook_FAIL_DUPLICATE_KEY_ERROR(String title, int price, int quantity, String url) throws SQLException {
        Class<? extends Exception> expectedException = SQLException.class;
        Exception e = assertThrows(expectedException, () -> {
                BookClass test = new BookClass(title, price, url, quantity);
                bookDao.insertBook(test);
        });
        SQLException ee = (SQLException) e;
        assertTrue(ee.getSQLState().compareTo(SQL_CODE_DUPLICATE_KEY_ERROR)==0);
    }


    @Test
    void deleteBook_SUCCESS() {

    }

    @Test
    void getAllBooks() {
    }

    @Test
    void getBook() {
    }

    @Test
    void check_Book_Exists() {
    }
}