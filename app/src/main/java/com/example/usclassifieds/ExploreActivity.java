package com.example.usclassifieds;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class ExploreActivity extends AppCompatActivity {

    GridView grid;
    private RequestQueue queue;
    // holds original list
    ArrayList<GridItem> itemList;
    // actual displayed list
    ArrayList<GridItem> filteredList;
    LocationManager lm;
    double currLat;
    double currLong;

    GridAdapter gridAdapter;

    private ArrayList<String> strSplitter(String str) {
        String[] split = str.split(",");
        ArrayList<String> returned = new ArrayList<>();
        for (String word : split) {
            returned.add(word.trim());
        }
        return returned;
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                    Log.d("menuItem", "onNavigationItemSelected: " + menuItem.toString());
                    Intent intent;
                    switch (menuItem.getItemId()) {
                        case R.id.buy_item:
                            intent = new Intent(ExploreActivity.this, ExploreActivity.class);
                            startActivity(intent);
                            break;
                        case R.id.user_profile_item:
                            intent = new Intent(ExploreActivity.this, ViewUserProfileActivity.class);
                            startActivity(intent);
                            break;
                        case R.id.sell_item:
                            intent = new Intent(ExploreActivity.this, CreateItemPostActivity.class);
                            startActivity(intent);
                            break;
                    }
                    return false;
                }
            };

    public void toCreatePost(View view) {
        Intent intent = new Intent(this, CreateItemPostActivity.class);
        startActivity(intent);
    }

    public void setSort(View view) {
        Spinner sorter = findViewById(R.id.spinner_sort);
        GridItem.sortBy = sorter.getSelectedItem().toString();
        Collections.sort(filteredList);
        grid.invalidateViews();
    }


    public void setFilter(View view) {
        Spinner filter = findViewById(R.id.spinner_filter);
        EditText filter_value = findViewById(R.id.filter_val);

        GridItem.filterBy = filter.getSelectedItem().toString();
        SharedPreferences preferences;
        switch (GridItem.filterBy) {
            case "Filter By...(Reset)":
                filteredList.clear();
                for (int i = 0; i < itemList.size(); ++i) {
                    filteredList.add(itemList.get(i));
                    Log.d("lists", "item is " + itemList.get(i).getTitle());
                }
                for (int i = 0; i < filteredList.size(); ++i) {
                    Log.d("filteredlists", "filtered item is " + filteredList.get(i).getTitle());
                }
                grid.invalidateViews();
                break;
            case "Price (Upper)":
                filteredList.clear();
                double upper = Double.parseDouble(filter_value.getText().toString());
                for (GridItem item : itemList) {
                    if (item.itemPrice <= upper) {
                        filteredList.add(item);
                    }
                }
                grid.invalidateViews();
                break;
            case "Proximity (Miles)":
                filteredList.clear();
                double upperDist = Double.parseDouble(filter_value.getText().toString());
                for (GridItem item : itemList) {
                    if (item.dist <= upperDist) {
                        filteredList.add(item);
                    }
                }
                grid.invalidateViews();
                break;
            case "Friends Only":
                preferences = getSharedPreferences("Login", MODE_PRIVATE);
                if (preferences.contains("userId")) {
                    filteredList.clear();
                    RequestQueue queueFriends = Volley.newRequestQueue(this);
                    String baseUrl = "http://167.172.197.162/api/v1/users/" + preferences.getInt("userId", -1);
                    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, baseUrl,
                            null, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.d("friendsSucc", "Friends worked!");
                            ArrayList<Integer> friends = new ArrayList<>();
                            try {
                                JSONArray jsonArr = response.getJSONArray("friends");
                                if (jsonArr.length() == 0) {
                                    // save some work
                                    return;
                                }
                                for (int i = 0; i < jsonArr.length(); ++i) {
                                    friends.add(jsonArr.getInt(i));
                                }
                                HashMap<Integer, Boolean> addedTracker = new HashMap<>();
                                for (int i = 0; i < itemList.size(); ++i) {
                                    addedTracker.put(i, false);
                                }
                                for (int i = 0; i < itemList.size(); ++i) {
                                    if (addedTracker.get(i) == false
                                            && friends.contains(itemList.get(i).getUserId())) {
                                        filteredList.add(itemList.get(i));
                                        addedTracker.put(i, true);
                                    }
                                }
                                grid.invalidateViews();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.d("error", "Error in Explore List friend response");
                            error.printStackTrace();
                        }
                    });
                    // Add the request to the RequestQueue.
                    queueFriends.add(jsonObjectRequest);
                }
                break;
            case "Tags (as CSV)":
                filteredList.clear();
                if (filter_value.getText().toString().isEmpty()) {
                    filteredList = new ArrayList<>(itemList);
                    grid.invalidateViews();
                    break;
                }
                ArrayList<String> valTags = strSplitter(filter_value.getText().toString());
                // to prevent adding duplicates
                HashMap<Integer, Boolean> addedTracker = new HashMap<>();
                for (int i = 0; i < itemList.size(); ++i) {
                    addedTracker.put(i, false);
                }
                for(int i = 0; i < valTags.size(); ++i) {
                    for (int j = 0; j < itemList.size(); ++j) {
                        if ((!addedTracker.get(j))
                        && itemList.get(j).tags.contains(valTags.get(i))) {
                            filteredList.add(itemList.get(j));
                            addedTracker.put(i, true);
                        }
                    }
                }
                grid.invalidateViews();
                break;
            case "Item Name":
                filteredList.clear();
                String searchStr = filter_value.getText().toString().toLowerCase();
                for (int i = 0; i < itemList.size(); ++i) {
                    if (itemList.get(i).getTitle().toLowerCase().contains(searchStr)) {
                        filteredList.add(itemList.get(i));
                    }
                }
                grid.invalidateViews();
                break;
        }
    }

    // Returns true if location provided, false otherwise
    @TargetApi(Build.VERSION_CODES.M)
    private boolean setLocation() {
        lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    Activity#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for Activity#requestPermissions for more details.
            // Here, thisActivity is the current activity
            ActivityCompat.requestPermissions(ExploreActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    1);
            return true;
        }
        Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (location != null) {
            currLong = location.getLongitude();
            currLat = location.getLatitude();
        }

        // Also set asynchronous listener
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 10, locationListener);

        return true;
    }

    private final LocationListener locationListener = new LocationListener() {
        public void onLocationChanged(Location location) {
            currLong = location.getLongitude();
            currLat = location.getLatitude();
            Log.d("lat", Double.toString(currLat));
            Log.d("long", Double.toString(currLong));
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explore);

        // Initialize Location Manager
        setLocation();
        Log.d("lat", Double.toString(currLat));
        Log.d("long", Double.toString(currLong));

        final Spinner spinnerFilter = findViewById(R.id.spinner_filter);
        // Listen for validating buttons
        spinnerFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapterFilter = ArrayAdapter.createFromResource(this,
                R.array.filters_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapterFilter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinnerFilter.setAdapter(adapterFilter);

        // Sort spinner
        Log.d("sorts", "Sorts code running");
        Spinner spinnerSort = findViewById(R.id.spinner_sort);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapterSort = ArrayAdapter.createFromResource(this,
                R.array.sorts_array, android.R.layout.simple_spinner_dropdown_item);
        // Specify the layout to use when the list of choices appears
        adapterSort.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinnerSort.setAdapter(adapterSort);

        FrameLayout include = findViewById(R.id.explore_include);
        BottomNavigationView navigation = (BottomNavigationView) include;
        navigation.setSelectedItemId(R.id.buy_item);
        navigation.setOnNavigationItemSelectedListener(navListener);

        final EditText filter_val = findViewById(R.id.filter_val);
        final Button filter_button = findViewById(R.id.apply_filter);
        // start initially with it false
        filter_button.setEnabled(false);

        // set button based on selected input
        spinnerFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                {
                    String val = filter_val.getText().toString();
                    String fil = spinnerFilter.getSelectedItem().toString();
                    switch(fil) {
                        case "Filter By...(Reset)":
                        case "Friends Only":
                            filter_button.setEnabled(true);
                            break;
                        case "Price (Upper)":
                        case "Proximity (Miles)":
                            try {
                                if (TextUtils.isEmpty(val)) {
                                    filter_button.setEnabled(false);
                                }
                                else {
                                    double temp = Double.parseDouble(val);
                                    filter_button.setEnabled(true);
                                }
                            } catch (NumberFormatException e) {
                                filter_button.setEnabled(false);
                            } finally {
                                break;
                            }
                        default:
                            if (TextUtils.isEmpty(val)) {
                                filter_button.setEnabled(false);
                            }
                            else {
                                filter_button.setEnabled(true);
                            }
                            break;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //Redirect to Map
        Button ViewMap = findViewById(R.id.ViewMap);
        ViewMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String baseUrl = "http://167.172.197.162/api/v1/items";
                    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, baseUrl,
                            null, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.d("Response", "MAPRESPONSE!!!");
                            try {
                                Intent intent = new Intent(ExploreActivity.this, MapsActivity.class);
                                JSONArray jsonArr = response.getJSONArray("items");
//                                SharedPreferences preferences = getSharedPreferences("Login", MODE_PRIVATE);
                                intent.putExtra("number",jsonArr.length());
                                Log.d("number", new Integer(jsonArr.length()).toString());
                                for (int i = 0; i < jsonArr.length(); ++i) {
                                    JSONObject item = jsonArr.getJSONObject(i);
                                    Log.d("responsesLat", item.getString("latitude"));
                                    Log.d("responsesLong", item.getString("longitude"));

                                    intent.putExtra("allItems", true);
                                    intent.putExtra("Id" + i, item.getString("id"));
                                    intent.putExtra("Name"+i,item.getString("name"));
                                    intent.putExtra("Lat"+i,item.getString("latitude"));
                                    intent.putExtra("Long"+i,item.getString("longitude"));

                                }
                                startActivity(intent);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.d("error", "Error in Initial Explore response");
                            error.printStackTrace();
                        }
                    });

// Add the request to the RequestQueue.
                    queue.add(jsonObjectRequest);




                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        // Enable buttons at correct times;
        filter_val.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                Log.d("valueField", "Value is empty: " + filter_val.getText().toString().isEmpty());
                String val = filter_val.getText().toString();
                String fil = spinnerFilter.getSelectedItem().toString();
                switch(fil) {
                    case "Filter By...(Reset)":
                    case "Friends Only":
                        filter_button.setEnabled(true);
                        break;
                    case "Price (Upper)":
                    case "Proximity (Miles)":
                        try {
                            if (TextUtils.isEmpty(val)) {
                                filter_button.setEnabled(false);
                            }
                            else {
                                double temp = Double.parseDouble(val);
                                filter_button.setEnabled(true);
                            }
                        } catch (NumberFormatException e) {
                            filter_button.setEnabled(false);
                        } finally {
                            break;
                        }
                    default:
                        if (TextUtils.isEmpty(val)) {
                            filter_button.setEnabled(false);
                        }
                        else {
                            filter_button.setEnabled(true);
                        }
                        break;
                }
            }
        });

        // Sending a request with RequestQueue
        itemList = new ArrayList<>();
        filteredList = new ArrayList<>();
        queue = Volley.newRequestQueue(this);
        String baseUrl = "http://167.172.197.162/api/v1/items";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, baseUrl,
                null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("Response", "Response!!!");
                try {
                    grid = findViewById(R.id.explore_grid);
                    JSONArray jsonArr = response.getJSONArray("items");
                    SharedPreferences preferences = getSharedPreferences("Login", MODE_PRIVATE);
                    Float userLat = preferences.getFloat("latitude", (float) 0.0);
                    Float userLon = preferences.getFloat("longitude", (float) 0.0);

                    for (int i = 0; i < jsonArr.length(); ++i) {
                        JSONObject item = jsonArr.getJSONObject(i);
                        Log.d("responses", item.getString("description"));

                        itemList.add(new GridItem(item.getInt("id"), item.getString("name"),
                                item.getString("image"), item.getInt("user_id"), item.getDouble("price"),
                                item.getJSONArray("tags"), item.getDouble("latitude"),
                                item.getDouble("longitude"), userLat, userLon, item.getString("description")));
                        filteredList.add(new GridItem(item.getInt("id"), item.getString("name"),
                                item.getString("image"), item.getInt("user_id"), item.getDouble("price"),
                                item.getJSONArray("tags"), item.getDouble("latitude"),
                                item.getDouble("longitude"), userLat, userLon, item.getString("description")));
                    }
                    grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            Log.d("itemId", view.findViewById(R.id.grid_item_title).toString());
                            // Start detail page upon item click
                            // Send data to ItemActivity
                            int selectedId = filteredList.get(position).getItemId();
                            Intent intent = new Intent(ExploreActivity.this, ItemActivity.class);
                            intent.putExtra("itemId", selectedId);
                            startActivity(intent);
                        }
                    });
                    gridAdapter = new GridAdapter(ExploreActivity.this, R.layout.grid_view_items, filteredList);
                    grid.setAdapter(gridAdapter);
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("error", "Error in Initial Explore response");
                error.printStackTrace();
            }
        });

// Add the request to the RequestQueue.
        queue.add(jsonObjectRequest);
    }

}
