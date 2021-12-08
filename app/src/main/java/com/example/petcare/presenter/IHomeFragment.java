package com.example.petcare.presenter;

import com.example.petcare.adapter.AdapterTus;
import com.example.petcare.model.Status;

import java.util.List;

public interface IHomeFragment {
    void openGallery(int code);
    void requestPermissionsTus(String[] permission, int code);
    void toast(String s);
    void dismissDialog();
    void setAdapterTus(List<Status> statusList);
}
