package com.example.petcare.presenter;

import androidx.appcompat.widget.PopupMenu;

import com.example.petcare.model.Comment;

import java.util.List;

public interface IPost {
    void liked();
    void unLiked();
    void toast(String s);
    void adapterCmt(List<Comment> comments);
    void sendCmt();
    void luotLike(String i);
    void luotCmt(String i);
    void deleteTus();
}
