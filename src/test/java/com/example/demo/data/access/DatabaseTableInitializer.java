package com.example.demo.data.access;

import com.example.demo.backend.BookBusinessLogic;
import com.example.demo.backend.dto.Book;
import com.example.demo.backend.dto.User;
import com.example.demo.common.exceptions.DaoException;
import com.example.demo.data.access.interfaces.BookDao;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;


@Slf4j
@ExtendWith(SpringExtension.class)
public class DatabaseTableInitializer {

    static JdbcUserDao userDao = new JdbcUserDao();
    static BookDao bookDao = new JdbcBookDao();
    static BookBusinessLogic dbl = new BookBusinessLogic();

    public static void dropBookshelf() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        String query = "DROP TABLE IF EXISTS bookshelf";
        List<Object> paramList = new ArrayList<Object>();
        Method method = JdbcDao.class.getDeclaredMethod("executeUpdate", String.class, List.class);
        method.setAccessible(true);
        int updated = (int) method.invoke(bookDao, query, paramList);
        log.info("Dropped bookshelf table");
    }

    public static void dropBookUser() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        String query = "DROP table IF EXISTS book_user CASCADE";
        List<Object> param_list = new ArrayList<Object>();
        Method method = JdbcDao.class.getDeclaredMethod("executeUpdate", String.class, List.class);
        method.setAccessible(true);
        int updated = (int) method.invoke(bookDao, query, param_list);
        log.info("Dropped book_user table");
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
        log.info(" bookshelf table created.");
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
        log.info("Bookshelf table created.");
    }



    public static void fillInBookUser2() throws DaoException {

        String[][] users =
          {{"English", "Johnny","00011110000"},
            {"Whatever", "Whatsoever","00011110001"},
            {"Fax", "Machine","00011110002"},
            {"Vending", "Machine","00011110003"},
            {"Goda", "Takeshi","00011110004"},
            {"のび", "のび太","00011110005"},
            {"Family", "First","00011110006"},
            {"白い", "犬","00011110007"},
            {"茶色い", "猫","12345678910"},
            {"松尾", "賢明","00011110008"},
            {"Toto", "Tata","00011110009"},
            {"AES", "RSA","00000000000"},
            {"Munro", "Bill","00000000001"},
            {"Shota", "Nagayama","00000000002"}};

        String queries = "";
        for(String[] user: users){
            queries = queries.concat("INSERT INTO book_user(familyname, firstname, phonenumber) values(\'"
              + user[0] + "\',"
              + "\'" + user[1] + "\',"
              + "\'" + user[2] + "\'"
              + ");"
            );
        }
        userDao.executeUpdate(queries);
    }


    public static void fillInBookUser() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, DaoException {
        String[][] users =
          {{"English", "Johnny","00011110000"},
            {"Whatever", "Whatsoever","00011110001"},
            {"Fax", "Machine","00011110002"},
            {"Vending", "Machine","00011110003"},
            {"Goda", "Takeshi","00011110004"},
            {"のび", "のび太","00011110005"},
            {"Family", "First","00011110006"},
            {"白い", "犬","00011110007"},
            {"茶色い", "猫","12345678910"},
            {"松尾", "賢明","00011110008"},
            {"Toto", "Tata","00011110009"},
            {"AES", "RSA","00000000000"},
            {"Munro", "Bill","00000000001"},
            {"Shota", "Nagayama","00000000002"}};

        for(String[] user: users){
            User test = User.builder()
            .familyName(user[0])
            .firstName(user[1])
            .phoneNumber(user[2])
              .build();
            userDao.insertBookUser(test);
        }

    }


    public static void fillInBooks2() throws DaoException {

        String[][] books =
          {{"First book ever", "100","2","https://a.example.com"},
            {"マイクロソフトの本", "2000","1","https://a.example.com"},
            {"Java SE11 Silver 問題集", "3200","4","https://a.example.com"},
            {"Java SE11 Gold 問題集", "3200","1","https://a.example.com"},
            {"ABCDEFG", "1200","1","https://a.example.com"},
            {"あいうえお", "1200","1","https://a.example.com"},
            {"白い犬の本", "1200","1","https://a.example.com"},
            {"猫の写真集", "2200","1","https://a.example.com"},
            {"Java SE11 Silver 参考書", "3400","1","https://a.example.com"},
            {"1231232", "1200","1","https://a.example.com"},
            {"1", "1200","1","\'\'"},
            {"\"\"", "1200","10000000","\'\'"},
            {"無料の本","12","1","https://cheap.example.com"}};
        String queries = "";
        for(String[] book: books){
            queries = queries.concat("INSERT INTO bookshelf(title, price, quantity, url) values(\'"
              + book[0] + "\',"
              + "\'" + book[1] + "\',"
              + "\'" + book[2] + "\',"
              + "\'" + book[3] + "\'"
              + ");"
            );
        }
        userDao.executeUpdate(queries);
    }
    public static void fillInBooks() throws DaoException {
        String[][] books =
          {{"First book ever", "100","2","https://a.example.com"},
            {"マイクロソフトの本", "2000","1","https://a.example.com"},
            {"Java SE11 Silver 問題集", "3200","4","https://a.example.com"},
            {"Java SE11 Gold 問題集", "3200","1","https://a.example.com"},
            {"ABCDEFG", "1200","1","https://a.example.com"},
            {"あいうえお", "1200","1","https://a.example.com"},
            {"白い犬の本", "1200","1","https://a.example.com"},
            {"猫の写真集", "2200","1","https://a.example.com"},
            {"Java SE11 Silver 参考書", "3400","1","https://a.example.com"},
            {"1231232", "1200","1","https://a.example.com"},
            {"1", "1200","1","\'\'"},
            {"\"\"", "1200","10000000","\'\'"},
            {"無料の本","12","1","https://cheap.example.com"}};

        for(String[] book: books){
            Book test = new Book(book[0],Integer.parseInt(book[1]),book[3],Integer.parseInt(book[2]));
            bookDao.insertBook(test);
        }

    }

}