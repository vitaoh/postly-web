package com.victor.postlyweb.modelo;

public class ChatMessage {

    private String id;
    private String chatId;
    private String senderId;
    private String text;
    private Long timestamp;

    public ChatMessage() {
        this.id = "";
        this.chatId = "";
        this.senderId = "";
        this.text = "";
        this.timestamp = 0L;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
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
