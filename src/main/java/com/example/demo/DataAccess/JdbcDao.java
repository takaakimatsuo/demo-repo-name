package com.example.demo.DataAccess;

import com.example.demo.Backend.CustomExceptions.DaoException;
import com.example.demo.Backend.CustomExceptions.DbException;


import java.sql.*;
import java.util.List;

import static com.example.demo.Backend.staticErrorMessages.StaticMessages.DB_FAILED_CONNECTION;
import static com.example.demo.Backend.staticErrorMessages.StaticMessages.DB_FAILED_DISCONNECTION;

public interface JdbcDao {

  default Connection connectToDB() throws DbException {
    String url = "jdbc:postgresql://ec2-174-129-253-169.compute-1.amazonaws.com/d9vsaknll1319";
    String user = "lfoagdwpzckmuq";
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

  default void closeDB(Connection con) throws DbException {
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



  default void parameterMapper(PreparedStatement pstmt, List<Object> params) throws SQLException {
    int index = 1;
    for (final Object p: params) {
      if (p.getClass() == Integer.class) {
        pstmt.setInt(index, (int)p);
      } else if (p.getClass() == String.class) {
        pstmt.setString(index, (String) p);
      } else if (p.getClass() == boolean.class) {
        pstmt.setBoolean(index, (boolean) p);
      }
      index++;
    }
  }


  /* Used for executing an arbitrary SQL query.
   * @param:  String query - Simple raw query for sql.
   *          List<Object> params - A list of parameters as objects.
   */
  default ResultSet executeQuery(String query, List<Object> params) throws DaoException, DbException {
    Connection con = null;
    try {
      //Establish DB connection.
      con = connectToDB();
      PreparedStatement pstmt = con.prepareStatement(query);
      parameterMapper(pstmt, params);
      System.out.println("[INFO] Trying to safely execute query " + pstmt.toString());
      ResultSet rs = pstmt.executeQuery();
      System.out.println("[INFO] Executed query ");
      return rs;
    } catch(SQLException e) {
      e.printStackTrace();
      throw new DaoException(e.getMessage() + ", SQL state:" + e.getSQLState());
    } finally {
      closeDB(con);
    }
  }



  /* Used for executing an arbitrary SQL query.
   * @param: String query - Simple raw query for sql.
   *         List<Object> params - A list of parameters as objects.
   */
  default int executeUpdate(String query, List<Object> params) throws DaoException, DbException {
    Connection con = null;
    try {
      //Establish DB connection.
      con = connectToDB();
      PreparedStatement pstmt = con.prepareStatement(query);
      parameterMapper(pstmt, params);
      System.out.println("[INFO] Trying to safely execute query " + pstmt.toString());
      int update = pstmt.executeUpdate();
      System.out.println("[INFO] Executed query ");
      return update;
    } catch (SQLException e) {
      e.printStackTrace();
      throw new DaoException(e.getMessage() + ", SQL state:" + e.getSQLState());
    } finally {
      //Close DB connection.
      closeDB(con);
    }
  }



  /* Used for executing an arbitrary SQL query.
   * @param: String query - Simple raw query for sql.
   */
  default ResultSet executeQuery(String query) throws DaoException, DbException {
    Connection con = null;
    try {
      //Establish DB connection.
      con = connectToDB();
      PreparedStatement pstmt = con.prepareStatement(query);
      System.out.println("[INFO] Trying to safely execute query " + pstmt.toString());
      ResultSet rs = pstmt.executeQuery();
      System.out.println("[INFO] Executed query ");
      return rs;
    } catch (SQLException e) {
      e.printStackTrace();
      throw new DaoException(e.getMessage() + ", SQL state:" + e.getSQLState());
    } finally {
      closeDB(con);
    }
  }
}
