package com.example.demo.DataAccess;

import com.example.demo.backend.custom.exceptions.DaoException;
import com.example.demo.backend.custom.exceptions.DbException;
import com.example.demo.backend.CustomObjects.BookClass;
import org.junit.Ignore;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static com.example.demo.backend.staticErrorCodes.SqlErrorCodes.*;

public class BookDaoTest {



    static JdbcBookDao bookDao = new JdbcBookDao();

    public static void dropBookshelf() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        String query = "DROP TABLE IF EXISTS bookshelf";
        List<Object> paramList = new ArrayList<Object>();
        Method method = JdbcDao.class.getDeclaredMethod("executeUpdate", String.class, List.class);
        method.setAccessible(true);
        int updated = (int) method.invoke(bookDao, query, paramList);
        System.out.println("[INFO] Dropped bookshelf table");
    }

    public static void dropBookUser() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        String query = "DROP table IF EXISTS book_user CASCADE";
        List<Object> param_list = new ArrayList<Object>();
        Method method = JdbcDao.class.getDeclaredMethod("executeUpdate", String.class, List.class);
        method.setAccessible(true);
        int updated = (int) method.invoke(bookDao, query, param_list);
        System.out.println("[INFO] Dropped book_user table");
    }

    public static void createBookshelf() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        String query =
                "CREATE TABLE bookshelf\n" +
                "(id    SERIAL    NOT NULL,\n" +
                "title   TEXT  UNIQUE NOT NULL,\n" +
                "price    INTEGER NOT NULL CHECK (price >= 0) DEFAULT 0,\n" +
                "quantity INTEGER NOT NULL CHECK (quantity >= 0) DEFAULT 1,\n" +
                "url TEXT NOT NULL DEFAULT '',\n" +
                "borrowedBy VARCHAR[] NOT NULL default '{}',\n" +
                "updatedAt timestamp with time zone DEFAULT statement_timestamp(),\n" +
                "createdAt timestamp with time zone DEFAULT current_timestamp ,\n" +
                "PRIMARY KEY (id));";
        List<Object> paramList = new ArrayList<Object>();
        Method method = JdbcDao.class.getDeclaredMethod("executeUpdate", String.class, List.class);
        method.setAccessible(true);
        int updated = (int)method.invoke(bookDao,query,paramList);
        System.out.println("[INFO] bookshelf table created.");
    }

    public static void createBookUser() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        String query =
                "CREATE TABLE book_user(\n" +
                "id    SERIAL    NOT NULL,\n" +
                "familyName   TEXT NOT NULL,\n" +
                "firstName    TEXT NOT NULL,\n" +
                "phoneNumber VARCHAR NOT NULL,\n" +
                "PRIMARY KEY (phoneNumber));";
        List<Object> param_list = new ArrayList<Object>();
        Method method = JdbcDao.class.getDeclaredMethod("executeUpdate", String.class, List.class);
        method.setAccessible(true);
        int updated = (int)method.invoke(bookDao,query,param_list);
        System.out.println("[INFO] bookshelf table created.");
    }

    @BeforeAll
    static void initAll() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        dropBookshelf();
        dropBookUser();
        createBookshelf();
        createBookUser();
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
     void insertBookShelf_SUCCESS(String title, int price, int quantity, String url) throws DbException, DaoException {
        BookClass test = new BookClass(title, price, url, quantity);
        bookDao.insertBook(test);
    }




    @ParameterizedTest
    @CsvSource({
            "First book ever, 100,-2,https://a.example.com",
            "This also fails, -1200,1,https://a.example.com",
            "This too, 1200,-1,\"\""
    })
    /*This should fail due to invalid input*/
    void insertBookShelf_FAIL(String title, int price, int quantity, String url) throws SQLException {
        Class<? extends Exception> expectedException = SQLException.class;
        Exception e = assertThrows(expectedException, () -> {
            BookClass test = new BookClass(title, price, url, quantity);
            bookDao.insertBook(test);
        });
    }

    @ParameterizedTest
    @CsvSource({
            "First book ever, 100,2,https://a.example.com",
            "あい, 10000000,1,\"\"",
    })
        /*This should fail due to duplicated key*/
    void insertBookShelf_FAIL_DUPLICATE_KEY(String title, int price, int quantity, String url) throws SQLException {
        Class<? extends Exception> expectedException = SQLException.class;
        Exception e = assertThrows(expectedException, () -> {
            BookClass test = new BookClass(title, price, url, quantity);
            bookDao.insertBook(test);
        });
        SQLException ee = (SQLException) e;
        assertTrue(ee.getSQLState().compareTo(SQL_CODE_DUPLICATE_KEY_ERROR)==0);
    }

    /*
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
        dao.insertBookUser(test);
    }*/

    @ParameterizedTest
    @CsvSource({
            "00011110000,1",
            "00011110001,1",
            "00011110002,3",
            "00011110003,3",
            "00011110004,3",
            "00011110006,7"
    })
    void updateBook_borrowed(String phoneNumber, Integer bookId) throws DbException, DaoException {
        bookDao.updateBook_borrowed(bookId,phoneNumber);
    }

    @Ignore
    @Test
    void updateBook_returned() {
    }

    @Ignore
    @Test
    void updateBook_lost() {
    }


    /*
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
    }*/

    @Ignore
    @Test
    void deleteBook_SUCCESS() {

    }

    @Ignore
    @Test
    void getAllBooks() {
    }

    @Ignore
    @Test
    void getBook() {
    }

    @Ignore
    @Test
    void check_Book_Exists() {
    }
}