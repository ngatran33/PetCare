package com.example.petcare.presenter;

import android.content.Context;

import com.example.petcare.model.Status;

import java.util.List;

public interface IProfile {
    void onMessage(String s);
    void openGallery(int code);
    void requestPermissionsAnh(String[] s, int code);
    void setAdapterPrf(List<Status> statusList);
    void toast(String s);
}
