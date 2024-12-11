package com.example.conectamobile;

public class User {
    private String userId;
    private String name;
    private String email;

    // Constructor vacío (requerido para Firebase)
    public User() {
    }

    // Constructor con parámetros
    public User(String userId, String name, String email) {
        this.userId = userId;
        this.name = name;
        this.email = email;
    }

    // Getter para userId
    public String getUserId() {
        return userId;
    }

    // Getter para name
    public String getName() {
        return name;
    }

    // Getter para email
    public String getEmail() {
        return email;
    }

    // Setter para userId
    public void setUserId(String userId) {
        this.userId = userId;
    }

    // Setter para name
    public void setName(String name) {
        this.name = name;
    }

    // Setter para email
    public void setEmail(String email) {
        this.email = email;
    }
}
