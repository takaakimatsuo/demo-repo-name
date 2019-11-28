package com.example.demo.data.access;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.SpringBootTest;

import javax.sql.DataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class MockitoJdbcBookDaoTest {

  @InjectMocks
  @Autowired
  JdbcBookDao dao;


  @Test
  void insertBook() {
    //TODO
  }

  @Test
  void deleteBook() {
    //TODO
  }

  @Test
  void getAllBooks() {
    //TODO
  }

  @Test
  void getBook() {
    //TODO
  }
}