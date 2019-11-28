package com.example.demo;
import static com.example.demo.application.InputValidator.*;

import com.example.demo.application.DemoController;
import com.example.demo.backend.custom.myexceptions.DaoException;
import com.example.demo.backend.custom.myexceptions.DbException;
import com.example.demo.backend.custom.Dto.BookUser;
import com.example.demo.backend.custom.Dto.PatchBookClass;
import com.example.demo.data.access.BookDao;
import com.example.demo.data.access.JdbcBookUserDao;
import com.example.demo.data.access.BookDaoTest;
import com.example.demo.backend.custom.myenums.ServiceStatus;
import com.example.demo.backend.custom.myexceptions.InputFormatExeption;
import com.example.demo.backend.custom.Dto.BookClass;
import com.example.demo.backend.custom.Dto.ResponseBooks;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.rules.ExpectedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static com.example.demo.backend.errormessages.StaticMessages.BOOK_BORROWED;
import static com.example.demo.backend.errormessages.StaticMessages.BOOK_NO_STOCK;
import static com.example.demo.backend.errormessages.StaticMessages.BOOK_NOT_EXISTING;
import static com.example.demo.backend.errormessages.StaticMessages.BOOK_CANNOT_BE_RETURNED;
import static com.example.demo.backend.errormessages.StaticMessages.BOOK_CANNOT_BE_LOST;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class PracticeDemoControllerTest {

    @Autowired
    @Qualifier("JdbcBookDao")
    BookDao bDao;

    @Autowired
    JdbcBookUserDao uDao;

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
            "Vending, Machine,00011110003",
            "Goda, Takeshi,00011110004",
            "のび, のび太,00011110005",
            "Family, First,00011110006",
            "白い, 犬,00011110007",
            "茶色い, 猫,12345678910",
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
            "白い犬の本, 1200,1,https://a.example.com",
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
            "-2120, false",/*Wrong ID*/
            "0, false",/*Wrong ID*/
            "1, true",
            "2, true",
            "3, true",
            "4, true",
            "5, true",
            "8, true",
            "12, true",
            "14, false",/*Not in database*/
            "19, false",/*Not in database*/
            "18, false",/*Not in database*/
            ",false"/*No ID*/
    })
    void 本の検索の成功と失敗例(String i, boolean expected) throws InputFormatExeption, DbException, DaoException {
        ResponseBooks books = controller.getBook(i);
        if(books.getBooks().size()>0)
            assertTrue((books.getBooks().get(0).getId() == Integer.parseInt(i)) == expected);
        else
            assertTrue(false == expected);
    }


    @Test
    void 本の全検索＿成功() throws InputFormatExeption, DbException{
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
    void データが空の本の追加_失敗() throws InputFormatExeption, SQLException, DbException, DaoException {
        BookClass test = new BookClass();
        ResponseBooks books = controller.postBook(test);
        assertTrue(books.getResponseHeader().getStatus()== ServiceStatus.ERR);
    }


//
//    /**
//     * ${TODO}
//     * Format not matching the requirement must raise {@link com.example.demo.backend.custom.myexceptions.InputFormatExeption InputFormatExeption}.
//     * This is currently testing on DemoBusinessLogic directly, as the controller doesnt throw any error yet.
//     * @param title Book title
//     * @param price Book price
//     * @param quantity Quantity of books
//     * @param url URL related to the book
//     * @throws InputFormatExeption An exception that gets raised when the user input isn't satisfying the requirement.
//     * @throws SQLException
//     */
//    @ParameterizedTest
//    @CsvSource({
//            ", 1,1,https://a.example.com",
//            "AA, -1,1,https://a.example.com",
//            "BB, 1,-100,https://a.example.com",
//            ",1,1,",
//            ",-1,-1,"
//    })
//    void フォーマットが間違っている本の追加_失敗(String title, int price, int quantity, String url) throws InputFormatExeption, SQLException {
//        Class<? extends Exception> expectedException = InputFormatExeption.class;
//        assertThrows(expectedException,()->{
//            BookClass test = new BookClass(title, price, url, quantity);
//            ResponseBooks response = new ResponseBooks();
//            response = new DemoBusinessLogic().addBook(assureBookClass(test));});
//    }



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

    @ParameterizedTest
    @CsvSource({
      "1, 00011110007, 0",
      "1, 00011110008, 0",
      "4, 00011110007, 0",
      "3, 00011110008, 0",
      "12, 00000000000, 0",
      "11, 00000000001, 0",
    })
    void 本の貸し出し_成功(String bookId, String phoneNumber, int action) {
        PatchBookClass pb = new PatchBookClass();
        pb.setBorrower(phoneNumber);
        pb.setStatus(action);
        ResponseBooks books = controller.patchBook(bookId, pb);
        assertTrue(books.getResponseHeader().getMessage().equals(BOOK_BORROWED));
    }

    @ParameterizedTest
    @CsvSource({
      "1, 00011110001, 0",
      "4, 00011110008, 0",
      "12, 00000000001, 0",
    })
    void 在庫の無い本の貸し出し_失敗(String bookId, String phoneNumber, int action) {
        PatchBookClass pb = new PatchBookClass();
        pb.setBorrower(phoneNumber);
        pb.setStatus(action);
        ResponseBooks books = controller.patchBook(bookId, pb);
        assertTrue(books.getResponseHeader().getMessage().equals(BOOK_NO_STOCK));
    }

    @ParameterizedTest
    @CsvSource({
      "123, 00011110008, 0",
      "321, 00011110008, 0",
      "123, 00000000001, 0",
    })
    void 存在しない本の貸し出し_失敗(String bookId, String phoneNumber, int action) {
        PatchBookClass pb = new PatchBookClass();
        pb.setBorrower(phoneNumber);
        pb.setStatus(action);
        ResponseBooks books = controller.patchBook(bookId, pb);
        assertTrue(books.getResponseHeader().getMessage().equals(BOOK_NOT_EXISTING));
    }

    @ParameterizedTest
    @CsvSource({
      "1, 00011110008, 1",
      "4, 00011110008, 1",
      "12, 00000000001, 1",
    })
    void 借りていない本の返却_失敗(String bookId, String phoneNumber, int action) {
        PatchBookClass pb = new PatchBookClass();
        pb.setBorrower(phoneNumber);
        pb.setStatus(action);
        ResponseBooks books = controller.patchBook(bookId, pb);
        assertTrue(books.getResponseHeader().getMessage().equals(BOOK_CANNOT_BE_RETURNED));
    }

    @ParameterizedTest
    @CsvSource({
      "1, 00011110002, 2",
      "4, 00011110008, 2",
      "12, 00000000001, 2",
    })
    void 借りていない本の紛失報告_失敗(String bookId, String phoneNumber, int action) {
        PatchBookClass pb = new PatchBookClass();
        pb.setBorrower(phoneNumber);
        pb.setStatus(action);
        ResponseBooks books = controller.patchBook(bookId, pb);
        assertTrue(books.getResponseHeader().getMessage().equals(BOOK_CANNOT_BE_LOST));
    }


    @Test
    void DELETE_book() {
    }
}