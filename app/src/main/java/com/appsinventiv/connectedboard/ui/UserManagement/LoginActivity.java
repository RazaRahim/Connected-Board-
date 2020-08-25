package com.appsinventiv.connectedboard.ui.UserManagement;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import com.appsinventiv.connectedboard.Models.StudentModel;
import com.appsinventiv.connectedboard.Models.TeacherModel;
import com.appsinventiv.connectedboard.R;
import com.appsinventiv.connectedboard.Utils.CommonUtils;
import com.appsinventiv.connectedboard.Utils.SharedPrefs;
import com.appsinventiv.connectedboard.ui.Admin.AdminActivity;
import com.appsinventiv.connectedboard.ui.MainActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    EditText username, password;
    RadioButton admin, student, teacher;
    Button login;
    String userType;
    TextView signup;
    HashMap<String, TeacherModel> teacherMap = new HashMap<>();
    HashMap<String, StudentModel> studentMap = new HashMap<>();
    DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        setContentView(R.layout.activity_login);
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        admin = findViewById(R.id.admin);
        student = findViewById(R.id.student);
        teacher = findViewById(R.id.teacher);
        signup = findViewById(R.id.signup);
        login = findViewById(R.id.login);


        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAlert();
            }
        });

        admin.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    userType = "admin";
                    username.setHint("Admin Id");

                }

            }
        });
        student.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    userType = "student";
                    username.setHint("Roll Number");

                }

            }
        });
        teacher.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    userType = "teacher";
                    username.setHint("Username");

                }

            }
        });


        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (userType == null) {
                    CommonUtils.showToast("Please select login type");
                } else if (username.getText().length() == 0) {
                    username.setError("Enter username");
                } else if (password.getText().length() == 0) {
                    password.setError("Enter password");
                } else {
                    checkLogin();
                }
            }
        });

        getTeachersFromServer();
        getStudentsDataFromServer();

    }

    private void showAlert() {
        final Dialog dialog = new Dialog(this);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View layout = layoutInflater.inflate(R.layout.alert_dialog_curved, null);

        dialog.setContentView(layout);

        TextView student = layout.findViewById(R.id.student);
        TextView teacher = layout.findViewById(R.id.teacher);

        student.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, StudentRegister.class));

            }
        });
        teacher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, TeacherRegister.class));

            }
        });


        dialog.show();

    }

    private void getTeachersFromServer() {
        mDatabase.child("Teachers").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        TeacherModel model = snapshot.getValue(TeacherModel.class);
                        if (model != null) {
                            teacherMap.put(model.getUsername(), model);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void getStudentsDataFromServer() {
        mDatabase.child("Students").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        StudentModel model = snapshot.getValue(StudentModel.class);
                        if (model != null) {
                            studentMap.put(model.getRollNumber(), model);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void checkLogin() {
        if (userType.equals("student")) {

            if (studentMap.containsKey(username.getText().toString())) {
                StudentModel studentModel = studentMap.get(username.getText().toString());
                if (studentModel.getPassword().equals(password.getText().toString())) {
                    CommonUtils.showToast("Successfully Logged in");
                    SharedPrefs.setStudentModel(studentModel);
                    SharedPrefs.setLoggedInAs("student");
                    SharedPrefs.setDepartment(studentModel.getDepartment());

                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    finish();
                } else {
                    CommonUtils.showToast("Wrong password");
                }

            } else {
                CommonUtils.showToast("Account does not exists");
            }


        } else if (userType.equals("teacher")) {
            if (teacherMap.containsKey(username.getText().toString())) {
                TeacherModel teacherModel = teacherMap.get(username.getText().toString());
                if (teacherModel.getPassword().equals(password.getText().toString())) {
                    CommonUtils.showToast("Successfully Logged in");
                    SharedPrefs.setTeacherModel(teacherModel);
                    SharedPrefs.setDepartment(teacherModel.getDepartment());
                    SharedPrefs.setLoggedInAs("teacher");
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    finish();
                } else {
                    CommonUtils.showToast("Wrong password");
                }

            } else {
                CommonUtils.showToast("Account does not exists");
            }


        } else if (userType.equals("admin")) {
            if (username.getText().toString().equalsIgnoreCase("admin") &&
                    password.getText().toString().equalsIgnoreCase("admin123")) {
                //loginasadmin
                SharedPrefs.setLoggedInAs("admin");

                startActivity(new Intent(LoginActivity.this, AdminActivity.class));
                finish();
            } else {
                CommonUtils.showToast("Wrong username or password");
            }
        }
    }

}
