package DatabaseQueryApplier;
import com.example.demo.ResponseBooks;
import com.example.demo.BookClass;
import com.example.demo.ResponseMsg;
import com.example.demo.response_status;
//import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
//import org.springframework.web.bind.annotation.PostMapping;

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
    ConnAndStat(){
        this.conn = null;
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
        ConnAndStat cs = new ConnAndStat();
        try {
            //Establish DB connection.
            cs = connectToDB(cs.conn);
            System.out.println("[INFO] Trying to execute query "+query);
            final String sql = query;
            ResultSet rs = cs.stmt.executeQuery(sql);
            return rs;
        }catch(SQLException e){
            System.out.println("[ERROR] Something is wrong in ExecuteQuery()");
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



    public ResponseBooks getAllBooks() throws SQLException{
        ResponseBooks res = new ResponseBooks();
        ResponseMsg msg = new ResponseMsg();
        List<BookClass> lb = new ArrayList<>();
        try {
            //String query = "select title AS \"Book Title\", borrowed_by, (SELECT ARRAY( select family_name from book_user u JOIN books b ON u.phone_number = ANY(b.borrowed_by) WHERE b.title = Z.title)) from books AS Z";
            String query = "select title from bookshelf";
            ResultSet rs = ExecuteQuery(query);
            System.out.println(rs);
            int colCount = rs.getMetaData().getColumnCount();
            System.out.println("取得したカラム数:" + colCount);
            msg.setMsg("All books searched!");
            //ObjectMapper mapper = new ObjectMapper();

            while (rs.next()) {
                BookClass book = new BookClass();
                book.setPrice(rs.getInt("PRICE"));
                book.setQuantity(rs.getInt("QUANTITY"));
                book.setTitle(rs.getString("TITLE"));
                book.setUrl(rs.getString("URL"));
                lb.add(book);
            }

            res.setResponseStatus(msg);
            res.setListBooks(lb);

        }catch (SQLException e) {
            msg.setMsg(e.toString());
            msg.setRS(response_status.ERR);
            res.setResponseStatus(msg);
            throw new SQLException("SQL query failure: ", e);
        }
        return res;
    }



    public ResponseMsg addBook(BookClass book) throws SQLException, JsonProcessingException {
        ResponseMsg rm = new ResponseMsg();
        String output = "";
        int flag = 0;
        try {
            System.out.println("Hello there!");
            String check_existance_query = "Select count(title) AS checking from bookshelf WHERE title ='"+book.getTitle()+"' LIMIT 1";
            ResultSet check = ExecuteQuery(check_existance_query);
            while(check.next()) {
                flag = check.getInt("checking");//1 if a book with the same title already exists.
            }
            if(flag==0) {
                String query = "INSERT INTO bookshelf(title,price,quantity,url) values('" + book.getTitle() + "'," + book.getPrice() + "," + book.getQuantity() + ",'" + book.getUrl() + "') RETURNING 'All OK'";
                System.out.println("[QUERY] " + query);
                ResultSet rs = ExecuteQuery(query);
                //System.out.println(rs);
                //ObjectMapper mapper = new ObjectMapper();
                //String json = mapper.writeValueAsString(book);
                output = "All ok. Book inserted to database.";
            }else{
                output = "The same book already exists in the database.";
                rm.setRS(response_status.ERR);
            }

        }catch(SQLException e /*| JsonProcessingException e*/){
            output += e;
            System.out.println("Error!!!");
            throw e;
        }
        rm.setMsg(output);
        return rm;
    }




}


