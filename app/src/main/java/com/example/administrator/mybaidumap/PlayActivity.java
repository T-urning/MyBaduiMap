package com.example.administrator.mybaidumap;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.MediaController;
import android.widget.VideoView;

public class PlayActivity extends Activity {
    private VideoView videoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);
        Intent intent = getIntent();
        String path = intent.getStringExtra("filename");
        videoView = (VideoView) findViewById(R.id.video_view);
        videoView.setVideoPath(path);
        videoView.setMediaController(new MediaController(this));
        videoView.start();
    }
}
