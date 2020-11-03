package com.razarahim.connectedboard.ui.Admin;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.razarahim.connectedboard.Adapters.PostsAdapter;
import com.razarahim.connectedboard.Models.PostModel;
import com.razarahim.connectedboard.R;
import com.razarahim.connectedboard.Utils.CommonUtils;
import com.razarahim.connectedboard.Utils.SharedPrefs;
import com.razarahim.connectedboard.ui.DownloadFile;
import com.razarahim.connectedboard.ui.ListOfFaqs;
import com.razarahim.connectedboard.ui.MainActivity;
import com.razarahim.connectedboard.ui.PlayVideo;
import com.razarahim.connectedboard.ui.Splash;
import com.razarahim.connectedboard.ui.UserManagement.LoginActivity;
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
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class AdminActivity extends AppCompatActivity {


    CardView posts, departments, faqs;
    Button logout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

//        teachers = findViewById(R.id.teachers);
//        students = findViewById(R.id.students);
        logout = findViewById(R.id.logout);
        faqs = findViewById(R.id.faqs);
        departments = findViewById(R.id.departments);
        posts = findViewById(R.id.posts);

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPrefs.logout();
                Intent i = new Intent(AdminActivity.this, Splash.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
                finish();

            }
        });

        posts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AdminActivity.this, AdminListOfPosts.class));
            }
        });
        faqs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AdminActivity.this, ListOfFaqs.class));
            }
        });

        departments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AdminActivity.this, ListOfDepartments.class));

            }
        });


    }


}
