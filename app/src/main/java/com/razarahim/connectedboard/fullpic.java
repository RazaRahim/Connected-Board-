package com.razarahim.connectedboard;

import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.content.Loader;

import android.app.AlertDialog;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.github.chrisbanes.photoview.PhotoView;
import com.squareup.picasso.Picasso;

public class fullpic extends AppCompatActivity {

    String image;
    PhotoView img;

    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullpic);
        getSupportActionBar().hide();


        progressBar = findViewById(R.id.pg);


        progressBar.setVisibility(View.GONE);
        img = findViewById(R.id.imageV);
        image = getIntent().getStringExtra("imag");
        Picasso.get().load(image).into(img);


    }
}