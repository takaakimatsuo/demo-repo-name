package com.example.demo.backend.custom.Dto;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ResponseBooks {

  @Builder.Default
  private List<Book> books = new ArrayList<Book>();
  @Builder.Default
  private MessageHeader messageHeader = new MessageHeader();


}
