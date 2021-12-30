package com.example.usclassifieds;

public class FriendItem {
    private String imgUrl;
    private String username;
    private int userId;

    FriendItem(String imgUrl, String name, int id) {
        this.imgUrl = imgUrl;
        username = name;
        userId = id;
    }

    public String getImg() {
        return imgUrl;
    }

    public String getUsername() {
        return username;
    }

    public int getId() {
        return userId;
    }
}
