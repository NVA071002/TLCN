package com.example.webstore.entities;

public class FormData {

    private String email;
    private String password;

    public FormData(String email, String password) {
        this.email = email;
        this.password = password;
    }
    public FormData() {

    }
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
