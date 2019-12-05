package com.example.demo.application;

import com.example.demo.backend.custom.Dto.ResponseUsers;
import com.example.demo.common.enums.Messages;
import com.example.demo.common.exceptions.DaoException;
import com.example.demo.common.exceptions.DbException;
import com.example.demo.data.access.DatabaseTableInitializer;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@AutoConfigureMockMvc
@SpringBootTest
public class MockMvcUserControllerTest {

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  UserController controller;

  @BeforeAll
  static void initAll() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, DaoException {
    DatabaseTableInitializer.dropBookshelf();
    DatabaseTableInitializer.dropBookUser();
    DatabaseTableInitializer.createBookshelf();
    DatabaseTableInitializer.createBookUser();
    DatabaseTableInitializer.fillInBookUser();
    DatabaseTableInitializer.fillInBooks();
  }


  String acquireBodyAsErrorMessage(ResultActions result) throws UnsupportedEncodingException, JsonProcessingException {
    MvcResult output = result.andReturn();
    return output.getResponse().getContentAsString();
  }

  ResponseUsers acquireBodyAsResponseUsers(ResultActions result) throws UnsupportedEncodingException, JsonProcessingException {
    MvcResult output = result.andReturn();
    String contentAsString = output.getResponse().getContentAsString();
    return objectMapper.readValue(contentAsString, ResponseUsers.class);
  }


  @Nested
  @DisplayName("ユーザの追加に関するテスト")
  public class test1{

    @DisplayName("正しいユーザの追加")
    @Test
    void postUserTest() throws Exception {
      ResultActions result =  mockMvc.perform(post("/users")
        .contentType(MediaType.APPLICATION_JSON)
        .content("{\"familyName\":\"mockDude\", \"firstName\":\"mockDude\", \"phoneNumber\":\"08044440011\"}"))
        .andDo(print())
        .andExpect(status().isOk());

      ResponseUsers response = acquireBodyAsResponseUsers(result);
      assertEquals(response.getMessageHeader().getMessage(), Messages.USER_INSERTED);
    }

    @DisplayName("携帯電話番号が間違ったフォーマットの状態でユーザの追加")
    @Test
    void postUserTest_WRONG_INPUT() throws Exception {
      ResultActions result =  mockMvc.perform(post("/users")
        .contentType(MediaType.APPLICATION_JSON)
        .content("{\"familyName\":\"mockDude\", \"firstName\":\"mockDude\", \"phoneNumber\":\"08041\"}"))
        .andDo(print())
        .andExpect(status().isBadRequest());

      String errorMsg = acquireBodyAsErrorMessage(result);
      assertEquals(errorMsg, Messages.INVALID_PHONENUMBER.getMessageKey());
    }

  }

}
