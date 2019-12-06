package com.example.demo.backend.dto;

import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Getter(AccessLevel.PUBLIC)
@Setter(AccessLevel.PUBLIC)
@NoArgsConstructor
@AllArgsConstructor
public class ResponseUsers {

  @Builder.Default
  private List<User> users  = new ArrayList<>();

  @Builder.Default
  private MessageHeader messageHeader = new MessageHeader();

}

