package com.example.petcare.model;

public class Comment {
    String id;
    String userId;
    String idPost;
    String cmt;

    public Comment(String userId, String idPost, String cmt, String id) {
        this.userId = userId;
        this.idPost = idPost;
        this.cmt = cmt;
        this.id = id;
    }

    public Comment() {
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

    public String getComment() {
        return cmt;
    }

    public void setComment(String cmt) {
        this.cmt = cmt;
    }
}
