package com.example.petcare.model;

public class ThuY {
    String id;
    String anh;
    String diaChi;
    String fanpage;
    String mota;
    String name;
    String sdt;
    String thoigian;
    String website;

    public ThuY() {
    }

    public ThuY(String anh, String diaChi, String fanpage, String mota, String name, String sdt, String thoigian, String website,String id) {
        this.anh = anh;
        this.diaChi = diaChi;
        this.fanpage = fanpage;
        this.mota = mota;
        this.name = name;
        this.sdt = sdt;
        this.thoigian = thoigian;
        this.website = website;
        this.id = id;
    }

    public String getAnh() {
        return anh;
    }

    public void setAnh(String anh) {
        this.anh = anh;
    }

    public String getDiaChi() {
        return diaChi;
    }

    public void setDiaChi(String diaChi) {
        this.diaChi = diaChi;
    }

    public String getFanpage() {
        return fanpage;
    }

    public void setFanpage(String fanpage) {
        this.fanpage = fanpage;
    }

    public String getMota() {
        return mota;
    }

    public void setMota(String mota) {
        this.mota = mota;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSdt() {
        return sdt;
    }

    public void setSdt(String sdt) {
        this.sdt = sdt;
    }

    public String getThoigian() {
        return thoigian;
    }

    public void setThoigian(String thoigian) {
        this.thoigian = thoigian;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
