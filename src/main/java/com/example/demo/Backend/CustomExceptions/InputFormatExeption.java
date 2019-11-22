package com.example.demo.Backend.CustomExceptions;

public class InputFormatExeption extends Exception {
    public InputFormatExeption(String errorMessage){
        super(errorMessage);
    }
}
