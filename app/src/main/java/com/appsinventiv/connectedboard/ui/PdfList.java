package com.appsinventiv.connectedboard.ui;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.appsinventiv.connectedboard.Adapters.PostsAdapter;
import com.appsinventiv.connectedboard.Models.PostModel;
import com.appsinventiv.connectedboard.R;
import com.appsinventiv.connectedboard.Utils.CommonUtils;
import com.appsinventiv.connectedboard.Utils.SharedPrefs;
import com.appsinventiv.connectedboard.ui.UserManagement.StudentEdit;
import com.appsinventiv.connectedboard.ui.UserManagement.TeacherEdit;
import com.bumptech.glide.Glide;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

public class PdfList extends AppCompatActivity {

    RecyclerView recyclerview;
    PostsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf_list);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        this.setTitle("PDF List");

        recyclerview = findViewById(R.id.recyclerview);
        recyclerview.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        adapter = new PostsAdapter(this, MainActivity.pdfList, false, new PostsAdapter.PostsAdapterCallbacks() {
            @Override
            public void onDownload(PostModel model) {
                CommonUtils.showToast("Downloading");
                if (model.getType().equalsIgnoreCase("image")) {
                    String string = Long.toHexString(Double.doubleToLongBits(Math.random()));
                    DownloadFile.fromUrl(model.getUrl(), string + ".jpg");

                } else if (model.getType().equalsIgnoreCase("video")) {
                    String string = Long.toHexString(Double.doubleToLongBits(Math.random()));
                    DownloadFile.fromUrl(model.getUrl(), string + ".mp4");

                }else if(model.getType().equalsIgnoreCase("pdf")){
                    String string = Long.toHexString(Double.doubleToLongBits(Math.random()));
                    DownloadFile.fromUrl(model.getUrl(), string + ".pdf");
                }
            }

            @Override
            public void onShare(PostModel model) {
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_TEXT, model.getUrl());
                startActivity(Intent.createChooser(shareIntent, "Share post via.."));
            }

            @Override
            public void onDelete(PostModel model) {

            }

            @Override
            public void onPlayVideo(PostModel model) {
                Intent i = new Intent(PdfList.this, PlayVideo.class);
                i.putExtra("videoUrl", model.getUrl());
                startActivity(i);
            }

            @Override
            public void onOpenFile(PostModel model) {
                Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(model.getUrl()));
                startActivity(i);
            }
        });

        recyclerview.setAdapter(adapter);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {


            finish();
        }

        return super.onOptionsItemSelected(item);
    }


}
