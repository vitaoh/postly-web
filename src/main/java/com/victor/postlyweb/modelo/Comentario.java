package com.victor.postlyweb.modelo;

public class Comentario {

    private String id;
    private String postId;
    private String userId;
    private String text;
    private Long timestamp;

    public Comentario() {
        this.id = "";
        this.postId = "";
        this.userId = "";
        this.text = "";
        this.timestamp = 0L;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }
}
