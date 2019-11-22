package com.example.demo.Backend.CustomObjects;

public class BookUser {

    private String familyName = "";
    private String firstName = "";
    private String phoneNumber = "";


    public BookUser(){

    }

    public BookUser(String familyName, String firstName, String phoneNumber){
        setPhoneNumber(phoneNumber);
        setFirstName(firstName);
        setFamilyName(familyName);
    }

    public void setFamilyName(String familyName){
        this.familyName = familyName;
    }
    public String getFamilyName(){
        return familyName;
    }

    public void setFirstName(String firstName){
        this.firstName = firstName;
    }
    public String getFirstName(){
        return firstName;
    }

    public void setPhoneNumber(String phoneNumber){
        this.phoneNumber = phoneNumber;
    }
    public String getPhoneNumber(){
        return phoneNumber;
    }

}
