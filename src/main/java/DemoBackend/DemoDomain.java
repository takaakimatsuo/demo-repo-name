package DemoBackend;
import SQLappliers.BookStatus;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import static SQLappliers.PSQL_APIs.*;

//import static BackEnd_domain.*;


public final class DemoDomain {


    //Simply return all books stored in the bookshelf table.
    //Input: None
    //Output: ResponseBooks res - A list of BookClass objects.
    public ResponseBooks getAllBooks() throws SQLException{
        ResponseBooks res = new ResponseBooks();
        ResponseMsg msg = new ResponseMsg();
        List<BookClass> lb = new ArrayList<>();
        try {
            String query = "select id, title, price, quantity, (SELECT ARRAY( select family_name ||' '|| first_name from book_user u JOIN bookshelf b ON u.phone_number = ANY(b.borrowed_by) WHERE b.title = OuterQuery.title)) AS \"customers\", url from bookshelf AS OuterQuery ORDER BY id DESC;";
            System.out.println("[INFO] Requesting query execution");
            ResultSet rs = ExecuteQuery(query);
            System.out.println("[INFO] Done query execution");
            msg.setMsg("All books searched!");

            while (rs.next()) {
                BookClass book = new BookClass();
                book.setId(rs.getInt("ID"));
                book.setPrice(rs.getInt("PRICE"));
                book.setQuantity(rs.getInt("QUANTITY"));
                book.setTitle(rs.getString("TITLE"));
                book.setUrl(rs.getString("URL"));
                String arr = rs.getString("CUSTOMERS");
                String[] ary = splitStringIntoArray(arr, ",", new String[]{"\"", "}", "{"});
                book.setBorrowed_by(ary);
                lb.add(book);
            }

            res.setListBooks(lb);
            res.setResponseStatus(msg);

        }catch (SQLException e) {
            msg.setMsg(e.toString());
            msg.setRS(response_status.ERR);
            res.setResponseStatus(msg);
            throw new SQLException("SQL query failure: ", e);
        }
        return res;
    }





    public ResponseBooks removeBook(Integer id) throws SQLException{
        ResponseBooks res = new ResponseBooks();
        ResponseMsg msg = new ResponseMsg();
        try {
            int update = deleteBook(id);
            msg.setMsg(update+" Data deleted from table.");
            res.setResponseStatus(msg);

        }catch (SQLException e) {
            msg.setMsg(e.toString());
            msg.setRS(response_status.ERR);
            res.setResponseStatus(msg);
            throw new SQLException("SQL query failure: ", e);
        }
        return res;

    }


