package com.razarahim.connectedboard;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.messaging.FirebaseMessaging;
import com.razarahim.connectedboard.ui.MainActivity;
import com.razarahim.connectedboard.ui.UserManagement.LoginActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class otp extends AppCompatActivity {

    String verificationCodeBySystem;
    Button verify_btn;
    EditText phoneNoEnteredByTheUser;
    ProgressBar progressBar;
    final String URL = "https://fcm.googleapis.com/fcm/send";
    public RequestQueue mRequestQue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);


        verify_btn = findViewById(R.id.verify_btn);
        phoneNoEnteredByTheUser = findViewById(R.id.verification_code_entered_by_user);
        progressBar = findViewById(R.id.progress_bar);

        String phoneNo = getIntent().getStringExtra("PhoneNo");
        sendVerificationCodeToUser(phoneNo);


        verify_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendNotification();
                String code = phoneNoEnteredByTheUser.getText().toString();

                if (code.isEmpty() || code.length() < 6) {
                    phoneNoEnteredByTheUser.setError("Wrong OTP...");
                    phoneNoEnteredByTheUser.requestFocus();
                    return;
                }
                progressBar.setVisibility(View.VISIBLE);
                verifyCode(code);
            }
        });



        mRequestQue = Volley.newRequestQueue(this);
        FirebaseMessaging.getInstance().subscribeToTopic("demo");

    }

    private void sendVerificationCodeToUser(String phoneNo) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+92" + phoneNo,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                TaskExecutors.MAIN_THREAD,               // Activity (for callback binding)
                mCallbacks);        // OnVerificationStateChangedCallbacks
    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        @Override
        public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            verificationCodeBySystem = s;
        }

        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
            String code = phoneAuthCredential.getSmsCode();
            if (code != null) {
                progressBar.setVisibility(View.VISIBLE);
                verifyCode(code);
            }
        }


        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {
            Toast.makeText(otp.this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    };

    private void verifyCode(String codeByUser) {

        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationCodeBySystem, codeByUser);
        signInTheUserByCredentials(credential);

    }

    private void signInTheUserByCredentials(PhoneAuthCredential credential) {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(otp.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {

                            Toast.makeText(otp.this, "Your Account has been created successfully!", Toast.LENGTH_SHORT).show();

                            //Perform Your required action here to either let the user sign In or do something required
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);

                        } else {
                            Toast.makeText(otp.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });

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
