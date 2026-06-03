package com.victor.postlyweb.modelo;

public class Usuario {

    private String uid;
    private String name;
    private String username;
    private String email;
    private String photo;

    public Usuario() {
        this.uid = "";
        this.name = "";
        this.username = "";
        this.email = "";
    }

    public Usuario(String uid, String name, String username, String email, String photo) {
        this.uid = uid;
        this.name = name;
        this.username = username;
        this.email = email;
        this.photo = photo;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

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

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }
}
