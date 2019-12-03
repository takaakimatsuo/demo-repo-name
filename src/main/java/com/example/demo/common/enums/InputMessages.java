package com.example.demo.common.enums;

public enum InputMessages {

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

  InputMessages(String msgKey) {
    messageKey = msgKey;
  }

  public String getMessageKey() {
    return messageKey;
  }
}
