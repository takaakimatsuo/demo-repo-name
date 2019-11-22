package UnitTests;


import DemoBackend.CustomExceptions.InputFormatExeption;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ExtendWith(SpringExtension.class)
public class TestTest {

    @Test
    public void test_succeed_checkURL() throws InputFormatExeption {
        assertEquals(1,1);
    }
}


