package com.example.petcare.model;

public class Like {
    String userId;
    String idPost;
    String id;

    public Like(String userId, String idPost,String id) {
        this.userId = userId;
        this.idPost = idPost;
        this.id=id;
    }

    public Like() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getIdPost() {
        return idPost;
    }

    public void setIdPost(String idPost) {
        this.idPost = idPost;
    }
}
