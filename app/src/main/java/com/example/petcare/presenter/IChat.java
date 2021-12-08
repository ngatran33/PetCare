package com.example.petcare.presenter;

import com.example.petcare.model.User;

import java.util.List;

public interface IChat {
    void setAdapterChat(List<User> userList);
    void setAdapterReadUser(List<User> userList);
}
