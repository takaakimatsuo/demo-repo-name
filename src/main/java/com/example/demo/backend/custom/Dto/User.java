package com.example.demo.backend.custom.Dto;


import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class User {
  private String familyName;
  private String firstName;
  private String phoneNumber;
}
