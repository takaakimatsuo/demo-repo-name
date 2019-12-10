package com.example.demo.backend;

import com.example.demo.backend.dto.ResponseUserEntities;
import com.example.demo.backend.entity.UserEntity;
import com.example.demo.common.enums.Messages;
import com.example.demo.common.exceptions.UserBusinessLogicException;
import com.example.demo.data.access.interfaces.JpaUserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserEntityBusinessLogic {

  private ResponseUserEntities ures;

  @Autowired
  private JpaUserDao repository;

  public ResponseUserEntities getAllUserEntities() throws UserBusinessLogicException {
    ures =  ResponseUserEntities.builder().build();
    List<UserEntity> users = repository.findAll();
    if (users.isEmpty()) {
      throw new UserBusinessLogicException(Messages.USER_NOT_EXISTING);
   } else {
      ures.getMessageHeader().setMessage(Messages.USER_FOUND);
    }
    ures.setUsers(users);
    return ures;
  }


  public ResponseUserEntities getUserEntity(Integer id) throws UserBusinessLogicException {
    ures =  ResponseUserEntities.builder().build();
//    List<UserEntity> users = repository.fi
//    if (users.isEmpty()) {
//      throw new UserBusinessLogicException(Messages.USER_NOT_EXISTING);
//    } else {
//      ures.getMessageHeader().setMessage(Messages.USER_FOUND);
//    }
//    ures.setUsers(users);
    return ures;
  }


}
