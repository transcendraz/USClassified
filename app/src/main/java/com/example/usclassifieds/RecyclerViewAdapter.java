package com.example.usclassifieds;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewHolders>{
    private List<FriendObject> itemList;
    private Context context;
    public RecyclerViewAdapter(Context context, List<FriendObject> itemList) {
        this.itemList = itemList;
        this.context = context;
    }
    @Override
    public RecyclerViewHolders onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.friend_list_item, null);
        RecyclerViewHolders rcv = new RecyclerViewHolders(layoutView);
        return rcv;
    }
    @Override
    public void onBindViewHolder(RecyclerViewHolders holder, int position) {
        holder.friendName.setText("Friend ID: " + itemList.get(position).getFriendName());
//        holder.friendName.setText("Song Year: " + itemList.get(position).getSongYear());
//        holder.songAuthor.setText("Song Author: " + itemList.get(position).getSongAuthor());
    }
    @Override
    public int getItemCount() {
        return this.itemList.size();
    }


}
