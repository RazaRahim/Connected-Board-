package com.appsinventiv.connectedboard.ui;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.MediaController;
import android.widget.VideoView;

import com.appsinventiv.connectedboard.R;
import com.appsinventiv.connectedboard.Utils.SharedPrefs;
import com.appsinventiv.connectedboard.ui.Admin.AdminActivity;
import com.appsinventiv.connectedboard.ui.UserManagement.LoginActivity;

import androidx.appcompat.app.AppCompatActivity;

public class PlayVideo extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_video);

        String LINK = getIntent().getStringExtra("videoUrl");
        VideoView videoView = (VideoView) findViewById(R.id.video);
        MediaController mc = new MediaController(this);
        mc.setAnchorView(videoView);
        mc.setMediaPlayer(videoView);
        Uri video = Uri.parse(LINK);
        videoView.setMediaController(mc);
        videoView.setVideoURI(video);
        videoView.start();


    }


}
