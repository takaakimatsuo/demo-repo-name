package DatabaseQueryApplier;

import com.example.demo.BookClass;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.web.bind.annotation.PostMapping;

import java.awt.print.Book;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

//import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;



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

    //Used for executing an arbitrary SQL query.
    //Input: String query - Simple raw query for sql.
    private ResultSet ExecuteQuery(String query) throws SQLException{
        Connection conn = null;
        ConnAndStat cs = new ConnAndStat(conn);
        try {
            //Establish DB connection.
            cs = connectToDB(conn);
            System.out.println("[INFO] Executed query.");
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

            ObjectMapper mapper = new ObjectMapper();

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



    public String addBook(BookClass book) throws SQLException, JsonProcessingException {
        String output = "";
        try {
            String query = "Insert into book(title,price,quantity,borrowed,url) values('"+book.getTitle()+"',"+book.getPrice()+","+book.getQuantity()+",0,'"+book.getURL()+"')";
            System.out.println("[QUERY] "+query);
            ResultSet rs = ExecuteQuery(query);
            System.out.println(rs);
            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString(book);

            output = "{ message: This is a test Message. book:"+book+"}";

        }catch(SQLException | JsonProcessingException e){
            output += e;
            throw e;
        }
        return output;
    }




}


