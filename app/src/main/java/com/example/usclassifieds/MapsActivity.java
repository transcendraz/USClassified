package com.example.usclassifieds;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.FrameLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleMap.OnInfoWindowClickListener {

    private GoogleMap mMap;
    private RequestQueue queue;
    private ArrayList<Marker> markers;
    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                    Log.d("menuItem", "onNavigationItemSelected: " + menuItem.toString());
                    Intent intent = null;
                    switch (menuItem.getItemId()) {
                        case R.id.buy_item:
                            intent = new Intent(MapsActivity.this, ExploreActivity.class);
                            startActivity(intent);
                            break;
                        case R.id.user_profile_item:
                            intent = new Intent(MapsActivity.this, ViewUserProfileActivity.class);
                            startActivity(intent);
                            break;

                        case R.id.sell_item:
                            intent = new Intent(MapsActivity.this, CreateItemPostActivity.class);
                            startActivity(intent);
                            break;
                    }
                    return false;
                }
            };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        FrameLayout include = findViewById(R.id.explore_include);
        BottomNavigationView navigation = (BottomNavigationView) include;
        navigation.setSelectedItemId(R.id.buy_item);
        navigation.setOnNavigationItemSelectedListener(navListener);

    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {

        Intent intent = getIntent();
        double latitude=-1;
        double longitude=-1;
        mMap = googleMap;
        mMap.setInfoWindowAdapter(new MapWindowAdapter(MapsActivity.this));
        Log.d("number",Integer.valueOf(intent.getExtras().getInt("number")).toString());
        if (intent.getExtras() != null) {
            loadMarkers();
            if (!intent.getExtras().containsKey("allItems")) {
                latitude = Double.parseDouble(intent.getExtras().getString("Lat"+0));
            }
            else {
                latitude=34.0215;
            }
            if (!intent.getExtras().containsKey("allItems")) {
                longitude= Double.parseDouble(intent.getExtras().getString("Long"+0));
            }
            else {
                longitude=-118.288;
            }
            LatLng location = new LatLng(latitude, longitude);
            mMap.moveCamera(CameraUpdateFactory.newLatLng(location));
            mMap.moveCamera(CameraUpdateFactory.zoomTo(15));
            mMap.setOnInfoWindowClickListener(this);
        }
    }

    private void loadMarkers() {
        markers = new ArrayList<>();
        final Intent intent = getIntent();
        if (intent.getExtras() != null) {
            queue = Volley.newRequestQueue(this);
            for (int i = 0; i < intent.getExtras().getInt("number"); ++i) {
                // Make async request to get images using item ids
                // Send volley request using item ID to retrieve data
                String baseUrl = "http://167.172.197.162/api/v1/items/" +
                        Integer.valueOf(intent.getExtras().getString("Id" + i)).toString();
                Log.d("baseUrl", baseUrl);
                final int finalI = i;
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, baseUrl,
                        null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("Response", "Response from a Map Item");
                        try {
                            JSONObject item = response;
                            // get important data
                            String itemId = item.getString("id");
                            String imgUrl = item.getString("image");
                            String description = item.getString("description");
                            String price = String.valueOf(item.getDouble("price"));
                            double latitude = Double.parseDouble(intent.getExtras().getString("Lat" + finalI));
                            double longitude = Double.parseDouble(intent.getExtras().getString("Long" + finalI));
                            // Add a marker in Given Location and move the camera
                            LatLng location = new LatLng(latitude, longitude);
                            Marker marker = mMap.addMarker(new MarkerOptions().position(location).title(intent.getExtras()
                                    .getString("Name" + finalI)));
                            setUsernameSnippet(item.getString("user_id"), marker);
                            HashMap<String, String> infoMap = new HashMap<>();
                            infoMap.put("item_id", itemId);
                            infoMap.put("image_url", imgUrl);
                            infoMap.put("description", description);
                            infoMap.put("price", "$" + price);
                            marker.setTag(infoMap);
                            markers.add(marker);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("error", "Error in Map Item response");
                        error.printStackTrace();
                    }
                });

                // Add the request to the RequestQueue.
                queue.add(jsonObjectRequest);
            }
            queue.start();
        }
    }

    private void setUsernameSnippet(String userId, final Marker marker) {
        if (queue != null) {
            String baseUrl = "http://167.172.197.162/api/v1/users/" + userId;
            Log.d("baseUrl", baseUrl);
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, baseUrl,
                    null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.d("Response", "Response from a Map Item User request");
                    try {
                        JSONObject user = response;
                        marker.setSnippet(user.getString("name"));

                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d("error", "Error in Map Item response");
                    error.printStackTrace();
                }
            });

            // Add the request to the RequestQueue.
            queue.add(jsonObjectRequest);
        }
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        if (marker.getTag() != null) {
            // Redirect to item page
            String itemId = ((HashMap<String, String>)marker.getTag()).get("item_id");
            Intent intent = new Intent(MapsActivity.this, ItemActivity.class);
            intent.putExtra("itemId", Integer.parseInt(itemId));
            startActivity(intent);
        }
    }
}
