package com.example.demo.backend.custom.Dto;

import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
