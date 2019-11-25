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


    //TODO
    @Override
    public BookStatus checkBookStatus(Integer bookId, String phone_number) throws SQLException {

        String list = jdbcTemplate.queryForObject("select borrowedBy from bookshelf where id = ? LIMIT 1", new RowMapper<String>() {
            public String mapRow(ResultSet rs, int rowNum) throws SQLException {
                String borrowedBy = null;

                if(!rs.isBeforeFirst()){//SQL returned an empty output. No data matched the condition.
                    System.out.println("[INFO] Book with id="+bookId+" doesnt exist in the table.");
                    return borrowedBy;
                }else{
                    if(rs.next()) {
                        return rs.getString("BORROWEDBY");
                    }
                }
                return borrowedBy;
            }
            }, bookId);

        if(list==null) {
            System.out.println("list =  null ");
            return BookStatus.BOOK_NOT_EXISTING;
        }else{
            System.out.println("list = " + list);
            String[] customers = splitStringIntoArray(list, ",", new String[]{"\"", "}", "{"});
            if (Arrays.asList(customers).contains(phone_number)) {//Book is borrowed by the user.
                System.out.println("[INFO] User already borrowing the book.");
                return BookStatus.BOOK_BORROWED_BY_THIS_USER;
            }else{
                System.out.println("[INFO] User not borrowing the book yet.");
                return BookStatus.BOOK_NOT_BORROWED_BY_THIS_USER;
            }
        }

    }



    //@Override
    public boolean checkBookStockAvailability(Integer book_id) throws SQLException {
        return false;
    }

    //@Override
    public void updateBook_borrowed(Integer book_id, String phone_number) throws SQLException, BookException {

    }

    //@Override
    public void updateBook_returned(Integer book_id, String phone_number) throws SQLException {

    }

    //@Override
    public void updateBook_lost(Integer book_id, String phone_number) throws SQLException {

    }

    //@Override
    public int updateBook_data(Integer book_id, BookClass book) throws SQLException {
        return 0;
    }
}
