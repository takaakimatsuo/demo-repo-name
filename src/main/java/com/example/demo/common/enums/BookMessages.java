package com.example.demo.common.enums;

public enum BookMessages {

  UNEXPECTED("Unexpected output. This should generally not happen."),
  BOOK_BORROWED("Borrowed book successfully."),
  BOOK_RETURNED("Returned book successfully."),
  BOOK_LOST("Reported lost book successfully."),
  BOOK_LOST_AND_DELETED("Reported lost book successfully. The book has been deleted as quantity became less than 0."),
  BOOK_DELETED("Book deleted successfully"),
  BOOK_INSERTED("New book inserted to database."),
  BOOK_NOT_EXISTING_OR_IS_BORROWED("Book does not exist, or is currently borrowed by someone and thus cannot be disturbed."),
  UPDATE_SUCCESS_BOOK("Book data successfully updated."),
  UPDATE_FAILED_BOOK("Updating database failed."),
  BOOK_NOT_EXISTING("Book does not exist."),
  BOOK_FOUND("Book found."),
  BOOK_NOT_FOUND("No book found."),
  BOOK_CANNOT_BE_DOUBLE_BORROWED("Book already borrowed by the same user."),
  BOOK_CANNOT_BE_RETURNED("Trying to return a book that has not been borrowed by the user."),
  BOOK_CANNOT_BE_LOST("Trying to report a book as lost, which has not been borrowed by the user."),
  BOOK_NO_STOCK("No stock available."),
  BOOK_DUPLICATE("The same book already exists in the database."),
  DB_FAILED_CONNECTION("Failed to establish connection between the database"),
  DB_FAILED_DISCONNECTION("Failed to disconnect from the database");


  private String messageKey;

  BookMessages(String msgKey) {
    messageKey = msgKey;
  }

  public String getMessageKey() {
    return messageKey;
  }

}
