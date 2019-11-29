package com.example.demo.backend.messages;

public class StaticBookMessages {

  public static String UNEXPECTED = "Unexpected output. This should generally not happen.";
  public static String INVALID_STATUS = "Invalid status input.";
  public static String BOOK_BORROWED = "Borrowed book successfully.";
  public static String BOOK_RETURNED = "Returned book successfully.";
  public static String BOOK_LOST = "Reported lost book successfully.";
  public static String BOOK_DELETED  = "Book deleted successfully";
  public static String BOOK_INSERTED = "New book inserted to database.";
  public static String UPDATE_FAILED_NO_MATCH_BOOK = "Nothing updated. Wrong ID or the book is currently borrowed by someone.";
  public static String UPDATE_SUCCESS_BOOK = "Book data successfully updated.";
  public static String UPDATE_FAILED_BOOK  = "Updating database failed.";
  public static String BOOK_NOT_EXISTING = "Book does not exist.";
  public static String BOOK_FOUND = "Book found.";
  public static String BOOK_NOT_FOUND = "No book found.";
  public static String BOOK_CANNOT_BE_DOUBLE_BORROWED = "Book already borrowed by the same user.";
  public static String BOOK_CANNOT_BE_RETURNED = "Trying to return a book that has not been borrowed by the user.";
  public static String BOOK_CANNOT_BE_LOST  = "Trying to report a book as lost, which has not been borrowed by the user.";
  public static String BOOK_NO_STOCK  = "No stock available.";
  public static String BOOK_DUPLICATE = "The same book already exists in the database.";
  public static String DB_FAILED_CONNECTION  = "Failed to establish connection between the database";
  public static String DB_FAILED_DISCONNECTION  = "Failed to disconnect from the database";
}
