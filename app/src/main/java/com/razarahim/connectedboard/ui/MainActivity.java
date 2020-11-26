package com.razarahim.connectedboard.ui;

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
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.razarahim.connectedboard.Adapters.LikeAdapter;
import com.razarahim.connectedboard.Adapters.PostsAdapter;
import com.razarahim.connectedboard.Models.PostModel;
import com.razarahim.connectedboard.R;
import com.razarahim.connectedboard.Utils.CommonUtils;
import com.razarahim.connectedboard.Utils.SharedPrefs;
import com.razarahim.connectedboard.comments;
import com.razarahim.connectedboard.ui.UserManagement.StudentEdit;
import com.razarahim.connectedboard.ui.UserManagement.TeacherEdit;
import com.bumptech.glide.Glide;


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

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    RecyclerView recyclerview;
    LikeAdapter adapter;
    private ArrayList<PostModel> itemList = new ArrayList<>();
    private ArrayList<String> Keyss = new ArrayList<>();
    public static ArrayList<PostModel> pdfList = new ArrayList<>();
    DatabaseReference mDatabase;
    private Toolbar toolbar;
    EditText search;

//    private int counter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        search = findViewById(R.id.search);
        recyclerview = findViewById(R.id.recyclerview);
        recyclerview.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, true));




        adapter = new LikeAdapter(this, itemList,Keyss, false,
                new LikeAdapter.PostsAdapterCallbacks() {
            @Override
            public void onDownload(PostModel model) {
                CommonUtils.showToast("Downloading");
                if (model.getType().equalsIgnoreCase("image")) {
                    String string = Long.toHexString(Double.doubleToLongBits(Math.random()));
                    DownloadFile.fromUrl(model.getUrl(), string + ".jpg");

                } else if (model.getType().equalsIgnoreCase("video")) {
                    String string = Long.toHexString(Double.doubleToLongBits(Math.random()));
                    DownloadFile.fromUrl(model.getUrl(), string + ".mp4");

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
                Intent i = new Intent(MainActivity.this, PlayVideo.class);
                i.putExtra("videoUrl", model.getUrl());
                startActivity(i);
            }

            @Override
            public void onOpenFile(PostModel model) {
                Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(model.getUrl()));
                startActivity(i);
            }

            @Override
            public void onComments(PostModel model) {
//           Intent intent = new Intent(MainActivity.this, comments.class);
//           intent.putExtra("postKey",);
            }
        });

        recyclerview.setAdapter(adapter);
        getDataFromServer();
        initDrawer();

        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                adapter.filter(s.toString());
            }
        });
    }

    private void getDataFromServer() {
        mDatabase.child("Posts").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        PostModel model = snapshot.getValue(PostModel.class);
                        String Key = snapshot.getKey();
                        if (model != null) {
                            if (model.getDepartment() != null) {
                                if (!model.getType().equalsIgnoreCase("pdf")) {
                                    if (model.getDepartment().equalsIgnoreCase("all")) {
                                        itemList.add(model);
                                        Keyss.add(Key);
                                    } else if (model.getDepartment().equalsIgnoreCase(SharedPrefs.getDepartment())) {
                                        itemList.add(model);
                                        Keyss.add(Key);
                                    }
                                } else {
                                    pdfList.add(model);
                                }
                            }
                        }

                    }
//                    Collections.sort(itemList, new Comparator<PostModel>() {
//                        @Override
//                        public int compare(PostModel listData, PostModel t1) {
//                            Long ob1 = listData.getTime();
//                            Long ob2 = t1.getTime();
//                            return ob2.compareTo(ob1);
//
//                        }
//                    });

                    adapter.setItemList(itemList,Keyss);
                    adapter.updateList(itemList,Keyss);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void initDrawer() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        toggle.getDrawerArrowDrawable().setColor(Color.WHITE);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);

        View headerView = navigationView.getHeaderView(0);

        CircleImageView image = headerView.findViewById(R.id.imageView);
        TextView navUsername = (TextView) headerView.findViewById(R.id.name_drawer);
        TextView navSubtitle = (TextView) headerView.findViewById(R.id.subtitle_drawer);
        if (SharedPrefs.getLoggedInAs().equalsIgnoreCase("teacher")) {
            Glide.with(MainActivity.this).load(SharedPrefs.getTeacherModel().getPicUrl()).into(image);

            navUsername.setText("Welcome, " + SharedPrefs.getTeacherModel().getName());
        } else if (SharedPrefs.getLoggedInAs().equalsIgnoreCase("student")) {
            Glide.with(MainActivity.this).load(SharedPrefs.getStudentModel().getPicUrl()).into(image);

            navUsername.setText("Welcome, " + SharedPrefs.getStudentModel().getName());

        } else {
            Glide.with(MainActivity.this).load(R.drawable.logo).into(image);
            navUsername.setText("Connected board");

        }
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            // Handle the camera action
        } else if (id == R.id.nav_profile) {
            if (SharedPrefs.getLoggedInAs().equalsIgnoreCase("student")) {
                startActivity(new Intent(MainActivity.this, StudentEdit.class));
            } else if (SharedPrefs.getLoggedInAs().equalsIgnoreCase("teacher")) {
                startActivity(new Intent(MainActivity.this, TeacherEdit.class));

            }
//            startActivity(new Intent(MainActivity.this, Profile.class));

        } else if (id == R.id.nav_pdf) {
            startActivity(new Intent(MainActivity.this, PdfList.class));


        } else if (id == R.id.nav_faqs) {
            startActivity(new Intent(MainActivity.this, AddFaq.class));


        } else if (id == R.id.nav_signuout) {
            SharedPrefs.logout();
            Intent i = new Intent(MainActivity.this, Splash.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
            finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}
