package com.example.conectamobile;

public class Contact {
    private String contactId;
    private String name;
    private String email;

    // Constructor vacío necesario para Firebase
    public Contact() {}

    // Constructor con parámetros
    public Contact(String contactId, String name, String email) {
        this.contactId = contactId;
        this.name = name;
        this.email = email;
    }

    // Getters
    public String getContactId() {
        return contactId;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    // Setters
    public void setContactId(String contactId) {
        this.contactId = contactId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
