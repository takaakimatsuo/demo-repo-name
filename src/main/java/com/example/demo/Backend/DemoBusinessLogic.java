package com.example.demo.Backend;

import com.example.demo.Backend.CustomExceptions.DuplicateBookException;
import com.example.demo.DataAccess.BookDao;
import com.example.demo.Backend.CustomENUMs.response_status;
import com.example.demo.Backend.CustomExceptions.BookException;
import com.example.demo.Backend.CustomObjects.*;
import com.example.demo.DataAccess.CustomENUMs.BookStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;
import java.sql.*;
import java.util.List;
import static com.example.demo.Backend.staticErrorCodes.SQLErrorCodes.*;
import static com.example.demo.Backend.staticErrorMessages.staticMessages.*;


@Component
public final class DemoBusinessLogic {

    //@Autowired
    private ResponseBooks res;

    @Autowired
    //@Qualifier("JdbcBookDao")
    @Qualifier("SpringBookDao")
    private BookDao dao;

    /*
    * Simply return all books stored in the bookshelf table.
    * @param: None
    * @return: ResponseBooks res - A list of BookClass objects with the ResponseHeader class.
    */
     public ResponseBooks getAllBooks() throws SQLException{
         res = new ResponseBooks();
        try {
            List<BookClass> books = dao.getAllBooks();
            res.getResponseHeader().setMessage(BOOK_FOUND);
            res.getResponseHeader().setStatus(response_status.OK);
            res.setBooks(books);
        }catch (SQLException e) {
            //TODO
            res.getResponseHeader().setMessage(e.toString());
            res.getResponseHeader().setStatus(response_status.ERR);
            System.out.println("[ERROR] SQL failure"+e.getMessage());
            throw new SQLException("SQL query failure: ", e);
        }
        return res;
    }



    public ResponseBooks removeBook(Integer id) throws SQLException{
        res = new ResponseBooks();
        try {
            int update = dao.deleteBook(id);
            if(update == 0){
                res.getResponseHeader().setMessage(BOOK_NOT_EXISTING);
                res.getResponseHeader().setStatus(response_status.ERR);
            }else {
                res.getResponseHeader().setMessage(BOOK_DELETED);
                res.getResponseHeader().setStatus(response_status.OK);
            }
        }catch (SQLException e) {
            //TODO
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
            List<BookClass> books = dao.getBook(id);
            if(books.size()==0) {
                res.getResponseHeader().setMessage(BOOK_NOT_EXISTING);
                res.getResponseHeader().setStatus(response_status.ERR);
            }
            else{
                res.getResponseHeader().setMessage(BOOK_FOUND);
                res.getResponseHeader().setStatus(response_status.OK);
            }
            res.setBooks(books);


        }catch (SQLException e) {
            //TODO
            res.getResponseHeader().setMessage(e.toString());
            res.getResponseHeader().setStatus(response_status.ERR);
            throw new SQLException("SQL query failure: ", e);
        }
        return res;
    }


    private int assureUserBookRelation(BookStatus current_status, int requested_status) throws BookException {
        System.out.println("Current Status = "+current_status.toString());
        if(current_status == BookStatus.UNKNOWN){
            System.out.println("[Error] Unexpected output. This should not happen.");
            throw new BookException(UNEXPECTED);
        }else if (current_status == BookStatus.BOOK_NOT_EXISTING) {
            System.out.println("[Error] Book does not exist.");
            throw new BookException(BOOK_NOT_EXISTING);
        }else if(current_status == BookStatus.BOOK_BORROWED_BY_THIS_USER && requested_status == 0){
            System.out.println("[Error] Book already borrowed by the same user.");
            throw new BookException(BOOK_CANNOT_BE_DOUBLE_BORROWED);
        }else if (current_status == BookStatus.BOOK_NOT_BORROWED_BY_THIS_USER && requested_status == 1){
            System.out.println("[Error] Trying to return a book that has not been borrowed by the user.");
            throw new BookException(BOOK_CANNOT_BE_RETURNED);
        }else if (current_status == BookStatus.BOOK_NOT_BORROWED_BY_THIS_USER && requested_status == 2){
            System.out.println("[Error] Trying to report a book as lost, which has not been borrowed by the user.");
            throw new BookException(BOOK_CANNOT_BE_LOST);
        }
        return requested_status;
    }




    public ResponseBooks updateBook(Integer book_id, PatchBookClass upd_status) throws SQLException{
        res = new ResponseBooks();
        System.out.println(book_id+", "+upd_status.getBorrower()+", "+upd_status.getStatus());
        int action = upd_status.getStatus();//0 = Borrow, 1 = Return, 2 = Lost.
        BookStatus current_status = dao.checkBookStatus(book_id, upd_status.getBorrower());

        try{
            //Check inconsistency. e.g. User trying to return a book that has not been borrowed.
            switch(assureUserBookRelation(current_status, action)){
                case 0:{//Borrow!
                    dao.updateBook_borrowed(book_id, upd_status.getBorrower());
                    res.getResponseHeader().setMessage(BOOK_BORROWED);
                    res.getResponseHeader().setStatus(response_status.OK);
                    break;
                }
                case 1:{
                    dao.updateBook_returned(book_id, upd_status.getBorrower());
                    res.getResponseHeader().setMessage(BOOK_RETURNED);
                    res.getResponseHeader().setStatus(response_status.OK);
                    break;
                }
                case 2:{;
                    dao.updateBook_lost(book_id,upd_status.getBorrower());
                    res.getResponseHeader().setMessage(BOOK_LOST);
                    res.getResponseHeader().setStatus(response_status.OK);
                    break;
                } default:{
                    res.getResponseHeader().setMessage(INVALID_STATUS);
                    res.getResponseHeader().setStatus(response_status.ERR);
                    break;
                }
            }
        }catch(BookException e){
            //TODO
            res.getResponseHeader().setStatus(response_status.ERR);
            res.getResponseHeader().setMessage(e.getMessage());
        }
        return res;
    }



    public ResponseBooks addBook(BookClass book) throws DuplicateBookException {
        res = new ResponseBooks();
        try {
            List<BookClass> books = dao.insertBook(book);
            res.setBooks(books);
            res.getResponseHeader().setMessage(BOOK_INSERTED);
            res.getResponseHeader().setStatus(response_status.OK);
            return res;
        }catch(DuplicateBookException e){
            //TODO
            res.getResponseHeader().setMessage(BOOK_DUPLICATE);
            res.getResponseHeader().setStatus(response_status.ERR);
            return res;
        }
    }

    public ResponseBooks replaceBook(Integer book_id, BookClass book) throws DuplicateBookException {
        res = new ResponseBooks();
         try {
             int updated = dao.updateBook_data(book_id, book);
             if(updated == 0){
                 res.getResponseHeader().setMessage(UPDATE_FAILED_NO_MATCH_BOOK);
                 res.getResponseHeader().setStatus(response_status.ERR);
             }else{
                 res.getResponseHeader().setMessage(UPDATE_SUCCESS_BOOK);
                 res.getResponseHeader().setStatus(response_status.OK);
             }
         }catch(DuplicateBookException e){
             //TODO
             System.out.println(e.getMessage());
             res.getResponseHeader().setMessage(BOOK_DUPLICATE);
             res.getResponseHeader().setStatus(response_status.ERR);
             return res;
         }
         return res;
    }


}


