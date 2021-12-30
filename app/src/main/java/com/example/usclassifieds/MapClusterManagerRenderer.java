package com.example.usclassifieds;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.google.maps.android.ui.IconGenerator;

public class MapClusterManagerRenderer extends DefaultClusterRenderer<MapClusterItem> {
    private final IconGenerator iconGenerator;
    private ImageView imgView;
    private final int markerWidth;
    private final int markerHeight;

    public MapClusterManagerRenderer(Context context, GoogleMap map, ClusterManager<MapClusterItem> clusterManager) {
        super(context, map, clusterManager);
//        this.iconGenerator = iconGenerator;
//        this.imgView = imgView;
//        this.markerWidth = markerWidth;
//        this.markerHeight = markerHeight;

        iconGenerator = new IconGenerator(context.getApplicationContext());
        imgView = new ImageView(context.getApplicationContext());
        markerWidth = (int) context.getResources().getDimension(R.dimen.fab_margin);//TODO: Change this
        markerHeight = (int) context.getResources().getDimension(R.dimen.fab_margin);//TODO: Change this
        imgView.setLayoutParams(new ViewGroup.LayoutParams(markerWidth, markerHeight));
        int padding = (int) context.getResources().getDimension(R.dimen.fab_margin);//TODO: Change this
        imgView.setPadding(padding, padding, padding, padding);
        iconGenerator.setContentView(imgView);
    }

    @Override
    protected void onBeforeClusterItemRendered(MapClusterItem item, MarkerOptions markerOptions) {
        imgView.setImageResource(item.getImage());
        Bitmap icon = iconGenerator.makeIcon();
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon)).title(item.getTitle());
    }

    @Override
    protected boolean shouldRenderAsCluster(Cluster<MapClusterItem> cluster) {
        // No clustering!
        return false;
    }
}
