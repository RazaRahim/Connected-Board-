package com.appsinventiv.connectedboard.ui.UserManagement;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.appsinventiv.connectedboard.Models.StudentModel;
import com.appsinventiv.connectedboard.Models.TeacherModel;
import com.appsinventiv.connectedboard.R;
import com.appsinventiv.connectedboard.Utils.CommonUtils;
import com.appsinventiv.connectedboard.Utils.CompressImage;
import com.appsinventiv.connectedboard.Utils.GifSizeFilter;
import com.appsinventiv.connectedboard.Utils.Glide4Engine;
import com.appsinventiv.connectedboard.Utils.SharedPrefs;
import com.appsinventiv.connectedboard.ui.MainActivity;
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
import java.util.concurrent.atomic.AtomicBoolean;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import de.hdodenhof.circleimageview.CircleImageView;

public class StudentRegister extends AppCompatActivity {


    CircleImageView image;
    EditText name, username, password, whichClass, phone;
    String gender = "Male";
    RadioButton male, female;
    Button register;
    TextView login;

    DatabaseReference mDatabase;
    private HashMap<String, StudentModel> studentMap = new HashMap<>();
    String imgUrl;
    StorageReference mStorageRef;
    private static final int REQUEST_CODE_CHOOSE = 23;
    private String compressedPath;

    RelativeLayout wholeLayout;
    private List<Uri> mSelected = new ArrayList<>();
    private ArrayList<String> departmentList = new ArrayList<>();
    private String departmentChosen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_register);
        getPermissions();

        this.setTitle("Student register");
        image = findViewById(R.id.image);
        name = findViewById(R.id.name);
        phone = findViewById(R.id.phone);
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        whichClass = findViewById(R.id.whichClass);
        male = findViewById(R.id.male);
        female = findViewById(R.id.female);
        register = findViewById(R.id.register);
        login = findViewById(R.id.login);
        wholeLayout = findViewById(R.id.wholeLayout);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        mStorageRef = FirebaseStorage.getInstance().getReference();

        getStudentsFromDb();
        getDeparmentsFromDB();


        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (name.getText().length() == 0) {
                    name.setError("Enter text");
                } else if (username.getText().length() == 0) {
                    username.setError("Enter text");
                } else if (password.getText().length() == 0) {
                    password.setError("Enter text");
                } else if (whichClass.getText().length() == 0) {
                    whichClass.setError("Enter text");
                } else if (mSelected.size() == 0) {
                    CommonUtils.showToast("Select image");
                } else {
                    checkUser();
                }

            }
        });

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initMatisse();
            }
        });


        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        male.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (buttonView.isPressed()) {
                    if (isChecked) {
                        gender = "male";
                    }
                }
            }
        });
        female.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (buttonView.isPressed()) {
                    if (isChecked) {
                        gender = "female";
                    }
                }
            }
        });


    }

    private void getDeparmentsFromDB() {
        mDatabase.child("Departments").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String dep = snapshot.getValue(String.class);
                        if (dep != null) {
                            departmentList.add(dep);
                        }
                    }
                    setupSpinner();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void setupSpinner() {

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, departmentList);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spinner spinner = findViewById(R.id.spinner);
        spinner.setAdapter(dataAdapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                departmentChosen = departmentList.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    private void checkUser() {
        if (studentMap.containsKey(username.getText().toString())) {
            CommonUtils.showToast("Username already taken\nPlease login");
        } else {
            if (mSelected.size() > 0) {
                putPictures(compressedPath);
            } else {
                registerUser();
            }

        }
    }

    private void initMatisse() {
        Matisse.from(StudentRegister.this)
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

    private void getStudentsFromDb() {
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_CHOOSE && resultCode == RESULT_OK) {
            mSelected = Matisse.obtainResult(data);

            CompressImage compressImage = new CompressImage(StudentRegister.this);
            Glide.with(StudentRegister.this).load(mSelected.get(0)).into(image);
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
                                registerUser();
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

    private void registerUser() {

        final StudentModel model = new StudentModel(
                name.getText().toString(),
                username.getText().toString(),
                whichClass.getText().toString(),
                password.getText().toString(),
                gender,
                imgUrl, departmentChosen, phone.getText().toString()
        );
        mDatabase.child("Students").child(username.getText().toString()).setValue(model).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                SharedPrefs.setStudentModel(model);
                wholeLayout.setVisibility(View.GONE);
                CommonUtils.showToast("Successfully registered");
                SharedPrefs.setLoggedInAs("student");
                SharedPrefs.setDepartment(departmentChosen);
                startTeacherActivity();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                CommonUtils.showToast(e.getMessage());
            }
        });
    }

    private void startTeacherActivity() {
        startActivity(new Intent(StudentRegister.this, MainActivity.class));
        finish();

    }


}
