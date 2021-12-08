package com.example.petcare.model;

import android.provider.ContactsContract;

import java.util.Date;

public class Status implements Comparable<Status>{
    String id;
    String img;
    String loai;
    String mota;
    String userid;
    String date;

    public Status(String id, String img, String loai, String mota, String userid, String date) {
        this.id = id;
        this.img = img;
        this.loai = loai;
        this.mota = mota;
        this.userid = userid;
        this.date = date;
    }

    public Status() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }


    public String getLoai() {
        return loai;
    }

    public void setLoai(String loai) {
        this.loai = loai;
    }

    public String getMota() {
        return mota;
    }

    public void setMota(String mota) {
        this.mota = mota;
    }

    public String getUserId() {
        return userid;
    }

    public void setUserId(String userId) {
        this.userid = userId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    @Override
    public int compareTo(Status o) {
        int i = o.getDate().compareTo(this.date);
        return i;
    }
}
