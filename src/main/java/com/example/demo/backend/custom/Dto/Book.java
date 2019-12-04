package com.example.demo.backend.custom.Dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

@NoArgsConstructor
@AllArgsConstructor
@Getter(AccessLevel.PUBLIC)
@Setter(AccessLevel.PUBLIC)
@Builder
@Component
public class Book {
  private int id;
  @Builder.Default
  private String title = "";
  @Builder.Default
  private int price = 0;
  @Builder.Default
  private String url = "";
  @Builder.Default
  private int quantity = 1;
  @Builder.Default
  private String[] borrowedBy = {};


  public Book(String title, int price, String url, int quantity) {
    setTitle(title);
    setPrice(price);
    setUrl(url);
    setQuantity(quantity);
  }
}
