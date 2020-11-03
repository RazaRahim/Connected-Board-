package com.razarahim.connectedboard.ui.UserManagement;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RelativeLayout;

import com.razarahim.connectedboard.Models.StudentModel;
import com.razarahim.connectedboard.Models.TeacherModel;
import com.razarahim.connectedboard.R;
import com.razarahim.connectedboard.Utils.CommonUtils;
import com.razarahim.connectedboard.Utils.CompressImage;
import com.razarahim.connectedboard.Utils.GifSizeFilter;
import com.razarahim.connectedboard.Utils.Glide4Engine;
import com.razarahim.connectedboard.Utils.SharedPrefs;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.filter.Filter;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import de.hdodenhof.circleimageview.CircleImageView;

public class TeacherEdit extends AppCompatActivity {


    CircleImageView image;
    EditText name, username, password, phone;
    String gender = "Male";
    RadioButton male, female;
    Button register;

    DatabaseReference mDatabase;
    String imgUrl;
    StorageReference mStorageRef;
    private static final int REQUEST_CODE_CHOOSE = 23;
    private String compressedPath;

    RelativeLayout wholeLayout;
    private List<Uri> mSelected = new ArrayList<>();
    private TeacherModel model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_edit);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        getPermissions();

        this.setTitle("Teacher Profile");
        image = findViewById(R.id.image);
        name = findViewById(R.id.name);
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        phone = findViewById(R.id.phone);
        male = findViewById(R.id.male);
        female = findViewById(R.id.female);
        register = findViewById(R.id.register);
        wholeLayout = findViewById(R.id.wholeLayout);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        mStorageRef = FirebaseStorage.getInstance().getReference();

        getStudentFromDb();


        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (name.getText().length() == 0) {
                    name.setError("Enter text");
                } else if (username.getText().length() == 0) {
                    username.setError("Enter text");
                } else if (password.getText().length() == 0) {
                    password.setError("Enter text");
                } else if (phone.getText().length() == 0) {
                    phone.setError("Enter text");
                } else {
                    if (mSelected.size() > 0) {
                        putPictures(compressedPath);
                    } else {
                        updateProfile();
                    }

                }

            }
        });

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initMatisse();
            }
        });


        male.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (buttonView.isPressed()) {
                    if (isChecked) {
                        gender = "Male";
                    }
                }
            }
        });
        female.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (buttonView.isPressed()) {
                    if (isChecked) {
                        gender = "Female";
                    }
                }
            }
        });
    }

    private void updateProfile() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("name", name.getText().toString());
        map.put("phone", phone.getText().toString());
        map.put("username", username.getText().toString());
        map.put("picUrl", imgUrl);
        map.put("password", password.getText().toString());
        map.put("gender", gender);
        mDatabase.child("Teachers").child(model.getUsername()).updateChildren(map).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                CommonUtils.showToast("Updated");
                wholeLayout.setVisibility(View.GONE);

            }
        });

    }


    private void initMatisse() {
        Matisse.from(TeacherEdit.this)
                .choose(MimeType.ofImage())
                .countable(true)
                .maxSelectable(1)
                .showSingleMediaType(true)
                .addFilter(new GifSizeFilter(320, 320, 5 * Filter.K * Filter.K))
                .gridExpectedSize(getResources().getDimensionPixelSize(R.dimen.grid_expected_size))
                .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
                .thumbnailScale(0.85f)
                .imageEngine(new Glide4Engine())
                .forResult(REQUEST_CODE_CHOOSE);
    }

    private void getStudentFromDb() {
        mDatabase.child("Teachers").child(SharedPrefs.getTeacherModel().getUsername()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    model = dataSnapshot.getValue(TeacherModel.class);
                    if (model != null) {
                        name.setText(model.getName());
                        username.setEnabled(false);
                        username.setText(model.getUsername());
                        phone.setText(model.getPhone());
                        password.setText(model.getPassword());
                        if (model.getGender().equalsIgnoreCase("male")) {
                            gender = "Male";
                            male.setChecked(true);
                        } else {
                            gender = "Female";
                            female.setChecked(true);
                        }

                        imgUrl = model.getPicUrl();
                        Glide.with(TeacherEdit.this).load(model.getPicUrl()).into(image);
                        SharedPrefs.setTeacherModel(model);
                    }
                }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_CHOOSE && resultCode == RESULT_OK) {
            mSelected = Matisse.obtainResult(data);

            CompressImage compressImage = new CompressImage(TeacherEdit.this);
            Glide.with(TeacherEdit.this).load(mSelected.get(0)).into(image);
//            try {
            compressedPath = compressImage.compressImage("" + mSelected.get(0));
//
//            } catch (Exception e) {
//                CommonUtils.showToast(e.getMessage());
//            }


        }

    }

    private void getPermissions() {
        int PERMISSION_ALL = 1;
        String[] PERMISSIONS = {
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,


        };

        if (!hasPermissions(this, PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        } else {
        }
    }


    public boolean hasPermissions(Context context, String... permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                } else {

                }
            }
        }
        return true;
    }

    private void putPictures(String path) {
        wholeLayout.setVisibility(View.VISIBLE);
        String imgName = Long.toHexString(Double.doubleToLongBits(Math.random()));


        Uri file = Uri.fromFile(new File(path));


        StorageReference riversRef = mStorageRef.child("Photos").child(imgName);

        riversRef.putFile(file)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    @SuppressWarnings("VisibleForTests")
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // Get a URL to the uploaded content

                        taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                imgUrl = uri.toString();
                                updateProfile();
                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                        // ...
                        CommonUtils.showToast(exception.getMessage());
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
