package com.example.petcare.presenter;

public interface IEditPost {
    void openGallery(int code);

    void requestPermissionsTus(String[] permission, int code);

    void intentPost();

    void toast(String post);
}
