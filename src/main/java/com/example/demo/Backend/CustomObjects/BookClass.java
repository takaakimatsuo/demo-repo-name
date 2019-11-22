package com.example.demo.Backend.CustomObjects;

public class BookClass {
    private int id;
    private String title = "";
    private int price = 0;
    private String url = "";
    private int quantity = 1;
    private String[] borrowed_by = {};

    public BookClass(){

    }

    public BookClass(String title, int price, String url, int quantity, String[] borrowedBy){
        setTitle(title);
        setPrice(price);
        setUrl(url);
        setQuantity(quantity);
        setBorrowedBy(borrowedBy);
    }

    public BookClass(String title, int price, String url, int quantity){
        setTitle(title);
        setPrice(price);
        setUrl(url);
        setQuantity(quantity);
    }

    public void setId(int id){
        this.id = id;
    }
    public int getId(){
        return id;
    }

    public void setBorrowedBy(String[] msns){
        this.borrowed_by = msns;
    }
    public String[] getBorrowedBy(){
        return this.borrowed_by;
    }

    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    public int getPrice() {
        return price;
    }
    public void setPrice(int price) {
        this.price = price;
    }

    public String getUrl() {
        return url;
    }
    public void setUrl(String url) {
        this.url = url;
    }

    public int getQuantity() {
        return quantity;
    }
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
