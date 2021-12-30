package com.example.usclassifieds;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class GridAdapter extends ArrayAdapter {

    ArrayList<GridItem> itemList = new ArrayList<GridItem>();

    public GridAdapter(Context context, int textViewResourceId, ArrayList objects) {
        super(context, textViewResourceId, objects);
        itemList = objects;
    }
    @Override
    public int getCount() {
        return super.getCount();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        v = inflater.inflate(R.layout.grid_view_items, null);
        TextView textView = v.findViewById(R.id.grid_item_title);
        TextView descriptionView = v.findViewById(R.id.grid_item_description);
        ImageView imageView = v.findViewById(R.id.grid_item_image);
        textView.setText(itemList.get(position).getTitle());
        descriptionView.setText(itemList.get(position).getDescription());


        if (URLUtil.isValidUrl(itemList.get(position).getImage())) {
            Picasso.get().load(itemList.get(position).getImage()).into(imageView);
        }
        else {
            imageView.setImageResource(R.drawable.ic_icon);
        }
        return v;

    }
}
