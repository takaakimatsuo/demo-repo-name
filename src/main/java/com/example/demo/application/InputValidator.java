package com.example.demo.application;

import static com.example.demo.application.messages.StaticInputErrorMessages.BOOK_CLASS_NULL;
import static com.example.demo.application.messages.StaticInputErrorMessages.BORROWER_NULL;
import static com.example.demo.application.messages.StaticInputErrorMessages.EMPTY_TITLE;
import static com.example.demo.application.messages.StaticInputErrorMessages.INTEGER_NULL;
import static com.example.demo.application.messages.StaticInputErrorMessages.INVALID_ID;
import static com.example.demo.application.messages.StaticInputErrorMessages.INVALID_PHONENUMBER;
import static com.example.demo.application.messages.StaticInputErrorMessages.INVALID_STATUS;
import static com.example.demo.application.messages.StaticInputErrorMessages.INVALID_URL;
import static com.example.demo.application.messages.StaticInputErrorMessages.NEGATIVE_PRICE;
import static com.example.demo.application.messages.StaticInputErrorMessages.NEGATIVE_QUANTITY;
import static com.example.demo.application.messages.StaticInputErrorMessages.PATCHBOOK_CLASS_NULL;
import static com.example.demo.application.messages.StaticInputErrorMessages.PHONENUMBER_NULL;
import static com.example.demo.application.messages.StaticInputErrorMessages.URL_NULL;
import static com.example.demo.application.messages.StaticInputErrorMessages.USER_CLASS_NULL;
import static com.example.demo.application.messages.StaticInputErrorMessages.ZERO_QUANTITY;

import com.example.demo.backend.custom.Dto.BookClass;
import com.example.demo.backend.custom.Dto.BookUser;
import com.example.demo.backend.custom.Dto.PatchBookClass;
import com.example.demo.backend.custom.exceptions.InputFormatException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InputValidator {
  private static final String PHONE_NUMBER_FORMAT = "^\\d{10,15}$";
  private static final String URL_FORMAT = "^https?://[a-z\\.:/\\+\\-\\#\\?\\=\\&\\;\\%\\~]+$";
  private static Pattern URL_PATTERN = Pattern.compile(URL_FORMAT);
  private static Pattern PHONENUMBER_PATTERN = Pattern.compile(PHONE_NUMBER_FORMAT);


  /**
   * Makes sure that the inputted String is Integer compatible.
   * @param integer This is expected to be parsable to an Integer.
   * @return Returns the value of the inputted String as an Integer.
   * @throws InputFormatException An exception that gets raised when the user input doesnt satisfy the requirement.
   */
  static Integer assureInteger(String integer) throws InputFormatException {
    Integer result = null;
    if (integer == null) {
      throw new InputFormatException(INTEGER_NULL);
    }
    try {
      result = Integer.valueOf(integer);
    } catch (IllegalStateException | NumberFormatException e) {
      throw new InputFormatException(INVALID_ID);
    }
    return result;
  }


  static Integer assurePositive(Integer integer) throws InputFormatException {

    if (integer == null) {
      throw new InputFormatException(INTEGER_NULL);
    }

    if (integer < 0) {
      throw new InputFormatException(INVALID_ID);
    }

    return integer;
  }



  /**
   * ${TODO}
   * @param url
   * @throws InputFormatException An exception that gets raised when the user input doesnt satisfy the requirement.
   */
  public static void assureURL(String url) throws InputFormatException {

    if (url == null) {
      throw new InputFormatException(URL_NULL);
    }
    if (!url.isEmpty()) {
      //URL can be left empty
      Matcher m = URL_PATTERN.matcher(url);
      if (!m.find()) {
        throw new InputFormatException(INVALID_URL);
      }
    }
  }

  public static void assureAllBorrowedBy(String[] phoneNumbers) throws InputFormatException {
    if (phoneNumbers == null) {
      throw new InputFormatException(BORROWER_NULL);
    }
    for (String phoneNumber: phoneNumbers) {
      assureBorrowed_by(phoneNumber);
    }
  }

  private static void assureBorrowed_bys_name(String[] names) throws InputFormatException {
    if (names == null) {
      throw new InputFormatException(BORROWER_NULL);
    }
  }

  public static void assureBorrowed_by(String number) throws InputFormatException {
    if (number == null) {
      throw new InputFormatException(PHONENUMBER_NULL);
    }
    Matcher m = PHONENUMBER_PATTERN.matcher(number);
    if (!m.find()) {
      throw new InputFormatException(INVALID_PHONENUMBER);
    }
  }


  private static void assureQuantity(int quantity) throws InputFormatException {
    if (quantity == 0) {
      throw new InputFormatException(ZERO_QUANTITY);
    }
    else if (quantity < 0) {
      throw new InputFormatException(NEGATIVE_QUANTITY);
    }
  }

  private static void assurePrice(int price) throws InputFormatException {
    if (price < 0) {
      throw new InputFormatException(NEGATIVE_PRICE);
    }
  }

  private static void assureTitle(String title) throws InputFormatException {
    if (title == null || title.isEmpty()) {
      throw new InputFormatException(EMPTY_TITLE);
    }
  }


  public static BookClass assureBookClassNames(BookClass book) throws InputFormatException {
    if (book == null) {
      throw new InputFormatException(BOOK_CLASS_NULL);
    }
    assureBorrowed_bys_name(book.getBorrowedBy());
    assureURL(book.getUrl());
    assureQuantity(book.getQuantity());
    assurePrice(book.getPrice());
    assureTitle(book.getTitle());
    return book;
  }


  public static BookClass assureBookClass(BookClass book) throws InputFormatException {
    if (book == null) {
      throw new InputFormatException(BOOK_CLASS_NULL);
    }
    assureAllBorrowedBy(book.getBorrowedBy());
    assureURL(book.getUrl());
    assureQuantity(book.getQuantity());
    assurePrice(book.getPrice());
    assureTitle(book.getTitle());
    return book;
  }


  static BookUser assureBookUser(BookUser user) throws InputFormatException {
    if (user == null) {
      throw new InputFormatException(USER_CLASS_NULL);
    }
    assureBorrowed_by(user.getPhoneNumber());
    return user;
  }

  private static int assureStatus(int status) throws InputFormatException {
    if (status != 0 && status != 1 && status != 2) {
      throw new InputFormatException(INVALID_STATUS);
    }
    return status;
  }


  public static PatchBookClass assurePatchBookClass(PatchBookClass book) throws InputFormatException {
    if (book == null) {
      throw new InputFormatException(PATCHBOOK_CLASS_NULL);
    }
    assureStatus(book.getStatus());
    assureBorrowed_by(book.getBorrower());
    return book;
  }


}
