package com.example.cryptov;

import java.util.List;

public class Users {
    String username, email, password;
    List<String> coins;

    public Users(String username, String email, String password, List<String> coins) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.coins = coins;
    }


    public Users(){}



    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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

    public List<String> getCoins() {
        return coins;
    }

    public void setCoins(List<String> coins) {
        this.coins = coins;
    }
}
