package com.example.usclassifieds;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.view.View;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import java.util.List;

public class ViewUserItemsActivity extends AppCompatActivity {

    public RequestQueue queue;
    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private RecyclerViewAdapter adapter;
    private ItemsAdapter recAdapter;
    private List<UserObject> friendlist;
    private List<String> friend_names;
    private ArrayList<FriendItem> fList;
    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                    Log.d("menuItem", "onNavigationItemSelected: " + menuItem.toString());
                    Intent intent = null;
                    switch (menuItem.getItemId()) {
                        case R.id.buy_item:
                            intent = new Intent(ViewUserItemsActivity.this, ExploreActivity.class);
                            startActivity(intent);
                            break;
                        case R.id.user_profile_item:
                            intent = new Intent(ViewUserItemsActivity.this, ViewUserProfileActivity.class);
                            startActivity(intent);
                            return true;
                        case R.id.sell_item:
                            intent = new Intent(ViewUserItemsActivity.this, CreateItemPostActivity.class);
                            startActivity(intent);
                            break;
                    }
                    return false;
                }
            };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("userProfile", "User Items code is being run!");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_items);
        recyclerView = findViewById(R.id.recyclerView2);
        recyclerView.setHasFixedSize(true);
//        friend_names = new ArrayList<String>();
        fList = new ArrayList<>();

        layoutManager = new LinearLayoutManager(this);
//        recAdapter = new FriendsAdapter(fList);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(recAdapter);


        // Sending a request with RequestQueue

        SharedPreferences sp = getApplicationContext().getSharedPreferences("Login", MODE_PRIVATE);
        int CurruserId=sp.getInt("userId",-1);
        // Send volley request using item ID to retrieve data
        queue = Volley.newRequestQueue(this);
        String baseUrl = "http://167.172.197.162/api/v1/users/"+new Integer(CurruserId).toString();
        Log.d("baseUrl",baseUrl);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, baseUrl,
                null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("Response", "Response!!!");
                try {
                    JSONObject item = response;
                    JSONArray itemsId = item.getJSONArray("items");
                    for (int i = 0; i < itemsId.length(); ++i) {
                        // send a request to get the name of each friend
                        boolean lastEl = (i == itemsId.length() - 1);
                        requestItemNameJsonObject(itemsId.getInt(i), lastEl);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // TODO: Handle error
                Log.d("error", "Error in response");
                error.printStackTrace();
            }
        });

        // Add the request to the RequestQueue.
        queue.add(jsonObjectRequest);
        queue.start();


//
//        Toolbar toolbar = findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//        toolbar.setNavigationIcon(R.drawable.ic_icon);

//        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(getApplicationContext(),ViewUserProfileActivity.class));
//            }
//        });

        FrameLayout include = findViewById(R.id.explore_include);
        BottomNavigationView navigation = (BottomNavigationView) include;
        navigation.setSelectedItemId(R.id.user_profile_item);
        navigation.setOnNavigationItemSelectedListener(navListener);

    }

    private void requestItemNameJsonObject(final int id, final boolean last) {
        // Send volley request using item ID to retrieve data
        queue = Volley.newRequestQueue(this);
        String baseUrl = "http://167.172.197.162/api/v1/items/" + id;
        Log.d("baseUrl", baseUrl);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, baseUrl,
                null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("Response", "Response!!!");
                try {
                    JSONObject item = response;
                    Log.d("responses", response.toString());
                    fList.add(new FriendItem(item.getString("image"),
                            item.getString("name"), id));
                    if (recAdapter != null) {
                        recAdapter.notifyDataSetChanged();
                    }
                    if (last) {
                        recAdapter = new ItemsAdapter(fList);
                        recyclerView.setAdapter(recAdapter);
                        recAdapter.setOnItemClickListener(new ItemsAdapter.OnItemClickListener() {
                            @Override
                            public void onItemClick(int position) {
                                // item has been clicked, so go to user profile of that item
                                Intent intent = new Intent(ViewUserItemsActivity.this, ItemActivity.class);
                                intent.putExtra("itemId", fList.get(position).getId());
                                startActivity(intent);
                            }
                        });
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // TODO: Handle error
                Log.d("error", "Error in response");
                error.printStackTrace();
            }
        });

        // Add the request to the RequestQueue.
        queue.add(jsonObjectRequest);
        queue.start();
//        if (currentUser)return currentUser;
    }
}
