package com.victor.postlyweb.modelo;

import java.util.ArrayList;
import java.util.List;

public class ChatThread {

    private String id;
    private List<String> participants;
    private String lastMessage;
    private String lastSenderId;
    private Long lastTimestamp;
    private Long createdAt;
    private Long updatedAt;

    public ChatThread() {
        this.id = "";
        this.participants = new ArrayList<>();
        this.lastMessage = "";
        this.lastSenderId = "";
        this.lastTimestamp = 0L;
        this.createdAt = 0L;
        this.updatedAt = 0L;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<String> getParticipants() {
        return participants;
    }

    public void setParticipants(List<String> participants) {
        this.participants = participants;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public String getLastSenderId() {
        return lastSenderId;
    }

    public void setLastSenderId(String lastSenderId) {
        this.lastSenderId = lastSenderId;
    }

    public Long getLastTimestamp() {
        return lastTimestamp;
    }

    public void setLastTimestamp(Long lastTimestamp) {
        this.lastTimestamp = lastTimestamp;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Long createdAt) {
        this.createdAt = createdAt;
    }

    public Long getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Long updatedAt) {
        this.updatedAt = updatedAt;
    }
}
