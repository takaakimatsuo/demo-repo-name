package DatabaseQueryApplier;

import com.example.demo.BookClass;
import org.springframework.web.bind.annotation.PostMapping;

import java.awt.print.Book;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class psqlApi {
    private Connection conn = null;
    private final String url = "jdbc:postgresql://ec2-174-129-253-169.compute-1.amazonaws.com/d9vsaknll1319";
    private final String user = "lfoagdwpzckmuq";
    private final String password = "7cf9b7a5b57780ee7f45c96cac75808dd2cc2ba77b123cf0948cfb290ad1d93c";


    private Statement connectToDB() throws SQLException{
        Statement stmt = null;
        try {
            conn = DriverManager.getConnection(url, user, password);
            stmt = conn.createStatement();
            System.out.println("Opened DB connection.");
        }catch(SQLException e){
            throw new SQLException("Database Connection: ", e) ;
        }
        return stmt;
    }

    private void closeDB(Connection con) throws SQLException{
        try{
            if (con != null){
                System.out.println("\\ Closed DB connection");
                con.close();
            }
        }catch (SQLException e){
            throw new SQLException("Database Disconnection: ", e);
        }
    }

    private ResultSet ExecuteQuery(String query) throws SQLException{
        try {
            Statement stmt = connectToDB();
            final String sql = query;
            //String sql = "SELECT * FROM book";
            ResultSet rs = stmt.executeQuery(sql);
            return rs;
        }catch(SQLException e){
            throw e;
        }
    }
    public String getBookFromTitle(String title) throws SQLException {
        String output = "";
        try {
            String query = "SELECT * FROM book WHERE TITLE = "+title;
            ResultSet rs = ExecuteQuery(query);
            System.out.println(rs);
            int colCount = rs.getMetaData().getColumnCount();
            System.out.println("取得したカラム数:" + colCount);

            while (rs.next()) {
                ///行からデータを取得
                output += rs.getString("TITLE") + ": " + rs.getInt("PRICE") + "yen";
                System.out.println(rs.getString("TITLE"));
                System.out.print(rs.getInt("PRICE"));
            }
        } catch (SQLException e) {
            throw new SQLException("SQL query failure: ", e);
        }
        return output;
    }

    public String getBookFromID(int ID) throws SQLException {
        String output = "";
        try {
            String query = "SELECT * FROM book WHERE ID = "+ID;
            ResultSet rs = ExecuteQuery(query);
            System.out.println(rs);
            int colCount = rs.getMetaData().getColumnCount();
            System.out.println("取得したカラム数:" + colCount);

            while (rs.next()) {
                ///行からデータを取得
                output += rs.getString("TITLE") + ": " + rs.getInt("PRICE") + "yen";
                System.out.println(rs.getString("TITLE"));
                System.out.print(rs.getInt("PRICE"));
            }
        } catch (SQLException e) {
            throw new SQLException("SQL query failure: ", e);
        }
        return output;
    }

    public List<BookClass> getAllBooks() throws SQLException{
        List<BookClass> lb = new ArrayList<>();

        try {
            String query = "SELECT * FROM book";
            ResultSet rs = ExecuteQuery(query);
            System.out.println(rs);
            int colCount = rs.getMetaData().getColumnCount();
            System.out.println("取得したカラム数:" + colCount);

            while (rs.next()) {
                BookClass book = new BookClass();
                book.setPrice(rs.getInt("PRICE"));
                book.setQuantity(rs.getInt("QUANTITY"));
                book.setTitle(rs.getString("TITLE"));
                book.setURL(rs.getString("URL"));
                lb.add(book);
            }
        }catch (SQLException e) {
            throw new SQLException("SQL query failure: ", e);
        }
        return lb;
    }

    public void addBook(int ID) throws SQLException {
        try {
            String query = "SELECT * FROM book WHERE ID = " + ID;
            ResultSet rs = ExecuteQuery(query);

        }catch(SQLException e){
            throw e;
        }
    }




}


