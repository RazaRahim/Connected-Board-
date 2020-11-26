package com.razarahim.connectedboard.ui.Admin;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.razarahim.connectedboard.Models.PostModel;
import com.razarahim.connectedboard.R;
import com.razarahim.connectedboard.Utils.CommonUtils;
import com.razarahim.connectedboard.Utils.CompressImage;
import com.razarahim.connectedboard.Utils.CompressImageToThumbnail;
import com.razarahim.connectedboard.Utils.GifSizeFilter;
import com.razarahim.connectedboard.Utils.Glide4Engine;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.iceteck.silicompressorr.SiliCompressor;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.filter.Filter;

import java.io.File;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;

import org.json.JSONException;
import org.json.JSONObject;

public class AddPost extends AppCompatActivity {


    CardView document, picture;
    ImageView picChosen;
    TextView fileChosen;
    EditText description;
    String likes;
    Button post;
    RelativeLayout wholeLayout;
    private static final int REQUEST_CODE_CHOOSE = 23;

    private List<Uri> mSelected = new ArrayList<>();
    StorageReference mStorageRef;

    DatabaseReference mDatabase;
    private String liveUrl;
    private String compressedPath;
    private String finalVideoPath;
    private boolean videoCompressed;
    private Uri videoPath;
    String fileChosenType;
    private static final int REQUEST_CODE_FILE = 25;
    private Uri Fpath;
    private ArrayList<String> departmentList = new ArrayList<>();
    private String departmentChosen;
    final String URL = "https://fcm.googleapis.com/fcm/send";
    public RequestQueue mRequestQue;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        getPermissions();
        this.setTitle("Add Post");

        mDatabase = FirebaseDatabase.getInstance().getReference();

        mStorageRef = FirebaseStorage.getInstance().getReference();
        document = findViewById(R.id.document);
        picture = findViewById(R.id.picture);
        picChosen = findViewById(R.id.picChosen);
        wholeLayout = findViewById(R.id.wholeLayout);
        fileChosen = findViewById(R.id.fileChosen);
        description = findViewById(R.id.description);
        post = findViewById(R.id.post);


        picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initMatisse();
            }
        });


        getDeparmentsFromDB();
        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPostAlert();
            }
        });

        document.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.setType("application/pdf");
                startActivityForResult(i, REQUEST_CODE_FILE);
            }
        });



        mRequestQue = Volley.newRequestQueue(this);
        FirebaseMessaging.getInstance().subscribeToTopic("demo");




    }

    private void showPostAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Alert");
        builder.setMessage("Post Now? ");

        // add the buttons
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (fileChosenType.equalsIgnoreCase("video")) {
                    if (videoCompressed) {
                        uploadFile(finalVideoPath);
                    } else {
                        CommonUtils.showToast("Compressing video");
                    }
                } else {
                    if (fileChosenType.equalsIgnoreCase("pdf")) {
                        uploadFile("" + Fpath);
                    } else {
                        uploadFile(compressedPath);
                    }
                }

            }
        });
        builder.setNegativeButton("Cancel", null);

        // create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }


    private void uploadFile(String path) {
        wholeLayout.setVisibility(View.VISIBLE);
        String imgName = Long.toHexString(Double.doubleToLongBits(Math.random()));

        Uri file;
        if (fileChosenType.equalsIgnoreCase("pdf")) {
            file = Uri.parse(path);

        } else {
            file = Uri.fromFile(new File(path));
        }


        StorageReference riversRef = mStorageRef.child(fileChosenType).child(imgName);

        riversRef.putFile(file)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    @SuppressWarnings("VisibleForTests")
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // Get a URL to the uploaded content
                        AddPost.this.sendNotification();
                        taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                liveUrl = uri.toString();
                                postNow(fileChosenType);
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

    private void postNow(final String type) {
        final String id = mDatabase.push().getKey();

//        String savecurrenttime, savecurrentdate;
//        Calendar calendar = Calendar.getInstance();
//        @SuppressLint("SimpleDateFormat") SimpleDateFormat currentdate= new SimpleDateFormat("MMM dd ");
//        savecurrentdate = currentdate.format(calendar.getTime());
//        @SuppressLint("SimpleDateFormat") SimpleDateFormat currenttime= new SimpleDateFormat("hh:mm a");
//        savecurrenttime = currenttime.format(calendar.getTime());
//
//        long finallytime = Integer.parseInt(savecurrenttime+"  "+currentdate);

        PostModel model = new PostModel(id, description.getText().toString(), liveUrl, type, System.currentTimeMillis(),
                departmentChosen,likes);
        mDatabase.child("Posts").child(id).setValue(model).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                if (type.equalsIgnoreCase("video")) {
                    CompressImageToThumbnail compressImage = new CompressImageToThumbnail(AddPost.this);
                    putVideoPicture(compressImage.compressImage("" + CommonUtils.getVideoPic("" + liveUrl)), id);
                } else {
                    CommonUtils.showToast("Successfully posted");
                    finish();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });

    }

    private void putVideoPicture(String path, final String postId) {
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
                                HashMap<String, Object> map = new HashMap<>();
                                map.put("videoImgUrl", uri.toString());
                                mDatabase.child("Posts").child(postId).updateChildren(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        CommonUtils.showToast("Successfully posted");
                                        finish();
                                    }
                                });
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

    private void getDeparmentsFromDB() {
        mDatabase.child("Departments").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    departmentList.clear();
                    departmentList.add("All");
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

    private void initMatisse() {
        Matisse.from(this)
                .choose(MimeType.ofAll())
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_CHOOSE && resultCode == RESULT_OK) {
            mSelected.clear();
            mSelected = Matisse.obtainResult(data);

            CompressImage compressImage = new CompressImage(AddPost.this);
            if (mSelected.get(0).toString().contains("video")) {
                videoPath = mSelected.get(0);
                initVideo();
                fileChosen.setText("Video: " + videoPath);
                fileChosenType = "video";
                picChosen.setVisibility(View.VISIBLE);
                Glide.with(this).load(Uri.parse(CommonUtils.getRealPathFromURI(videoPath)).getPath()).into(picChosen);

            } else {
                fileChosen.setText("Image: " + mSelected.get(0));

                fileChosenType = "image";
                Glide.with(AddPost.this).load(mSelected.get(0)).into(picChosen);
                compressedPath = compressImage.compressImage("" + mSelected.get(0));
                picChosen.setVisibility(View.VISIBLE);
            }


        } else if (requestCode == REQUEST_CODE_FILE && data != null) {
            Fpath = data.getData();
            fileChosen.setText("File: " + Fpath);
            fileChosenType = "pdf";


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

    private void initVideo() {
        try {

            File f = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES) + "/Silicompressor/videos");
            if (f.mkdirs() || f.isDirectory())

                new VideoCompressAsyncTask(this).execute(CommonUtils.getRealPathFromURI(videoPath), f.getPath());
        } catch (Exception e) {
            e.printStackTrace();
        }

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

    class VideoCompressAsyncTask extends AsyncTask<String, String, String> {

        Context mContext;

        public VideoCompressAsyncTask(Context context) {
            mContext = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected String doInBackground(String... paths) {
            String filePath = null;
            try {
                MediaMetadataRetriever mmr = new MediaMetadataRetriever();
                mmr.setDataSource(paths[0]);
                String width = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH);
                String height = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT);
                filePath = SiliCompressor.with(mContext).compressVideo(paths[0], paths[1],
                        Integer.parseInt(width), Integer.parseInt(height), 450000);

            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
            return filePath;

        }


        @Override
        protected void onPostExecute(String compressedFilePath) {
            super.onPostExecute(compressedFilePath);
            File imageFile = new File(compressedFilePath);
            float length = imageFile.length() / 1024f; // Size in KB
            String value;
            if (length >= 1024)
                value = length / 1024f + " MB";
            else
                value = length + " KB";
            String text = String.format(Locale.US, "%s\nName: %s\nSize: %s", getString(R.string.video_compression_complete), imageFile.getName(), value);

            finalVideoPath = compressedFilePath;
            videoCompressed = true;
            Log.i("Silicompressor", "Path: " + compressedFilePath);
        }
    }






    private void sendNotification() {

        JSONObject json = new JSONObject();
        try {
            json.put("to", "/topics/" + "demo");
            JSONObject notificationObj = new JSONObject();
            notificationObj.put("title", "Notice For You.");
            notificationObj.put("body", "click to check it out!");


            json.put("notification", notificationObj);


            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, URL,
                    json,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.d("MUR", "onResponse: ");
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d("MUR", "onError: " + error.networkResponse);
                }
            }
            ) {
                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> header = new HashMap<>();
                    header.put("content-type", "application/json");
                    header.put("authorization", "key=AAAAehQekhg:APA91bEq73P_Tcm4WN89lGACc41PKWQIKkNgdjKbWFAjPm07DDKhj9JXo204NcaMdHTJdL6fquF6UXr7VBtN80gb-AL7mTIrig1yAaBG3cJGQa60_w53QI1hQgbzQdecLemeLFEtKuUs");
                    return header;
                }
            };
            mRequestQue.add(request);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }




}
