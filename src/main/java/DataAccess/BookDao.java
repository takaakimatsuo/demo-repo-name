package DataAccess;


import DemoBackend.CustomExceptions.BookException;
import DemoBackend.CustomExceptions.DataNotFoundException;
import DemoBackend.CustomObjects.BookClass;
import DataAccess.CustomENUMs.BookStatus;

import java.awt.print.Book;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public final class BookDao {
    private static final String url = "jdbc:postgresql://ec2-174-129-253-169.compute-1.amazonaws.com/d9vsaknll1319";
    private static final String user = "lfoagdwpzckmuq";
    private static final String password = "7cf9b7a5b57780ee7f45c96cac75808dd2cc2ba77b123cf0948cfb290ad1d93c";




    private static Connection connectToDB() throws SQLException {
        Connection con;
        try {
            con = DriverManager.getConnection(url, user, password);
            System.out.println("[INFO] Successfully connected to DB.");
        }catch(SQLException e){
            e.printStackTrace();
            throw new SQLException("Database Connection Error: Wrong user/password/url.") ;
        }
        return con;
    }



    private static void closeDB(Connection con) throws SQLException{
        try{
            if (con != null){
                System.out.println("[INFO] Closed DB connection.");
                con.close();
            }
        }catch (SQLException e){
            System.out.println("[ERR] Failed closing DB connection.");
            e.printStackTrace();
            throw new SQLException("Database Disconnection failed.");
        }
    }




    private static void mapParameters_to_PrepareStatement(PreparedStatement pstmt, List<Object> params) throws SQLException {
        int index = 1;
        for(final Object p: params){
            if(p.getClass() == Integer.class){
                pstmt.setInt(index, (int)p);
            }else if(p.getClass() == String.class){
                pstmt.setString(index, (String) p);
            }else if(p.getClass() == boolean.class){
                pstmt.setBoolean(index, (boolean) p);
            }/*else if(p == null){
                pstmt.setNull(index);
            }*/
            index++;
        }
    }

    //Used for executing an arbitrary SQL query.
    //Input: String query - Simple raw query for sql.
    private static ResultSet SafeExecuteQuery(String query, List<Object> params) throws SQLException{
        Connection con = null;
        try {
            //Establish DB connection.
            con = connectToDB();
            PreparedStatement pstmt = con.prepareStatement(query);
            mapParameters_to_PrepareStatement(pstmt, params);
            System.out.println("[INFO] Trying to safely execute query "+pstmt.toString());
            ResultSet rs = pstmt.executeQuery();
            System.out.println("[INFO] Executed query ");
            return rs;
        }catch(SQLException e){
            System.out.println("[ERROR] System error when executing query : "+e);
            throw e;
        }finally {
            closeDB(con);
        }
    }

    //Used for executing an arbitrary SQL query.
    //Input: String query - Simple raw query for sql.
    private static ResultSet SafeExecuteQuery(String query) throws SQLException{
        Connection con = null;
        try {
            //Establish DB connection.
            con = connectToDB();
            PreparedStatement pstmt = con.prepareStatement(query);
            System.out.println("[INFO] Trying to safely execute query "+pstmt.toString());
            ResultSet rs = pstmt.executeQuery();
            System.out.println("[INFO] Executed query ");
            return rs;
        }catch(SQLException e){
            System.out.println("[ERROR] System error when executing query : "+e);
            throw e;
        }finally {
            closeDB(con);
        }
    }



    //Used for executing an arbitrary SQL query.
    //Input: String query - Simple raw query for sql.
    private static int SafeExecuteUpdate(String query, List<Object> params) throws SQLException{
        Connection con = null;
        try {
            //Establish DB connection.
            con = connectToDB();

            PreparedStatement pstmt = con.prepareStatement(query);
            mapParameters_to_PrepareStatement(pstmt, params);
            System.out.println("[INFO] Trying to safely execute query "+pstmt.toString());
            int update = pstmt.executeUpdate();
            System.out.println("[INFO] Executed query ");
            return update;
        }catch(SQLException e){
            System.out.println("[ERROR] SQLException: "+e.getMessage());
            throw e;
        }finally {
            //Close DB connection.
            closeDB(con);
        }
    }



    // Outputs:
    //      BOOK_NOT_EXISTING - Book not existing.
    //      BOOK_NOT_BORROWED_BY_THIS_USER - Book currently not borrowed by the user. The users action must be "borrow".
    //      BOOK_BORROWED_BY_THIS_USER - Book currently borrowed by the user. The users action must be either "return", or "lost".
    public BookStatus checkBookStatus(Integer book_id, String phone_number) throws SQLException {

        String query = "SELECT borrowed_by FROM bookshelf WHERE id = ?";
        List<Object> param_list = new ArrayList<Object>();
        param_list.add(book_id);
        BookStatus st = BookStatus.UNKNOWN;

        ResultSet check = SafeExecuteQuery(query,param_list);//Execute query.
        if(!check.isBeforeFirst()){//SQL returned an empty output. No data matched the condition.
            System.out.println("[INFO] Book with id="+book_id+" doesnt exist in the table.");
            st = BookStatus.BOOK_NOT_EXISTING;
        }else{
            if(check.next()) {
                String arr = check.getString("BORROWED_BY");
                String[] customers = splitStringIntoArray(arr, ",", new String[]{"\"", "}", "{"});
                if (Arrays.asList(customers).contains(phone_number)) {//Book is borrowed by the user.
                    System.out.println("[INFO] User already borrowing the book.");
                    st = BookStatus.BOOK_BORROWED_BY_THIS_USER;
                }else{
                    System.out.println("[INFO] User not borrowing the book yet.");
                    st = BookStatus.BOOK_NOT_BORROWED_BY_THIS_USER;
                }
            }
        }
        return st;
    }


    private static boolean check_BookStock_Availability(Integer book_id) throws SQLException {
        boolean available = false;
        //String query = "Select quantity, borrowed_by AS customers from bookshelf WHERE id = " + book_id;
        String query = "SELECT COALESCE(array_length(borrowed_by, 1), 0) < quantity as stock_available FROM bookshelf WHERE id = ?";
        List<Object> param_list = new ArrayList<Object>();
        param_list.add(book_id);
        ResultSet check = SafeExecuteQuery(query,param_list);//Execute query.
        if(check.next()) {
            available = check.getBoolean("STOCK_AVAILABLE");
        }
        return available;
    }




    public void updateBook_borrowed(Integer book_id, String phone_number) throws SQLException, BookException {
        if(check_BookStock_Availability(book_id)) {
            //String query = "UPDATE bookshelf SET borrowed_by = array_append(borrowed_by, '" + phone_number + "') WHERE id = " + book_id;
            String query = "UPDATE bookshelf SET borrowed_by = array_append(borrowed_by, ?) WHERE id = ?";
            List<Object> param_list = new ArrayList<Object>();
            param_list.add(phone_number);
            param_list.add(book_id);
            int updated = SafeExecuteUpdate(query,param_list);
        }else{
            throw new BookException("Book stock not available");
        }
    }




    public void updateBook_returned(Integer book_id, String phone_number) throws SQLException {
        String query = "UPDATE bookshelf SET borrowed_by = array_remove(borrowed_by, ?) WHERE id = ?";
        List<Object> param_list = new ArrayList<Object>();
        param_list.add(phone_number);
        param_list.add(book_id);
        int updated = SafeExecuteUpdate(query,param_list);
    }


    //Reports a book as lost. This will decrease the book's quantity by 1, and delete the entire data from the bookshelf table when the quantity becomes less than 0.
    public void updateBook_lost(Integer book_id, String phone_number) throws SQLException {
        //String query = "UPDATE bookshelf SET borrowed_by = array_remove(borrowed_by, '"+phone_number+"') SET quantity = quantity-1 WHERE id = " + book_id+" RETURNING quantity";
        String query = "UPDATE bookshelf SET borrowed_by = array_remove(borrowed_by, ?), quantity = (quantity-1) WHERE id = ? RETURNING quantity";
        List<Object> param_list = new ArrayList<Object>();
        param_list.add(phone_number);
        param_list.add(book_id);
        ResultSet rs = SafeExecuteQuery(query,param_list);
        //ResultSet rs = ExecuteQuery(query);
        if (rs.next()) {
            int quantity = rs.getInt("QUANTITY");
            if(quantity <= 0){//If quantity is less than 0
                new BookDao().deleteBook(book_id);//Simply remove the book from the bookshelf
            }
        }
    }

    public int updateBook_data(Integer book_id, BookClass book) throws SQLException {
        String query = "UPDATE bookshelf SET title = ?, price = ?, url = ?, quantity = ? where id = ? AND borrowed_by = \'{}\'";
        //String query = "UPDATE bookshelf SET title = ?, price = ?, url = ?, quantity = ? where id = ? AND borrowed_by = \'{}\' RETURNING array_length(borrowed_by,1) as borrowers;";
        //String query = "UPDATE bookshelf SET title = ?, price = ?, url = ?, quantity = ? WHERE id = ? AND array_length(borrowed_by,1)=0 RETURNING id";
        List<Object> param_list = new ArrayList<Object>();
        param_list.add(book.getTitle());
        param_list.add(book.getPrice());
        param_list.add(book.getUrl());
        param_list.add(book.getQuantity());
        param_list.add(book_id);
        int updated = SafeExecuteUpdate(query,param_list);
        return updated;

    }



    public List<BookClass> insertBook(BookClass book) throws SQLException{
        //String query = "INSERT INTO bookshelf(title,price,quantity,url) values('" + book.getTitle() + "'," + book.getPrice() + "," + book.getQuantity() + ",'" + book.getUrl() + "') RETURNING *";
        String query = "INSERT INTO bookshelf(title,price,quantity,url) values(?, ?, ?, ?) RETURNING *";
        List<Object> param_list = new ArrayList<Object>();
        param_list.add(book.getTitle());
        param_list.add(book.getPrice());
        param_list.add(book.getQuantity());
        param_list.add(book.getUrl());
        System.out.println("[QUERY] " + query);
        //List<BookClass> lb = new ArrayList<BookClass>(){};
        try {
            ResultSet rs = SafeExecuteQuery(query, param_list);
            List<BookClass> lb = copyBookClass_From_ResultSet(rs);//Also takes care the case with no data found
            return lb;
        }catch(SQLException e){
            System.out.println("Error code:"+e.getErrorCode());
            System.out.println("SQL state:"+e.getSQLState());
            System.out.println("Error message:"+e.getMessage());
            throw e;
        }

    }



    public int deleteBook(Integer book_id) throws SQLException {
        String query = "DELETE FROM bookshelf WHERE id = ?";
        List<Object> param_list = new ArrayList<Object>();
        param_list.add(book_id);
        int update = SafeExecuteUpdate(query,param_list);
        return update;
    }


    public List<BookClass> getAllBooks() throws SQLException{
        String query = "SELECT id, title, price, quantity, (SELECT ARRAY( select family_name ||' '|| first_name FROM book_user u JOIN bookshelf b ON u.phone_number = ANY(b.borrowed_by) WHERE b.title = OuterQuery.title)) AS \"borrowed_by\", url FROM bookshelf AS OuterQuery ORDER BY id DESC;";
        System.out.println("[INFO] Requesting query execution");
        ResultSet rs = SafeExecuteQuery(query);
        System.out.println("[INFO] Done query execution");
        List<BookClass> lb = copyBookClass_From_ResultSet(rs);
        return lb;
    }

    public List<BookClass> getBook(Integer id) throws SQLException{
        String query = "select id, title, price, quantity, (SELECT ARRAY( select family_name ||' '|| first_name from book_user u JOIN bookshelf b ON u.phone_number = ANY(b.borrowed_by) WHERE b.title = OuterQuery.title)) AS \"borrowed_by\", url from bookshelf AS OuterQuery WHERE id = ?";
        List<Object> param_list = new ArrayList<Object>();
        param_list.add(id);
        System.out.println("[INFO] Requesting query execution");
        ResultSet rs = SafeExecuteQuery(query,param_list);
        System.out.println("[INFO] Done query execution");
        List<BookClass> lb = copyBookClass_From_ResultSet(rs);
        return lb;
    }



    private static List<BookClass> copyBookClass_From_ResultSet(ResultSet rs)throws SQLException{
        List<BookClass> lb = new ArrayList<>();
        if(!rs.isBeforeFirst()) {
            return lb;
        }
        while (rs.next()) {
            BookClass book = new BookClass();
            book.setId(rs.getInt("ID"));
            book.setPrice(rs.getInt("PRICE"));
            book.setQuantity(rs.getInt("QUANTITY"));
            book.setTitle(rs.getString("TITLE"));
            book.setUrl(rs.getString("URL"));
            String arr = rs.getString("BORROWED_BY");
            String[] customers = splitStringIntoArray(arr, ",", new String[]{"\"", "}", "{"});
            book.setBorrowed_by(customers);
            lb.add(book);
        }
        return lb;
    }










    private static String[] splitStringIntoArray(String str, String splitter, String[] replacer){
        String[] adjusted_to_array = new String[0];
        if(str==null){
            return adjusted_to_array;
        }
        for(final String rep: replacer) {
            str = str.replace(rep, "");
        }
        if(str.split(splitter)[0].compareTo("")!=0){
            adjusted_to_array=str.split(splitter);
        }
        return adjusted_to_array;
    }







}
