package com.example.demo.application;

import com.example.demo.backend.custom.Dto.Book;
import com.example.demo.backend.custom.Dto.User;
import com.example.demo.backend.custom.Dto.PatchBook;
import com.example.demo.common.enums.Messages;
import com.example.demo.common.exceptions.InputFormatException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static com.example.demo.application.InputValidator.assureAllBorrowedBy;
import static com.example.demo.application.InputValidator.assureBookClassNames;
import static com.example.demo.application.InputValidator.assureBookUser;
import static com.example.demo.application.InputValidator.assureInteger;
import static com.example.demo.application.InputValidator.assurePatchBookClass;
import static com.example.demo.application.InputValidator.assurePositive;
import static com.example.demo.application.InputValidator.assureUrl;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class InputValidatorTest {

  List<String> dummyPhoneNumbers = new ArrayList<String>(){{add("08000001111");add("07000001111");}};
  List<String> dummyPhoneNumbers2 = new ArrayList<String>(){{add("08000001111"); add("07000111");}};
  List<String> dummyPhoneNumbers3 =  new ArrayList<String>(){{add("08000001111");add("07000111ACS");}};


  @DisplayName("assureIngeger()に関するテスト")
  @Nested
  class assureInteger{
    @Test
    @DisplayName("Null値が渡されている時")
    void assureInteger1() throws InputFormatException {
      String integer = null;
      Throwable e = assertThrows(InputFormatException.class, () -> {Integer i  = assureInteger(integer);});
      assertEquals(e.getMessage(),Messages.INTEGER_NULL.getMessageKey());
    }

    @Test
    @DisplayName("数字以外が渡されている時")
    void assureInteger2() throws InputFormatException {
      String integer = "ABC";
      Throwable e = assertThrows(InputFormatException.class, () -> {Integer i  = assureInteger(integer);});
      assertEquals(e.getMessage(),Messages.INVALID_ID.getMessageKey());
    }

    @Test
    @DisplayName("長すぎる数字が渡されている時")
    void assureInteger3() throws InputFormatException {
      String integer = "12312312312312312313123132131232131312";
      Throwable e = assertThrows(InputFormatException.class, () -> {Integer i  = assureInteger(integer);});
      assertEquals(e.getMessage(),Messages.INVALID_ID.getMessageKey());
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
      Throwable e = assertThrows(InputFormatException.class, () -> {
        assureUrl(url);});
      assertEquals(e.getMessage(), Messages.URL_NULL.getMessageKey());
    }

    @DisplayName("正しいURLが引数の時")
    @Test
    void assureURL2() throws InputFormatException {
      String url = "https://example.com";
      assureUrl(url);
    }

    @DisplayName("正しくないURLが引数の時")
    @Test
    void assureURL3() throws InputFormatException {
      String url = "abc";
      Throwable e = assertThrows(InputFormatException.class, () -> {
        assureUrl(url);});
      assertEquals(e.getMessage(), Messages.INVALID_URL.getMessageKey());
    }
  }

  @DisplayName("assureAllBorrowedBy()に関するテスト")
  @Nested
  class assureAllBorrowedBy {

    @DisplayName("正しい電話番号が引数の場合")
    @Test
    void assureAllBorrowedBy1() throws InputFormatException {
      assureAllBorrowedBy(dummyPhoneNumbers);
    }

    @DisplayName("正しくない電話番号が引数の場合")
    @Test
    void assureAllBorrowedBy2() throws InputFormatException {
      Throwable e = assertThrows(InputFormatException.class, () -> {assureAllBorrowedBy(dummyPhoneNumbers2);});
      assertEquals(e.getMessage(),Messages.INVALID_PHONENUMBER.getMessageKey());
    }

    @DisplayName("文字列混ざった引数の場合")
    @Test
    void assureAllBorrowedBy3() throws InputFormatException {

      Throwable e  = assertThrows(InputFormatException.class, () -> {assureAllBorrowedBy(dummyPhoneNumbers3);});
      assertEquals(e.getMessage(),Messages.INVALID_PHONENUMBER.getMessageKey());
    }
  }


  @DisplayName("assureBookClassNames()に関するテスト")
  @Nested
  class assureBookNames {
    @DisplayName("正しい引数の場合")
    @Test
    void assureBookClassNames1() throws InputFormatException {
      Book expected = new Book("Title",0,"https://example.com",1);
      Book actual = assureBookClassNames(expected);
      assertEquals(expected,actual);
    }

    @DisplayName("Nullが引数の場合")
    @Test
    void assureBookClassNames2() throws InputFormatException {
      Book example = null;
      Throwable e = assertThrows(InputFormatException.class, () -> {assureBookClassNames(example);});
      assertEquals(e.getMessage(),Messages.BOOK_CLASS_NULL.getMessageKey());
    }
  }

  @DisplayName("assureBookUser()に関するテスト")
  @Nested
  class assureBookUser {
    @DisplayName("正しい引数の場合")
    @Test
    void assureBookUser1() throws InputFormatException {
      User expected = User.builder()
        .familyName("family")
        .firstName("first")
        .phoneNumber("08000001111")
        .build();
      User actual = assureBookUser(expected);
      assertEquals(expected,actual);
    }

    @DisplayName("Nullが引数の場合")
    @Test
    void assureBookUser2() throws InputFormatException {
      User example = null;
      Throwable e = assertThrows(InputFormatException.class, () -> {
        User actual = assureBookUser(example);});
      assertEquals(e.getMessage(), Messages.USER_CLASS_NULL.getMessageKey());
    }
  }

  @Nested
  @DisplayName("PatchBookClass()に関するテスト")
  class assurePatchBook {
    @DisplayName("正しい引数の場合")
    @Test
    void assurePatchBookClass1() throws InputFormatException {
      PatchBook expected = new PatchBook("08011110000",0);
      PatchBook actual = assurePatchBookClass(expected);
      assertEquals(expected,actual);
    }

    @DisplayName("間違った電話番号がはいった引数の場合")
    @Test
    void assurePatchBookClass2() throws InputFormatException {
      PatchBook expected = new PatchBook("08010",0);
      Throwable e = assertThrows(InputFormatException.class, () -> { assurePatchBookClass(expected);});
      assertEquals(e.getMessage(), Messages.INVALID_PHONENUMBER.getMessageKey());
    }

    @DisplayName("間違ったステータスがはいった引数の場合")
    @Test
    void assurePatchBookClass3() throws InputFormatException {
      PatchBook expected = new PatchBook("08011110000",5);
      Throwable e = assertThrows(InputFormatException.class, () -> { assurePatchBookClass(expected);});
      assertEquals(e.getMessage(), Messages.INVALID_STATUS.getMessageKey());
    }

    @DisplayName("Nullが引数の場合")
    @Test
    void assurePatchBookClass4() throws InputFormatException {
      PatchBook expected = null;
      Throwable e = assertThrows(InputFormatException.class, () -> {assurePatchBookClass(expected);});
      assertEquals(e.getMessage(), Messages.PATCHBOOK_CLASS_NULL.getMessageKey());
    }

  }
}