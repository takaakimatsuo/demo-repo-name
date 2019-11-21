package UnitTests;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import DemoBackend.CustomExceptions.InputFormatExeption;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;

import static com.example.demo.InputResolver.*;

@RunWith(Parameterized.class)
public class InputResolverTest {

    //private static String test_urls[] = {"ht","","http","https://","http:","http://a","https://s"};
    private static String test_urls_succeed[] = {"http://a", "https://s", ""};
    private static String test_urls_fail[] = {"https://", "http://", "htt://s", "azacw", "https:///"};

        @Parameterized.Parameters
        public static Iterable<? extends Object> data() {
            return Arrays.asList(new Object[][]{{"http://a", null}, {"https://s", null}, {"", null}, {"a", InputFormatExeption.class},{" ", InputFormatExeption.class}, {"b", InputFormatExeption.class}, {null, InputFormatExeption.class}});
        }

        @Parameterized.Parameter(0)
        public String url;
        @Parameterized.Parameter(1)
        public Class<? extends Exception> expectedException;

        @Rule
        public ExpectedException thrown = ExpectedException.none();


        @Test
    public void test_succeed_checkURL() throws InputFormatExeption {

        if (expectedException != null) {
            thrown.expect(expectedException);
        }
        assureURL(url);

    }

/*
    @Test
    public void test_succeed_checkBook() throws InputFormatExeption {

        if (expectedException != null) {
            thrown.expect(expectedException);
        }
        //checkURL(url);

    }*/




    /*
    @Test (expected = Test.None.class  )//no exception expected
    public void test_succeed_checkURL() throws InputFormatExeption{
        try{
            checkURL(m1);
        }catch(InputFormatExeption e){
            throw e;
        }
    }*/

    /*
    @Test (expected = InputFormatExeption.class)
    public void test_fail_checkURL() throws InputFormatExeption{
        try{
            for(String url: test_urls_fail) {
                checkURL(url);
            }
        }catch(InputFormatExeption e){
            throw e;
        }
    }

    @Test
    public void test_checkPhoneNumber(){

        String expected = "a";
        String actual = "a";
        assertThat(actual,is(expected));
    }*/

}


