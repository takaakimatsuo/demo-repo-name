package com.example.demo.application;

import com.example.demo.backend.custom.exceptions.InputFormatExeption;
import com.example.demo.backend.custom.Dto.BookClass;
import com.example.demo.backend.custom.Dto.BookUser;
import com.example.demo.backend.custom.Dto.PatchBookClass;

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
   * @throws InputFormatExeption An exception that gets raised when the user input doesnt satisfy the requirement.
   */
  static Integer assureInteger(String integer) throws InputFormatExeption {
    Integer result = null;
    if (integer == null) {
      throw new InputFormatExeption("Integer should not be null");
    }
    try {
      result = Integer.valueOf(integer);
    } catch (IllegalStateException | NumberFormatException e) {
      throw new InputFormatExeption("Invalid id inputted in the path-parameter. id:" + integer);
    }
    return result;
  }


  static Integer assurePositive(Integer integer) throws InputFormatExeption {

    if (integer == null) {
      throw new InputFormatExeption("Integer should not be null");
    }

    if (integer < 0) {
      throw new InputFormatExeption("Invalid id inputted in the path-parameter. Id must be positive. id:" + integer);
    }

    return integer;
  }



  /**
   * ${TODO}
   * @param url
   * @throws InputFormatExeption An exception that gets raised when the user input doesnt satisfy the requirement.
   */
  public static void assureURL(String url) throws InputFormatExeption {

    if (url == null) {
      throw new InputFormatExeption("URL is null.");
    }
    if (!url.isEmpty()) {
      //URL can be left empty
      Matcher m = URL_PATTERN.matcher(url);
      if (!m.find()) {
        throw new InputFormatExeption("URL is not formatted correctly.");
      }
    }
  }

  public static void assureAllBorrowedBy(String[] phoneNumbers) throws InputFormatExeption {
    if (phoneNumbers == null) {
      throw new InputFormatExeption("Borrowers are null.");
    }
    for (String phoneNumber: phoneNumbers) {
      assureBorrowed_by(phoneNumber);
    }
  }

  private static void assureBorrowed_bys_name(String[] names) throws InputFormatExeption {
    if (names == null) {
      throw new InputFormatExeption("Borrowers name are null.");
    }
  }

  public static void assureBorrowed_by(String number) throws InputFormatExeption {
    if (number == null) {
      throw new InputFormatExeption("Phone number is null.");
    }
    Matcher m = PHONENUMBER_PATTERN.matcher(number);
    if (!m.find()) {
      throw new InputFormatExeption("Phone number is not formatted correctly.");
    }
  }


  private static void assureQuantity(int quantity) throws InputFormatExeption {
    if (quantity < 0) {
      throw new InputFormatExeption("Quantity is less than 0.");
    }
  }

  private static void assurePrice(int price) throws InputFormatExeption {
    if (price < 0) {
      throw new InputFormatExeption("Price is less than 0.");
    }
  }

  private static void assureTitle(String title) throws InputFormatExeption {
    if (title == null || title.isEmpty()) {
      throw new InputFormatExeption("Title cannot be empty.");
    }
  }


  public static BookClass assureBookClassNames(BookClass book) throws InputFormatExeption {
    if (book == null) {
      throw new InputFormatExeption("BookClass is null");
    }
    assureBorrowed_bys_name(book.getBorrowedBy());
    assureURL(book.getUrl());
    assureQuantity(book.getQuantity());
    assurePrice(book.getPrice());
    assureTitle(book.getTitle());
    return book;
  }


  public static String assureStringNotNull(String str) throws InputFormatExeption {
    if (str == null) {
      throw new InputFormatExeption("String is null");
    } else {
      return str;
    }
  }

  public static BookClass assureBookClass(BookClass book) throws InputFormatExeption {
    if (book == null) {
      throw new InputFormatExeption("BookClass is null");
    }
    assureAllBorrowedBy(book.getBorrowedBy());
    assureURL(book.getUrl());
    assureQuantity(book.getQuantity());
    assurePrice(book.getPrice());
    assureTitle(book.getTitle());
    return book;
  }


  static BookUser assureBookUser(BookUser user) throws InputFormatExeption {
    if (user == null) {
      throw new InputFormatExeption("UserClass is null");
    }
    assureBorrowed_by(user.getPhoneNumber());
    return user;
  }

  private static int assureStatus(int status) throws InputFormatExeption {
    if (status != 0 && status != 1 && status != 2) {
      throw new InputFormatExeption("Invalid status input. Status must be set to 0 (borrow), 1 (return) or 2 (lost). status = " + status);
    }
    return status;
  }


  public static PatchBookClass assurePatchBookClass(PatchBookClass book) throws InputFormatExeption {
    assureStatus(book.getStatus());
    assureBorrowed_by(book.getBorrower());
    return book;
  }


}
