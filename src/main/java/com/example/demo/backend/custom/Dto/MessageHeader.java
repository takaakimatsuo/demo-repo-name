package com.example.demo.backend.custom.Dto;

import com.example.demo.common.enums.Messages;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter(AccessLevel.PUBLIC)
@Setter(AccessLevel.PUBLIC)
public class MessageHeader {

  private Messages message;

  public String getMessageString() {
    return message.getMessageKey();
  }

}
