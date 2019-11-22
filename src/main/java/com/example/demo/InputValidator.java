package com.example.demo;

import com.example.demo.Backend.CustomExceptions.InputFormatExeption;
import com.example.demo.Backend.CustomObjects.BookClass;
import com.example.demo.Backend.CustomObjects.PatchBookClass;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InputValidator {
    private static final String PHONE_NUMBER_FORMAT = "^\\d{10,15}$";
    private static final String URL_FORMAT = "^https?://[a-z\\.:/\\+\\-\\#\\?\\=\\&\\;\\%\\~]+$";
    private static Pattern url_pattern = Pattern.compile(URL_FORMAT);
    private static Pattern phonenumber_pattern = Pattern.compile(PHONE_NUMBER_FORMAT);

    public static Integer assureInteger(String integer) throws InputFormatExeption {
        Integer result = null;

        if(integer==null){
            throw new InputFormatExeption("Integer should not be null");
        }
        try {
            result = Integer.valueOf(integer);

        } catch (IllegalStateException e) {
            String argument =  "Integer =" +integer;
            throw new InputFormatExeption("Invalid book_id inputted, argument:"+argument);
        }
        return result;
    }


    public static void assureURL(String url) throws InputFormatExeption{
        if(url==null){
            throw new InputFormatExeption("URL is null.");
        }
        if(!url.isEmpty()) {//URL can be left empty
            Matcher m = url_pattern.matcher(url);
            if(!m.find()){
                throw new InputFormatExeption("URL is not formatted correctly.");
            }
        }
    }

    public static void assureBorrowed_bys(String[] numbers) throws InputFormatExeption{
        if(numbers == null){
            throw new InputFormatExeption("Borrowers are null.");
        }
        for(String phone_number: numbers){
            assureBorrowed_by(phone_number);
        }
    }

    public static void assureBorrowed_bys_name(String[] names) throws InputFormatExeption{
        if(names == null){
            throw new InputFormatExeption("Borrowers name are null.");
        }

    }

    public static void assureBorrowed_by(String number) throws InputFormatExeption{
        if(number == null){
            throw new InputFormatExeption("Phone number is null.");
        }
        Matcher m = phonenumber_pattern.matcher(number);
        if (!m.find()) {
            throw new InputFormatExeption("Phone number is not formatted correctly.");
        }

    }


    public static void assureQuantity(int quantity) throws InputFormatExeption{
        if (quantity < 0) {
            throw new InputFormatExeption("Quantity is less than 0.");
        }
    }

    public static void assurePrice(int price) throws InputFormatExeption{
        if (price < 0) {
            throw new InputFormatExeption("Price is less than 0.");
        }
    }

    public static void assureTitle(String title) throws InputFormatExeption{
        if (title == null || title.isEmpty()) {
            throw new InputFormatExeption("Title cannot be empty.");
        }
    }



    public static BookClass assureBookClass_names(BookClass book) throws InputFormatExeption {
        if(book == null){
            throw new InputFormatExeption("BookClass is null");
        }
        try {
            assureBorrowed_bys_name(book.getBorrowedBy());
            assureURL(book.getUrl());
            assureQuantity(book.getQuantity());
            assurePrice(book.getPrice());
            assureTitle(book.getTitle());
        } catch (InputFormatExeption e) {
            throw e;
        }
        return book;

    }

    public static String assureStringNotNull(String str) throws InputFormatExeption {
        if(str==null){
            throw new InputFormatExeption("String is null");
        }
        else return str;
    }

    public static BookClass assureBookClass(BookClass book) throws InputFormatExeption {
        if(book == null){
            throw new InputFormatExeption("BookClass is null");
        }
        try {
            assureBorrowed_bys(book.getBorrowedBy());
            assureURL(book.getUrl());
            assureQuantity(book.getQuantity());
            assurePrice(book.getPrice());
            assureTitle(book.getTitle());
        } catch (InputFormatExeption e) {
            throw e;
        }
        return book;

    }

    public static int assureStatus(int status) throws InputFormatExeption {

        if(status!=0 && status!=1 && status!=2){
            throw new InputFormatExeption("Invalid status input. Status must be set to 0 (borrow), 1 (return) or 2 (lost). status = "+status);
        }
        return status;
    }


    public static PatchBookClass assurePatchBookClass(PatchBookClass book) throws InputFormatExeption {
        try {
            assureStatus(book.getStatus());
            assureBorrowed_by(book.getBorrower());
        } catch (InputFormatExeption e) {
            throw e;
        }
        return book;

    }


}
