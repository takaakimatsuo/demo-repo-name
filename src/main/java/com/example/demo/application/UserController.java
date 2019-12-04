package com.example.demo.application;

import com.example.demo.backend.UserBusinessLogic;
import com.example.demo.backend.custom.Dto.User;
import com.example.demo.backend.custom.Dto.ResponseUsers;
import com.example.demo.common.exceptions.BookBusinessLogicException;
import com.example.demo.common.exceptions.DaoException;
import com.example.demo.common.exceptions.InputFormatException;
import com.example.demo.common.exceptions.UserBusinessLogicException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import static com.example.demo.DemoApplication.logger;
import static com.example.demo.application.InputValidator.assureBookUser;
import static com.example.demo.application.InputValidator.assureInteger;
import static com.example.demo.application.InputValidator.assurePositive;


@RestController
public class UserController {

  @Autowired
  UserBusinessLogic dbl;

  /**
   * Receiver for the Post request with no path-variable.
   * This is used for inserting a new user data to the database.
   * @param user A UserClass object to be inserted.
   * @return A list of UserClass objects, with a ResponseHeader class.
   * The list will hold the data of inserted user.
   * Otherwise it is empty.
   */
  @CrossOrigin
  @PostMapping(value = "/users")
  public ResponseUsers postUser(@RequestBody User user) throws DaoException, InputFormatException, UserBusinessLogicException {
    ResponseUsers response = new ResponseUsers();
    try {
      response = dbl.addUser(assureBookUser(user));
    } catch (InputFormatException | DaoException | UserBusinessLogicException e) {
      logger.error("Error in getBook() in BookController.java: ",e);
      throw e;
    }
    return response;
  }


  /**
   * Receiver for the Post request with no path-variable.
   * This is used for inserting a new user data to the database.
   * @param userId Unique identifier for the user stored in the database.
   * @return An empty list of UserClass objects, with a ResponseHeader class.
   */
  @CrossOrigin
  @DeleteMapping(value = "/users/{id}")
  public ResponseUsers deleteUser(@PathVariable("id") String userId) throws DaoException, InputFormatException, UserBusinessLogicException {
    ResponseUsers response = new ResponseUsers();
    try {
      response = dbl.removeUser(assurePositive(assureInteger(userId)));
    } catch (InputFormatException | DaoException | UserBusinessLogicException e) {
      logger.error("Error in getBook() in BookController.java: ",e);
      throw e;
    }
    return response;
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
  public String handleException(BookBusinessLogicException e) {
    return e.getMessage();
  }


}
