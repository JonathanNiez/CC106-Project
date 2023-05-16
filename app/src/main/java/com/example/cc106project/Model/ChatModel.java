package com.example.cc106project.Model;

public class ChatModel {

    private String sender;
    private String receiver;
    private String message;
    private boolean isSeen;
    private String chatProductName, chatProductImage;
    private double chatProductPrice;

    public String getChatProductName() {
        return chatProductName;
    }

    public void setChatProductName(String chatProductName) {
        this.chatProductName = chatProductName;
    }

    public String getChatProductImage() {
        return chatProductImage;
    }

    public void setChatProductImage(String chatProductImage) {
        this.chatProductImage = chatProductImage;
    }

    public double getChatProductPrice() {
        return chatProductPrice;
    }

    public void setChatProductPrice(double chatProductPrice) {
        this.chatProductPrice = chatProductPrice;
    }

    public boolean isSeen() {
        return isSeen;
    }

    public void setSeen(boolean seen) {
        isSeen = seen;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ChatModel() {
    }

    public ChatModel(String sender, String receiver, String message, boolean isSeen
    ,String chatProductName, String chatProductImage, double chatProductPrice) {
        this.sender = sender;
        this.receiver = receiver;
        this.message = message;
        this.isSeen = isSeen;
        this.chatProductName = chatProductName;
        this.chatProductImage = chatProductImage;
        this.chatProductPrice = chatProductPrice;
    }
}
