package com.example.hospital;

public class User {

    String name, number, password, cpassword;

    public User(String name, String number, String password, String cpassword) {
        this.name = name;
        this.number = number;
        this.password = password;
        this.cpassword = cpassword;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCpassword() {
        return cpassword;
    }

    public void setCpassword(String cpassword) {
        this.cpassword = cpassword;
    }
}
