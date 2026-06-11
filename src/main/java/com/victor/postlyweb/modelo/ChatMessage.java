package com.victor.postlyweb.modelo;

import com.google.cloud.firestore.annotation.Exclude;

public class ChatMessage {

    private String id;
    private String chatId;
    private String senderId;
    private String text;
    private Long timestamp;
    private String type;
    private String mediaBase64;
    private String mediaMimeType;

    public ChatMessage() {
        this.id = "";
        this.chatId = "";
        this.senderId = "";
        this.text = "";
        this.timestamp = 0L;
        this.type = "text";
        this.mediaBase64 = "";
        this.mediaMimeType = "";
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMediaBase64() {
        return mediaBase64;
    }

    public void setMediaBase64(String mediaBase64) {
        this.mediaBase64 = mediaBase64;
    }

    public String getMediaMimeType() {
        return mediaMimeType;
    }

    public void setMediaMimeType(String mediaMimeType) {
        this.mediaMimeType = mediaMimeType;
    }

    @Exclude
    public boolean isImagem() {
        return "image".equals(type) && mediaBase64 != null && !mediaBase64.isBlank();
    }

    @Exclude
    public boolean isAudio() {
        return "audio".equals(type) && mediaBase64 != null && !mediaBase64.isBlank();
    }

    @Exclude
    public String getMediaDataUri() {
        if (mediaBase64 == null || mediaBase64.isBlank()) {
            return "";
        }
        String mime = mediaMimeType == null || mediaMimeType.isBlank()
                ? ("audio".equals(type) ? "audio/aac" : "image/jpeg")
                : mediaMimeType;
        return "data:" + mime + ";base64," + mediaBase64;
    }
}
