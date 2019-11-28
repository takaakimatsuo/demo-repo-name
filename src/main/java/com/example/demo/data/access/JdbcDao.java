package com.example.demo.data.access;

import com.example.demo.backend.custom.enums.ExceptionCodes;
import com.example.demo.backend.custom.exceptions.DaoException;
import com.example.demo.backend.custom.exceptions.DbException;


import java.sql.*;
import java.util.List;

import static com.example.demo.backend.errormessages.StaticMessages.DB_FAILED_CONNECTION;
import static com.example.demo.backend.errormessages.StaticMessages.DB_FAILED_DISCONNECTION;

public abstract class JdbcDao {

  /**
   * Tries to connect to the database.
   * @return Connection object
   * @throws DbException AN exception that raises when connecting/disconnecting from the database.
   */
  Connection connectToDB() throws DbException {
    String url = "jdbc:postgresql://ec2-174-129-253-169.compute-1.amazonaws.com/d9vsaknll1319";
    String user = "lfoagdwpzckmuq";
    //String user = "wrongUserOnPurpose";
    String password = "7cf9b7a5b57780ee7f45c96cac75808dd2cc2ba77b123cf0948cfb290ad1d93c";

    try {
      Connection con = DriverManager.getConnection(url, user, password);
      System.out.println("[INFO] Successfully connected to DB.");
      return con;
    } catch (SQLException e) {
      e.printStackTrace();
      throw new DbException(DB_FAILED_CONNECTION);
    }
  }

  /**
   * Tries to disconnect from the database.
   * @param con Connection object.
   * @throws DbException AN exception that raises when connecting/disconnecting from the database.
   */
  void closeDB(Connection con) throws DbException {
    try {
      if (con != null) {
        System.out.println("[INFO] Closed DB connection.");
        con.close();
      }
    } catch (SQLException e) {
      System.out.println("[ERR] Failed closing DB connection.");
      e.printStackTrace();
      throw new DbException(DB_FAILED_DISCONNECTION);
    }
  }





  /**
   * Inserts the parameters into the PreparedStatement one by one.
   * Currently assumes that each parameter is either an Integer, String or Boolean.
   * @param pstmt A raw PreparedStatement with no parameter inserted.
   * @param params A list of parameters to be inserted to the statement.
   * @throws DaoException An exception that raises when executing an SQL query.
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
      throw new DaoException(e.getMessage(),e.getCause(), ExceptionCodes.SYSTEM_ERROR);
    }
  }


  /**
   * Used for executing an arbitrary SQL query, with a given set of parameters.
   * The process includes the database connection, query execution, and the database disconnection.
   * @param query Arbitrary sql query.
   * @param params A list of parameters to be inserted to the statement.
   *               The number of elements must equal to the number of ? in the query.
   * @return Query output as ResultSet
   * @throws DaoException An exception that raises when executing an SQL query.
   * @throws DbException AN exception that raises when connecting/disconnecting from the database.
   */
  ResultSet executeQuery(String query, List<Object> params) throws DaoException, DbException {
    Connection con = null;
    try {
      con = connectToDB();
      PreparedStatement pstmt = con.prepareStatement(query);
      parameterMapper(pstmt, params);
      System.out.println("[INFO] Trying to safely execute query " + pstmt.toString());
      ResultSet rs = pstmt.executeQuery();
      System.out.println("[INFO] Executed query ");
      return rs;
    } catch (SQLException e) {
      e.printStackTrace();
      System.out.println("[ERROR] e.getMessage = " + e.getMessage());
      System.out.println("[ERROR] e.getSQLstate = " + e.getSQLState());
      System.out.println("[ERROR] e.getCause = " + e.getCause());
      throw new DaoException(e.getMessage() + "," + e.getSQLState(),e.getCause(),e.getSQLState(),ExceptionCodes.SYSTEM_ERROR);
    } finally {
      closeDB(con);
    }
  }



  /**
   * Used for executing an arbitrary SQL update, with a given set of parameters.
   * The process includes the database connection, query execution, and the database disconnection.
   * @param query Arbitrary sql query.
   * @param params A list of parameters to be inserted to the query.
   * @return The number of update rows.
   * @throws DaoException An exception that raises when executing an SQL query.
   * @throws DbException AN exception that raises when connecting/disconnecting from the database.
   */
  int executeUpdate(String query, List<Object> params) throws DaoException, DbException {
    Connection con = null;
    try {
      con = connectToDB();
      PreparedStatement pstmt = con.prepareStatement(query);
      parameterMapper(pstmt, params);
      System.out.println("[INFO] Trying to safely execute query " + pstmt.toString());
      int update = pstmt.executeUpdate();
      System.out.println("[INFO] Executed query ");
      return update;
    } catch (SQLException e) {
      e.printStackTrace();
      System.out.println("[ERROR] e.getMessage = " + e.getMessage());
      System.out.println("[ERROR] e.getSQLstate = " + e.getSQLState());
      System.out.println("[ERROR] e.getCause = " + e.getCause());
      throw new DaoException(e.getMessage() + "," + e.getSQLState(),e.getCause(), e.getSQLState(), ExceptionCodes.SYSTEM_ERROR);
    } finally {
      //Close DB connection.
      closeDB(con);
    }
  }



  /**
   * Used for executing an arbitrary SQL query.
   * The process includes the database connection, query execution, and the database disconnection.
   * @param query Arbitrary sql query.
   * @return Query output as ResultSet
   * @throws DaoException An exception that raises when executing an SQL query.
   * @throws DbException AN exception that raises when connecting/disconnecting from the database.
   */
   ResultSet executeQuery(String query) throws DaoException, DbException {
    Connection con = null;
    try {
      con = connectToDB();
      PreparedStatement pstmt = con.prepareStatement(query);
      System.out.println("[INFO] Trying to safely execute query " + pstmt.toString());
      ResultSet rs = pstmt.executeQuery();
      System.out.println("[INFO] Executed query ");
      return rs;
    } catch (SQLException e) {
      e.printStackTrace();
      System.out.println("[ERROR] e.getMessage = " + e.getMessage());
      System.out.println("[ERROR] e.getSQLstate = " + e.getSQLState());
      System.out.println("[ERROR] e.getCause = " + e.getCause());
      throw new DaoException(e.getMessage() + "," + e.getSQLState(),e.getCause(), e.getSQLState(), ExceptionCodes.SYSTEM_ERROR);
    } finally {
      closeDB(con);
    }
  }
}
