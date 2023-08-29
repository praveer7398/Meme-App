package com.example.memeapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    ImageView memeimage;
    Button next,share;
    ProgressBar progressBar;
    String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        memeimage=findViewById(R.id.memeimage);
        next=findViewById(R.id.next);
        share=findViewById(R.id.share);
        progressBar=findViewById(R.id.progressBar);
        memecall();
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                memecall();
            }
        });
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    shareimage();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });

    }
    public void memecall(){
        url=" https://meme-api.com/gimme";
        progressBar.setVisibility(View.VISIBLE);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            url=response.getString("url");
                            Glide.with(MainActivity.this).load(url).listener(new RequestListener<Drawable>() {
                                @Override
                                public boolean onLoadFailed(@Nullable GlideException e, @Nullable Object model, @NonNull Target<Drawable> target, boolean isFirstResource) {
                                    progressBar.setVisibility(View.INVISIBLE);
                                    return false;
                                }

                                @Override
                                public boolean onResourceReady(@NonNull Drawable resource, @NonNull Object model, Target<Drawable> target, @NonNull DataSource dataSource, boolean isFirstResource) {
                                    progressBar.setVisibility(View.INVISIBLE);
                                    return false;
                                }
                            }).into(memeimage);
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }

                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle error

                    }
                });
        // Access the RequestQueue through your singleton class.
        MySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);
    }
    public void shareimage() throws IOException {
        StrictMode.VmPolicy.Builder builder=new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        BitmapDrawable drawable=(BitmapDrawable) memeimage.getDrawable();
        Bitmap bitmap=drawable.getBitmap();
        File f =new File(getExternalCacheDir()+"/"+"Meme App"+".png");
        Intent shareimage=new Intent(Intent.ACTION_SEND);
        FileOutputStream outputStream=new FileOutputStream(f);
        bitmap.compress(Bitmap.CompressFormat.PNG,100,outputStream);
        outputStream.flush();
        outputStream.close();
        shareimage.setType("image/*");
        shareimage.putExtra(Intent.EXTRA_TEXT,"Hey, checkout this cool meme");
        shareimage.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(f));
        shareimage.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(shareimage);
    }
}