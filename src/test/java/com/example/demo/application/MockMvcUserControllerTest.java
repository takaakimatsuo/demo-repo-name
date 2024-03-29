package com.example.demo.application;

import com.example.demo.backend.dto.ResponseUsers;
import com.example.demo.common.enums.Messages;
import com.example.demo.common.exceptions.DaoException;
import com.example.demo.data.access.DatabaseTableInitializer;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.FixMethodOrder;
import org.junit.jupiter.api.*;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;



@Disabled
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
    DatabaseTableInitializer.fillInBookUser2();
    DatabaseTableInitializer.fillInBooks2();
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
  public class postUserTest{

    @DisplayName("正しいユーザの追加")
    @Test
    void postUserTest1() throws Exception {
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
    void postUserTest2() throws Exception {
      ResultActions result =  mockMvc.perform(post("/users")
        .contentType(MediaType.APPLICATION_JSON)
        .content("{\"familyName\":\"mockDude\", \"firstName\":\"mockDude\", \"phoneNumber\":\"08041\"}"))
        .andDo(print())
        .andExpect(status().isBadRequest());

      String errorMsg = acquireBodyAsErrorMessage(result);
      assertEquals(errorMsg, Messages.INVALID_PHONENUMBER.getMessageKey());
    }
  }



  @Nested
  @DisplayName("ユーザの検索に関するテスト")
  public class getUsers{

    @DisplayName("正しいユーザの検索")
    @Test
    void getUsers1() throws Exception {
      ResultActions result =  mockMvc.perform(get("/users"))
        .andDo(print())
        .andExpect(status().isOk());

      ResponseUsers response = acquireBodyAsResponseUsers(result);
      assertEquals(response.getMessageHeader().getMessage(), Messages.USER_FOUND);
    }
  }


  @Nested
  @DisplayName("ユーザの削除に関するテスト")
  public class deleteUser{

    @DisplayName("正しいユーザの削除")
    @Test
    void deleteUser1() throws Exception {
      String userId = "1";
      ResultActions result =  mockMvc.perform(delete("/users/"+userId))
        .andDo(print())
        .andExpect(status().isOk());

      ResponseUsers response = acquireBodyAsResponseUsers(result);
      assertEquals(response.getMessageHeader().getMessage(), Messages.USER_DELETED);
    }

    @DisplayName("存在しないユーザの削除")
    @Test
    void deleteUser2() throws Exception {
      String userId = "1";

      ResultActions result =  mockMvc.perform(delete("/users/"+userId))
        .andDo(print())
        .andExpect(status().isBadRequest());

      String errorMsg = acquireBodyAsErrorMessage(result);
      assertEquals(errorMsg, Messages.USER_NOT_EXISTING.getMessageKey());
    }
  }
}
