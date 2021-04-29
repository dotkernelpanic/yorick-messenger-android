package com.kernelpanic.yorickmessenger.database;

public class ChatMessage {

    private String macAddress;
    private int type;
    private String content;
    private String username;
    private Long currentTime;

    // Empty constructor
    public ChatMessage() {
    }

    public ChatMessage(String macAddress, int type, String content, String username, Long currentTime) {
        this.macAddress = macAddress;
        this.type = type;
        this.content = content;
        this.username = username;
        this.currentTime = currentTime;
    }

    public String getMacAddress() {
        return macAddress;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Long getCurrentTime() {
        return currentTime;
    }

    public void setCurrentTime(Long currentTime) {
        this.currentTime = currentTime;
    }
}
