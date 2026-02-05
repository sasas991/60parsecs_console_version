package com.parsecs;

public class userauth {
    private String username;
    private userrole role;

    public userauth(String username, userrole role) {
        this.username = username;
        this.role = role;
    }

    public String getUsername() {
        return username;
    }

    public userrole getRole() {
        return role;
    }
    
    public boolean isAdmin() {
        return role == userrole.ADMIN;
    }
}