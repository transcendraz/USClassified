package com.example.usclassifieds;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Comparator;

public class GridItem implements Comparator<GridItem>, Comparable<GridItem> {
    static String sortBy;
    static String filterBy;
    private String title;
    private int itemId;
    private String itemUrl;
    private int userId;
    double itemPrice;
    double lat;
    double lon;
    double dist;
    String description;
    public ArrayList<String> tags;

    public GridItem(int item, String name, String img, int id, double price, JSONArray tags,
                    double lat, double lon, double usrLat, double usrLon, String descrip)
    {
        this.itemId = item;
        this.title = name;
        this.itemUrl = img;
        this.userId = id;
        this.itemPrice = price;
        sortBy = "";
        filterBy = "";
        this.tags = setTags(tags);
        this.lat = lat;
        this.lon = lon;
        this.dist = distance(lat, lon, usrLat, usrLon);
        if (descrip.length() > 25) {
            this.description = descrip.substring(0, 25) + "...";
        }
        else {
            this.description = descrip;
        }
    }
    public String getTitle()
    {
        return title;
    }
    public String getImage()
    {
        return itemUrl;
    }
    public int getItemId() { return itemId; }

    private ArrayList<String> setTags(JSONArray tags) {
        ArrayList<String> returned = new ArrayList<>();
        for(int i = 0; i < tags.length(); ++i) {
            try {
                returned.add(tags.getString(i));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return returned;
    }
    public int getUserId() { return userId; }
    public double getPrice() { return itemPrice; }
    public String getDescription() { return description; }

    @Override
    public int compare(GridItem o1, GridItem o2) {
//        switch (sortBy) {
//            case "Price":
//                return Double.compare(o2.itemPrice, o1.itemPrice);
//            case "User":
//                return (o2.userId - o1.userId);
//            case "":
//                return (o2.itemId - o1.itemId);
//        }
        // default behavior
        return (o2.itemId - o1.itemId);
    }

    @Override
    public int compareTo(GridItem o) {
        switch (sortBy) {
            case "Item Name":
                return (this.title.compareToIgnoreCase(o.title));
            case "Price":
                return Double.compare(this.itemPrice, o.itemPrice);
            case "Proximity":
                return (Double.compare(this.dist, o.dist));
            case "User":
                return (this.userId - o.userId);
            case "Sort By...(Reset)":
                return (this.itemId - o.itemId);
        }
        // default behavior
        return (this.itemId - o.itemId);
    }

    // Calculate Distance
    private double distance(double lat1, double lon1, double lat2, double lon2) {
        if ((lat1 == lat2) && (lon1 == lon2)) {
            return 0;
        }
        else {
            double theta = lon1 - lon2;
            double dist = Math.sin(Math.toRadians(lat1))
                    * Math.sin(Math.toRadians(lat2)) + Math.cos(Math.toRadians(lat1))
                    * Math.cos(Math.toRadians(lat2)) * Math.cos(Math.toRadians(theta));
            dist = Math.acos(dist);
            dist = Math.toDegrees(dist);
            dist = dist * 60 * 1.1515;
            return (dist);
        }
    }
}
