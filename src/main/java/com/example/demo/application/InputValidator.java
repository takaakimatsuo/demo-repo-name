package com.example.demo.application;

import com.example.demo.backend.custom.Dto.Book;
import com.example.demo.backend.custom.Dto.PatchBook;
import com.example.demo.backend.custom.Dto.User;
import com.example.demo.common.enums.Messages;
import com.example.demo.common.exceptions.InputFormatException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InputValidator {
  private static final String PHONE_NUMBER_FORMAT = "^\\d{10,15}$";
  private static final String URL_FORMAT = "^https?://[a-z\\.:/\\+\\-\\#\\?\\=\\&\\;\\%\\~]+$";
  private static Pattern URL_PATTERN = Pattern.compile(URL_FORMAT);
  private static Pattern PHONENUMBER_PATTERN = Pattern.compile(PHONE_NUMBER_FORMAT);


  /**
   * Makes sure that the inputted String is Integer compatible.
   * @param integer An arbitrary number as String.
   * @return the value of the inputted String as an Integer.
   * @throws InputFormatException if input is not acceptable.
   */
  static Integer assureInteger(String integer) throws InputFormatException {
    Integer result = null;
    if (integer == null) {
      throw new InputFormatException(Messages.INTEGER_NULL);
    }
    try {
      result = Integer.valueOf(integer);
    } catch (IllegalStateException | NumberFormatException e) {
      throw new InputFormatException(Messages.INVALID_ID);
    }
    return result;
  }


  /**
   * Makes sure that the inputted Integer is positive.
   * @param integer An arbitrary number as Integer.
   * @return the number.
   * @throws InputFormatException if input is not acceptable.
   */
  static Integer assurePositive(Integer integer) throws InputFormatException {

    if (integer == null) {
      throw new InputFormatException(Messages.INTEGER_NULL);
    }

    if (integer < 0) {
      throw new InputFormatException(Messages.INVALID_ID);
    }

    return integer;
  }



  /**
   * Validates the URL format.
   * @param url URL as a String to be checked.
   * @throws InputFormatException if input is not acceptable.
   */
  public static void assureURL(String url) throws InputFormatException {

    if (url == null) {
      throw new InputFormatException(Messages.URL_NULL);
    }
    if (!url.isEmpty()) {
      //URL can be left empty
      Matcher m = URL_PATTERN.matcher(url);
      if (!m.find()) {
        throw new InputFormatException(Messages.INVALID_URL);
      }
    }
  }

  /**
   * Validates a list of phone numbers.
   * @param phoneNumbers A String list of phone numbers.
   * @throws InputFormatException if input is not acceptable.
   */
  public static void assureAllBorrowedBy(String[] phoneNumbers) throws InputFormatException {

    if (phoneNumbers == null) {
      return;
    }
    for (String phoneNumber: phoneNumbers) {
      assureBorrowedBy(phoneNumber);
    }
  }

  /**
   * Validates that names aren't null.
   * @param names A String list of names.
   * @throws InputFormatException if input is not acceptable.
   */
  private static void assureBorrowedByNames(String[] names) throws InputFormatException {
    if (names == null) {
      throw new InputFormatException(Messages.BORROWER_NULL);
    }
  }

  /**
   * Validates a of phone numbers.
   * @param phoneNumber A String list of phone numbers.
   * @throws InputFormatException if input is not acceptable.
   */
  public static void assureBorrowedBy(String phoneNumber) throws InputFormatException {
    if (phoneNumber == null) {
      throw new InputFormatException(Messages.PHONENUMBER_NULL);
    }
    Matcher m = PHONENUMBER_PATTERN.matcher(phoneNumber);
    if (!m.find()) {
      throw new InputFormatException(Messages.INVALID_PHONENUMBER);
    }
  }


  /**
   * Validates the quantity.
   * @param quantity An arbitrary int.
   * @throws InputFormatException if input is not acceptable.
   */
  private static void assureQuantity(int quantity) throws InputFormatException {
    if (quantity == 0) {
      throw new InputFormatException(Messages.ZERO_QUANTITY);
    } else if (quantity < 0) {
      throw new InputFormatException(Messages.NEGATIVE_QUANTITY);
    }
  }

  /**
   * Validates the price.
   * @param price An arbitrary int.
   * @throws InputFormatException if input is not acceptable.
   */
  private static void assurePrice(int price) throws InputFormatException {
    if (price < 0) {
      throw new InputFormatException(Messages.NEGATIVE_PRICE);
    }
  }

  /**
   * Validates the title.
   * @param title An arbitrary String.
   * @throws InputFormatException if input is not acceptable.
   */
  private static void assureTitle(String title) throws InputFormatException {
    if (title == null || title.isEmpty()) {
      throw new InputFormatException(Messages.EMPTY_TITLE);
    }
  }


  /**
   * Validates the {@link com.example.demo.backend.custom.Dto.Book Book} object.
   * Borrowers are written in names.
   * @param book {@link com.example.demo.backend.custom.Dto.Book Book} object to be checked.
   * @return The same {@link com.example.demo.backend.custom.Dto.Book Book} object.
   * @throws InputFormatException if input is not acceptable.
   */
  public static Book assureBookClassNames(Book book) throws InputFormatException {
    if (book == null) {
      throw new InputFormatException(Messages.BOOK_CLASS_NULL);
    }
    assureURL(book.getUrl());
    assureQuantity(book.getQuantity());
    assurePrice(book.getPrice());
    assureTitle(book.getTitle());
    return book;
  }


  /**
   * Validates the {@link com.example.demo.backend.custom.Dto.Book Book} object.
   * Borrowers are written in phone numbers.
   * @param book {@link com.example.demo.backend.custom.Dto.Book Book} object to be checked.
   * @return The same {@link com.example.demo.backend.custom.Dto.Book Book} object.
   * @throws InputFormatException if input is not acceptable.
   */
  public static Book assureBookClass(Book book) throws InputFormatException {
    if (book == null) {
      throw new InputFormatException(Messages.BOOK_CLASS_NULL);
    }
    assureAllBorrowedBy(book.getBorrowedBy());
    assureURL(book.getUrl());
    assureQuantity(book.getQuantity());
    assurePrice(book.getPrice());
    assureTitle(book.getTitle());
    return book;
  }


  /**
   * Validates the {@link com.example.demo.backend.custom.Dto.User User} object.
   * @param user  {@link com.example.demo.backend.custom.Dto.User User} object to be checked.
   * @return The same {@link com.example.demo.backend.custom.Dto.User User}} object.
   * @throws InputFormatException if input is not acceptable.
   */
  static User assureBookUser(User user) throws InputFormatException {
    if (user == null) {
      throw new InputFormatException(Messages.USER_CLASS_NULL);
    }
    assureBorrowedBy(user.getPhoneNumber());
    return user;
  }

  private static int assureStatus(int status) throws InputFormatException {
    if (status != 0 && status != 1 && status != 2) {
      throw new InputFormatException(Messages.INVALID_STATUS);
    }
    return status;
  }


  public static PatchBook assurePatchBookClass(PatchBook book) throws InputFormatException {
    if (book == null) {
      throw new InputFormatException(Messages.PATCHBOOK_CLASS_NULL);
    }
    assureStatus(book.getStatus());
    assureBorrowedBy(book.getBorrower());
    return book;
  }


}
