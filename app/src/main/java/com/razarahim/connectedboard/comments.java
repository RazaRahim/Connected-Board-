package com.razarahim.connectedboard;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.razarahim.connectedboard.Adapters.commentAdapter;
import com.razarahim.connectedboard.Models.FaqsModel;
import com.razarahim.connectedboard.Models.PostModel;
import com.razarahim.connectedboard.Models.commentModel;
import com.razarahim.connectedboard.Utils.SharedPrefs;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;

public class comments extends AppCompatActivity {

    EditText commentText;
    ImageButton comnt_Btn;
    DatabaseReference userref,commentref;
    String postKey;
//    String userId;
    RecyclerView recyclerView;
    commentAdapter adapter;
    private ArrayList<commentModel> itemList = new ArrayList<>();
    final String userId = SharedPrefs.getStudentModel().getRollNumber();
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);

        recyclerView=findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
//        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL ,false));
        recyclerView.scrollToPosition(itemList.size() - 1);




        postKey = getIntent().getStringExtra("postKey");

        FirebaseRecyclerOptions<commentModel> options =
                new FirebaseRecyclerOptions.Builder<commentModel>()
                .setQuery(FirebaseDatabase.getInstance().getReference().child("Comments").child(postKey),commentModel.class)
                .build();
        Toast.makeText(this, ""+options, Toast.LENGTH_LONG).show();
        adapter = new commentAdapter(options);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();




        userref= FirebaseDatabase.getInstance().getReference().child("Students");
        commentref=FirebaseDatabase.getInstance().getReference().child("Comments").child(postKey);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//        userId = user.getUid();
        final String userId = SharedPrefs.getStudentModel().getRollNumber();
        commentText = findViewById(R.id.comments);
        comnt_Btn = findViewById(R.id.comnt_Btn);

        comnt_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userref.child(userId).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()) {
                            String username = snapshot.child("name").getValue().toString();
                            String uiImage = snapshot.child("picUrl").getValue().toString();
                            processcomment(username,uiImage);
                            commentText.setText("");

                            recyclerView.smoothScrollToPosition(recyclerView.getAdapter().getItemCount());


                        }

//                        Collections.sort(itemList, new Comparator<commentModel>() {
//                        @Override
//                        public int compare(commentModel listData, commentModel t1) {
//                            String ob1 = listData.getTime();
//                            String ob2 = t1.getTime();
//                            return ob2.compareTo(ob1);
//
//                        }
//                    });

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });

    }

    @Override
    public void onStart(){
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop(){
        super.onStop();
        adapter.stopListening();
    }



    private void processcomment(String username, String uiImage) {
        String commentPost = commentText.getText().toString();
        String randompostkey =DateFormat.getDateTimeInstance().format(new Date());

        Calendar dateValue = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-mm-yy");
        String cdate = dateFormat.format(dateValue.getTime());

        SimpleDateFormat timeformate = new SimpleDateFormat("HH:mm");
        String cTime = timeformate.format(dateValue.getTime());

        HashMap cmnt = new HashMap();
        cmnt.put("uid",userId);
        cmnt.put("username",username);
        cmnt.put("userImage",uiImage);
        cmnt.put("usermsg",commentPost);
        cmnt.put("date",cdate);
        cmnt.put("Time",cTime);

        commentref.child(randompostkey).updateChildren(cmnt)
                .addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if(task.isSuccessful())
                            Toast.makeText(comments.this, "Comment Added", Toast.LENGTH_SHORT).show();
                        else
                            Toast.makeText(comments.this,task.toString(), Toast.LENGTH_SHORT).show();
                    }

                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }


}