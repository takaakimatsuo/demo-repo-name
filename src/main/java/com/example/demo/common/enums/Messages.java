package com.example.demo.common.enums;

public enum Messages {

  UNEXPECTED("System error. Unexpected output."),
  BOOK_BORROWED("Borrowed book successfully."),
  BOOK_RETURNED("Returned book successfully."),
  BOOK_LOST("Reported lost book successfully."),
  BOOK_LOST_AND_DELETED("Reported lost book successfully. The book has been deleted as its quantity became less than 0."),
  BOOK_DELETED("Book deleted successfully"),
  BOOK_INSERTED("New book inserted to database."),
  BOOK_NOT_EXISTING_OR_IS_BORROWED("Book does not exist, or is currently borrowed by someone and thus cannot be disturbed."),
  UPDATE_SUCCESS_BOOK("Book data successfully updated."),
  UPDATE_FAILED_BOOK("Updating book database failed."),
  BOOK_NOT_EXISTING("Book does not exist."),
  BOOK_FOUND("Book found."),
  BOOK_CANNOT_BE_DOUBLE_BORROWED("Book already borrowed by the same user."),
  BOOK_CANNOT_BE_RETURNED("Trying to return a book that has not been borrowed by the user."),
  BOOK_CANNOT_BE_LOST("Trying to report a book as lost, which has not been borrowed by the user."),
  BOOK_NO_STOCK("No stock available."),
  BOOK_DUPLICATE("The same book already exists in the database."),
  USER_FOUND("User found."),
  USER_NOT_EXISTING("User not existing."),
  USER_CANNOT_BE_DELETED("User cannot be deleted. Return all books beforehand."),
  USER_INSERTED("User successfully added!"),
  UPDATE_FAILED_USER("Updating user database failed."),
  USER_DELETED("User successfully deleted!"),
  USER_DUPLICATE("User with the same phone number already exists!"),
  USER_CLASS_NULL("UserClass is null"),
  BOOK_CLASS_NULL("BookClass is null"),
  INTEGER_NULL("Integer should not be null"),
  INVALID_ID("Invalid ID specified."),
  URL_NULL("URL is null"),
  INVALID_URL("URL is not formatted correctly."),
  BORROWER_NULL("Borrower(s) are null."),
  PHONENUMBER_NULL("Phone number is null."),
  INVALID_PHONENUMBER("Phone is not formatted correctly."),
  NEGATIVE_QUANTITY("Quantity is less than 0."),
  ZERO_QUANTITY("Quantity is 0."),
  NEGATIVE_PRICE("Price is less than 0."),
  EMPTY_TITLE("Title is empty."),
  INVALID_STATUS("Invalid status input. Status must be set to 0 (borrow), 1 (return) or 2 (lost)."),
  PATCHBOOK_CLASS_NULL("Book to be replaced is null");


  private String messageKey;

  Messages(String msgKey) {
    messageKey = msgKey;
  }

  public String getMessageKey() {
    return messageKey;
  }

}
