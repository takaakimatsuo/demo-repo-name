package com.example.demo.data.access;


import com.example.demo.common.exceptions.DaoException;
import com.example.demo.common.exceptions.DbException;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;


@Slf4j
public abstract class JdbcDao {

  /**
   * Used for connecting to the database.
   * @return Connection object, not {@code null}.
   * @throws DbException if connection fails.
   */
  Connection connectToDB() throws DbException {
    String url = "jdbc:postgresql://ec2-174-129-253-169.compute-1.amazonaws.com/d9vsaknll1319";
    String user = "lfoagdwpzckmuq";
    //String user = "wrongUserOnPurpose";
    String password = "7cf9b7a5b57780ee7f45c96cac75808dd2cc2ba77b123cf0948cfb290ad1d93c";

    try {
      Connection con = DriverManager.getConnection(url, user, password);
      log.info("Successfully connected to DB.");
      return con;
    } catch (SQLException e) {
      log.error("Failed connecting to DB.");
      throw new DbException(e.getMessage(),e.getCause(),e.getSQLState());
    }
  }

  /**
   * Used for disconnecting from the database.
   * @param con Connection object, not {@code null}.
   * @throws DbException if disconnection fails.
   */
  void closeDB(Connection con) throws DbException {
    try {
      if (con != null) {
        con.close();
        log.info("Successfully closed DB connection.");
      }
    } catch (SQLException e) {
      log.error("Failed closing DB connection.");
      throw new DbException(e.getMessage(),e.getCause(),e.getSQLState());
    }
  }





  /**
   * Inserts the parameters into the PreparedStatement one by one.
   * Currently assumes that each parameter is either an Integer, String or Boolean.
   * @param pstmt A raw PreparedStatement with no parameter inserted, not {@code null}.
   * @param params A list of parameters to be inserted to the statement, not {@code null}.
   * @throws DaoException if query execution fails.
   */
  void parameterMapper(PreparedStatement pstmt, List<Object> params) throws DaoException {
    try {
      int index = 1;
      for (final Object p : params) {
        if (p.getClass() == Integer.class) {
          pstmt.setInt(index, (int) p);
        } else if (p.getClass() == String.class) {
          pstmt.setString(index, (String) p);
        } else if (p.getClass() == boolean.class) {
          pstmt.setBoolean(index, (boolean) p);
        }
        index++;
      }
    } catch (SQLException e) {
      throw new DaoException(e.getMessage(),e.getCause(), e.getSQLState());
    }
  }


  /**
   * Used for executing an arbitrary SQL query, with a given set of parameters.
   * @param query An arbitrary sql query, not {@code null}.
   * @param params A list of parameters to be inserted to the statement, not {@code null}.
   * @return The query output as ResultSet, not {@code null}.
   * @throws DaoException if query execution fails.
   */
  ResultSet executeQuery(String query, List<Object> params) throws DaoException {
    Connection con = null;
    try {
      con = connectToDB();
      PreparedStatement pstmt = con.prepareStatement(query);
      parameterMapper(pstmt, params);
      log.info("Trying to safely execute query {} using JdbcDao.", pstmt.toString());
      ResultSet rs = pstmt.executeQuery();
      log.info("Executed query.");
      return rs;
    } catch (SQLException e) {
      e.printStackTrace();
      throw new DaoException(e.getMessage(),e.getCause(),e.getSQLState());
    } finally {
      closeDB(con);
    }
  }



  /**
   * Used for executing an arbitrary SQL update, with a given set of parameters.
   * @param query An arbitrary sql query, not {@code null}.
   * @param params A list of parameters to be inserted to the query, not {@code null}.
   * @return The number of update rows, not {@code null}.
   * @throws DaoException if query execution fails.
   */
  int executeUpdate(String query, List<Object> params) throws DaoException {
    Connection con = null;
    try {
      con = connectToDB();
      PreparedStatement pstmt = con.prepareStatement(query);
      parameterMapper(pstmt, params);
      log.info("Trying to safely execute query {} using JdbcDao.", pstmt.toString());
      int update = pstmt.executeUpdate();
      log.info("Executed query.");
      return update;
    } catch (SQLException e) {
      e.printStackTrace();
      throw new DaoException(e.getMessage(),e.getCause(), e.getSQLState());
    } finally {
      closeDB(con);
    }
  }


  /**
   * Used for executing an arbitrary SQL query.
   * @param query An arbitrary sql query, not {@code null}.
   * @return The query output as ResultSet, not {@code null}.
   * @throws DaoException if query execution fails.
   */
  ResultSet executeQuery(String query) throws DaoException {
    Connection con = null;
    try {
      con = connectToDB();
      PreparedStatement pstmt = con.prepareStatement(query);
      log.info("Trying to safely execute query {} using JdbcDao.", pstmt.toString());
      ResultSet rs = pstmt.executeQuery();
      log.info("Executed query.");
      return rs;
    } catch (SQLException e) {
      e.printStackTrace();
      throw new DaoException(e.getMessage(),e.getCause(), e.getSQLState());
    } finally {
      closeDB(con);
    }
  }
}
