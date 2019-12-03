package com.example.demo.application;

import com.example.demo.backend.UserBusinessLogic;
import com.example.demo.backend.custom.Dto.BookUser;
import com.example.demo.backend.custom.Dto.ResponseHeader;
import com.example.demo.backend.custom.Dto.ResponseUsers;
import com.example.demo.backend.custom.exceptions.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import static com.example.demo.application.InputValidator.*;


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
  public ResponseUsers postUser(@RequestBody BookUser user) throws DaoException, InputFormatException, UserException {
    ResponseUsers response = new ResponseUsers();
    try {
      response = dbl.addUser(assureBookUser(user));
    } catch (InputFormatException | DaoException | UserException e) {
      e.printStackTrace();
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
  public ResponseUsers deleteUser(@PathVariable("id") String userId) throws DaoException, InputFormatException, DbException, UserException {
    ResponseUsers response = new ResponseUsers();
    try {
      response = dbl.removeUser(assurePositive(assureInteger(userId)));
    } catch (InputFormatException | DaoException | UserException e) {
      e.printStackTrace();
      throw e;
    }
    return response;
  }




  @ExceptionHandler({DbException.class})
  @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
  @ResponseBody
  public String handleException(DbException e) {
    return e.getMessage();
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


  @ExceptionHandler({UserException.class})
  @ResponseStatus(value = HttpStatus.BAD_REQUEST)
  @ResponseBody
  public String handleException(BookException e) {
    return e.getMessage();
  }


}
