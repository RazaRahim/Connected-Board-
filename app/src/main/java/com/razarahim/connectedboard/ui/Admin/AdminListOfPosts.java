package com.razarahim.connectedboard.ui.Admin;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.razarahim.connectedboard.Adapters.PostsAdapter;
import com.razarahim.connectedboard.Models.PostModel;
import com.razarahim.connectedboard.R;
import com.razarahim.connectedboard.Utils.CommonUtils;
import com.razarahim.connectedboard.ui.MainActivity;
import com.razarahim.connectedboard.ui.PlayVideo;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class AdminListOfPosts extends AppCompatActivity {

    FloatingActionButton addPost;
    RecyclerView recyclerview;
    private ArrayList<PostModel> itemList = new ArrayList<>();
    DatabaseReference mDatabase;
    PostsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_of_post);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        this.setTitle("Posts");
        recyclerview = findViewById(R.id.recyclerview);
        addPost = findViewById(R.id.addPost);

        addPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AdminListOfPosts.this, AddPost.class));
            }
        });
        mDatabase = FirebaseDatabase.getInstance().getReference();
        recyclerview = findViewById(R.id.recyclerview);
        recyclerview.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        adapter = new PostsAdapter(this, itemList, true, new PostsAdapter.PostsAdapterCallbacks() {
            @Override
            public void onDownload(PostModel model) {
                CommonUtils.showToast("Downloading");

            }

            @Override
            public void onShare(PostModel model) {

            }

            @Override
            public void onDelete(PostModel model) {
                showDeleteAlert(model);

            }

            @Override
            public void onPlayVideo(PostModel model) {
                Intent i = new Intent(AdminListOfPosts.this, PlayVideo.class);
                i.putExtra("videoUrl", model.getUrl());
                startActivity(i);
            }

            @Override
            public void onOpenFile(PostModel model) {

            }

            @Override
            public void onLike(PostModel model) {
//                int counter = 0;
//                counter ++;
//                model.setLike(Integer.toString(counter));
//
//                Toast.makeText(AdminListOfPosts.this, "Likes it!", Toast.LENGTH_LONG).show();

            }
        });

        recyclerview.setAdapter(adapter);
        getDataFromServer();


    }

    private void showDeleteAlert(final PostModel model) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Alert");
        builder.setMessage("Do you want to delete this post? ");

        // add the buttons
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                mDatabase.child("Posts").child(model.getId()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        CommonUtils.showToast("Deleted");
                    }
                });

            }
        });
        builder.setNegativeButton("Cancel", null);

        // create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void getDataFromServer() {
        mDatabase.child("Posts").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    itemList.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        PostModel model = snapshot.getValue(PostModel.class);
                        if (model != null) {
                            itemList.add(model);

                        }
                    }
                    Collections.sort(itemList, new Comparator<PostModel>() {
                        @Override
                        public int compare(PostModel listData, PostModel t1) {
                            Long ob1 = listData.getTime();
                            Long ob2 = t1.getTime();
                            return ob2.compareTo(ob1);

                        }
                    });

                    adapter.setItemList(itemList);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

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
