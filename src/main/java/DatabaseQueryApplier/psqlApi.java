package DatabaseQueryApplier;

import com.example.demo.BookClass;
import org.springframework.web.bind.annotation.PostMapping;

import java.awt.print.Book;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;


final class ConnAndStat{
    Connection conn;
    Statement stmt;
    ConnAndStat( Connection conn, Statement stmt){
        this.conn = conn;
        this.stmt = stmt;
    }
    ConnAndStat( Connection conn){
        this.conn = conn;
        this.stmt = null;
    }
}

public class psqlApi {

    private final String url = "jdbc:postgresql://ec2-174-129-253-169.compute-1.amazonaws.com/d9vsaknll1319";
    private final String user = "lfoagdwpzckmuq";
    private final String password = "7cf9b7a5b57780ee7f45c96cac75808dd2cc2ba77b123cf0948cfb290ad1d93c";







    private ConnAndStat connectToDB(Connection conn) throws SQLException{
        Statement stmt = null;
        try {
            conn = DriverManager.getConnection(url, user, password);
            stmt = conn.createStatement();
            System.out.println("[INFO]  Opened DB connection.");
        }catch(SQLException e){
            throw new SQLException("Database Connection: ", e) ;
        }
        return new ConnAndStat(conn,stmt);
    }

    private void closeDB(Connection con) throws SQLException{
        try{
            if (con != null){
                System.out.println("[INFO] Closed DB connection.");
                con.close();
            }
        }catch (SQLException e){
            throw new SQLException("Database Disconnection: ", e);
        }
    }

    private ResultSet ExecuteQuery(String query) throws SQLException{
        Connection conn = null;
        ConnAndStat cs = new ConnAndStat(conn);
        try {
            //Establish DB connection.
            System.out.println("[INFO] Executed query.");
            cs = connectToDB(conn);
            final String sql = query;
            ResultSet rs = cs.stmt.executeQuery(sql);
            return rs;
        }catch(SQLException e){
            throw e;
        }finally {
            //Close DB connection.
            closeDB(cs.conn);
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



    public void addBook(BookClass book) throws SQLException {
        try {
            String query = "Insert into book(title,price,quantity,borrowed,url) values('"+book.getTitle()+"',"+book.getPrice()+","+book.getQuantity()+",0,'"+book.getURL()+"')";
            ResultSet rs = ExecuteQuery(query);
            System.out.println(rs);
        }catch(SQLException e){
            throw e;
        }
    }




}


