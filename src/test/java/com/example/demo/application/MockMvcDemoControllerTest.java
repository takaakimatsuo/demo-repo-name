package com.example.demo.application;

import com.example.demo.backend.custom.Dto.ResponseBooks;
import com.example.demo.backend.custom.Dto.ResponseUsers;
import com.example.demo.backend.custom.exceptions.DaoException;
import com.example.demo.backend.custom.exceptions.DbException;
import com.example.demo.data.access.BookDaoTest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import static org.junit.jupiter.api.Assertions.assertEquals;

import static com.example.demo.backend.messages.StaticUserMessages.USER_INSERTED;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;


@AutoConfigureMockMvc
@SpringBootTest
class MockMvcDemoControllerTest {

  @Autowired
  private ObjectMapper objectMapper;

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

  ResponseBooks acquireBodyAsResponseBooks(ResultActions result) throws UnsupportedEncodingException, JsonProcessingException {
    MvcResult output = result.andReturn();
    String contentAsString = output.getResponse().getContentAsString();
    return objectMapper.readValue(contentAsString, ResponseBooks.class);
  }

  ResponseUsers acquireBodyAsResponseUsers(ResultActions result) throws UnsupportedEncodingException, JsonProcessingException {
    MvcResult output = result.andReturn();
    String contentAsString = output.getResponse().getContentAsString();
    return objectMapper.readValue(contentAsString, ResponseUsers.class);
  }

  @DisplayName("ユーザの追加")
  @Test
  void postUserTest() throws Exception {
    ResultActions result =  mockMvc.perform(post("/users")
      .contentType(MediaType.APPLICATION_JSON)
      .content("{\"familyName\":\"mockDude\", \"firstName\":\"mockDude\", \"phoneNumber\":\"08044440011\"}"))
      .andDo(print())
      .andExpect(status().isOk());

    ResponseUsers response = acquireBodyAsResponseUsers(result);
    assertEquals(response.getResponseHeader().getMessage(),USER_INSERTED);
  }

  @DisplayName("携帯電話番号が間違ったフォーマットの状態でユーザの追加")
  @Test
  void postUserTest_WRONG_INPUT() throws Exception {
    ResultActions result =  mockMvc.perform(post("/users")
      .contentType(MediaType.APPLICATION_JSON)
      .content("{\"familyName\":\"mockDude\", \"firstName\":\"mockDude\", \"phoneNumber\":\"08041\"}"))
      .andDo(print())
      .andExpect(status().isBadRequest());
  }

  @DisplayName("本の全検索")
  @Test
  void getBooksTest() throws Exception {
    ResultActions result = mockMvc.perform(get("/books"))
      .andDo(print())
      .andExpect(status().isOk())
      .andExpect(MockMvcResultMatchers.jsonPath("$.books").exists());
  }


  @DisplayName("IDによる本の検索")
  @Test
  void getBookTest() throws Exception {
    int id = 1;
    ResultActions result = mockMvc.perform(
      MockMvcRequestBuilders
        .get("/books/{id}", id)
        .accept(MediaType.APPLICATION_JSON))
      .andExpect(status().isOk())
      .andExpect(MockMvcResultMatchers.jsonPath("$.books[0].id").value(id));
  }


  @DisplayName("存在しないIDによる本の全検索")
  @Test
  void getBookTest_INDEX_FAIL() throws Exception {
    int id = 1123;
    ResultActions result = mockMvc.perform(
      MockMvcRequestBuilders
        .get("/books/{id}", id)
        .accept(MediaType.APPLICATION_JSON))
      .andExpect(status().isBadRequest());
  }

  @DisplayName("条件を満たさないIDによる本の全検索")
  @Test
  void getBookTest_FORMAT_FAIL() throws Exception {
    int id = -213;
    ResultActions result = mockMvc.perform(
      MockMvcRequestBuilders
        .get("/books/{id}", id)
        .accept(MediaType.APPLICATION_JSON))
      .andExpect(status().isBadRequest());
  }

  @DisplayName("本の追加　その1")
  @Test
  void postBookTest() throws Exception {
    ResultActions result =  mockMvc.perform(post("/books")
      .contentType(MediaType.APPLICATION_JSON)
      .content("{\"title\":\"mockBook\", \"price\":100, \"quantity\":1, \"url\":\"https://test.com\"}"))
      .andDo(print())
      .andExpect(status().isOk());
  }

  @DisplayName("本の追加　その２")
  @Test
  void postBookTest2() throws Exception {
    ResultActions result =  mockMvc.perform(post("/books")
      .contentType(MediaType.APPLICATION_JSON)
      .content("{\"title\":\"mockBook2\", \"price\":100, \"quantity\":2, \"url\":\"https://test.com\"}"))
      .andDo(print())
      .andExpect(status().isOk());
  }

  @DisplayName("既に登録されている本の追加")
  @Test
  void postBookTest3() throws Exception {
    ResultActions result =  mockMvc.perform(post("/books")
      .contentType(MediaType.APPLICATION_JSON)
      .content("{\"title\":\"mockBook2\", \"price\":100, \"quantity\":2, \"url\":\"https://test.com\"}"))
      .andDo(print())
      .andExpect(status().isBadRequest());

  }

  @DisplayName("価格がマイナスの状態での本の追加")
  @Test
  void postBookTest4() throws Exception {
    ResultActions result =  mockMvc.perform(post("/books")
      .contentType(MediaType.APPLICATION_JSON)
      .content("{\"title\":\"mockBook2\", \"price\":-100, \"quantity\":2, \"url\":\"https://test.com\"}"))
      .andDo(print())
      .andExpect(status().isBadRequest());
  }

  @DisplayName("量がゼロの状態での本の追加")
  @Test
  void postBookTest5() throws Exception {
    ResultActions result =  mockMvc.perform(post("/books")
      .contentType(MediaType.APPLICATION_JSON)
      .content("{\"title\":\"mockBook2\", \"price\":100, \"quantity\":0, \"url\":\"https://test.com\"}"))
      .andDo(print())
      .andExpect(status().isBadRequest());
  }



  @DisplayName("本の貸し出し　その１")
  @Test
  void patchBookTest() throws Exception {
    int id = 1;
    ResultActions result =  mockMvc.perform(patch("/books/" + id)
      .contentType(MediaType.APPLICATION_JSON)
      .content("{\"borrower\":\"08044440011\",\"status\":0}"))
      .andDo(print())
      .andExpect(status().isOk());
  }

  @DisplayName("本の貸し出し　その２")
  @Test
  void putBookTest() throws Exception {
    int id = 2;
    ResultActions result =  mockMvc.perform(put("/books/" + id)
      .contentType(MediaType.APPLICATION_JSON)
      .content("{\"title\":\"mockBook3\", \"price\":100, \"quantity\":2, \"url\":\"https://test.com\"}"))
      .andDo(print())
      .andExpect(status().isOk());
  }

  @DisplayName("本の削除")
  @Test
  void deleteUserTest() throws Exception {
    int id = 1;
    ResultActions result =  mockMvc.perform(delete("/books/" + id))
      .andDo(print())
      .andExpect(status().isOk());
  }






}