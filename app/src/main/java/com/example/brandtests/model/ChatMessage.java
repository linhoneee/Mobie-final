package com.example.brandtests.model;

public class ChatMessage {
    private Long id;
    private String text;
    private Long userId;
    private String username;
    private String role;
    private Long roomId;
    private Boolean unRead;
    private String createdAt;
    private String updatedAt;
    private String createdAtAsLocalDateTime;
    private String createdAtAsString;
    // Các trường mới cho URL và loại phương tiện
    private String mediaUrl;    // URL của file lưu trên Cloudinary
    private String mediaType;   // Loại file (ví dụ: "image", "video", "audio")

    // Constructor không tham số
    public ChatMessage() {}

    // Constructor với tham số
    public ChatMessage(String text, Long userId, Long roomId, String username) {
        this.text = text;
        this.userId = userId;
        this.roomId = roomId;
        this.username = username;
    }

    public String getMediaUrl() {
        return mediaUrl;
    }

    public void setMediaUrl(String mediaUrl) {
        this.mediaUrl = mediaUrl;
    }

    public String getMediaType() {
        return mediaType;
    }

    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
    }

    // Getters và Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Long getRoomId() {
        return roomId;
    }

    public void setRoomId(Long roomId) {
        this.roomId = roomId;
    }

    public Boolean getUnRead() {
        return unRead;
    }

    public void setUnRead(Boolean unRead) {
        this.unRead = unRead;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getCreatedAtAsLocalDateTime() {
        return createdAtAsLocalDateTime;
    }

    public void setCreatedAtAsLocalDateTime(String createdAtAsLocalDateTime) {
        this.createdAtAsLocalDateTime = createdAtAsLocalDateTime;
    }

    public String getCreatedAtAsString() {
        return createdAtAsString;
    }

    public void setCreatedAtAsString(String createdAtAsString) {
        this.createdAtAsString = createdAtAsString;
    }
}
