package com.appsinventiv.connectedboard.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.appsinventiv.connectedboard.Adapters.FaqsListAdapter;
import com.appsinventiv.connectedboard.Adapters.PostsAdapter;
import com.appsinventiv.connectedboard.Models.FaqsModel;
import com.appsinventiv.connectedboard.Models.PostModel;
import com.appsinventiv.connectedboard.R;
import com.appsinventiv.connectedboard.Utils.CommonUtils;
import com.appsinventiv.connectedboard.ui.Admin.AddPost;
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

public class ListOfFaqs extends AppCompatActivity {

    RecyclerView recyclerview;
    private ArrayList<FaqsModel> itemList = new ArrayList<>();
    DatabaseReference mDatabase;
    FaqsListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_of_faqs);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        this.setTitle("Faqs");
        recyclerview = findViewById(R.id.recyclerview);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        recyclerview = findViewById(R.id.recyclerview);
        recyclerview.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));

        adapter = new FaqsListAdapter(this, itemList);
        recyclerview.setAdapter(adapter);
        getDataFromServer();


    }


    private void getDataFromServer() {
        mDatabase.child("Faqs").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    itemList.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        FaqsModel model = snapshot.getValue(FaqsModel.class);
                        if (model != null) {
                            itemList.add(model);

                        }
                    }
                    Collections.sort(itemList, new Comparator<FaqsModel>() {
                        @Override
                        public int compare(FaqsModel listData, FaqsModel t1) {
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
