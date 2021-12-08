package com.example.petcare.model;

public class User {
    String username;
    String id;
    String avt;
    String bgr;
    String status;
    String search;

    public User(String username, String id, String avt, String bgr, String status, String search) {
        this.username = username;
        this.id = id;
        this.avt = avt;
        this.bgr = bgr;
        this.status = status;
        this.search = search;
    }



    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }

    public User() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAvt() {
        return avt;
    }

    public void setAvt(String avt) {
        this.avt = avt;
    }

    public String getBgr() {
        return bgr;
    }

    public void setBgr(String bgr) {
        this.bgr = bgr;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
