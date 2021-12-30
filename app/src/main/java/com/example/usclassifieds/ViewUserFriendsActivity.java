package com.example.usclassifieds;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static android.graphics.drawable.ClipDrawable.HORIZONTAL;


public class ViewUserFriendsActivity extends AppCompatActivity {
    private RequestQueue queue;
    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private FriendsAdapter recAdapter;
    private ArrayList<FriendItem> fList;

    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                    Log.d("menuItem", "onNavigationItemSelected: " + menuItem.toString());
                    Intent intent = null;
                    switch (menuItem.getItemId()) {
                        case R.id.buy_item:
                            intent = new Intent(com.example.usclassifieds.ViewUserFriendsActivity
                                    .this, ExploreActivity.class);
                            startActivity(intent);
                            break;
                        case R.id.user_profile_item:
                            intent = new Intent(com.example.usclassifieds.ViewUserFriendsActivity.this, ViewUserProfileActivity.class);
                            startActivity(intent);
                            return true;
                        case R.id.sell_item:
                            intent = new Intent(com.example.usclassifieds.ViewUserFriendsActivity.this, CreateItemPostActivity.class);
                            startActivity(intent);
                            break;
                    }
                    return false;
                }
            };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("userProfile", "User Wishlist code is being run!");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_friends);
        recyclerView = findViewById(R.id.friends_recycler_view);
        recyclerView.setHasFixedSize(true);
//        friend_names = new ArrayList<String>();
        fList = new ArrayList<>();

        layoutManager = new LinearLayoutManager(this);
//        recAdapter = new FriendsAdapter(fList);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(recAdapter);

        // Populate list with asynchronous request
        requestJsonObject();

        // Sending a request with RequestQueue

        FrameLayout include = findViewById(R.id.explore_include);
        BottomNavigationView navigation = (BottomNavigationView) include;
        navigation.setSelectedItemId(R.id.user_profile_item);
        navigation.setOnNavigationItemSelectedListener(navListener);

    }


    private void requestJsonObject() {
        Intent intent = getIntent();
        int userId;
        if (intent.getExtras() != null) {
            userId = intent.getExtras().getInt("userId");
            Log.d("userId", new Integer(userId).toString());
        }
        else {
            SharedPreferences sp1 = this.getSharedPreferences("Login", MODE_PRIVATE);
            userId = sp1.getInt("userId", 0);
        }

        // Send volley request using item ID to retrieve data
        queue = Volley.newRequestQueue(this);
        String baseUrl = "http://167.172.197.162/api/v1/users/" + Integer.valueOf(userId).toString();
        Log.d("baseUrl", baseUrl);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, baseUrl,
                null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("Response", "Response from Friendslist!!!");
                try {
                    JSONObject item = response;
                    Log.d("responses", item.getString("friends"));
                    JSONArray friendIds = item.getJSONArray("friends");
                    for (int i = 0; i < friendIds.length(); ++i) {
                        // send a request to get the name of each friend
                        boolean lastEl = (i == friendIds.length() - 1);
                        requestFriendNameJsonObject(friendIds.getInt(i), lastEl);
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
                Log.d("error", "Error in FriendsList response");
                error.printStackTrace();
            }
        });

        // Add the request to the RequestQueue.
        queue.add(jsonObjectRequest);
        queue.start();
    }


    private void requestFriendNameJsonObject(final int id, final boolean last) {
        // Send volley request using item ID to retrieve data
        queue = Volley.newRequestQueue(this);
        String baseUrl = "http://167.172.197.162/api/v1/users/" + id;
        Log.d("baseUrl", baseUrl);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, baseUrl,
                null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("Response", "Response!!!");
                try {
                    JSONObject item = response;
                    Log.d("responses", response.toString());
                    fList.add(new FriendItem(item.getString("photo"),
                            item.getString("name"), id));
                    if (recAdapter != null) {
                        recAdapter.notifyDataSetChanged();
                    }
                    if (last) {
                        recAdapter = new FriendsAdapter(fList);
                        recyclerView.setAdapter(recAdapter);
                        recAdapter.setOnItemClickListener(new FriendsAdapter.OnItemClickListener() {
                            @Override
                            public void onItemClick(int position) {
                                // item has been clicked, so go to user profile of that item
                                Intent intent = new Intent(ViewUserFriendsActivity.this, ViewUserProfileActivity.class);
                                intent.putExtra("userId", fList.get(position).getId());
                                startActivity(intent);
                            }

                            @Override
                            public void onDeleteClick(int position) {
                                // delete the friend at this position in the list
                                delete_friend(position);
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
                Log.d("error", "Error in finding friend profile response");
                error.printStackTrace();
            }
        });

        // Add the request to the RequestQueue.
        queue.add(jsonObjectRequest);
        queue.start();
    }

    // Given the position of the frienditem, delete the friendship associated with that element
    private void delete_friend(final int position) {
        Intent intent = getIntent();
        int userId;
        if (intent.getExtras() != null) {
            userId = intent.getExtras().getInt("userId");
            Log.d("userId", new Integer(userId).toString());
        }
        else {
            SharedPreferences sp1 = this.getSharedPreferences("Login", MODE_PRIVATE);
            userId = sp1.getInt("userId", 0);
        }
        // Send volley request using item ID to retrieve data
        queue = Volley.newRequestQueue(this);
        String baseUrl = "http://167.172.197.162/api/v1/users/" + userId + "/friends/" + fList.get(position).getId();
        Log.d("baseUrl", baseUrl);
        final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.DELETE, baseUrl,
                null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("Response", "Friendship being deleted!!!");
                try {
                    JSONObject item = response;
                    fList.remove(position);
                    if (recAdapter != null) {
                        recAdapter.notifyDataSetChanged();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // TODO: Handle error
                Log.d("error", "Error in finding friend profile response");
                error.printStackTrace();
            }
        });

        // Add the request to the RequestQueue.
        queue.add(jsonObjectRequest);
        queue.start();
    }

}