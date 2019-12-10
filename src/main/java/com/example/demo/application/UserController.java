package com.example.demo.application;

import com.example.demo.backend.UserBusinessLogic;
import com.example.demo.backend.UserEntityBusinessLogic;
import com.example.demo.backend.dto.ResponseUserEntities;
import com.example.demo.backend.dto.ResponseUsers;
import com.example.demo.backend.dto.User;
import com.example.demo.common.exceptions.DaoException;
import com.example.demo.common.exceptions.InputFormatException;
import com.example.demo.common.exceptions.UserBusinessLogicException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import static com.example.demo.application.InputValidator.assureBookUser;
import static com.example.demo.application.InputValidator.assureInteger;
import static com.example.demo.application.InputValidator.assurePositive;


@Slf4j
@RestController
public class UserController {

  @Autowired
  UserBusinessLogic dbl;

  @Autowired
  UserEntityBusinessLogic uebl;

  /**
   * Used for inserting a new user data to the database.
   * @param user A UserClass object to be inserted.
   * @return A list of UserClass objects, with a ResponseHeader class.
   * @throws DaoException if query execution fails.
   * @throws InputFormatException if user input is not acceptable.
   * @throws UserBusinessLogicException if logic fails.
   */
  @CrossOrigin
  @PostMapping(value = "/users")
  public ResponseUsers postUser(@RequestBody User user) throws DaoException, InputFormatException, UserBusinessLogicException {
    try {
      return dbl.addUser(assureBookUser(user));
    } catch (InputFormatException | DaoException | UserBusinessLogicException e) {
      log.error("Error in postUser() in UserController.java: ", e);
      throw e;
    }
  }

  /**
   * Used for deleting a user data from the database.
   * @param userId Unique identifier for the user stored in the database.
   * @return An empty list of UserClass objects, with a ResponseHeader class.
   * @throws DaoException if query execution fails.
   * @throws InputFormatException if user input is not acceptable.
   * @throws UserBusinessLogicException if logic fails.
   */
  @CrossOrigin
  @DeleteMapping(value = "/users/{id}")
  public ResponseUsers deleteUser(@PathVariable("id") String userId) throws DaoException, InputFormatException, UserBusinessLogicException {

    try {
      return dbl.removeUser(assurePositive(assureInteger(userId)));
    } catch (InputFormatException | DaoException | UserBusinessLogicException e) {
      log.error("Error in deleteUser() in UserController.java: ",e);
      throw e;
    }
  }

  /**
   * Used for deleting a user data from the database.
   * @return A list of searched UserClass objects, with a ResponseHeader class.
   * @throws DaoException if query execution fails.
   * @throws UserBusinessLogicException if logic fails.
   */
  @CrossOrigin
  @GetMapping(value = "/users")
  public ResponseUsers getUsers() throws DaoException, UserBusinessLogicException {
    try {
      return dbl.getAllUsers();
    } catch (DaoException | UserBusinessLogicException e) {
      log.error("Error in deleteUser() in UserController.java: ",e);
      throw e;
    }
  }

  @CrossOrigin
  @GetMapping(value = "/users2")//JPA version
  public ResponseUserEntities getUserEntities() throws UserBusinessLogicException {
    try {
      return uebl.getAllUserEntities();
    } catch (UserBusinessLogicException e) {
      log.error("Error in deleteUser() in UserController.java: ",e);
      throw e;
    }
  }

  @CrossOrigin
  @GetMapping(value = "/users/{id}")
  public ResponseUserEntities getAUserEntity(@PathVariable("id") String userId) throws UserBusinessLogicException, InputFormatException {
    try {
      return uebl.getUserEntity(assurePositive(assureInteger(userId)));
    } catch (UserBusinessLogicException | InputFormatException e) {
      log.error("Error in deleteUser() in UserController.java: ",e);
      throw e;
    }
  }




  @ExceptionHandler({DaoException.class})
  @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
  @ResponseBody
  public String handleException(DaoException e) {
    return e.getMessage();
  }



  @ExceptionHandler({InputFormatException.class})
  @ResponseStatus(value = HttpStatus.BAD_REQUEST)
  @ResponseBody
  public String handleException(InputFormatException e) {
    return e.getMessage();
  }


  @ExceptionHandler({UserBusinessLogicException.class})
  @ResponseStatus(value = HttpStatus.BAD_REQUEST)
  @ResponseBody
  public String handleException(UserBusinessLogicException e) {
    return e.getMessage();
  }


}
