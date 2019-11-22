package DemoBackend;

import DataAccess.BookDao;
import DemoBackend.CustomENUMs.response_status;
import DemoBackend.CustomExceptions.BookException;
import DemoBackend.CustomObjects.*;
import DataAccess.CustomENUMs.BookStatus;
import org.springframework.stereotype.Component;

import javax.sound.midi.Soundbank;
import java.sql.*;
import java.util.List;
import static DemoBackend.staticErrorCodes.SQLErrorCodes.*;


@Component
public final class DemoBusinessLogic {

    private ResponseBooks res;
    //private static ResponseHeader header = new ResponseHeader();
    private static BookDao bookDao = new BookDao();

    /*
    * Simply return all books stored in the bookshelf table.
    * @param: None
    * @return: ResponseBooks res - A list of BookClass objects with the ResponseHeader class.
    */
     public ResponseBooks getAllBooks() throws SQLException{
         res = new ResponseBooks();
        try {
            List<BookClass> books = new BookDao().getAllBooks();
            res.getResponseHeader().setMessage("All books searched!");
            res.setBooks(books);

        }catch (SQLException e) {
            res.getResponseHeader().setMessage(e.toString());
            res.getResponseHeader().setStatus(response_status.ERR);
            throw new SQLException("SQL query failure: ", e);
        }
        return res;
    }





    public ResponseBooks removeBook(Integer id) throws SQLException{
        res = new ResponseBooks();
        try {
            int update = bookDao.deleteBook(id);
            res.getResponseHeader().setMessage(update+" Data deleted from table.");;
        }catch (SQLException e) {
            res.getResponseHeader().setMessage(e.toString());
            res.getResponseHeader().setStatus(response_status.ERR);
            throw new SQLException("SQL query failure: ", e);
        }
        return res;
    }



    //Simply return all books stored in the bookshelf table.
    //Input: Integer id - Unique identifier for the stored BookClass.
    //Output: ResponseBooks res - A list, but storing only a single BookClass object with the ResponseHeader class.
    public ResponseBooks getBook(Integer id) throws SQLException{
        res = new ResponseBooks();
        try {
            List<BookClass> books = bookDao.getBook(id);
            if(books.size()==0)
                res.getResponseHeader().setMessage("Book with id=" + id + " not found!");
            else
                res.getResponseHeader().setMessage("Book with id=" + id + " searched!");
            res.setBooks(books);

        }catch (SQLException e) {
            res.getResponseHeader().setMessage(e.toString());
            res.getResponseHeader().setStatus(response_status.ERR);
            throw new SQLException("SQL query failure: ", e);
        }
        return res;
    }


    private int assureUserBookRelation(BookStatus current_status, int requested_status) throws BookException {
        res = new ResponseBooks();
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
        }else if (current_status == BookStatus.BOOK_NOT_BORROWED_BY_THIS_USER && requested_status == 1){
            System.out.println("[Error] Trying to return a book that has not been borrowed by the user.");
            throw new BookException("Trying to return a book that has not been borrowed by the user.");
        }else if (current_status == BookStatus.BOOK_NOT_BORROWED_BY_THIS_USER && requested_status == 2){
            System.out.println("[Error] Trying to report a book as lost, which has not been borrowed by the user.");
            throw new BookException("Trying to report a book as lost, which has not been borrowed by the user.");

        }
        return requested_status;
    }




    public ResponseBooks updateBook(Integer book_id, PatchBookClass upd_status) throws SQLException{
        res = new ResponseBooks();
        System.out.println(book_id+", "+upd_status.getBorrower()+", "+upd_status.getStatus());
        int action = upd_status.getStatus();//0 = Borrow, 1 = Return, 2 = Lost.
        BookStatus current_status = bookDao.checkBookStatus(book_id, upd_status.getBorrower());

        try{
            //Check inconsistency. e.g. User trying to return a book that has not been borrowed.
            switch(assureUserBookRelation(current_status, action)){
                case 0:{//Borrow!
                    bookDao.updateBook_borrowed(book_id, upd_status.getBorrower());
                    res.getResponseHeader().setMessage("Borrowed book successfully.");
                    break;
                }
                case 1:{
                    bookDao.updateBook_returned(book_id, upd_status.getBorrower());
                    res.getResponseHeader().setMessage("Returned book successfully.");
                    break;
                }
                case 2:{;
                    bookDao.updateBook_lost(book_id,upd_status.getBorrower());
                    res.getResponseHeader().setMessage("Reported lost book successfully.");
                    break;
                } default:{
                    res.getResponseHeader().setMessage("Invalid status input.");
                    System.out.println("[ERROR] Invalid status input.");
                    break;
                }
            }
        }catch(BookException e){
            res.getResponseHeader().setStatus(response_status.ERR);
            res.getResponseHeader().setMessage(e.getMessage());
        }
        return res;
    }





    public ResponseBooks addBook(BookClass book) throws SQLException {
        res = new ResponseBooks();
        try {
            List<BookClass> books = bookDao.insertBook(book);
            res.setBooks(books);
            res.getResponseHeader().setMessage("All ok. Book inserted to database.");
            return res;
        }catch(SQLException e){
            if(e.getSQLState().compareTo(SQL_CODE_DUPLICATE_KEY_ERROR)==0){
                res.getResponseHeader().setMessage("The same book already exists in the database.");
                res.getResponseHeader().setStatus(response_status.ERR);
                return res;
            }else {
                res.getResponseHeader().setMessage(e.toString());
                System.out.println("[Error] "+e.toString());
                throw e;
            }
        }
    }

    public ResponseBooks replaceBook(Integer book_id, BookClass book) throws SQLException {
        res = new ResponseBooks();
         try {
             int updated = bookDao.updateBook_data(book_id, book);
             if(updated == 0){
                 res.getResponseHeader().setMessage("Nothing updated. Wrong ID or the book is currently borrowed by someone.");
                 res.getResponseHeader().setStatus(response_status.ERR);
             }else{
                 res.getResponseHeader().setMessage("Book data with id="+book+" successfully updated.");
             }
         }catch(SQLException e){
             System.out.println(e.getMessage());
             if(e.getSQLState().compareTo(SQL_CODE_DUPLICATE_KEY_ERROR)==0){
                 res.getResponseHeader().setMessage("The same book already exists in the database.");
                 res.getResponseHeader().setStatus(response_status.ERR);
                 return res;
             }else {
                 throw e;
             }
         }
         return res;
    }


}


