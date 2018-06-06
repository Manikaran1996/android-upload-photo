package com.ibm.imageupload;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private final int IMAGE_SELECT_CODE = 1;
    private ImageView imageView;
    private Button upload;
    private Button uploadDefault;
    String base64EncodingImage;
    private RequestQueue queue;
    private ConstraintLayout layout;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView = findViewById(R.id.imageView);
        upload = findViewById(R.id.button2);
        uploadDefault = findViewById(R.id.button);
        layout = findViewById(R.id.layout);

        uploadDefault.setOnClickListener(this);
        upload.setOnClickListener(this);

        queue = Volley.newRequestQueue(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_PICK);
        startActivityForResult(Intent.createChooser(intent, "Select Image"), IMAGE_SELECT_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == IMAGE_SELECT_CODE) {
            if(resultCode == RESULT_OK) {
                try {
                    Uri uri = data.getData();
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                    imageView.setImageBitmap(bitmap);
                    imageView.setVisibility(View.VISIBLE);
                    upload.setVisibility(View.VISIBLE);
                    uploadDefault.setVisibility(View.GONE);
                    sendImage(bitmap);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else
                Toast.makeText(this, "ERROR" + "", Toast.LENGTH_SHORT).show();
        }
    }

    private void sendImage(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        base64EncodingImage = Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT);
        Log.d("Bitmap", "Length of encoding = " + base64EncodingImage.length() +
                "\n Size = " + bitmap.getWidth() + " x " + bitmap.getHeight());
        Toast.makeText(this,  "Length of base64 encoding : " + base64EncodingImage.length(), Toast.LENGTH_SHORT).show();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("image", base64EncodingImage);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        /*JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, "https://hello-world-service.mybluemix.net/upload/", jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Toast.makeText(MainActivity.this, response.toString(), Toast.LENGTH_SHORT).show();
                        hideProgressBar();
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MainActivity.this, "Error : " + error.getMessage(), Toast.LENGTH_SHORT).show();
                        hideProgressBar();
                    }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };*/
        StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://192.169.119.18:8000/upload2", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(MainActivity.this, response, Toast.LENGTH_SHORT).show();
                hideProgressBar();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                hideProgressBar();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<>();
                params.put("image", base64EncodingImage);
                return params;
            }
        };
        showProgressBar();
        //queue.add(jsonObjectRequest);
        queue.add(stringRequest);
    }

    public void showProgressBar() {
        progressBar = new ProgressBar(this, null, android.R.attr.progressBarStyle);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        params.addRule(RelativeLayout.CENTER_VERTICAL);
        params.addRule(RelativeLayout.CENTER_HORIZONTAL);
        progressBar.setBackgroundColor(Color.argb(0xa, 0x0, 0x0, 0x0));
        layout.addView(progressBar, params);
        progressBar.setVisibility(View.VISIBLE);
    }

    public void hideProgressBar() {
        progressBar.setVisibility(View.GONE);
    }


}
