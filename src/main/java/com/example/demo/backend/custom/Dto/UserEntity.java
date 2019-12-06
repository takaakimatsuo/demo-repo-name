package com.example.demo.backend.custom.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name="book_user")
public class UserEntity {
  @Id
  @Column(name="phnumberone")
  private String phonenumber;

  @Column(name="familyname")
  private String familyname;

  @Column(name="firstName")
  private String firstname;
}
