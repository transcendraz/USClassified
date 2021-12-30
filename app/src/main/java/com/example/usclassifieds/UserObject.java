package com.example.usclassifieds;
import com.google.gson.annotations.SerializedName;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class UserObject {
    @SerializedName("buy_count")
    private String buyCount;
    @SerializedName("email")
    private String emailString;
    @SerializedName("friend_list")
    private ArrayList<String> friendList;
    @SerializedName("user_id")
    private String userId;
    @SerializedName("item_list")
    private ArrayList<String> itemList;
    @SerializedName("user_name")
    private String userName;
    @SerializedName("phone_number")
    private String phoneNumber;
    @SerializedName("rating")
    private Integer rating;

    public UserObject(String email, String userName, String userId)
    {
        this.userId = userId;
        this.userName = userName;
        this.emailString = email;
    }

    public String getUserName()
    {
        return userName;
    }
    public String getUserId() {
        return userId;
    }
    public String getEmail() {
        return emailString;
    }
    public Integer getRating() {return rating;}
    public void setRating(Integer rating) {this.rating = rating;}

}
