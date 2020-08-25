package com.appsinventiv.connectedboard.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.appsinventiv.connectedboard.Models.FaqsModel;
import com.appsinventiv.connectedboard.R;
import com.appsinventiv.connectedboard.Utils.CommonUtils;
import com.appsinventiv.connectedboard.Utils.SharedPrefs;
import com.appsinventiv.connectedboard.ui.Admin.AdminActivity;
import com.appsinventiv.connectedboard.ui.UserManagement.LoginActivity;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.googlecode.mp4parser.authoring.Edit;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class AddFaq extends AppCompatActivity {

    EditText message, title;
    Button send;
    DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_faq);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        this.setTitle("Faqs");
        send = findViewById(R.id.send);
        title = findViewById(R.id.title);
        message = findViewById(R.id.message);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (title.getText().length() == 0) {
                    title.setText("Enter title");
                } else if (message.getText().length() == 0) {
                    message.setError("Enter message");
                } else {
                    showAlert();
                }
            }

        });


    }

    private void showAlert() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Alert");
        builder.setMessage("Send message? ");

        // add the buttons
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String id = mDatabase.push().getKey();
                FaqsModel model = new FaqsModel(id, title.getText().toString(),
                        message.getText().toString(),
                        SharedPrefs.getLoggedInAs().equalsIgnoreCase("teacher") ? SharedPrefs.getTeacherModel().getName() : SharedPrefs.getStudentModel().getName(),
                        System.currentTimeMillis()
                );

                mDatabase.child("Faqs").child(id).setValue(model).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        CommonUtils.showToast("Message sent");
                        finish();
                    }
                });

            }
        });
        builder.setNegativeButton("Cancel", null);

        // create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();
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
