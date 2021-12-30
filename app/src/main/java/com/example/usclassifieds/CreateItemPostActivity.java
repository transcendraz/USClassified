package com.example.usclassifieds;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.usclassifieds.customvolleyrequest.AppHelper;
import com.example.usclassifieds.customvolleyrequest.VolleyMultipartRequest;

import com.google.android.material.bottomnavigation.BottomNavigationView;


public class CreateItemPostActivity extends AppCompatActivity {
    String CREATE_ITEM_POST_URL = "http://167.172.197.162/api/v1/items";
    String UPLOAD_IMAGE_POST_URL = "http://167.172.197.162/api/v1/images";

    private static final int IMAGE_PICK_CODE = 1000;
    private static final int PERMISSION_CODE = 1001;

    RequestQueue mRequestQueue;

    ImageView mImageView;
    Button mUploadButton;
    Button mCreatePostButton;
    EditText mPriceInput;
    String imageURL = "";
    float latitude=0;
    float longitude=0;

    File imageFile;
    ProgressDialog progressDialog;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_item_post);
        mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        //VIEWS

        //upload images
        mImageView = findViewById(R.id.image_view);
        mUploadButton = findViewById(R.id.choose_image_btn);

        mUploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //check runtime permissions
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
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

            }
        });

        //price input
        mPriceInput = findViewById(R.id.post_item_price);
        mPriceInput.addTextChangedListener(new NumberTextWatcher(mPriceInput, "#,###"));

        //create item post
        mCreatePostButton = findViewById(R.id.create_post_btn);

        mCreatePostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                progressDialog = new ProgressDialog(CreateItemPostActivity.this);
                progressDialog.setMessage("Uploading, please wait...");
                progressDialog.show();

                Map<String, Object> parameters = new HashMap<>();
                EditText postName = findViewById(R.id.post_title);
                EditText postDescription = findViewById(R.id.post_description);
                EditText postPrice = mPriceInput;
                EditText postTags = findViewById(R.id.post_tags);
                String[] tags = postTags.getText().toString().replace(" ","").split(",");


                if(postName.getText().length()==0) {
                    progressDialog.dismiss();
                    Toast.makeText(CreateItemPostActivity.this, "You must include a title.", Toast.LENGTH_LONG).show();
                    return;
                }
                else if (postPrice.getText().length()==0) {
                    progressDialog.dismiss();
                    Toast.makeText(CreateItemPostActivity.this, "You must include a price.", Toast.LENGTH_LONG).show();
                    return;
                }
                else if (imageURL.equals("")) {
                    progressDialog.dismiss();
                    Toast.makeText(CreateItemPostActivity.this, "You must upload an image.", Toast.LENGTH_LONG).show();
                    return;
                }

                SharedPreferences preferences = getSharedPreferences("Login", MODE_PRIVATE);

                latitude = preferences.getFloat("latitude", 0);
                longitude = preferences.getFloat("longitude", 0);
                int user_id = preferences.getInt("userId", 1);


                parameters.put("name", postName.getText().toString());
                parameters.put("price", postPrice.getText().toString().substring(1));
                parameters.put("image", imageURL);
                parameters.put("user_id", user_id);
                parameters.put("latitude", String.valueOf(latitude));
                parameters.put("longitude", String.valueOf(longitude));
                parameters.put("description", postDescription.getText().toString());
                parameters.put("tags", tags);




                JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, CREATE_ITEM_POST_URL, new JSONObject(parameters), new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("json", response.toString());
                        progressDialog.dismiss();

                        Intent intent = new Intent(CreateItemPostActivity.this, ExploreActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle error
                        Toast.makeText(CreateItemPostActivity.this, "Some error occurred -> " + error, Toast.LENGTH_LONG).show();

                    }
                }) {
                    //adding parameters to send

                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String, String> parameters = new HashMap<String, String>();
                        parameters.put("Content-Type", "application/json");
                        parameters.put("Accept", "application/json");
                        return parameters;
                    }
                };

                mRequestQueue.add(request);
            }
        });


        FrameLayout include = findViewById(R.id.create_item_include);
        BottomNavigationView navigation = (BottomNavigationView) include;
        navigation.setSelectedItemId(R.id.sell_item);
        navigation.setOnNavigationItemSelectedListener(navListener);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                    Log.d("menuItem", "onNavigationItemSelected: " + menuItem.toString());
                    Intent intent = null;
                    switch (menuItem.getItemId()) {
                        case R.id.buy_item:
                            intent = new Intent(CreateItemPostActivity.this, ExploreActivity.class);
                            startActivity(intent);
                            break;
                        case R.id.user_profile_item:
                            intent = new Intent(CreateItemPostActivity.this, ViewUserProfileActivity.class);
                            startActivity(intent);
                            return true;
                        case R.id.sell_item:
                            intent = new Intent(CreateItemPostActivity.this, CreateItemPostActivity.class);
                            startActivity(intent);
                            break;
                    }
                    return false;
                }
            };


    private void pickImageFromGallery() {
        // intent to pick image
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICK_CODE);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == IMAGE_PICK_CODE) {
            mImageView.setImageURI(data.getData());
            Uri filePath = data.getData();

            imageFile = new File(filePath.getPath());


            System.out.println("null: " + imageFile == null);
            System.out.println("exists: " + imageFile.exists());


            Log.d("taggggg", "null: " + String.valueOf(imageFile == null));
            Log.d("taggggg", "exists: " + imageFile.exists());


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
                    params.put("image", new DataPart("test_thumbnail.jpg", AppHelper.getFileDataFromDrawable(getBaseContext(), mImageView.getDrawable()), "image/jpeg"));

                    return params;
                }
            };
            RequestQueue mRequestQueue = Volley.newRequestQueue(getApplicationContext());
            mRequestQueue.add(multipartRequest);
            mRequestQueue.start();
        }


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


    class NumberTextWatcher implements TextWatcher {

        private final DecimalFormat df;
        private final DecimalFormat dfnd;
        private final EditText et;
        private boolean hasFractionalPart;
        private int trailingZeroCount;

        public NumberTextWatcher(EditText editText, String pattern) {
            df = new DecimalFormat(pattern);
            df.setDecimalSeparatorAlwaysShown(true);
            dfnd = new DecimalFormat("#,###.00");
            this.et = editText;
            hasFractionalPart = false;
        }

        @Override
        public void afterTextChanged(Editable s) {
            et.removeTextChangedListener(this);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
                if (s != null && !s.toString().isEmpty()) {
                    try {
                        int inilen, endlen;
                        inilen = et.getText().length();
                        String v = s.toString().replace(String.valueOf(df.getDecimalFormatSymbols().getGroupingSeparator()), "").replace("$", "");
                        Number n = df.parse(v);
                        int cp = et.getSelectionStart();
                        if (hasFractionalPart) {
                            StringBuilder trailingZeros = new StringBuilder();
                            while (trailingZeroCount-- > 0)
                                trailingZeros.append('0');
                            et.setText(df.format(n) + trailingZeros.toString());
                        } else {
                            et.setText(dfnd.format(n));
                        }
                        et.setText("$".concat(et.getText().toString()));
                        endlen = et.getText().length();
                        int sel = (cp + (endlen - inilen));
                        if (sel > 0 && sel < et.getText().length()) {
                            et.setSelection(sel);
                        } else if (trailingZeroCount > -1) {
                            et.setSelection(et.getText().length() - 3);
                        } else {
                            et.setSelection(et.getText().length());
                        }
                    } catch (NumberFormatException | ParseException e) {
                        e.printStackTrace();
                    }
                }
            }

            et.addTextChangedListener(this);
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            int index = s.toString().indexOf(String.valueOf(df.getDecimalFormatSymbols().getDecimalSeparator()));
            trailingZeroCount = 0;
            if (index > -1) {
                for (index++; index < s.length(); index++) {
                    if (s.charAt(index) == '0')
                        trailingZeroCount++;
                    else {
                        trailingZeroCount = 0;
                    }
                }
                hasFractionalPart = true;
            } else {
                hasFractionalPart = false;
            }
        }
    }
}
