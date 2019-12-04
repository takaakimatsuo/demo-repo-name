package com.example.demo.application;


import com.example.demo.backend.custom.Dto.ResponseBooks;
import com.example.demo.common.enums.Messages;
import com.example.demo.common.exceptions.DaoException;
import com.example.demo.data.access.DatabaseTableInitializer;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.runners.MethodSorters;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.junit.jupiter.api.Assertions.assertEquals;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@AutoConfigureMockMvc
@SpringBootTest
class MockMvcBookControllerTest {

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  BookController controller;

  @BeforeAll
  static void initAll() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, DaoException {
    DatabaseTableInitializer.dropBookshelf();
    DatabaseTableInitializer.dropBookUser();
    DatabaseTableInitializer.createBookshelf();
    DatabaseTableInitializer.createBookUser();
    DatabaseTableInitializer.fillInBookUser();
    DatabaseTableInitializer.fillInBooks();
  }

  @Before
  void setup(){
    mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
  }


  String acquireBodyAsErrorMessage(ResultActions result) throws UnsupportedEncodingException {
    MvcResult output = result.andReturn();
    return output.getResponse().getContentAsString();
  }

  ResponseBooks acquireBodyAsResponseBooks(ResultActions result) throws UnsupportedEncodingException, JsonProcessingException {
    MvcResult output = result.andReturn();
    String contentAsString = output.getResponse().getContentAsString();
    return objectMapper.readValue(contentAsString, ResponseBooks.class);
  }


  @Nested
  @DisplayName("本の検索に関するテスト")
  class test2 {
    @DisplayName("正しい本の全検索")
    @Test
    void getBooksTest() throws Exception {
      ResultActions result = mockMvc.perform(get("/books"))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$.books").exists());

      ResponseBooks response = acquireBodyAsResponseBooks(result);

      assertEquals(Messages.BOOK_FOUND, response.getMessageHeader().getMessage());
    }

    @DisplayName("正しいIDによる本の検索")
    @Test
    void getBookTest() throws Exception {
      int id = 1;
      ResultActions result = mockMvc.perform(
        MockMvcRequestBuilders
          .get("/books/{id}", id)
          .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$.books[0].id").value(id));

      ResponseBooks response = acquireBodyAsResponseBooks(result);
      assertEquals(response.getMessageHeader().getMessage(), Messages.BOOK_FOUND);
    }

    @DisplayName("存在しない本の検索")
    @Test
    void getBookTest_INDEX_FAIL() throws Exception {
      int id = 1123;
      ResultActions result = mockMvc.perform(
        MockMvcRequestBuilders
          .get("/books/{id}", id)
          .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest());

      String errorMsg = acquireBodyAsErrorMessage(result);
      assertEquals(errorMsg, Messages.BOOK_NOT_EXISTING.getMessageKey());

    }

    @DisplayName("条件を満たさないID指定による本の検索")
    @Test
    void getBookTest_FORMAT_FAIL() throws Exception {
      int id = -213;
      ResultActions result = mockMvc.perform(
        MockMvcRequestBuilders
          .get("/books/{id}", id)
          .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest());

      String errorMsg = acquireBodyAsErrorMessage(result);
      assertEquals(errorMsg,Messages.INVALID_ID.getMessageKey());

    }

  }


  @Nested
  @DisplayName("本の追加に関するテスト")
  class test3{
    @DisplayName("正しい本の追加　その1")
    @Test
    void postBookTest() throws Exception {
      ResultActions result =  mockMvc.perform(post("/books")
        .contentType(MediaType.APPLICATION_JSON)
        .content("{\"title\":\"mockBook\", \"price\":100, \"quantity\":1, \"url\":\"https://test.com\"}"))
        .andDo(print())
        .andExpect(status().isOk());

      ResponseBooks response = acquireBodyAsResponseBooks(result);
      assertEquals(response.getMessageHeader().getMessage(), Messages.BOOK_INSERTED);
    }

