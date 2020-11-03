package com.razarahim.connectedboard.ui;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.razarahim.connectedboard.Models.FaqsModel;
import com.razarahim.connectedboard.R;
import com.razarahim.connectedboard.Utils.CommonUtils;
import com.razarahim.connectedboard.Utils.SharedPrefs;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class ViewFaq extends AppCompatActivity {

    TextView message, title, faqBy, time;
    DatabaseReference mDatabase;

    String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_faq);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        this.setTitle("Faqs");
        id = getIntent().getStringExtra("id");
        title = findViewById(R.id.title);
        message = findViewById(R.id.message);
        time = findViewById(R.id.time);
        faqBy = findViewById(R.id.faqBy);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        getDataFromServer();


    }

    private void getDataFromServer() {
        mDatabase.child("Faqs").child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    FaqsModel model = dataSnapshot.getValue(FaqsModel.class);
                    if (model != null) {
                        faqBy.setText("Faq by: " + model.getName());
                        time.setText(CommonUtils.getFormattedDate(model.getTime()));
                        title.setText(model.getTitle());
                        message.setText(model.getMessage());
                    }
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
