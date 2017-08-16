package com.example.mytest;

import android.app.Activity;
import android.media.MediaPlayer;
import android.os.Environment;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

public class MainActivity extends Activity {

    private VideoView videoView;
    private String path =Environment.getExternalStorageDirectory()+"/DCIM/Camera/main.mp4";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        videoView = (VideoView) findViewById(R.id.video_view);
        videoView.setVideoPath(path);
        videoView.setMediaController(new MediaController(this));
        videoView.start();

    }
}