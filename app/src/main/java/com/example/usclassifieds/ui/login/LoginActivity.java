package com.example.usclassifieds.ui.login;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.usclassifieds.CreateItemPostActivity;
import com.example.usclassifieds.ExploreActivity;
import com.example.usclassifieds.ItemActivity;
import com.example.usclassifieds.R;
import com.example.usclassifieds.customvolleyrequest.AppHelper;
import com.example.usclassifieds.customvolleyrequest.FetchPath;
import com.example.usclassifieds.customvolleyrequest.VolleyMultipartRequest;
import com.example.usclassifieds.data.model.LoggedInUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class LoginActivity extends AppCompatActivity {

    LoggedInUser currUser;

    private SharedPreferences preferences;
    private RequestQueue queue;

    private static final int IMAGE_PICK_CODE = 1000;
    private static final int PERMISSION_CODE = 1001;

    String UPLOAD_IMAGE_POST_URL = "http://167.172.197.162/api/v1/images";

    ImageView mImageView;

    File imageFile;
    String imageURL = null;
    ProgressDialog progressDialog;

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    public void uploadProfilePic(final View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            int permission = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

            if (permission != PackageManager.PERMISSION_GRANTED) {
                // We don't have permission so prompt the user
                ActivityCompat.requestPermissions(
                        this,
                        PERMISSIONS_STORAGE,
                        REQUEST_EXTERNAL_STORAGE
                );
            }

            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_DENIED) {
                //permission not granted, so request it
                String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE};

                requestPermissions(permissions, PERMISSION_CODE);
            } else {
                pickImageFromGallery();
            }
        } else {
            pickImageFromGallery();
        }

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("image", imageURL);


    }

    private void pickImageFromGallery() {
        // intent to pick image
        progressDialog = new ProgressDialog(LoginActivity.this);
        progressDialog.setMessage("Uploading Image, please wait...");
        progressDialog.show();
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICK_CODE);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == IMAGE_PICK_CODE) {
            Uri filePath = data.getData();

            mImageView = findViewById(R.id.profile_pic);
            mImageView.setImageURI(data.getData());

            //imageFile = new File(FetchPath.getPath(getBaseContext(), filePath));

            VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(Request.Method.POST, UPLOAD_IMAGE_POST_URL, new Response.Listener<NetworkResponse>() {
                @Override
                public void onResponse(NetworkResponse response) {
                    String resultResponse = new String(response.data);
                    JSONObject json = new JSONObject();
                    try {
                        json = new JSONObject(resultResponse);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    try {
                        imageURL = json.getString("image_url");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    progressDialog.dismiss();

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    error.printStackTrace();
                }
            }) {

                @Override
                protected Map<String, DataPart> getByteData() {
                    Map<String, DataPart> params = new HashMap<>();
                    // file name could found file base or direct access from real path
                    // for now just get bitmap data from ImageView

                    params.put("image", new DataPart("profile_pic.jpg", AppHelper.getFileDataFromDrawable(getBaseContext(), mImageView.getDrawable()), "image/jpeg"));
                    return params;
                }
            };
            RequestQueue mRequestQueue = Volley.newRequestQueue(getApplicationContext());
            mRequestQueue.add(multipartRequest);
            mRequestQueue.start();
        }


    }

    private byte[] getByte(File file) {
        byte[] getBytes = {};
        try {
            getBytes = new byte[(int) file.length()];
            InputStream is = new FileInputStream(file);
            is.read(getBytes);
            is.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return getBytes;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //permission was granted
                    pickImageFromGallery();
                } else {
                    //permission was denied

                    Toast.makeText(this, "Permission denied...!", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }


    public void register(final View view) {
        EditText usernameEditText = findViewById(R.id.reg_username);
        final EditText passwordEditText = findViewById(R.id.reg_password);
        EditText emailEditText = findViewById(R.id.reg_email);
        EditText phoneEditText = findViewById(R.id.reg_phone);
        final String name = usernameEditText.getText().toString();
        final String pass = passwordEditText.getText().toString();
        String email = emailEditText.getText().toString();
        String phone = phoneEditText.getText().toString();

        queue = Volley.newRequestQueue(this);
        String url = "http://167.172.197.162/api/v1/users";
        JSONObject testUser = new JSONObject();
        try {
            testUser.put("name", name);
            testUser.putOpt("password", pass);
            testUser.putOpt("email", email);
            if (!phone.isEmpty()) {
                testUser.putOpt("phone_number", phone);
            }
            testUser.putOpt("photo",imageURL);
        } catch (JSONException jsone) {
            jsone.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url,
                testUser, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("Response", "Response!!!");
                try {
                    Log.d("responses: ", response.toString());
                    JSONObject item = response;

                    currUser = new LoggedInUser(item.getInt("id"), name);

                    Log.d("REGMSG", "Register in as " + currUser.getDisplayName());

                    // Set EditText values before logging in
                    EditText sign_username = findViewById(R.id.username);
                    EditText sign_password = findViewById(R.id.password);
                    sign_username.setText(name);
                    sign_password.setText(pass);

                    // Login with newly created user
                    login(view);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // TODO: Handle error
                Log.d("error", "Error in response");
                NetworkResponse networkResponse = error.networkResponse;
                if (networkResponse != null) {
                    if (networkResponse.statusCode == 400) {
                        // wrong credentials, so alert user and clear password field
                        Toast.makeText(LoginActivity.this,
                                "Registration Failed! Username, Password, and Email are required, and Phone Number must be only numbers.",
                                Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

        queue.add(jsonObjectRequest);

    }

    public void login(View view) {

        EditText usernameEditText = findViewById(R.id.username);
        final EditText passwordEditText = findViewById(R.id.password);
        final String name = usernameEditText.getText().toString();
        String pass = passwordEditText.getText().toString();

        queue = Volley.newRequestQueue(this);
        String baseUrl = "http://167.172.197.162/api/v1/login";
        JSONObject testUser = new JSONObject();
        try {
            testUser.put("name", name);
            testUser.putOpt("password", pass);
        } catch (JSONException jsone) {
            jsone.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, baseUrl,
                testUser, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("Response", "Response!!!");
                try {
                    Log.d("responses: ", response.toString());
                    JSONObject item = response;

                    currUser = new LoggedInUser(item.getInt("id"), name);
                    Log.d("MSG", "Logged in as " + currUser.getDisplayName());
                    // Create SharedPreferences object, save userId and displayName
                    SharedPreferences sp = getApplicationContext().getSharedPreferences("Login", MODE_PRIVATE);
                    SharedPreferences.Editor Ed = sp.edit();
                    Ed.putInt("userId", currUser.getUserId());
                    Ed.putString("displayName", currUser.getDisplayName());
                    Ed.commit();

                    // Start Explore Activity
                    Intent intent = new Intent(LoginActivity.this, ExploreActivity.class);
                    startActivity(intent);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // TODO: Handle error
                Log.d("error", "Error in response");
                NetworkResponse networkResponse = error.networkResponse;
                if (networkResponse != null) {
                    if (networkResponse.statusCode == 400) {
                        // wrong credentials, so alert user and clear password field
                        Toast.makeText(LoginActivity.this,
                                "Login Failed! No User with those credentials was found",
                                Toast.LENGTH_LONG).show();
                        passwordEditText.setText("");
                    }
                }
            }
        });

        queue.add(jsonObjectRequest);

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d("Find Location", "in find_location");


        String location_context = Context.LOCATION_SERVICE;
        LocationManager locationManager = (LocationManager) getSystemService(location_context);
        List<String> providers = locationManager.getProviders(true);
        for (String provider : providers) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    Activity#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for Activity#requestPermissions for more details.
                    return;
                }
            }
            locationManager.requestLocationUpdates(provider, 1000, 0,
                    new LocationListener() {

                        public void onLocationChanged(Location location) {
                        }

                        public void onProviderDisabled(String provider) {
                        }

                        public void onProviderEnabled(String provider) {
                        }

                        public void onStatusChanged(String provider, int status,
                                                    Bundle extras) {
                        }
                    });
            Location location = locationManager.getLastKnownLocation(provider);
            // Got last known location. In some rare situations this can be null.
            if (location != null) {
                // Logic to handle location object
                System.out.println("GOT LOCATION");
                System.out.println("latitude: "+location.getLatitude());
                System.out.println("longitude: "+location.getLongitude());

                SharedPreferences sp = getApplicationContext().getSharedPreferences("Login", MODE_PRIVATE);
                SharedPreferences.Editor Ed = sp.edit();
                Ed.putFloat("latitude", (float)location.getLatitude());
                Ed.putFloat("longitude", (float)location.getLongitude());

                Ed.commit();
            }
            else {
                System.out.println("DID NOT GET LOC");
            }
        }

        setContentView(R.layout.activity_login);



        // check if SharedPreferences has already been made
        preferences = getSharedPreferences("Login", MODE_PRIVATE);
        if (preferences.contains("userId")) {
            // Then immediately start next activity
            Toast.makeText(this, "Automatically Logging in " + preferences.getString("displayName", null), Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, ExploreActivity.class);
            startActivity(intent);
        }

    }

}
