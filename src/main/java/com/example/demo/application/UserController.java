package com.example.demo.application;

import com.example.demo.backend.UserBusinessLogic;
import com.example.demo.backend.custom.Dto.User;
import com.example.demo.backend.custom.Dto.ResponseUsers;
import com.example.demo.common.exceptions.BookBusinessLogicException;
import com.example.demo.common.exceptions.DaoException;
import com.example.demo.common.exceptions.InputFormatException;
import com.example.demo.common.exceptions.UserBusinessLogicException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import static com.example.demo.application.InputValidator.assureBookUser;
import static com.example.demo.application.InputValidator.assureInteger;
import static com.example.demo.application.InputValidator.assurePositive;


@Slf4j
@RestController
public class UserController {

  @Autowired
  UserBusinessLogic dbl;

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
   * @throws InputFormatException if user input is not acceptable.
   * @throws UserBusinessLogicException if logic fails.
   */
  @CrossOrigin
  @GetMapping(value = "/users/")
  public ResponseUsers deleteUser() throws DaoException, UserBusinessLogicException {
    try {
      return dbl.getAllUsers();
    } catch (DaoException | UserBusinessLogicException e) {
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
