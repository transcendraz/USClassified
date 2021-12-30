package com.example.usclassifieds;
import android.view.View;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

public class RecyclerViewHolders extends RecyclerView.ViewHolder implements View.OnClickListener{
    public TextView friendId;
    public TextView friendName;
    public TextView songAuthor;
    public RecyclerViewHolders(View itemView) {
        super(itemView);
        itemView.setOnClickListener(this);
        friendId = (TextView)itemView.findViewById(R.id.friend_name);
        friendName = (TextView)itemView.findViewById(R.id.friend_name);
//        songAuthor = (TextView)itemView.findViewById(R.id.song_author);
    }
    @Override
    public void onClick(View view) {
    }
}