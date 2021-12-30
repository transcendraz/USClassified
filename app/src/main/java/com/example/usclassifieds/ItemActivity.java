package com.example.usclassifieds;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.android.volley.AuthFailureError;
import com.like.LikeButton;
import com.like.OnLikeListener;
import com.squareup.picasso.Picasso;
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
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;

public class ItemActivity extends AppCompatActivity implements OnMapReadyCallback {
    private RequestQueue queue;
    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                    Log.d("menuItem", "onNavigationItemSelected: " + menuItem.toString());
                    Intent intent = null;
                    switch (menuItem.getItemId()) {
                        case R.id.buy_item:
                            intent = new Intent(ItemActivity.this, ExploreActivity.class);
                            startActivity(intent);
                            break;
                        case R.id.user_profile_item:
                             intent = new Intent(ItemActivity.this, ViewUserProfileActivity.class);
                             startActivity(intent);
                             break;

                        case R.id.sell_item:
                            intent = new Intent(ItemActivity.this, CreateItemPostActivity.class);
                            startActivity(intent);
                            break;
                    }
                    return false;
                }
            };




    private GoogleMap mMap;
    double latitude;
    double longitude;
    int userId;
    int itemId;
    int CurruserId;
    int BuyerId;
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
        mMap = googleMap;
        // Add a marker in Given Location and move the camera
        LatLng location = new LatLng(latitude, longitude);
        mMap.addMarker(new MarkerOptions().position(location).title("Trade Location"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(location));
    }




    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);




        // Sending a request with RequestQueue

        Intent intent = getIntent();
        itemId=-1;
        if (intent.getExtras() != null) {
            itemId = intent.getExtras().getInt("itemId");
            Log.d("itemId", new Integer(itemId).toString());
        }
        SharedPreferences sp = getApplicationContext().getSharedPreferences("Login", MODE_PRIVATE);
        CurruserId=sp.getInt("userId",-1);

        // Send volley request using item ID to retrieve data
        queue = Volley.newRequestQueue(this);
        String baseUrl = "http://167.172.197.162/api/v1/items/"+new Integer(itemId).toString();

        Log.d("baseUrl",baseUrl);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, baseUrl,
                null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("Response", "Response!!!");
                try {
                    final JSONObject item = response;
                    Log.d("responses", item.getString("name"));
                    Log.d("responses", item.getString("price"));
                    Log.d("responses", item.getString("image"));
                    Log.d("responses", item.getString("user_id"));
                    Log.d("responses", item.getString("latitude"));
                    Log.d("responses", item.getString("longitude"));
                    Log.d("responses", item.getString("tags"));
                    Log.d("responses", item.getString("description"));

                    userId=item.getInt("user_id");
                    latitude=Double.parseDouble(item.getString("latitude"));
                    longitude=Double.parseDouble(item.getString("longitude"));

                    //Set ItemName
                    TextView ItemName=(TextView)findViewById(R.id.ItemName);
                    ItemName.setText(item.getString("name"));

                    //TODO: uncomment following line if pic system is online.
                    //Needs modification
                    ImageView ItemPic=(ImageView)findViewById(R.id.ItemPic);
                    String imageUrl = item.getString("image");

                    //Loading image using Picasso
                    Picasso.get().load(imageUrl).into(ItemPic);

                    //Set Description
                    TextView Description=(TextView)findViewById(R.id.Description);
                    Description.setText("Description:\n"+item.getString("description"));

                    //Set Price
                    TextView Price=(TextView)findViewById(R.id.Price);
                    Price.setText("Price:\n$"+item.getString("price"));


                    //Buy item
                    Button Buy= (Button) findViewById(R.id.Buy);
                    Buy.setOnClickListener(new View.OnClickListener(){
                        @Override
                        public void onClick(View v){
                            if(userId!=CurruserId){
                                Toast.makeText(ItemActivity.this, "You cannot sell other people's item!", Toast.LENGTH_LONG).show();
                            }else {
                                EditText BuyerName = findViewById(R.id.BuyerName);
                                Log.d("Buyername",BuyerName.getText().toString());
                                if(BuyerName.getText().length()==0) {
                                    Toast.makeText(ItemActivity.this, "You must include a name.", Toast.LENGTH_LONG).show();
                                    return;
                                }
                                String GetName="http://167.172.197.162/api/v1/users/name/"+BuyerName.getText().toString();
                                JsonObjectRequest jsonObjectRequest2 = new JsonObjectRequest(Request.Method.GET, GetName,
                                        null, new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        Log.d("Response", "Response!!!");
                                        try {
                                            JSONObject BuyResponse = response;
                                            Log.d("responses", BuyResponse.toString());
                                            BuyerId=response.getInt("id");
                                            Log.d("responses",new Integer(BuyerId).toString());

                                            if(BuyerId==0){
                                                Toast.makeText(ItemActivity.this, "Such Username does not exist.", Toast.LENGTH_LONG).show();
                                                return;
                                            }
                                            if(BuyerId==CurruserId){
                                                Toast.makeText(ItemActivity.this, "Cannot sell items to yourself", Toast.LENGTH_LONG).show();
                                                return;
                                            }

                                            String SellUrl = "http://167.172.197.162/api/v1/items/" + itemId + "/seller/" + CurruserId + "/buyer/" + BuyerId;
                                            JsonObjectRequest jsonObjectRequest3 = new JsonObjectRequest(Request.Method.DELETE, SellUrl,
                                                    null, new Response.Listener<JSONObject>() {
                                                @Override
                                                public void onResponse(JSONObject response) {
                                                    Log.d("Response", "Response!!!");
                                                    try {
                                                        JSONObject BuyResponse = response;
                                                        Log.d("responses", BuyResponse.toString());
                                                        Intent intent = new Intent(ItemActivity.this, ExploreActivity.class);
                                                        Toast.makeText(ItemActivity.this, "Item Successfully Sold!", Toast.LENGTH_LONG).show();
                                                        startActivity(intent);
                                                    } catch (Exception e) {
                                                        e.printStackTrace();
                                                        Toast.makeText(ItemActivity.this, "Such Username does not exist.", Toast.LENGTH_LONG).show();
                                                        return;
                                                    }
                                                }
                                            }, new Response.ErrorListener() {
                                                @Override
                                                public void onErrorResponse(VolleyError error) {
                                                    // TODO: Handle error
                                                    Log.d("error", "Error in response");

                                                }
                                            });
                                            queue.add(jsonObjectRequest3);
                                            queue.start();
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                            Toast.makeText(ItemActivity.this, "Such Username does not exist.", Toast.LENGTH_LONG).show();
                                            return;
                                        }
                                    }
                                }, new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        // TODO: Handle error
                                        Log.d("error", "Error in response");
                                        error.printStackTrace();
                                        Toast.makeText(ItemActivity.this, "Such Username does not exist.", Toast.LENGTH_LONG).show();
                                        return;
                                    }
                                });
                                queue.add(jsonObjectRequest2);
                                queue.start();


                            }
                        }
                    });



                    //Add to Wishlist
                    com.like.LikeButton Like = (com.like.LikeButton)findViewById(R.id.star_button);
                    SharedPreferences sp = getApplicationContext().getSharedPreferences("Login", MODE_PRIVATE);
                    int CurruserId=sp.getInt("userId",-1);
                    String GETUrl = "http://167.172.197.162/api/v1/users/"+CurruserId+"/wishlist";
                    JsonObjectRequest jsonObjectRequest4 = new JsonObjectRequest(Request.Method.GET, GETUrl,
                            null, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.d("Response", "Response!!!");
                            try {
                                JSONObject item = response;
                                Log.d("responses", response.toString());
                                com.like.LikeButton Like = (com.like.LikeButton)findViewById(R.id.star_button);
                                boolean find=false;
                                JSONArray a=response.getJSONArray("items");
                                Intent intent = getIntent();
                                int itemId=-1;
                                if (intent.getExtras() != null) {
                                    itemId = intent.getExtras().getInt("itemId");
                                    Log.d("InLIKE", new Integer(itemId).toString());
                                }
                                for(int i=0;i<a.length();i++){
                                    JSONObject b=a.getJSONObject(i);
                                    int c=b.getInt("id");
                                    Log.d("See",new Integer(c).toString());
                                    Log.d("See2",new Integer(itemId).toString());
                                    if(c==itemId){
                                        find=true;
                                    }
                                }
                                if(find==true){
                                    Like.setLiked(true);
                                }else{
                                    Like.setLiked(false);
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
                    queue.add(jsonObjectRequest4);
                    queue.start();


                    Like.setOnLikeListener(new OnLikeListener() {

                        @Override
                        public void liked(LikeButton likeButton) {
                            SharedPreferences sp = getApplicationContext().getSharedPreferences("Login", MODE_PRIVATE);
                            int CurruserId=sp.getInt("userId",-1);
                            Map<String, String> parameters = new HashMap<String, String>();

                            Log.d("CurrUserId",new Integer(CurruserId).toString());
                            Intent intent = getIntent();
                            int itemId=-1;
                            if (intent.getExtras() != null) {
                                itemId = intent.getExtras().getInt("itemId");
                                Log.d("InLIKE", new Integer(itemId).toString());
                            }

                            parameters.put("item_id", new Integer(itemId).toString());

                            for(Map.Entry<String, String> entry : parameters.entrySet()) {
                                System.out.println(entry.getKey() + ":" + entry.getValue());
                            }

                            String AddToWishlist = "http://167.172.197.162/api/v1/users/"+CurruserId+"/wishlist";
                            Log.d("CheckHere",AddToWishlist);
                            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, AddToWishlist, new JSONObject(parameters), new Response.Listener<JSONObject>() {

                                @Override
                                public void onResponse(JSONObject response) {
                                    Log.d("json", response.toString());
                                }
                            }, new Response.ErrorListener() {

                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    // TODO: Handle error
                                    Toast.makeText(ItemActivity.this, "Some error occurred -> " + error, Toast.LENGTH_LONG).show();



                                }
                            });


                            queue.add(request);
                            queue.start();

                        }

                        @Override
                        public void unLiked(LikeButton likeButton) {
                            SharedPreferences sp = getApplicationContext().getSharedPreferences("Login", MODE_PRIVATE);
                            int CurruserId=sp.getInt("userId",-1);
                            Intent intent = getIntent();
                            int itemId=-1;
                            if (intent.getExtras() != null) {
                                itemId = intent.getExtras().getInt("itemId");
                                Log.d("InLIKE", new Integer(itemId).toString());
                            }
                            String DeleteUrl = "http://167.172.197.162/api/v1/users/"+CurruserId+"/wishlist/"+itemId;
                            JsonObjectRequest jsonObjectRequest3 = new JsonObjectRequest(Request.Method.DELETE, DeleteUrl,
                                    null, new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    Log.d("Response", "Response!!!");
                                    try {
                                        JSONObject item = response;
                                        Log.d("responses", response.toString());


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
                            queue.add(jsonObjectRequest3);
                            queue.start();
                        }
                    });
//                        public void onClick(View v) {
//
//
//
//                        }
//                    });







                    //Set Tag
                    JSONArray tagArray = response.getJSONArray("tags");
                    TextView Tags=(TextView)findViewById(R.id.Tags);
                    Tags.setText("Tags:\n");
                    for(int i=0;i<tagArray.length();i++){
                        if(i%2==1){
                            Tags.append(tagArray.get(i)+"");
                        }else {
                            if (i == 0) {
                                Tags.append(tagArray.get(i)+" ");
                            }else{
                                Tags.append("\n" + tagArray.get(i) + " ");
                            }
                        }
                    }

                    //Redirect to Seller Info
                    Button ViewSeller = (Button)findViewById(R.id.ViewSeller);
                    ViewSeller.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            try {
                                Log.d("userId", item.getString("user_id"));
                                // Start detail page upon item click
                                // Send data to ItemActivity
                                int selectedId = Integer.parseInt(item.getString("user_id"));
                                Intent intent = new Intent(ItemActivity.this, ViewUserProfileActivity.class);
                                intent.putExtra("userId", selectedId);
                                startActivity(intent);
                            }catch (JSONException e) {
                                e.printStackTrace();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });

                    //Redirect to Map
                    Button ViewMap = (Button)findViewById(R.id.ViewMap);
                    ViewMap.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            try {
                                Log.d("latitude", item.getString("latitude"));
                                Log.d("longitude", item.getString("longitude"));
                                // Start detail page upon item click
                                // Send data to ItemActivity
                                double latitude = Double.parseDouble(item.getString("latitude"));
                                double longitude = Double.parseDouble(item.getString("longitude"));

                                Intent intent = new Intent(ItemActivity.this, MapsActivity.class);
                                intent.putExtra("Id0", item.getString("id"));
                                intent.putExtra("number",1);
                                intent.putExtra("Lat0", item.getString("latitude"));
                                intent.putExtra("Long0", item.getString("longitude"));
                                intent.putExtra("Name0", item.getString("name"));
                                startActivity(intent);

                            }catch (JSONException e) {
                                e.printStackTrace();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });

                    //Get the Seller's info
                    String baseUrl2 = "http://167.172.197.162/api/v1/users/"+item.getString("user_id");
                    JsonObjectRequest jsonObjectRequest2 = new JsonObjectRequest(Request.Method.GET, baseUrl2,
                            null, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.d("Response", "Response!!!");
                            try {
                                JSONObject item = response;
                                Log.d("responses", item.getString("name"));
                                Log.d("responses", item.getString("email"));
                                Log.d("responses", item.getString("phone_number"));

                                TextView ContactInfo=(TextView)findViewById(R.id.ContactInfo);
                                ContactInfo.setText("ContactInfo:\n" +
                                        "Username: "+item.getString("name")+"\n"+
                                        "Email: "+item.getString("email"));
                                if(item.getString("phone_number")!="null"){
                                    ContactInfo.append("\n"+item.getString("phone_number"));
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
                    queue.add(jsonObjectRequest2);
                    queue.start();

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


        //Map Stuffs
        setContentView(R.layout.activity_item);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);



        FrameLayout include = findViewById(R.id.explore_include);
        BottomNavigationView navigation = (BottomNavigationView) include;
        navigation.setSelectedItemId(R.id.buy_item);
        navigation.setOnNavigationItemSelectedListener(navListener);

    }

}
