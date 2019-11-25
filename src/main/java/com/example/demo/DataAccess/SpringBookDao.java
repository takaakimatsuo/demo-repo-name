package com.example.demo.DataAccess;

import com.example.demo.Backend.CustomExceptions.BookException;
import com.example.demo.Backend.CustomExceptions.DuplicateBookException;
import com.example.demo.Backend.CustomObjects.BookClass;
import com.example.demo.DataAccess.CustomENUMs.BookStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component("SpringBookDao")
public class SpringBookDao implements BookDao{

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public List<BookClass> getAllBooks() throws SQLException {
        List<BookClass> list = jdbcTemplate.query("select * from bookshelf", new RowMapper<BookClass>() {
            public BookClass mapRow(ResultSet rs, int rowNum) throws SQLException {
                BookClass book = new BookClass();
                book.setId(rs.getInt("ID"));
                book.setTitle(rs.getString("TITLE"));
                book.setPrice(rs.getInt("PRICE"));
                book.setQuantity(rs.getInt("QUANTITY"));
                book.setUrl(rs.getString("URL"));
                return book;
            }
        });
        return list;
    }

    @Override
    public List<BookClass> getBook(Integer book_id) throws SQLException {
        List<BookClass> list = jdbcTemplate.query("select * from bookshelf where id = ?", new RowMapper<BookClass>() {
            public BookClass mapRow(ResultSet rs, int rowNum) throws SQLException {
            BookClass book = new BookClass();
            book.setId(rs.getInt("ID"));
            book.setTitle(rs.getString("TITLE"));
            book.setPrice(rs.getInt("PRICE"));
            book.setQuantity(rs.getInt("QUANTITY"));
            book.setUrl(rs.getString("URL"));
            return book;
        }
    },book_id);

        return list;
    }

    @Override
    public List<BookClass> insertBook(BookClass book) throws DuplicateBookException {

        List<BookClass> list;
        try {
            list = jdbcTemplate.query("INSERT INTO bookshelf(title,price,quantity,url) values(?, ?, ?, ?) RETURNING *",
                    new RowMapper<BookClass>() {
                        public BookClass mapRow(ResultSet rs, int rowNum) throws SQLException {
                            BookClass book = new BookClass();
                            book.setId(rs.getInt("ID"));
                            book.setTitle(rs.getString("TITLE"));
                            book.setPrice(rs.getInt("PRICE"));
                            book.setQuantity(rs.getInt("QUANTITY"));
                            book.setUrl(rs.getString("URL"));
                            return book;
                        }
                    }, book.getTitle(), book.getPrice(), book.getQuantity(), book.getUrl());

        }catch (DataAccessException e) {
            //TODO
            throw new DuplicateBookException("Same book already exists");
        }
        return list;
    }

    @Override
    public int deleteBook(Integer book_id) throws SQLException {
        int updated = jdbcTemplate.update("DELETE FROM bookshelf WHERE id = ?",book_id);
        return updated;
    }


    @Override
    public BookStatus checkBookStatus(Integer bookId, String phone_number) throws SQLException {

        BookStatus st = BookStatus.UNKNOWN;

        List<BookClass> customers = jdbcTemplate.query("select borrowedby from bookshelf where id = ?", new RowMapper<BookClass>() {
            public BookClass mapRow(ResultSet rs, int rowNum) throws SQLException {
                BookClass book = new BookClass();
                book.setBorrowedBy( splitStringIntoArray(rs.getString("BORROWEDBY"), ",", new String[]{"\"", "}", "{"}));
                return book;
            }
        },bookId);

        if(customers.size()==0) {
            System.out.println("list =  null ");
            st = BookStatus.BOOK_NOT_EXISTING;
        }else{
            if (Arrays.asList(customers.get(0).getBorrowedBy()).contains(phone_number)) {//Book is borrowed by the user.
                System.out.println("[INFO] User already borrowing the book.");
                st = BookStatus.BOOK_BORROWED_BY_THIS_USER;
            }else{
                System.out.println("[INFO] User not borrowing the book yet.");
                st =  BookStatus.BOOK_NOT_BORROWED_BY_THIS_USER;
            }
        }
        return st;

    }



    @Override
    public boolean checkBookStockAvailability(Integer bookId) throws SQLException {
        Boolean available = false;
        available = jdbcTemplate.queryForObject("SELECT COALESCE(array_length(borrowedBy, 1), 0) < quantity as stock_available FROM bookshelf WHERE id = ?", new RowMapper<Boolean>() {
            public Boolean mapRow(ResultSet rs, int rowNum) throws SQLException {
                return rs.getBoolean("STOCK_AVAILABLE");
            }
        },bookId);
        return available;
    }

    @Override
    public void updateBook_borrowed(Integer bookId, String phoneNumber) throws SQLException, BookException {
        if(checkBookStockAvailability(bookId)) {
            int updated = jdbcTemplate.update("UPDATE bookshelf SET borrowedBy = array_append(borrowedBy, ?) WHERE id = ?",phoneNumber,bookId);
        }else{
            throw new BookException("Book stock not available");
        }
    }

    @Override
    public void updateBook_returned(Integer bookId, String phoneNumber) throws SQLException {
        int updated = jdbcTemplate.update("UPDATE bookshelf SET borrowedBy = array_remove(borrowedBy, ?) WHERE id = ?",phoneNumber,bookId);
    }

    @Override
    public void updateBook_lost(Integer bookId, String phoneNumber) throws SQLException {
        Integer remainingQuantity = jdbcTemplate.queryForObject("UPDATE bookshelf SET borrowedBy = array_remove(borrowedBy, ?), quantity = (quantity-1) WHERE id = ? RETURNING quantity", new RowMapper<Integer>() {
            public Integer mapRow(ResultSet rs, int rowNum) throws SQLException {
                return rs.getInt("QUANTITY");
            }
        },phoneNumber,bookId);
        if(remainingQuantity <= 0){//If quantity is less than 0
            deleteBook(bookId);//Simply remove the book from the bookshelf
        }
    }

    @Override
    public int updateBook_data(Integer bookId, BookClass book) throws DuplicateBookException {
        try {
            int updated = jdbcTemplate.update("UPDATE bookshelf SET title = ?, price = ?, url = ?, quantity = ? where id = ? AND borrowedBy = \'{}\'", book.getTitle(), book.getPrice(), book.getUrl(), book.getQuantity(), bookId);
            return updated;
        }catch(DataAccessException e){
            throw new DuplicateBookException("Book with same title already exists");
        }

    }
}
