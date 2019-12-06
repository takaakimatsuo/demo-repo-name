package com.example.demo.data.access.interfaces;

import com.example.demo.backend.dto.User;
import com.example.demo.common.exceptions.DaoException;

import java.util.List;

public interface UserDao {

  List<User> getAllUsers() throws DaoException;

  int deleteBookUser(Integer userId) throws DaoException;

  List<String> getBorrowedBookTitles(Integer userId) throws DaoException;

  int insertBookUser(User book) throws DaoException;
}