    //Simply return all books stored in the bookshelf table.
    //Input: Integer id - Unique identifier for the stored BookClass.
    //Output: ResponseBooks res - A list, but storing only a single BookClass object.
    public ResponseBooks getBook(Integer id) throws SQLException{
        ResponseBooks res = new ResponseBooks();
        ResponseMsg msg = new ResponseMsg();
        List<BookClass> lb = new ArrayList<>();
        try {

            String query = "select id, title, price, quantity, (SELECT ARRAY( select family_name ||' '|| first_name from book_user u JOIN bookshelf b ON u.phone_number = ANY(b.borrowed_by) WHERE b.title = OuterQuery.title)) AS \"customers\", url from bookshelf AS OuterQuery WHERE id = "+id;
            System.out.println("[INFO] Requesting query execution");
            ResultSet rs = ExecuteQuery(query);
            System.out.println("[INFO] Done query execution");

            if(!rs.isBeforeFirst()){
                msg.setMsg("No book found with the given ID = "+id+".");
            }else{
                msg.setMsg("A book found with the given ID = "+id+".");
            }
            while (rs.next()) {
                BookClass book = new BookClass();
                book.setId(rs.getInt("ID"));
                book.setPrice(rs.getInt("PRICE"));
                book.setQuantity(rs.getInt("QUANTITY"));
                book.setTitle(rs.getString("TITLE"));
                book.setUrl(rs.getString("URL"));
                String arr = rs.getString("CUSTOMERS");
                arr = arr.replace("}","");
                arr = arr.replace("{","");
                arr = arr.replace("\"","");
                String[] ary = arr.split(",");
                book.setBorrowed_by(ary);
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


    public void check_Status_inconsistency(BookStatus current_status, int requested_status) throws BookException{
        if(current_status == BookStatus.UNKNOWN){
            throw new BookException("Unexpected output. This should not happen.");
        }else if (current_status == BookStatus.BOOK_NOT_EXISTING) {
            throw new BookException("Book does not exist.");
        }else if(current_status == BookStatus.BOOK_BORROWED_BY_THIS_USER && requested_status == 0){
            throw new BookException("Book already borrowed by the same user.");
        }else if (current_status == BookStatus.BOOK_NOT_BORROWED_BY_THIS_USER && requested_status == 1) {
            throw new BookException("Trying to return a book that has not been borrowed by the user.");
        }
    }

    public ResponseBooks updateBookStatus(UpdateBookStatus upd_status) throws SQLException{
        ResponseBooks res = new ResponseBooks();
        ResponseMsg rm = new ResponseMsg();
        int action = upd_status.getStatus();//0 = Borrow, 1 = Return, 2 = Lost.

        BookStatus current_status = checkBookStatus(upd_status.getBook_id(), upd_status.getPhone_number());

        try{
            //Check inconsistency. e.g. User trying to return a book that has not been borrowed.
            check_Status_inconsistency(current_status, action);
            System.out.println("All ok!");
            switch(action){
                case 0:{//Borrow!
                    borrowBook(upd_status.getBook_id(), upd_status.getPhone_number());
                    break;
                }
                case 1:{
                    returnBook(upd_status.getBook_id(), upd_status.getPhone_number());
                    break;
                }
                case 2:{;
                    break;
                } default:{
                    System.out.println("[ERROR] Invalid status inputted.");
                    break;
                }
            }
        }catch(BookException e){
            rm.setMsg(e.getMessage());
        }

        /*try {
            //String query = "UPDATE SET status = "+upd_status.getStatus()+" WHERE id=";

        }catch(SQLException e){
            rm.setMsg(e.toString());
            System.out.println("[Error] "+e.toString());
            throw e;
        }*/
        return res;
    }



    public ResponseBooks addBook(BookClass book) throws SQLException {
        ResponseBooks res = new ResponseBooks();
        ResponseMsg rm = new ResponseMsg();
        int flag = 0;
        try {
            //System.out.println("Hello there!");
            String check_existance_query = "Select count(title) AS checking from bookshelf WHERE title ='"+book.getTitle()+"' LIMIT 1";
            ResultSet check = ExecuteQuery(check_existance_query);

            ResultSetMetaData rsmd = check.getMetaData();
            int columns = rsmd.getColumnCount();
            //String[] str = new String[columns];
            for (int x = 1; x <= columns; x++) {
                System.out.println(rsmd.getColumnName(x));
                //str[x] = rsmd.getColumnName(x);
            }


            while(check.next()) {
                flag = check.getInt("checking");//1 if a book with the same title already exists.
            }
            if(flag==0) {
                String query = "INSERT INTO bookshelf(title,price,quantity,url) values('" + book.getTitle() + "'," + book.getPrice() + "," + book.getQuantity() + ",'" + book.getUrl() + "') RETURNING id";
                System.out.println("[QUERY] " + query);
                ResultSet rs  = ExecuteQuery(query);

                while (rs.next()) {
                    book.setId(rs.getInt("ID"));
                }
                rm.setMsg("All ok. Book inserted to database.");

            }else{
                rm.setMsg("The same book already exists in the database.");
                rm.setRS(response_status.ERR);
            }

        }catch(SQLException e){
            rm.setMsg(e.toString());
            System.out.println("[Error] "+e.toString());
            throw e;
        }
        res.setResponseStatus(rm);
        res.getListBooks();
        List<BookClass>bo = new ArrayList<>();
        bo.add(book);
        res.setListBooks(bo);
        return res;
    }




}


