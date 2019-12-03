package com.example.demo.data.access.custom.enums;

public enum UserAction {
  BORROW(0),
  RETURN(1),
  LOST(2);

  private int action;
  UserAction(int i) {
    action = i;
  }

  public int getAction() {
    return this.action;
  }

}
