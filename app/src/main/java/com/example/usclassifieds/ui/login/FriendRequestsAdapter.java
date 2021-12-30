package com.example.usclassifieds.ui.login;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.usclassifieds.FriendItem;
import com.example.usclassifieds.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class FriendRequestsAdapter extends RecyclerView.Adapter<FriendRequestsAdapter.FriendsViewHolder> {

    private ArrayList<FriendItem> list;
    private OnItemClickListener listener;


    public interface OnItemClickListener {
        void onItemClick(int position);
        void onDeleteClick(int position);
        void onAcceptClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener aListener) {
        listener = aListener;
    }

    public FriendRequestsAdapter(ArrayList<FriendItem> friendList) {
        list = friendList;
    }

    public static class FriendsViewHolder extends RecyclerView.ViewHolder {

        public ImageView mImageView;
        public TextView mTextView;
        public TextView mIdTextView;
        public ImageView mDeleteImg;
        public ImageView mAcceptImg;
        public FriendsViewHolder(@NonNull View itemView, final OnItemClickListener aListener) {
            super(itemView);
            mImageView = itemView.findViewById(R.id.friend_card_img);
            mTextView = itemView.findViewById(R.id.friend_card_text);
            mIdTextView = itemView.findViewById(R.id.friend_id);
            mAcceptImg= itemView.findViewById(R.id.friend_request_accept);
            mDeleteImg = itemView.findViewById(R.id.friend_request_delete);


            mAcceptImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (aListener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            aListener.onAcceptClick(position);
                        }
                    }
                }
            });

            mDeleteImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (aListener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            aListener.onDeleteClick(position);
                        }
                    }
                }
            });

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
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.friend_request_card, parent, false);
        FriendsViewHolder holder = new FriendsViewHolder(v, listener);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull FriendsViewHolder holder, int position) {
        FriendItem currItem = list.get(position);
        holder.mTextView.setText(currItem.getUsername());
        if(currItem.getImg()!="null") {
            Picasso.get().load(currItem.getImg()).into(holder.mImageView);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
