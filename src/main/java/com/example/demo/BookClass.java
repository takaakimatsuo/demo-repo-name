package com.example.demo;

public class BookClass {
    private String title;
    private int price;
    private String url;
    private int quantity;
    private int borrowed;

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

    public String getURL() {
        return url;
    }
    public void setURL(String url) {
        this.url = url;
    }

    public int getQuantity() {
        return quantity;
    }
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
