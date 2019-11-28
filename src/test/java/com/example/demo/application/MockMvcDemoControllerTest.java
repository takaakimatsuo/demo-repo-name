package com.example.demo.application;

import com.example.demo.backend.custom.exceptions.DaoException;
import com.example.demo.backend.custom.exceptions.DbException;
import com.example.demo.data.access.BookDaoTest;
import org.junit.Before;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.lang.reflect.InvocationTargetException;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;


@AutoConfigureMockMvc
@SpringBootTest
class MockMvcDemoControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  DemoController controller;

  @BeforeAll
  static void initAll() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, DbException, DaoException {
    BookDaoTest.dropBookshelf();
    BookDaoTest.dropBookUser();
    BookDaoTest.createBookshelf();
    BookDaoTest.createBookUser();
    BookDaoTest.fillInBookUser();
    BookDaoTest.fillInBooks();
  }

  @Before
  void setup(){
    mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
  }


  @Test
  public void postUserTest() throws Exception {
    ResultActions result =  mockMvc.perform(post("/users")
      .contentType(MediaType.APPLICATION_JSON)
      .content("{\"familyName\":\"mockDude\", \"firstName\":\"mockDude\", \"phoneNumber\":\"08044440011\"}"))
      .andDo(print())
      .andExpect(status().isOk());
  }

  @Test
  public void postUserTest2() throws Exception {
    ResultActions result =  mockMvc.perform(post("/users")
      .contentType(MediaType.APPLICATION_JSON)
      .content("{\"familyMan\":\"mockDude\", \"firstName\":\"mockDude\", \"phoneNumber\":\"08044440012\"}"))
      .andDo(print())
      .andExpect(status().isOk());
  }

  @Test
  public void getBooksTest() throws Exception {
    ResultActions result = mockMvc.perform(get("/books"))
      .andDo(print())
      .andExpect(status().isOk())
      .andExpect(MockMvcResultMatchers.jsonPath("$.books").exists());
  }


  @Test
  public void getBookTest() throws Exception {
    int id = 1;
    ResultActions result = mockMvc.perform(
      MockMvcRequestBuilders
        .get("/books/{id}", id)
        .accept(MediaType.APPLICATION_JSON))
      .andExpect(status().isOk())
      .andExpect(MockMvcResultMatchers.jsonPath("$.books[0].id").value(id));
  }

  @Test
  public void postBookTest() throws Exception {
    ResultActions result =  mockMvc.perform(post("/books")
      .contentType(MediaType.APPLICATION_JSON)
      .content("{\"title\":\"mockBook\", \"price\":100, \"quantity\":1, \"url\":\"https://test.com\"}"))
      .andDo(print())
      .andExpect(status().isOk());
  }

  @Test
  public void postBookTest2() throws Exception {
    ResultActions result =  mockMvc.perform(post("/books")
      .contentType(MediaType.APPLICATION_JSON)
      .content("{\"title\":\"mockBook2\", \"price\":100, \"quantity\":2, \"url\":\"https://test.com\"}"))
      .andDo(print())
      .andExpect(status().isOk());
  }



  @Test
  public void patchUserTest() throws Exception {
    int id = 1;
    ResultActions result =  mockMvc.perform(patch("/books/" + id)
      .contentType(MediaType.APPLICATION_JSON)
      .content("{\"borrower\":\"08044440011\",\"status\":0}"))
      .andDo(print())
      .andExpect(status().isOk());
  }

  @Test
  public void putUserTest() throws Exception {
    int id = 2;
    ResultActions result =  mockMvc.perform(put("/books/" + id)
      .contentType(MediaType.APPLICATION_JSON)
      .content("{\"title\":\"mockBook3\", \"price\":100, \"quantity\":2, \"url\":\"https://test.com\"}"))
      .andDo(print())
      .andExpect(status().isOk());
  }

  @Test
  public void deleteUserTest() throws Exception {
    int id = 1;
    ResultActions result =  mockMvc.perform(delete("/books/" + id))
      .andDo(print())
      .andExpect(status().isOk());
  }






}