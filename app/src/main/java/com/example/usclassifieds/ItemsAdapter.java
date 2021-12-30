package com.example.usclassifieds;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ItemsAdapter extends RecyclerView.Adapter<ItemsAdapter.FriendsViewHolder> {

    private ArrayList<FriendItem> list;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener aListener) {
        listener = aListener;
    }

    public ItemsAdapter(ArrayList<FriendItem> friendList) {
        list = friendList;
    }

    public static class FriendsViewHolder extends RecyclerView.ViewHolder {

        public ImageView mImageView;
        public TextView mTextView;

        public FriendsViewHolder(@NonNull View itemView, final OnItemClickListener aListener) {
            super(itemView);
            mImageView = itemView.findViewById(R.id.item_card_img);
            mTextView = itemView.findViewById(R.id.item_card_text);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (aListener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            aListener.onItemClick(position);
                        }
                    }
                }
            });

        }
    }

    @NonNull
    @Override
    public FriendsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_card, parent, false);
        FriendsViewHolder holder = new FriendsViewHolder(v, listener);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull FriendsViewHolder holder, int position) {
        FriendItem currItem = list.get(position);
//        holder.mImageView.setImageResource(currItem.getImg());
        holder.mTextView.setText(currItem.getUsername());
        Picasso.get().load(currItem.getImg()).into(holder.mImageView);

    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
