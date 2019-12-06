package com.example.demo.backend.dto;


import com.example.demo.backend.entity.UserEntity;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Getter(AccessLevel.PUBLIC)
@Setter(AccessLevel.PUBLIC)
@NoArgsConstructor
@AllArgsConstructor
public class ResponseUserEntities {

  @Builder.Default
  private List<UserEntity> users  = new ArrayList<>();

  @Builder.Default
  private MessageHeader messageHeader = new MessageHeader();

}