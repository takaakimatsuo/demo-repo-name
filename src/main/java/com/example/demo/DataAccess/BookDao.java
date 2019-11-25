package com.example.demo.DataAccess;

import com.example.demo.Backend.CustomExceptions.BookException;
import com.example.demo.Backend.CustomExceptions.DuplicateBookException;
import com.example.demo.Backend.CustomObjects.BookClass;
import com.example.demo.DataAccess.CustomENUMs.BookStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public interface BookDao {


    List<BookClass> getAllBooks() throws SQLException;
    List<BookClass> getBook(Integer bookId) throws SQLException;
    List<BookClass> insertBook(BookClass book) throws DuplicateBookException;
    int deleteBook(Integer bookId) throws SQLException;

    BookStatus checkBookStatus(Integer bookId, String phone_number) throws SQLException;
    boolean checkBookStockAvailability(Integer bookId) throws SQLException;

    void updateBook_borrowed(Integer bookId, String phone_number) throws SQLException, BookException;
    void updateBook_returned(Integer bookId, String phone_number) throws SQLException;
    void updateBook_lost(Integer bookId, String phone_number) throws SQLException;
    int updateBook_data(Integer bookId, BookClass book) throws SQLException;

    default String[] splitStringIntoArray(String str, String splitter, String[] replacer) {
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
