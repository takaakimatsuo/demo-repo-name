package SQLappliers;


import DemoBackend.BookException;

import java.sql.*;
import java.util.Arrays;

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




public final class PSQL_APIs {


    private static final String url = "jdbc:postgresql://ec2-174-129-253-169.compute-1.amazonaws.com/d9vsaknll1319";
    private static final String user = "lfoagdwpzckmuq";
    private static final String password = "7cf9b7a5b57780ee7f45c96cac75808dd2cc2ba77b123cf0948cfb290ad1d93c";


    public static String[] splitStringIntoArray(String str, String splitter, String[] replacer){
        String[] adjusted_to_array = new String[0];
        String output = str;
        if(str==null){
            return adjusted_to_array;
        }
        for(String rep: replacer) {
            System.out.println("rep = "+rep+", from str="+output);
            output = output.replace(rep, "");
        }
        if(output.split(splitter)[0].compareTo("")!=0){
            adjusted_to_array=output.split(splitter);
        }
        return adjusted_to_array;
    }

    private static final ConnAndStat connectToDB(Connection conn) throws SQLException {
        Statement stmt = null;
        try {
            conn = DriverManager.getConnection(url, user, password);
            stmt = conn.createStatement();
            System.out.println("[INFO]  Opened DB connection.");
        }catch(SQLException e){
            System.out.println("[ERR] Failed opening DB connection.");
            throw new SQLException("Database Connection: ", e) ;
        }
        return new ConnAndStat(conn,stmt);
    }

    private static final void closeDB(Connection con) throws SQLException{
        try{
            if (con != null){
                System.out.println("[INFO] Closed DB connection.");
                con.close();
            }
        }catch (SQLException e){
            System.out.println("[ERR] Failed closing DB connection.");
            throw new SQLException("Database Disconnection: ", e);
        }
    }

    //Used for executing an arbitrary SQL query.
    //Input: String query - Simple raw query for sql.
    public static final ResultSet ExecuteQuery(String query) throws SQLException{
        ConnAndStat cs = new ConnAndStat();
        try {
            //Establish DB connection.
            cs = connectToDB(cs.conn);
            System.out.println("[INFO] Trying to execute query "+query);
            final String sql = query;
            ResultSet rs = cs.stmt.executeQuery(sql);
            System.out.println("[INFO] Executed query ");
            return rs;
        }catch(SQLException e){
            System.out.println("[ERROR] Something is wrong in ExecuteQuery() : "+e);
            throw e;
        }finally {
            //Close DB connection.
            closeDB(cs.conn);
        }
    }

    //Used for executing an arbitrary SQL query.
    //Input: String query - Simple raw query for sql.
    public static final int ExecuteUpdate(String query) throws SQLException{
        ConnAndStat cs = new ConnAndStat();
        try {
            //Establish DB connection.
            cs = connectToDB(cs.conn);
            System.out.println("[INFO] Trying to execute query "+query);
            final String sql = query;
            int update = cs.stmt.executeUpdate(sql);
            System.out.println("[INFO] Executed query ");
            return update;
        }catch(SQLException e){
            System.out.println("[ERROR] SQLException: "+e.getMessage());
            throw e;
        }finally {
            //Close DB connection.
            closeDB(cs.conn);
        }
    }


    // Outputs:
    //      BOOK_NOT_EXISTING - Book not existing.
    //      BOOK_NOT_BORROWED_BY_THIS_USER - Book currently not borrowed by the user. The users action must be "borrow".
    //      BOOK_BORROWED_BY_THIS_USER - Book currently borrowed by the user. The users action must be either "return", or "lost".
    public static final BookStatus checkBookStatus(Integer book_id, String phone_number) throws SQLException {

        String query = "Select borrowed_by AS customers from bookshelf WHERE id = " + book_id;
        BookStatus st = BookStatus.UNKNOWN;
        try {
            ResultSet check = ExecuteQuery(query);//Execute query.
            if(!check.isBeforeFirst()){//SQL returned an empty output. No data matched the condition.
                System.out.println("[INFO] Book with id="+book_id+" doesnt exist in the table.");
                 st = BookStatus.BOOK_NOT_EXISTING;
            }else{
                if(check.next()) {
                    String arr = check.getString("CUSTOMERS");
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
        }catch (SQLException e){
            throw e;
        }
        return st;
    }


    public static final boolean checkStockAvailability(Integer book_id) throws SQLException {
        boolean availability = false;
        String query = "Select quantity, borrowed_by AS customers from bookshelf WHERE id = " + book_id;
        ResultSet check = ExecuteQuery(query);//Execute query.
        if(check.next()) {
            String quantity = check.getString("QUANTITY");
            String arr = check.getString("CUSTOMERS");
            String[] customers = splitStringIntoArray(arr, ",", new String[]{"\"", "}", "{"});
            if(Integer.parseInt(quantity) > customers.length){
                System.out.println("[INFO] Book stock available");
                availability = true;
            }

            System.out.println(Integer.parseInt(quantity)+">"+customers.length);
        }
        return availability;
    }

    public static final void borrowBook(Integer book_id, String phone_number) throws SQLException, BookException {
        if(checkStockAvailability(book_id)) {
            String query = "UPDATE bookshelf SET borrowed_by = array_append(borrowed_by, '" + phone_number + "') WHERE id = " + book_id;
            int updated = ExecuteUpdate(query);
        }else{
            throw new BookException("Book stock not available");
        }
    }

    public static final void returnBook(Integer book_id, String phone_number) throws SQLException {
        String query = "UPDATE bookshelf SET borrowed_by = array_remove(borrowed_by, '"+phone_number+"') WHERE id = " + book_id;
        int updated = ExecuteUpdate(query);
    }

    public static final void lostBook(Integer book_id, String phone_number) throws SQLException {
        String query = "UPDATE bookshelf SET borrowed_by = array_remove(borrowed_by, '"+phone_number+"') SET quantity = quantity-1 WHERE id = " + book_id+" RETURNING quantity";
        ResultSet rs = ExecuteQuery(query);
        if (rs.next()) {
            int quantity = rs.getInt("QUANTITY");
            if(quantity < 0){//If quantity is less than 0
                deleteBook(book_id);//Simply remove the book from the bookshelf
            }
        }
    }

    public static final int deleteBook(Integer book_id) throws SQLException {
        String query = "DELETE FROM bookshelf WHERE id = "+book_id;
        int update = ExecuteUpdate(query);
        return update;
    }





}
