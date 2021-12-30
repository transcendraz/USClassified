package com.example.usclassifieds;
import com.google.gson.annotations.SerializedName;
public class FriendObject {
    @SerializedName("friend_id")
    private String friendId;
    @SerializedName("friend_name")
    private String friendName;

    public FriendObject(String friendId)
    {
        this.friendId = friendId;

    }
    public String getFriendId() {
        return friendId;
    }
    public String getFriendName() {return friendName;}
    public void setFriendName(String friendName){this.friendName = friendName;}

}