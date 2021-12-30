package com.example.usclassifieds;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;

import android.content.SharedPreferences;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;


import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.List;

import static java.security.AccessController.getContext;
import static java.lang.Integer.parseInt;

public class ViewUserProfileActivity extends AppCompatActivity {
    private RequestQueue queue;
    private UserObject currentUser;
    private TextView user_name, user_phone, user_email, user_buysell, power_usr_badge_txt;
    private ImageView user_img, user_badge_display;
    private RatingBar user_rating_bar;

    private int userId;


    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                    Log.d("menuItem", "onNavigationItemSelected: " + menuItem.toString());
                    Intent intent = null;
                    switch (menuItem.getItemId()) {
                        case R.id.buy_item:
                            intent = new Intent(ViewUserProfileActivity.this, ExploreActivity.class);
                            intent.putExtra("selected", "Explore");
                            startActivity(intent);
                            break;
                        case R.id.user_profile_item:
                            intent = new Intent(ViewUserProfileActivity.this,
                                    ViewUserProfileActivity.class);
                            intent.putExtra("selected", "UserProfile");
                            startActivity(intent);
                            break;
                        case R.id.sell_item:
                            intent = new Intent(ViewUserProfileActivity.this, CreateItemPostActivity.class);
                            intent.putExtra("selected", "CreateItem");
                            startActivity(intent);
                            break;
                    }
                    return false;
                }
            };

    public void toViewItem(View view) {
        Intent intent = new Intent(this, ViewUserItemsActivity.class);
        startActivity(intent);
    }

    public void toViewWishlist(View view) {
        Intent intent = new Intent(this, ViewUserWishlistActivity.class);
        startActivity(intent);
    }

    public void toViewFriends(View view) {
        Intent intent = new Intent(this, ViewUserFriendsActivity.class);
        startActivity(intent);
    }

    public void toViewFriendRequest(View view){
        Intent intent = new Intent(this, ViewUserFriendRequestsActivity.class);
        startActivity(intent);
    }
    private void getPendingFriendRequests()
    {
        String baseUrl = "http://167.172.197.162/api/v1/users/";
        Log.d("PendingFriendReqs", "Sending request...");
        String curr_user_id = new String(String.valueOf(this.getSharedPreferences("Login", MODE_PRIVATE).getInt("userId", 0)));
        String base_url_for_pending_fqs = baseUrl + curr_user_id + "/friend_requests/";

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


    private void deleteFriend(int friend_id)
    {
        String baseUrl = "http://167.172.197.162/api/v1/users/";
        Log.d("DeleteFriend", "Sending request...");
        String curr_user_id = new String(String.valueOf(this.getSharedPreferences("Login", MODE_PRIVATE).getInt("userId", 0)));
        String base_url_for_friend_delete = baseUrl + curr_user_id + "/friends/" + friend_id;

        queue = Volley.newRequestQueue(this);
        JSONObject friend_del_status = new JSONObject();

        Log.d("UrlForFriendReqStatus",base_url_for_friend_delete);
        JsonObjectRequest friendReqStatusRequest = new JsonObjectRequest(Request.Method.DELETE, base_url_for_friend_delete, friend_del_status, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("Response", "Response from DeleteFriend!!!");
                try {
                    JSONObject item = response;
                    Log.d("FullResponse: ", item.toString());

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

    private void changeFriendRequest(int action, int friend_id)
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

    public void sendFriendRequest(View view)
    {
        String baseUrl = "http://167.172.197.162/api/v1/users/";
        Log.d("SendRequest", "Sending request...");
        String curr_user_id = new String(String.valueOf(this.getSharedPreferences("Login", MODE_PRIVATE).getInt("userId", 0)));
        int potential_friend_id = userId;
        //check whether attempting to friend request self (will fix button showing later)
        if (potential_friend_id == parseInt(curr_user_id))
        {
            Toast.makeText(getApplicationContext(),"Cannot friend request yourself!",Toast.LENGTH_SHORT).show();
            return;
        }
        String base_url_for_friend_status = baseUrl + curr_user_id + "/friends/" + potential_friend_id;
        sendFriendStatusRequest(base_url_for_friend_status, curr_user_id, potential_friend_id);
    }

    private void sendFriendStatusRequest(String baseURLForFriendStatus, final String curr_user_id, final int potential_friend_id)
    {
        queue = Volley.newRequestQueue(this);
        JSONObject friend_status = new JSONObject();

        Log.d("baseUrlForFriendStatus",baseURLForFriendStatus);
        JsonObjectRequest friendStatusRequest = new JsonObjectRequest(Request.Method.GET, baseURLForFriendStatus, friend_status, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("Response", "Response from FriendStatusRequest!!!");
                try {
                    JSONObject item = response;
                    Log.d("FullResponse: ", item.toString());
//                    setFriendStatus(item.getInt("status"));
                    sendJSONFriendRequest(curr_user_id, potential_friend_id, item.getInt("status"));

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
        queue.add(friendStatusRequest);
        queue.start();
    }

    private void sendJSONFriendRequest(String curr_user_id, int potential_friend_id, int friend_status)
    {
        String baseUrl = "http://167.172.197.162/api/v1/users/";

        Log.d("Friend_status:", String.valueOf(friend_status));

        if (friend_status == 0)
        {
            Toast.makeText(getApplicationContext(),"Friend request sent!",Toast.LENGTH_SHORT).show();
            JSONObject friend_id = new JSONObject();
            try {
                friend_id.put("id", potential_friend_id);

            } catch (JSONException jsone)
            {
                jsone.printStackTrace();
            }
            queue = Volley.newRequestQueue(this);
            String base_URL_for_send_request = baseUrl + curr_user_id + "/friends";
            Log.d("baseUrl",base_URL_for_send_request);
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, base_URL_for_send_request,
                    friend_id, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.d("Response", "Response!!!");
                    try {
                        JSONObject item = response;
                        Log.d("FullResponse: ", item.toString());

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

        }
        else if (friend_status == 1)
        {
            Toast.makeText(getApplicationContext(),"Friend request already sent.",Toast.LENGTH_SHORT).show();
            return;
        }
        else if (friend_status == 2)
        {
            Toast.makeText(getApplicationContext(),"Friend request pending.",Toast.LENGTH_SHORT).show();
            return;
        }
        else if (friend_status == 3)
        {
            Toast.makeText(getApplicationContext(),"Already friends!",Toast.LENGTH_SHORT).show();
            return;
        }
        else
        {
            Toast.makeText(getApplicationContext(),"Invalid!!!!",Toast.LENGTH_SHORT).show();
            return;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("userProfile", "User Profile code is being run!");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_profile);
        user_email = (TextView) findViewById(R.id.user_email);
        user_name =  (TextView) findViewById(R.id.user_profile_name);
        user_phone = (TextView) findViewById(R.id.user_phone);
        user_img = (ImageView) findViewById(R.id.user_profile_image);
        user_rating_bar = (RatingBar) findViewById(R.id.user_rating);
        user_buysell = (TextView) findViewById(R.id.user_buy_and_sell);
        user_badge_display = (ImageView)findViewById(R.id.user_badge);
        power_usr_badge_txt = (TextView)findViewById(R.id.power_user_badge_text);
        // Sending a request with RequestQueue

        Intent intent = getIntent();
        userId=-1;

        SharedPreferences sp1 = this.getSharedPreferences("Login", MODE_PRIVATE);
        userId = sp1.getInt("userId", 0);

        if (intent.getExtras() != null && intent.getExtras().containsKey("userId") && (int)(intent.getExtras().get("userId")) != userId) { //if not logged in user

            userId = intent.getExtras().getInt("userId");
            Log.d("userId", new Integer(userId).toString());

            View wishlist = findViewById(R.id.my_wishlist);
            wishlist.setVisibility(View.INVISIBLE);

            View friends = findViewById(R.id.my_friends);
            friends.setVisibility(View.INVISIBLE);

//            View settings = findViewById(R.id.my_settings);
//            settings.setVisibility(View.INVISIBLE);

            View settings = findViewById(R.id.my_friend_requests);
            settings.setVisibility(View.INVISIBLE);

            View friendButton = findViewById(R.id.addFriendButton);
            friendButton.setVisibility(View.VISIBLE);

            user_rating_bar.setIsIndicator(false);


        }
        else //logged in user
        {


            View wishlist = findViewById(R.id.my_wishlist);
            wishlist.setVisibility(View.VISIBLE);

            View friends = findViewById(R.id.my_friends);
            friends.setVisibility(View.VISIBLE);

//            View settings = findViewById(R.id.my_settings);
//            settings.setVisibility(View.INVISIBLE);

            View settings = findViewById(R.id.my_friend_requests);
            settings.setVisibility(View.VISIBLE);

            View friendButton = findViewById(R.id.addFriendButton);
            friendButton.setVisibility(View.INVISIBLE);

            user_rating_bar.setIsIndicator(true);

        }

        // Send volley request using item ID to retrieve data
        queue = Volley.newRequestQueue(this);
        String baseUrl = "http://167.172.197.162/api/v1/users/"+new Integer(userId).toString();
        Log.d("baseUrl",baseUrl);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, baseUrl,
                null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("Response", "Response!!!");
                try {
                    JSONObject item = response;
                    Log.d("FullResponse: ", item.toString());
                    Log.d("responses", item.getString("email"));
                    Log.d("responses", item.getString("phone_number"));
                    Log.d("responses", item.getString("name"));
                    Log.d("photos", item.getString("photo"));

                     currentUser = new UserObject(item.getString("email"), item.getString("name"), item.getString("id"));
                     user_name.setText(item.getString("name"));//currentUser.getUserName());
                     user_email.setText(currentUser.getEmail());

                     Picasso.get().load(item.getString("photo")).into(user_img);


                    user_phone.setText(item.getString("phone_number").equals("null") ?  "" : item.getString("phone_number"));


                     Gson gson = new Gson();
                     Type type = new TypeToken<List<Integer>>() {}.getType();
                     List<Integer> ratings_list = gson.fromJson(item.getString("ratings"), type);
                     int usr_rating = 0;
                     for (int i = 0; i < ratings_list.size(); ++i) usr_rating += ratings_list.get(i);
                     if (ratings_list.size() > 0) usr_rating /= ratings_list.size();
                    currentUser.setRating(usr_rating);
                    Log.d("helloooooo",new Integer(currentUser.getRating()).toString());
                    user_rating_bar.setMax(5);
                    user_rating_bar.setRating(currentUser.getRating());
                    String buy_and_sell_count = item.getString("buy_count") + "/" + item.getString("sell_count");
                    user_buysell.setText(buy_and_sell_count);

                    if (parseInt(item.getString("buy_count")) >= 5 || parseInt(item.getString("sell_count")) >= 5)
                    {
                        user_badge_display.setVisibility(View.VISIBLE);
                        power_usr_badge_txt.setVisibility(View.VISIBLE);
                    }
                    else {
                        user_badge_display.setVisibility(View.INVISIBLE);
                        power_usr_badge_txt.setVisibility(View.INVISIBLE);
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



        FrameLayout include = findViewById(R.id.user_profile_include);
        BottomNavigationView navigation = (BottomNavigationView) include;
        navigation.setSelectedItemId(R.id.user_profile_item);
        navigation.setOnNavigationItemSelectedListener(navListener);

    }
}


