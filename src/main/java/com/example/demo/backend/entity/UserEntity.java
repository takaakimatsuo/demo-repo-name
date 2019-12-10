package com.example.demo.backend.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name="book_user")
public class UserEntity {

  @Id
  @Column(name="phonenumber")
  private String phonenumber;

  @Column(name="familyname")
  private String familyname;

  @Column(name="firstname")
  private String firstname;

  @Column(name="id")
  private String id;
}