    @DisplayName("正しい本の追加　その２")
    @Test
    void postBookTest2() throws Exception {
      ResultActions result =  mockMvc.perform(post("/books")
        .contentType(MediaType.APPLICATION_JSON)
        .content("{\"title\":\"mockBook5\", \"price\":100, \"quantity\":2, \"url\":\"https://test.com\"}"))
        .andDo(print())
        .andExpect(status().isOk());

      ResponseBooks response = acquireBodyAsResponseBooks(result);
      assertEquals(response.getMessageHeader().getMessage(), Messages.BOOK_INSERTED);
    }

    @DisplayName("本の重複追加(タイトルはユニークでないといけない)")
    @Test
    void postBookTest3() throws Exception {

      ResultActions result =  mockMvc.perform(post("/books")
        .contentType(MediaType.APPLICATION_JSON)
        .content("{\"title\":\"DuplicatedBook\", \"price\":100, \"quantity\":2, \"url\":\"https://test.com\"}"))
        .andDo(print())
        .andExpect(status().isOk());

      ResponseBooks response = acquireBodyAsResponseBooks(result);
      assertEquals(response.getMessageHeader().getMessage(), Messages.BOOK_INSERTED);


      ResultActions result2 =  mockMvc.perform(post("/books")
        .contentType(MediaType.APPLICATION_JSON)
        .content("{\"title\":\"DuplicatedBook\", \"price\":100, \"quantity\":2, \"url\":\"https://test.com\"}"))
        .andDo(print())
        .andExpect(status().isBadRequest());

      String errorMsg = acquireBodyAsErrorMessage(result2);
      assertEquals(errorMsg, Messages.BOOK_DUPLICATE.getMessageKey());
    }

    @DisplayName("価格がマイナスの状態での本の追加")
    @Test
    void postBookTest4() throws Exception {
      ResultActions result =  mockMvc.perform(post("/books")
        .contentType(MediaType.APPLICATION_JSON)
        .content("{\"title\":\"PRICEBOOK\", \"price\":-100, \"quantity\":2, \"url\":\"https://test.com\"}"))
        .andDo(print())
        .andExpect(status().isBadRequest());

      String errorMsg = acquireBodyAsErrorMessage(result);
      assertEquals(errorMsg, Messages.NEGATIVE_PRICE.getMessageKey());
    }

    @DisplayName("量がゼロの状態での本の追加")
    @Test
    void postBookTest5() throws Exception {
      ResultActions result =  mockMvc.perform(post("/books")
        .contentType(MediaType.APPLICATION_JSON)
        .content("{\"title\":\"QAUTNTITYBOOK\", \"price\":100, \"quantity\":0, \"url\":\"https://test.com\"}"))
        .andDo(print())
        .andExpect(status().isBadRequest());

      String errorMsg = acquireBodyAsErrorMessage(result);
      assertEquals(errorMsg,Messages.ZERO_QUANTITY.getMessageKey());
    }

    @DisplayName("量がマイナスの状態での本の追加")
    @Test
    void postBookTest6() throws Exception {
      ResultActions result =  mockMvc.perform(post("/books")
        .contentType(MediaType.APPLICATION_JSON)
        .content("{\"title\":\"QAUTNTITYBOOK\", \"price\":100, \"quantity\":-10, \"url\":\"https://test.com\"}"))
        .andDo(print())
        .andExpect(status().isBadRequest());

      String errorMsg = acquireBodyAsErrorMessage(result);
      assertEquals(errorMsg, Messages.NEGATIVE_QUANTITY.getMessageKey());
    }

  }



  @Nested
  @DisplayName("本の貸し出しに関するテスト")
  class test4{
    @DisplayName("正しい本の貸し出し　その１")
    @Test
    void patchBookTest1() throws Exception {
      int id = 8;
      ResultActions result =  mockMvc.perform(patch("/books/" + id)
        .contentType(MediaType.APPLICATION_JSON)
        .content("{\"borrower\":\"08044440011\",\"status\":0}"))
        .andDo(print())
        .andExpect(status().isOk());

      ResponseBooks response = acquireBodyAsResponseBooks(result);
      assertEquals(response.getMessageHeader().getMessage(), Messages.BOOK_BORROWED);
    }

