package com.example.usclassifieds;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.usclassifieds.ui.login.FriendRequestsAdapter;
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


public class ViewUserFriendRequestsActivity extends AppCompatActivity {
    private RequestQueue queue;
    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private FriendRequestsAdapter recAdapter;
    private ArrayList<FriendItem> fList;

    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                    Log.d("menuItem", "onNavigationItemSelected: " + menuItem.toString());
                    Intent intent = null;
                    switch (menuItem.getItemId()) {
                        case R.id.buy_item:
                            intent = new Intent(com.example.usclassifieds.ViewUserFriendRequestsActivity
                                    .this, ExploreActivity.class);
                            startActivity(intent);
                            break;
                        case R.id.user_profile_item:
                            intent = new Intent(com.example.usclassifieds.ViewUserFriendRequestsActivity.this, ViewUserProfileActivity.class);
                            startActivity(intent);
                            return true;
                        case R.id.sell_item:
                            intent = new Intent(com.example.usclassifieds.ViewUserFriendRequestsActivity.this, CreateItemPostActivity.class);
                            startActivity(intent);
                            break;
                    }
                    return false;
                }
            };






    private void getPendingFriendRequests()
    {
        String baseUrl = "http://167.172.197.162/api/v1/users/";
        Log.d("PendingFriendReqs", "Sending request...");
        String curr_user_id = new String(String.valueOf(this.getSharedPreferences("Login", MODE_PRIVATE).getInt("userId", 0)));
        String base_url_for_pending_fqs = baseUrl + curr_user_id + "/friend_requests";

        queue = Volley.newRequestQueue(this);
        JSONObject friend_req_status = new JSONObject();

        Log.d("UrlForFriendReqStatus",base_url_for_pending_fqs);
        JsonObjectRequest friendReqStatusRequest = new JsonObjectRequest(Request.Method.GET, base_url_for_pending_fqs, friend_req_status, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("Response", "Response from PendingFriendRequest!!!");
                try {
                    JSONObject item = response;
                    Log.d("FullResponse: ", item.toString());
                    JSONArray friendIds = item.getJSONArray("friend_requests");
                    Log.d("FullResponse222: ", friendIds.toString());
                    for (int i = 0; i < friendIds.length(); ++i) {
                        // send a request to get the name of each friend
                        JSONObject person=friendIds.getJSONObject(i);
                        int Tid=person.getInt("id");
                        boolean lastEl = (i == friendIds.length() - 1);
                        requestFriendNameJsonObject(Tid, lastEl);
                    }
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
        queue.add(friendReqStatusRequest);
        queue.start();
    }


//    private void deleteFriend(int friend_id)
//    {
//        String baseUrl = "http://167.172.197.162/api/v1/users/";
//        Log.d("DeleteFriend", "Sending request...");
//        String curr_user_id = new String(String.valueOf(this.getSharedPreferences("Login", MODE_PRIVATE).getInt("userId", 0)));
//        String base_url_for_friend_delete = baseUrl + curr_user_id + "/friends/" + friend_id;
//
//        queue = Volley.newRequestQueue(this);
//        JSONObject friend_del_status = new JSONObject();
//
//        Log.d("UrlForFriendReqStatus",base_url_for_friend_delete);
//        JsonObjectRequest friendReqStatusRequest = new JsonObjectRequest(Request.Method.DELETE, base_url_for_friend_delete, friend_del_status, new Response.Listener<JSONObject>() {
//            @Override
//            public void onResponse(JSONObject response) {
//                Log.d("Response", "Response from DeleteFriend!!!");
//                try {
//                    JSONObject item = response;
//                    Log.d("FullResponse: ", item.toString());
//
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                // TODO: Handle error
//                Log.d("error", "Error in response");
//                error.printStackTrace();
//            }
//        });
//
//        // Add the request to the RequestQueue.
//        queue.add(friendReqStatusRequest);
//        queue.start();
//    }

    private void changeFriendRequest(int action, int friend_id, final int position)
    {
        String baseUrl = "http://167.172.197.162/api/v1/users/";
        Log.d("Accept/Delete Friend", "Sending request...");
        String curr_user_id = new String(String.valueOf(this.getSharedPreferences("Login", MODE_PRIVATE).getInt("userId", 0)));
        String base_url_for_friend_AD = baseUrl + curr_user_id + "/friends/" + friend_id;

        queue = Volley.newRequestQueue(this);
        JSONObject friend_acc_del_status = new JSONObject();
        try {
            friend_acc_del_status.put("action", action);

        } catch (JSONException jsone)
        {
            jsone.printStackTrace();
        }
        Log.d("UrlForFriendReqUpdate",base_url_for_friend_AD);
        JsonObjectRequest friendReqStatusRequest = new JsonObjectRequest(Request.Method.PUT, base_url_for_friend_AD, friend_acc_del_status, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("Response", "Response from Accept/Reject Friend!!!");
                try {
                    JSONObject item = response;
                    Log.d("FullResponse: ", item.toString());
                    fList.remove(position);
                    if (recAdapter != null) {
                        recAdapter.notifyDataSetChanged();
                    }
                    Toast.makeText(getApplicationContext(),"Success！",Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(),"Error",Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // TODO: Handle error
                Log.d("error", "Error in response");
                Toast.makeText(getApplicationContext(),"Error",Toast.LENGTH_SHORT).show();
                error.printStackTrace();
            }
        });

        // Add the request to the RequestQueue.
        queue.add(friendReqStatusRequest);
        queue.start();
    }














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
        getPendingFriendRequests();

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
                        recAdapter = new FriendRequestsAdapter(fList);
                        recyclerView.setAdapter(recAdapter);
                        recAdapter.setOnItemClickListener(new FriendRequestsAdapter.OnItemClickListener() {
                            @Override
                            public void onItemClick(int position) {
                                // item has been clicked, so go to user profile of that item
                                Intent intent = new Intent(ViewUserFriendRequestsActivity.this, ViewUserProfileActivity.class);
                                intent.putExtra("userId", fList.get(position).getId());
                                startActivity(intent);
                            }
                            @Override
                            public void onAcceptClick(int position) {
                                // delete the friend at this position in the list
                                changeFriendRequest(0,fList.get(position).getId(),position);
                                Toast.makeText(getApplicationContext(),"Friend request accepted",Toast.LENGTH_SHORT).show();


                            }
                            @Override
                            public void onDeleteClick(int position) {
                                // delete the friend at this position in the list
                                changeFriendRequest(1,fList.get(position).getId(),position);
                                Toast.makeText(getApplicationContext(),"Friend request declined.",Toast.LENGTH_SHORT).show();


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
//        Intent intent = getIntent();
//        int userId;
//        if (intent.getExtras() != null) {
//            userId = intent.getExtras().getInt("userId");
//            Log.d("userId", new Integer(userId).toString());
//        }
//        else {
//            SharedPreferences sp1 = this.getSharedPreferences("Login", MODE_PRIVATE);
//            userId = sp1.getInt("userId", 0);
//        }
//        // Send volley request using item ID to retrieve data
//        queue = Volley.newRequestQueue(this);
//        String baseUrl = "http://167.172.197.162/api/v1/users/" + userId + "/friends/" + fList.get(position).getId();
//        Log.d("baseUrl", baseUrl);
//        final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.DELETE, baseUrl,
//                null, new Response.Listener<JSONObject>() {
//            @Override
//            public void onResponse(JSONObject response) {
//                Log.d("Response", "Friendship being deleted!!!");
//                try {
//                    JSONObject item = response;
//                    fList.remove(position);
//                    if (recAdapter != null) {
//                        recAdapter.notifyDataSetChanged();
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                // TODO: Handle error
//                Log.d("error", "Error in finding friend profile response");
//                error.printStackTrace();
//            }
//        });
//
//        // Add the request to the RequestQueue.
//        queue.add(jsonObjectRequest);
//        queue.start();
    }

}