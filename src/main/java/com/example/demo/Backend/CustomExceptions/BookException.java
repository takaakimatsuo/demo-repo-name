package com.example.demo.Backend.CustomExceptions;

public class BookException extends Exception {
    public BookException(String errorMessage){
        super(errorMessage);
    }
}
