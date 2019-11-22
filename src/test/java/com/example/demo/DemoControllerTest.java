package com.example.demo;
import static com.example.demo.InputValidator.*;
import DemoBackend.CustomExceptions.InputFormatExeption;
import DemoBackend.CustomObjects.BookClass;
import DemoBackend.CustomObjects.ResponseBooks;
import DemoBackend.DemoBusinessLogic;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.rules.ExpectedException;
import org.junit.runners.Parameterized;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.bind.annotation.RequestBody;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class DemoControllerTest {

    @ParameterizedTest
    @CsvSource({
            "2,3",
            "4,5"
    })
    void odd_and_even_example(int num, int num2) {
        assertTrue(num%2==0 && num2%2==1);
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
            "8, false",
            "19, true",/*Not in database*/
            "18, true",
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

    /*
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
    }*/


    @Test
    void GET_books() {
    }

    @Test
    void POST_books() {
    }

    @Test
    void PUT_book() {
    }

    @Test
    void PATCH_book() {
    }

    @Test
    void DELETE_book() {
    }
}