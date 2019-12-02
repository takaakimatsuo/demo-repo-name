package com.example.demo.application;

import com.example.demo.backend.custom.Dto.BookClass;
import com.example.demo.backend.custom.Dto.BookUser;
import com.example.demo.backend.custom.Dto.PatchBookClass;
import com.example.demo.backend.custom.exceptions.InputFormatException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;


import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.example.demo.application.messages.StaticInputErrorMessages.*;
import static org.junit.jupiter.api.Assertions.*;
import static com.example.demo.application.InputValidator.*;

class InputValidatorTest {

  @DisplayName("assureIngeger()に関するテスト")
  @Nested
  class assureInteger{
    @Test
    @DisplayName("Null値が渡されている時")
    void assureInteger1() throws InputFormatException {
      String integer = null;
      Throwable e = assertThrows(InputFormatException.class, () -> {Integer i  = assureInteger(integer);});
      assertEquals(e.getMessage(),INTEGER_NULL);
    }

    @Test
    @DisplayName("数字以外が渡されている時")
    void assureInteger2() throws InputFormatException {
      String integer = "ABC";
      Throwable e = assertThrows(InputFormatException.class, () -> {Integer i  = assureInteger(integer);});
      assertEquals(e.getMessage(),INVALID_ID);
    }

    @Test
    @DisplayName("長すぎる数字が渡されている時")
    void assureInteger3() throws InputFormatException {
      String integer = "12312312312312312313123132131232131312";
      Throwable e = assertThrows(InputFormatException.class, () -> {Integer i  = assureInteger(integer);});
      assertEquals(e.getMessage(),INVALID_ID);
    }

    @Test
    @DisplayName("正しい引数の時")
    void assureInteger4() throws InputFormatException {
      String integer = "12";
      Integer i  = assureInteger(integer);
      Integer expected = Integer.parseInt(integer);
      Integer actual = i;
      assertEquals(expected,actual);
    }
  }

  @DisplayName("assurePositive()に関するテスト")
  @Nested
  class assurePositive {
    @DisplayName("正しい引数の時")
    @Test
    void assurePositive1() throws InputFormatException {
      Integer integer = 12;
      Integer i  = assurePositive(integer);
      Integer expected = integer;
      Integer actual = i;
      assertEquals(expected,actual);
    }

    @DisplayName("負の引数の時")
    @Test
    void assurePositive2() throws InputFormatException {
      Integer integer = -12;
      assertThrows(InputFormatException.class, () -> {Integer i  = assurePositive(integer);});
    }

    @DisplayName("nullが引数の時")
    @Test
    void assurePositive3() throws InputFormatException {
      Integer integer = null;
      assertThrows(InputFormatException.class, () -> {Integer i  = assurePositive(integer);});
    }
  }


  @DisplayName("assureURL()に関するテスト")
  @Nested
  class assureURL {
    @DisplayName("nullが引数の時")
    @Test
    void assureURL1() throws InputFormatException {
      String url = null;
      Throwable e = assertThrows(InputFormatException.class, () -> {assureURL(url);});
      assertEquals(e.getMessage(), URL_NULL);
    }

    @DisplayName("正しいURLが引数の時")
    @Test
    void assureURL2() throws InputFormatException {
      String url = "https://example.com";
      assureURL(url);
    }

    @DisplayName("正しくないURLが引数の時")
    @Test
    void assureURL3() throws InputFormatException {
      String url = "abc";
      Throwable e = assertThrows(InputFormatException.class, () -> {assureURL(url);});
      assertEquals(e.getMessage(),INVALID_URL);
    }
  }

  @DisplayName("assureAllBorrowedBy()に関するテスト")
  @Nested
  class assureAllBorrowedBy{

    @DisplayName("正しい電話番号が引数の場合")
    @Test
    void assureAllBorrowedBy1() throws InputFormatException {
      String[] phoneNumbers = {"08000001111","07000001111"};
      assureAllBorrowedBy(phoneNumbers);
    }

    @DisplayName("正しくない電話番号が引数の場合")
    @Test
    void assureAllBorrowedBy2() throws InputFormatException {
      String[] phoneNumbers = {"08000","07000001111"};
      Throwable e = assertThrows(InputFormatException.class, () -> {assureAllBorrowedBy(phoneNumbers);});
      assertEquals(e.getMessage(),INVALID_PHONENUMBER);
    }

    @DisplayName("文字列混ざった引数の場合")
    @Test
    void assureAllBorrowedBy3() throws InputFormatException {
      String[] phoneNumbers = {"080ABC","07000001111"};
      Throwable e  = assertThrows(InputFormatException.class, () -> {assureAllBorrowedBy(phoneNumbers);});
      assertEquals(e.getMessage(),INVALID_PHONENUMBER);
    }
  }


  @DisplayName("assureBookClassNames()に関するテスト")
  @Nested
  class assureBookClassNames {
    @DisplayName("正しい引数の場合")
    @Test
    void assureBookClassNames1() throws InputFormatException {
      BookClass expected = new BookClass("Title",0,"https://example.com",1);
      BookClass actual = assureBookClassNames(expected);
      assertEquals(expected,actual);
    }

    @DisplayName("Nullが引数の場合")
    @Test
    void assureBookClassNames2() throws InputFormatException {
      BookClass example = null;
      Throwable e = assertThrows(InputFormatException.class, () -> {assureBookClassNames(example);});
      assertEquals(e.getMessage(),BOOK_CLASS_NULL);
    }
  }

  @DisplayName("assureBookUser()に関するテスト")
  @Nested
  class assureBookUser {
    @DisplayName("正しい引数の場合")
    @Test
    void assureBookUser1() throws InputFormatException {
      BookUser expected = new BookUser("damilyName","firstName","08000001111");
      BookUser actual = assureBookUser(expected);
      assertEquals(expected,actual);
    }

    @DisplayName("Nullが引数の場合")
    @Test
    void assureBookUser2() throws InputFormatException {
      BookUser example = null;
      Throwable e = assertThrows(InputFormatException.class, () -> {BookUser actual = assureBookUser(example);});
      assertEquals(e.getMessage(),USER_CLASS_NULL);
    }
  }

  @Nested
  @DisplayName("PatchBookClass()に関するテスト")
  class assurePatchBookClass {
    @DisplayName("正しい引数の場合")
    @Test
    void assurePatchBookClass1() throws InputFormatException {
      PatchBookClass expected = new PatchBookClass("08011110000",0);
      PatchBookClass actual = assurePatchBookClass(expected);
      assertEquals(expected,actual);
    }

    @DisplayName("間違った電話番号がはいった引数の場合")
    @Test
    void assurePatchBookClass2() throws InputFormatException {
      PatchBookClass expected = new PatchBookClass("08010",0);
      Throwable e = assertThrows(InputFormatException.class, () -> { assurePatchBookClass(expected);});
      assertEquals(e.getMessage(),INVALID_PHONENUMBER);
    }

    @DisplayName("間違ったステータスがはいった引数の場合")
    @Test
    void assurePatchBookClass3() throws InputFormatException {
      PatchBookClass expected = new PatchBookClass("08011110000",5);
      Throwable e = assertThrows(InputFormatException.class, () -> { assurePatchBookClass(expected);});
      assertEquals(e.getMessage(),INVALID_STATUS);
    }

    @DisplayName("Nullが引数の場合")
    @Test
    void assurePatchBookClass4() throws InputFormatException {
      PatchBookClass expected = null;
      Throwable e = assertThrows(InputFormatException.class, () -> {assurePatchBookClass(expected);});
      assertEquals(e.getMessage(),PATCHBOOK_CLASS_NULL);
    }

  }
}