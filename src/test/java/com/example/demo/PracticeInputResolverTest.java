package com.example.demo;


import com.example.demo.backend.custom.myexceptions.InputFormatExeption;
import com.example.demo.backend.custom.objects.BookClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;

import static com.example.demo.InputValidator.*;

@RunWith(Enclosed.class)
public class InputResolverTest {

    @RunWith(Parameterized.class)
    public static class Test_URL {

        @Parameterized.Parameters
        public static Iterable<? extends Object> dataURL() {
            return Arrays.asList(new Object[][]{
                    {"http://a", null},
                    {"https://s", null},
                    {"", null},
                    {"a", InputFormatExeption.class},
                    {" ", InputFormatExeption.class},
                    {"b", InputFormatExeption.class},
                    {null, InputFormatExeption.class}});
        }
        @Parameterized.Parameter(0)
        public static String url;
        @Parameterized.Parameter(1)
        public static Class<? extends Exception> expectedException;

        @Rule
        public ExpectedException thrown = ExpectedException.none();

        @Test
        public void test_succeed_checkURL() throws InputFormatExeption {

            if (expectedException != null) {
                thrown.expect(expectedException);
            }
            assureURL(url);
        }
    }





    @RunWith(Parameterized.class)
    public static class Test_PhoneNumber {

        @Parameterized.Parameters
        public static Iterable<? extends Object> dataURL() {
            return Arrays.asList(new Object[][]{
                    {"08044660000", null},
                    {"07000000000", null},
                    {"01011111111", null},
                    {"a", InputFormatExeption.class},
                    {" ", InputFormatExeption.class},
                    {"b", InputFormatExeption.class},
                    {null, InputFormatExeption.class}});
        }

        @Parameterized.Parameter(0)
        public static String phone;
        @Parameterized.Parameter(1)
        public static Class<? extends Exception> expectedException;

        @Rule
        public ExpectedException thrown = ExpectedException.none();

        @Test
        public void test_succeed_checkURL() throws InputFormatExeption {

            if (expectedException != null) {
                thrown.expect(expectedException);
            }
            assureBorrowed_by(phone);
        }
    }



    @RunWith(Parameterized.class)
    public static class Test_BookClass {

        @Parameterized.Parameters
        public static Iterable<? extends Object> dataURL() {
            return Arrays.asList(new Object[][]{
                    {new BookClass("",100,"",2,new String[]{}), InputFormatExeption.class},
                    {new BookClass("Title",-100,"",2,new String[]{}), InputFormatExeption.class},
                    {new BookClass("Title",100,"",-2,new String[]{}), InputFormatExeption.class},
                    {new BookClass("Title",100,"",2,null), InputFormatExeption.class},
                    {new BookClass(null,100,"",2,new String[]{}), InputFormatExeption.class},
                    {new BookClass("Title",100,"",2,new String[]{}), null},
                    {new BookClass(), InputFormatExeption.class},
                    {null, InputFormatExeption.class}});
        }

        @Parameterized.Parameter(0)
        public static BookClass book;
        @Parameterized.Parameter(1)
        public static Class<? extends Exception> expectedException;

        @Rule
        public ExpectedException thrown = ExpectedException.none();

        @Test
        public void test_succeed_checkURL() throws InputFormatExeption {

            if (expectedException != null) {
                thrown.expect(expectedException);
            }
            assureBookClass(book);
        }
    }


}


