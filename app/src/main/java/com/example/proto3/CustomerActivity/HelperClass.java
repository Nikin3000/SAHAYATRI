package com.example.proto3.CustomerActivity;

public class HelperClass {
    String phone, vemail, username, password;
    public String getPhone() {
        return phone;
    }
    public void setName(String phone) {
        this.phone = phone;
    }
    public String getVEmail() {
        return vemail;
    }
    public void setEmail(String vemail) {
        this.vemail = vemail;
    }
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public HelperClass(String phone, String vemail, String username, String password) {
        this.phone = phone;
        this.vemail = vemail;
        this.username = username;
        this.password = password;
    }
    public HelperClass() {
    }
}