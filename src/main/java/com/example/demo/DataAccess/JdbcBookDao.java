package com.example.demo.DataAccess;


import com.example.demo.Backend.CustomExceptions.BookException;
import com.example.demo.Backend.CustomExceptions.DuplicateBookException;
import com.example.demo.Backend.CustomObjects.BookClass;
import com.example.demo.DataAccess.CustomENUMs.BookStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


@Component("JdbcBookDao")
public final class JdbcBookDao implements BookDao {

    private static String url = "jdbc:postgresql://ec2-174-129-253-169.compute-1.amazonaws.com/d9vsaknll1319";
    private static String user = "lfoagdwpzckmuq";
    private static String password = "7cf9b7a5b57780ee7f45c96cac75808dd2cc2ba77b123cf0948cfb290ad1d93c";



    private Connection connectToDB() throws SQLException {
        try {
            Connection con = DriverManager.getConnection(url, user, password);
            System.out.println("[INFO] Successfully connected to DB.");
            return con;
        }catch(SQLException e){
            e.printStackTrace();
            throw new SQLException("Database Connection Error: Maybe wrong user/password/url.") ;
        }
    }


    private void closeDB(Connection con) throws SQLException{
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







    private void parameterMapper(PreparedStatement pstmt, List<Object> params) throws SQLException {
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
    private ResultSet ExecuteQuery(String query, List<Object> params) throws SQLException{
        Connection con = null;
        try {
            //Establish DB connection.
            con = connectToDB();
            PreparedStatement pstmt = con.prepareStatement(query);
            parameterMapper(pstmt, params);
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
    private int ExecuteUpdate(String query, List<Object> params) throws SQLException{
        Connection con = null;
        try {
            //Establish DB connection.
            con = connectToDB();

            PreparedStatement pstmt = con.prepareStatement(query);
            parameterMapper(pstmt, params);
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



    //Used for executing an arbitrary SQL query.
    //Input: String query - Simple raw query for sql.
    private ResultSet ExecuteQuery(String query) throws SQLException{
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





    @Override
    // Outputs:
    //      BOOK_NOT_EXISTING - Book not existing.
    //      BOOK_NOT_BORROWED_BY_THIS_USER - Book currently not borrowed by the user. The users action must be "borrow".
    //      BOOK_BORROWED_BY_THIS_USER - Book currently borrowed by the user. The users action must be either "return", or "lost".
    public BookStatus checkBookStatus(Integer bookId, String phone_number) throws SQLException {

        String query = "SELECT borrowedBy FROM bookshelf WHERE id = ?";
        List<Object> param_list = new ArrayList<Object>();
        param_list.add(bookId);
        BookStatus st = BookStatus.UNKNOWN;

        ResultSet check = ExecuteQuery(query,param_list);//Execute query.
        if(!check.isBeforeFirst()){//SQL returned an empty output. No data matched the condition.
            System.out.println("[INFO] Book with id="+bookId+" doesnt exist in the table.");
            st = BookStatus.BOOK_NOT_EXISTING;
        }else{
            if(check.next()) {
                String arr = check.getString("BORROWEDBY");
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


    @Override
    public boolean checkBookStockAvailability(Integer bookId) throws SQLException {
        boolean available = false;
        //String query = "Select quantity, borrowed_by AS customers from bookshelf WHERE id = " + book_id;
        String query = "SELECT COALESCE(array_length(borrowedBy, 1), 0) < quantity as stock_available FROM bookshelf WHERE id = ?";
        List<Object> param_list = new ArrayList<Object>();
        param_list.add(bookId);
        ResultSet check = ExecuteQuery(query,param_list);//Execute query.
        if(check.next()) {
            available = check.getBoolean("STOCK_AVAILABLE");
        }
        return available;
    }




    @Override
    public void updateBook_borrowed(Integer bookId, String phone_number) throws SQLException, BookException {
        if(checkBookStockAvailability(bookId)) {
            //String query = "UPDATE bookshelf SET borrowed_by = array_append(borrowed_by, '" + phone_number + "') WHERE id = " + book_id;
            String query = "UPDATE bookshelf SET borrowedBy = array_append(borrowedBy, ?) WHERE id = ?";
            List<Object> param_list = new ArrayList<Object>();
            param_list.add(phone_number);
            param_list.add(bookId);
            int updated = ExecuteUpdate(query,param_list);
        }else{
            throw new BookException("Book stock not available");
        }
    }




    @Override
    public void updateBook_returned(Integer bookId, String phone_number) throws SQLException {
        String query = "UPDATE bookshelf SET borrowedBy = array_remove(borrowedBy, ?) WHERE id = ?";
        List<Object> param_list = new ArrayList<Object>();
        param_list.add(phone_number);
        param_list.add(bookId);
        int updated = ExecuteUpdate(query,param_list);
    }


    @Override
    //Reports a book as lost. This will decrease the book's quantity by 1, and delete the entire data from the bookshelf table when the quantity becomes less than 0.
    public void updateBook_lost(Integer bookId, String phone_number) throws SQLException {
        //String query = "UPDATE bookshelf SET borrowed_by = array_remove(borrowed_by, '"+phone_number+"') SET quantity = quantity-1 WHERE id = " + book_id+" RETURNING quantity";
        String query = "UPDATE bookshelf SET borrowedBy = array_remove(borrowedBy, ?), quantity = (quantity-1) WHERE id = ? RETURNING quantity";
        List<Object> param_list = new ArrayList<Object>();
        param_list.add(phone_number);
        param_list.add(bookId);
        ResultSet rs = ExecuteQuery(query,param_list);
        //ResultSet rs = ExecuteQuery(query);
        if (rs.next()) {
            int quantity = rs.getInt("QUANTITY");
            if(quantity <= 0){//If quantity is less than 0
                deleteBook(bookId);//Simply remove the book from the bookshelf
            }
        }
    }

    @Override
    public int updateBook_data(Integer bookId, BookClass book) throws DuplicateBookException {
        String query = "UPDATE bookshelf SET title = ?, price = ?, url = ?, quantity = ? where id = ? AND borrowedBy = \'{}\'";
        //String query = "UPDATE bookshelf SET title = ?, price = ?, url = ?, quantity = ? where id = ? AND borrowed_by = \'{}\' RETURNING array_length(borrowed_by,1) as borrowers;";
        //String query = "UPDATE bookshelf SET title = ?, price = ?, url = ?, quantity = ? WHERE id = ? AND array_length(borrowed_by,1)=0 RETURNING id";
        List<Object> param_list = new ArrayList<Object>();
        param_list.add(book.getTitle());
        param_list.add(book.getPrice());
        param_list.add(book.getUrl());
        param_list.add(book.getQuantity());
        param_list.add(bookId);
        try {
            int updated = ExecuteUpdate(query, param_list);
            return updated;

        }catch(SQLException e){
            throw new DuplicateBookException(e.getMessage());
        }

    }



    @Override
    public List<BookClass> insertBook(BookClass book) throws DuplicateBookException{
        //String query = "INSERT INTO bookshelf(title,price,quantity,url) values('" + book.getTitle() + "'," + book.getPrice() + "," + book.getQuantity() + ",'" + book.getUrl() + "') RETURNING *";
        String query = "INSERT INTO bookshelf(title,price,quantity,url) values(?, ?, ?, ?) RETURNING *";
        List<Object> param_list = new ArrayList<Object>();
        param_list.add(book.getTitle());
        param_list.add(book.getPrice());
        param_list.add(book.getQuantity());
        param_list.add(book.getUrl());
        //System.out.println("[QUERY] " + query);
        //List<BookClass> lb = new ArrayList<BookClass>(){};
        try {
            ResultSet rs = ExecuteQuery(query, param_list);
            List<BookClass> lb = mapRow(rs);//Also takes care the case with no data found
            return lb;
        }catch(SQLException e){
            System.out.println("Error code:"+e.getErrorCode());
            System.out.println("SQL state:"+e.getSQLState());
            System.out.println("Error message:"+e.getMessage());
            throw new DuplicateBookException("Same book already exists");
        }

    }



    @Override
    public int deleteBook(Integer bookId) throws SQLException {
        String query = "DELETE FROM bookshelf WHERE id = ?";
        List<Object> param_list = new ArrayList<Object>();
        param_list.add(bookId);
        int update = ExecuteUpdate(query,param_list);
        return update;
    }



    @Override
    public List<BookClass> getAllBooks() throws SQLException{
        String query = "SELECT id, title, price, quantity, (SELECT ARRAY( select familyName ||' '|| firstName FROM book_user u JOIN bookshelf b ON u.phoneNumber = ANY(b.borrowedBy) WHERE b.title = OuterQuery.title)) AS \"borrowedBy\", url FROM bookshelf AS OuterQuery ORDER BY id DESC;";
        System.out.println("[INFO] Requesting query execution");
        ResultSet rs = ExecuteQuery(query);
        System.out.println("[INFO] Done query execution");
        List<BookClass> lb = mapRow(rs);
        return lb;
    }


    public List<BookClass> getBook(Integer bookId) throws SQLException{
        String query = "select id, title, price, quantity, (SELECT ARRAY( select familyName ||' '|| firstName from book_user u JOIN bookshelf b ON u.phoneNumber = ANY(b.borrowedBy) WHERE b.title = OuterQuery.title)) AS \"borrowedBy\", url from bookshelf AS OuterQuery WHERE id = ?";
        List<Object> param_list = new ArrayList<Object>();
        param_list.add(bookId);
        System.out.println("[INFO] Requesting query execution");
        ResultSet rs = ExecuteQuery(query,param_list);
        System.out.println("[INFO] Done query execution");
        List<BookClass> lb = mapRow(rs);
        return lb;
    }


    private List<BookClass> mapRow(ResultSet rs)throws SQLException{
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
            String arr = rs.getString("BORROWEDBY");
            String[] customers = splitStringIntoArray(arr, ",", new String[]{"\"", "}", "{"});
            book.setBorrowedBy(customers);
            lb.add(book);
        }
        return lb;
    }











//
//
//
//    private static String[] splitStringIntoArray(String str, String splitter, String[] replacer){
//        String[] adjusted_to_array = new String[0];
//        if(str==null){
//            return adjusted_to_array;
//        }
//        for(final String rep: replacer) {
//            str = str.replace(rep, "");
//        }
//        if(str.split(splitter)[0].compareTo("")!=0){
//            adjusted_to_array=str.split(splitter);
//        }
//        return adjusted_to_array;
//    }











/*


    private static List<BookUser> copyBookUserFromResultSet(ResultSet rs)throws SQLException{
        List<BookUser> lu = new ArrayList<>();
        if(!rs.isBeforeFirst()) {
            return lu;
        }
        while (rs.next()) {
            BookUser user = new BookUser();
            user.setFamilyName(rs.getString("familyName"));
            user.setFirstName(rs.getString("firstName"));
            user.setFamilyName(rs.getString("phoneNumber"));
            lu.add(user);
        }
        return lu;
    }

    public List<BookUser> insertBookUser(BookUser book) throws SQLException{
        String query = "INSERT INTO book_user(familyName,firstName,phoneNumber) values(?, ?, ?) RETURNING *";
        List<Object> paramList = new ArrayList<Object>();
        paramList.add(book.getFamilyName());
        paramList.add(book.getFirstName());
        paramList.add(book.getPhoneNumber());
        //System.out.println("[QUERY] " + query);
        try {
            ResultSet rs = SafeExecuteQuery(query, paramList);
            List<BookUser> lu = copyBookUserFromResultSet(rs);//Also takes care the case with no data found
            return lu;
        }catch(SQLException e){
            System.out.println("Error code:"+e.getErrorCode());
            System.out.println("SQL state:"+e.getSQLState());
            System.out.println("Error message:"+e.getMessage());
            throw e;
        }

    }

 */



}
