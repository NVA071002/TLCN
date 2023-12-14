package com.example.watchesstore.models;

import java.util.List;

public class Invoice {

    private String date;

    private String status;
    private int totalPrice;

    private int Id;
    private List<ItemsCart> listProduct;

    private  String userEmail;

    public List<ItemsCart> getListProduct() {
        return listProduct;
    }
    public Invoice(){}
    public Invoice(String date, String status, int totalPrice, int id, List<ItemsCart> listProduct, String userEmail) {
        this.date = date;
        this.status = status;
        this.totalPrice = totalPrice;
        Id = id;
        this.listProduct = listProduct;
        this.userEmail = userEmail;
    }

    public void setListProduct(List<ItemsCart> listProduct) {
        this.listProduct = listProduct;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public Invoice(String date, String status, int totalPrice, int id) {
        this.date = date;
        this.status = status;
        this.totalPrice = totalPrice;
        Id = id;
    }

    public Invoice(String date, String status, int totalPrice) {
        this.date = date;
        this.status = status;
        this.totalPrice = totalPrice;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }



    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(int totalPrice) {
        this.totalPrice = totalPrice;
    }
}