    @DisplayName("正しい本の貸し出し　その２")
    @Test
    void putBookTest2() throws Exception {
      int id = 2;
      ResultActions result =  mockMvc.perform(patch("/books/" + id)
        .contentType(MediaType.APPLICATION_JSON)
        .content("{\"borrower\":\"08044440011\",\"status\":0}"))
        .andDo(print())
        .andExpect(status().isOk());

      ResponseBooks response = acquireBodyAsResponseBooks(result);
      assertEquals(response.getMessageHeader().getMessage(), Messages.BOOK_BORROWED);
    }

    @DisplayName("存在しない本の貸し出し")
    @Test
    void putBookTest3() throws Exception {
      int id = 1332;
      ResultActions result =  mockMvc.perform(patch("/books/" + id)
        .contentType(MediaType.APPLICATION_JSON)
        .content("{\"borrower\":\"08044440011\",\"status\":0}"))
        .andDo(print())
        .andExpect(status().isBadRequest());

      String errorMsg = acquireBodyAsErrorMessage(result);
      assertEquals(errorMsg, Messages.BOOK_NOT_EXISTING.getMessageKey());
    }

      @DisplayName("借りていない本の返却")
      @Test
      void putBookTest4() throws Exception {
        int id = 2;
        ResultActions result =  mockMvc.perform(patch("/books/" + id)
          .contentType(MediaType.APPLICATION_JSON)
          .content("{\"borrower\":\"08044440010\",\"status\":1}"))
          .andDo(print())
          .andExpect(status().isBadRequest());

        String errorMsg = acquireBodyAsErrorMessage(result);
        assertEquals(errorMsg, Messages.BOOK_CANNOT_BE_RETURNED.getMessageKey());
    }

    @DisplayName("借りていない本の紛失")
    @Test
    void putBookTest5() throws Exception {
      int id = 4;

      ResultActions result2 =  mockMvc.perform(patch("/books/" + id)
        .contentType(MediaType.APPLICATION_JSON)
        .content("{\"borrower\":\"08044440010\",\"status\":2}"))
        .andDo(print())
        .andExpect(status().isBadRequest());

      String errorMsg = acquireBodyAsErrorMessage(result2);
      assertEquals(errorMsg, Messages.BOOK_CANNOT_BE_LOST.getMessageKey());
    }

    @DisplayName("正しい本の紛失、および削除")
    @Test
    void putBookTest6() throws Exception {
      int id = 6;
      ResultActions result =  mockMvc.perform(patch("/books/" + id)
        .contentType(MediaType.APPLICATION_JSON)
        .content("{\"borrower\":\"08044440010\",\"status\":0}"))
        .andDo(print())
        .andExpect(status().isOk());

      ResponseBooks response = acquireBodyAsResponseBooks(result);
      assertEquals(response.getMessageHeader().getMessage(), Messages.BOOK_BORROWED);

      ResultActions result2 =  mockMvc.perform(patch("/books/" + id)
        .contentType(MediaType.APPLICATION_JSON)
        .content("{\"borrower\":\"08044440010\",\"status\":2}"))
        .andDo(print())
        .andExpect(status().isOk());

      response = acquireBodyAsResponseBooks(result2);
      assertEquals(response.getMessageHeader().getMessage(), Messages.BOOK_LOST_AND_DELETED);
    }


    @DisplayName("正しい本の紛失")
    @Test
    void putBookTest7() throws Exception {
      int id = 4;

      ResultActions result =  mockMvc.perform(patch("/books/" + id)
        .contentType(MediaType.APPLICATION_JSON)
        .content("{\"borrower\":\"08044440010\",\"status\":0}"))
        .andDo(print())
        .andExpect(status().isOk());

      ResponseBooks response = acquireBodyAsResponseBooks(result);
      assertEquals(response.getMessageHeader().getMessage(), Messages.BOOK_BORROWED);

      ResultActions result2 =  mockMvc.perform(patch("/books/" + id)
        .contentType(MediaType.APPLICATION_JSON)
        .content("{\"borrower\":\"08044440010\",\"status\":2}"))
        .andDo(print())
        .andExpect(status().isOk());

      response = acquireBodyAsResponseBooks(result2);
      assertEquals(response.getMessageHeader().getMessage(), Messages.BOOK_LOST_AND_DELETED);
    }


