package DemoBackend;
import DemoBackend.CustomENUMs.response_status;
import DemoBackend.CustomExceptions.BookException;
import DemoBackend.CustomObjects.BookClass;
import DemoBackend.CustomObjects.ResponseBooks;
import DemoBackend.CustomObjects.ResponseHeader;
import DemoBackend.CustomObjects.UpdateBookStatus;
import SQLappliers.CustomENUMs.BookStatus;

import java.sql.*;
import java.util.List;
import static SQLappliers.PSQL_APIs.*;

//import static BackEnd_domain.*;


public final class DemoDomain {


    //Simply return all books stored in the bookshelf table.
    //Input: None
    //Output: ResponseBooks res - A list of BookClass objects.
    public ResponseBooks getAllBooks() throws SQLException{
        ResponseBooks res = new ResponseBooks();
        ResponseHeader header = new ResponseHeader();

        try {
            List<BookClass> lb = searchAllBooks();
            header.setMsg("All books searched!");
            res.setBooks(lb);
            res.setResponseHeader(header);

        }catch (SQLException e) {
            header.setMsg(e.toString());
            header.setRS(response_status.ERR);
            res.setResponseHeader(header);
            throw new SQLException("SQL query failure: ", e);
        }
        return res;
    }





    public ResponseBooks removeBook(Integer id) throws SQLException{
        ResponseBooks res = new ResponseBooks();
        ResponseHeader header = new ResponseHeader();
        try {
            int update = deleteBook(id);
            header.setMsg(update+" Data deleted from table.");
            res.setResponseHeader(header);

        }catch (SQLException e) {
            header.setMsg(e.toString());
            header.setRS(response_status.ERR);
            res.setResponseHeader(header);
            throw new SQLException("SQL query failure: ", e);
        }
        return res;

    }



    //Simply return all books stored in the bookshelf table.
    //Input: Integer id - Unique identifier for the stored BookClass.
    //Output: ResponseBooks res - A list, but storing only a single BookClass object.
    public ResponseBooks getBook(Integer id) throws SQLException{
        ResponseBooks res = new ResponseBooks();
        ResponseHeader header = new ResponseHeader();

        try {
            List<BookClass> lb = searchBook(id);
            if(lb.size()==0)
                header.setMsg("Book with id=" + id + " not found!");
            else
                header.setMsg("Book with id=" + id + " searched!");
            res.setResponseHeader(header);
            res.setBooks(lb);

        }catch (SQLException e) {
            header.setMsg(e.toString());
            header.setRS(response_status.ERR);
            res.setResponseHeader(header);
            throw new SQLException("SQL query failure: ", e);
        }
        return res;
    }


    private void check_Status_inconsistency(BookStatus current_status, int requested_status) throws BookException {
        System.out.println("Current Status = "+current_status.toString());
        if(current_status == BookStatus.UNKNOWN){
            System.out.println("[Error] Unexpected output. This should not happen.");
            throw new BookException("Unexpected output. This should not happen.");
        }else if (current_status == BookStatus.BOOK_NOT_EXISTING) {
            System.out.println("[Error] Book does not exist.");
            throw new BookException("Book does not exist.");
        }else if(current_status == BookStatus.BOOK_BORROWED_BY_THIS_USER && requested_status == 0){
            System.out.println("[Error] Book already borrowed by the same user.");
            throw new BookException("Book already borrowed by the same user.");
        }else if (current_status == BookStatus.BOOK_NOT_BORROWED_BY_THIS_USER && (requested_status == 1 || requested_status == 2)){
            System.out.println("[Error] Trying to return/report a book that has not been borrowed by the user.");
            throw new BookException("Trying to return a book that has not been borrowed by the user.");
        }/*else if(current_status == BookStatus.BOOK_STOCK_NOT_AVAILABLE && requested_status == 0){
            System.out.println("[Error] Book stock not available.");
            throw new BookException("Book stock not available.");
        }*/
    }




    public ResponseBooks updateBookStatus(UpdateBookStatus upd_status) throws SQLException{
        ResponseBooks res = new ResponseBooks();
        ResponseHeader rm = new ResponseHeader();
        int action = upd_status.getStatus();//0 = Borrow, 1 = Return, 2 = Lost.

        BookStatus current_status = checkBookStatus(upd_status.getBook_id(), upd_status.getPhone_number());

        try{
            //Check inconsistency. e.g. User trying to return a book that has not been borrowed.
            check_Status_inconsistency(current_status, action);
            switch(action){
                case 0:{//Borrow!
                    borrowBook(upd_status.getBook_id(), upd_status.getPhone_number());
                    rm.setMsg("Borrowed book successfully.");
                    break;
                }
                case 1:{
                    returnBook(upd_status.getBook_id(), upd_status.getPhone_number());
                    rm.setMsg("Returned book successfully.");
                    break;
                }
                case 2:{;
                    lostBook(upd_status.getBook_id(),upd_status.getPhone_number());
                    rm.setMsg("Reported lost book successfully.");
                    break;
                } default:{
                    rm.setMsg("Invalid status input.");
                    System.out.println("[ERROR] Invalid status input.");
                    break;
                }
            }
        }catch(BookException e){
            rm.setRS(response_status.ERR);
            rm.setMsg(e.getMessage());
        }
        res.setResponseHeader(rm);
        return res;
    }



    public ResponseBooks addBook(BookClass book) throws SQLException {
        ResponseBooks res = new ResponseBooks();
        ResponseHeader rm = new ResponseHeader();

        try {
            //Check if the book-to-be-added already exists in the bookshelf table.
            boolean this_book_already_exists = check_Book_Exists(book.getTitle());

            if(!this_book_already_exists) {
                List<BookClass> lb = insertBook(book);
                res.setBooks(lb);
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
        res.setResponseHeader(rm);
        return res;
    }


}


