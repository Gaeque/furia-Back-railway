package com.gaeque.furia.DTOs;

public class ChatMessage {
    private String sender;
    private String receiverId;
    private String content;

    public ChatMessage() {
    }

    public ChatMessage(String sender, String receiverId, String content) {
        this.sender = sender;
        this.receiverId = receiverId;
        this.content = content;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