    @DisplayName("正しい本の返却")
    @Test
    void putBookTest8() throws Exception {
      int id = 5;

      ResultActions result =  mockMvc.perform(patch("/books/" + id)
        .contentType(MediaType.APPLICATION_JSON)
        .content("{\"borrower\":\"08044440010\",\"status\":0}"))
        .andDo(print())
        .andExpect(status().isOk());

      ResponseBooks response = acquireBodyAsResponseBooks(result);
      assertEquals(response.getMessageHeader().getMessage(), Messages.BOOK_BORROWED);

      ResultActions result2 =  mockMvc.perform(patch("/books/" + id)
        .contentType(MediaType.APPLICATION_JSON)
        .content("{\"borrower\":\"08044440010\",\"status\":1}"))
        .andDo(print())
        .andExpect(status().isOk());

      response = acquireBodyAsResponseBooks(result2);
      assertEquals(response.getMessageHeader().getMessage(), Messages.BOOK_RETURNED);
    }
  }


  @DisplayName("本の削除に関するテスト")
  @Nested
  class test5{
    @DisplayName("正しい本の削除")
    @Test
    void deleteUserTest1() throws Exception {


      int id = 10;
      ResultActions result2 =  mockMvc.perform(delete("/books/" + id))
        .andDo(print())
        .andExpect(status().isOk());

      ResponseBooks response2 = acquireBodyAsResponseBooks(result2);
      assertEquals(response2.getMessageHeader().getMessage(), Messages.BOOK_DELETED);
    }

    @DisplayName("存在していないIDの本の削除")
    @Test
    void deleteUserTest2() throws Exception {
      int id = 114;
      ResultActions result =  mockMvc.perform(delete("/books/" + id))
        .andDo(print())
        .andExpect(status().isBadRequest());

      String errorMsg = acquireBodyAsErrorMessage(result);
      assertEquals(errorMsg, Messages.BOOK_NOT_EXISTING.getMessageKey());
    }
  }


  @DisplayName("本の置き換えに関するテスト")
  @Nested
  class test6 {
    @DisplayName("正しい本の置き換え")
    @Test
    void putBookTest() throws Exception {
      int id = 2;
      ResultActions result =  mockMvc.perform(put("/books/" + id)
        .contentType(MediaType.APPLICATION_JSON)
        .content("{\"title\":\"REPLACEDBOOK\", \"price\":1000, \"quantity\":5, \"url\":\"https://test.com\"}"))
        .andDo(print())
        .andExpect(status().isOk());

      ResponseBooks response = acquireBodyAsResponseBooks(result);
      assertEquals(response.getMessageHeader().getMessage(), Messages.UPDATE_SUCCESS_BOOK);
    }

    @DisplayName("存在しない本の置き換え")
    @Test
    void putBookTest2() throws Exception {
      int id = 20000;
      ResultActions result =  mockMvc.perform(put("/books/" + id)
        .contentType(MediaType.APPLICATION_JSON)
        .content("{\"title\":\"REPLACEDBOOK\", \"price\":1000, \"quantity\":5, \"url\":\"https://test.com\"}"))
        .andDo(print())
        .andExpect(status().isBadRequest());

      String errorMsg = acquireBodyAsErrorMessage(result);
      assertEquals(errorMsg, Messages.BOOK_NOT_EXISTING_OR_IS_BORROWED.getMessageKey());
    }

    @DisplayName("既に存在している本のタイトルでの置き換え")
    @Test
    void putBookTest3() throws Exception {
      int id = 10;
      ResultActions result =  mockMvc.perform(put("/books/" + id)
        .contentType(MediaType.APPLICATION_JSON)
        .content("{\"title\":\"マイクロソフトの本\", \"price\":1000, \"quantity\":5, \"url\":\"https://test.com\"}"))
        .andDo(print())
        .andExpect(status().isBadRequest());

      String errorMsg = acquireBodyAsErrorMessage(result);
      assertEquals(errorMsg, Messages.BOOK_DUPLICATE.getMessageKey());
    }
  }




}