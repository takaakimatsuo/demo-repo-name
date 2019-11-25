package com.example.demo.Backend.CustomExceptions;

public class DataNotFoundException extends BookException {
    public DataNotFoundException(String errorMessage){
        super(errorMessage);
    }
}
