package com.appsinventiv.connectedboard.ui.Admin;

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

import com.appsinventiv.connectedboard.R;
import com.appsinventiv.connectedboard.Utils.CommonUtils;
import com.appsinventiv.connectedboard.Utils.SharedPrefs;
import com.appsinventiv.connectedboard.ui.MainActivity;
import com.appsinventiv.connectedboard.ui.UserManagement.LoginActivity;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import androidx.appcompat.app.AppCompatActivity;

public class AddDepartment extends AppCompatActivity {

    Button update;
    EditText department;
    DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_department);
        department = findViewById(R.id.department);
        update = findViewById(R.id.update);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        this.setTitle("Add Department");


        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (department.getText().length() == 0) {
                    department.setError("Enter department name");
                } else {
                    updateData();
                }
            }
        });


    }

    private void updateData() {
        mDatabase.child("Departments").child(department.getText().toString()).setValue(department.getText().toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                CommonUtils.showToast("Department Added");
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
