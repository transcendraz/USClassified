package com.example.usclassifieds;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class MapWindowAdapter implements GoogleMap.InfoWindowAdapter {

    private final View mView;
    private Context mContext;

    public MapWindowAdapter(Context mContext) {
        this.mContext = mContext;
        this.mView = LayoutInflater.from(mContext).inflate(R.layout.custom_info_window, null);
    }

    private void renderWindowText(Marker marker, View view) {
        String title = marker.getTitle();
        TextView itemTitle = view.findViewById(R.id.info_title);
        itemTitle.setText(title);
        if (marker.getSnippet() != "") {
            String snippet = marker.getSnippet();
            TextView itemSnippet = view.findViewById(R.id.info_snippet);
            itemSnippet.setText(snippet);
        }
        if (marker.getTag() != null) {
            ImageView imageView = view.findViewById(R.id.info_img);
            Picasso.get().load(((HashMap<String, String>)marker.getTag()).get("image_url")).into(imageView);
            TextView description = view.findViewById(R.id.info_description);
            description.setText(((HashMap<String, String>)marker.getTag()).get("description"));
            TextView price = view.findViewById(R.id.info_price);
            price.setText(((HashMap<String, String>)marker.getTag()).get("price"));
        }
    }

    // For whole window
    @Override
    public View getInfoWindow(Marker marker) {
        renderWindowText(marker, mView);
        return mView;
    }

    // Initialize this for contents
    @Override
    public View getInfoContents(Marker marker) {
        renderWindowText(marker, mView);
        return mView;
    }
}
