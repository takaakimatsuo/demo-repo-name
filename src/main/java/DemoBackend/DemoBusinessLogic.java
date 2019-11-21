package DemoBackend;
import DataAccess.BookDao;
import DemoBackend.CustomENUMs.response_status;
import DemoBackend.CustomExceptions.BookException;
import DemoBackend.CustomObjects.*;
import DataAccess.CustomENUMs.BookStatus;
import org.springframework.stereotype.Component;

import java.sql.*;
import java.util.List;
import static DataAccess.BookDao.*;

//import static BackEnd_domain.*;


@Component
public final class DemoBusinessLogic {

    private static ResponseBooks res = new ResponseBooks();
    private static ResponseHeader header = new ResponseHeader();
    private static BookDao bookDao = new BookDao();
    //private static ResponseBooks res;
    //private static ResponseHeader header;

    /*
    * Simply return all books stored in the bookshelf table.
    * @param: None
    * @return: ResponseBooks res - A list of BookClass objects with the ResponseHeader class.
    */
     public ResponseBooks getAllBooks() throws SQLException{

        try {
            List<BookClass> books = new BookDao().searchAllBooks();
            header.setMessage("All books searched!");
            res.setBooks(books);
            res.setResponseHeader(header);

        }catch (SQLException e) {
            header.setMessage(e.toString());
            header.setStatus(response_status.ERR);
            res.setResponseHeader(header);
            throw new SQLException("SQL query failure: ", e);
        }
        return res;
    }





    public ResponseBooks removeBook(Integer id) throws SQLException{

        try {
            int update = bookDao.deleteBook(id);
            header.setMessage(update+" Data deleted from table.");
            res.setResponseHeader(header);

        }catch (SQLException e) {
            header.setMessage(e.toString());
            header.setStatus(response_status.ERR);
            res.setResponseHeader(header);
            throw new SQLException("SQL query failure: ", e);
        }
        return res;

    }



    //Simply return all books stored in the bookshelf table.
    //Input: Integer id - Unique identifier for the stored BookClass.
    //Output: ResponseBooks res - A list, but storing only a single BookClass object with the ResponseHeader class.
    public ResponseBooks getBook(Integer id) throws SQLException{

        try {
            List<BookClass> books = bookDao.searchBook(id);
            if(books.size()==0)
                header.setMessage("Book with id=" + id + " not found!");
            else
                header.setMessage("Book with id=" + id + " searched!");
            res.setResponseHeader(header);
            res.setBooks(books);

        }catch (SQLException e) {
            header.setMessage(e.toString());
            header.setStatus(response_status.ERR);
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


/*

    public ResponseBooks updateBookStatus(UpdateBookStatus upd_status) throws SQLException{
        ResponseBooks res = new ResponseBooks();
        ResponseHeader header = new ResponseHeader();
        int action = upd_status.getStatus();//0 = Borrow, 1 = Return, 2 = Lost.

        BookStatus current_status = checkBookStatus(upd_status.getBook_id(), upd_status.getPhone_number());

        try{
            //Check inconsistency. e.g. User trying to return a book that has not been borrowed.
            check_Status_inconsistency(current_status, action);
            switch(action){
                case 0:{//Borrow!
                    borrowBook(upd_status.getBook_id(), upd_status.getPhone_number());
                    header.setMessage("Borrowed book successfully.");
                    break;
                }
                case 1:{
                    returnBook(upd_status.getBook_id(), upd_status.getPhone_number());
                    header.setMessage("Returned book successfully.");
                    break;
                }
                case 2:{;
                    lostBook(upd_status.getBook_id(),upd_status.getPhone_number());
                    header.setMessage("Reported lost book successfully.");
                    break;
                } default:{
                    header.setMessage("Invalid status input.");
                    System.out.println("[ERROR] Invalid status input.");
                    break;
                }
            }
        }catch(BookException e){
            header.setStatus(response_status.ERR);
            header.setMessage(e.getMessage());
        }
        res.setResponseHeader(header);
        return res;
    }

*/


    public ResponseBooks updateBookStatus(Integer book_id, PatchBookClass upd_status) throws SQLException{

        int action = upd_status.getStatus();//0 = Borrow, 1 = Return, 2 = Lost.

        BookStatus current_status = bookDao.checkBookStatus(book_id, upd_status.getBorrower());

        try{
            //Check inconsistency. e.g. User trying to return a book that has not been borrowed.
            check_Status_inconsistency(current_status, action);
            switch(action){
                case 0:{//Borrow!
                    bookDao.borrowBook(book_id, upd_status.getBorrower());
                    header.setMessage("Borrowed book successfully.");
                    break;
                }
                case 1:{
                    bookDao.returnBook(book_id, upd_status.getBorrower());
                    header.setMessage("Returned book successfully.");
                    break;
                }
                case 2:{;
                    bookDao.lostBook(book_id,upd_status.getBorrower());
                    header.setMessage("Reported lost book successfully.");
                    break;
                } default:{
                    header.setMessage("Invalid status input.");
                    System.out.println("[ERROR] Invalid status input.");
                    break;
                }
            }
        }catch(BookException e){
            header.setStatus(response_status.ERR);
            header.setMessage(e.getMessage());
        }
        res.setResponseHeader(header);
        return res;
    }





    public ResponseBooks addBook(BookClass book) throws SQLException {

        try {
            //Check if the book-to-be-added already exists in the bookshelf table.
            boolean this_book_already_exists = bookDao.check_Book_Exists(book.getTitle());

            if(!this_book_already_exists) {
                List<BookClass> books = bookDao.insertBook(book);
                res.setBooks(books);
                header.setMessage("All ok. Book inserted to database.");
            }else{
                header.setMessage("The same book already exists in the database.");
                header.setStatus(response_status.ERR);
            }

        }catch(SQLException e){
            header.setMessage(e.toString());
            System.out.println("[Error] "+e.toString());
            throw e;
        }
        res.setResponseHeader(header);
        return res;
    }


}


