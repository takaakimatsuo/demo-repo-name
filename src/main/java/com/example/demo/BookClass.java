package com.example.demo;


public class BookClass {
    private String title;
    private int price;
    private String url;
    private int quantity;
    private String[] borrowed_by_msn;
    //private String[] borrowed_by_name;
    private ResponseMsg status;

    public void setBorrowed_by_msn(String[] msns){
        this.borrowed_by_msn = msns;
    }

    public String[] getBorrowed_by_msn(){
        return this.borrowed_by_msn;
    }

    public ResponseMsg getResponseStatus(){
        return status;
    }

    public void setResponseStatus(ResponseMsg rs){
        this.status = rs;
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
