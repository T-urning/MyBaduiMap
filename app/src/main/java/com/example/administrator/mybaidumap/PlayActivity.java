package com.example.administrator.mybaidumap;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.MediaController;
import android.widget.VideoView;

public class PlayActivity extends Activity {
    private VideoView videoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);
        Intent intent = getIntent();
        String path = intent.getStringExtra("fileName");
        videoView = (VideoView) findViewById(R.id.video_view);
        videoView.setVideoPath(path);
        videoView.setMediaController(new MediaController(this));
        //videoView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        videoView.start();
    }


    public static void actionStart(Context context, String savePath){
        Intent intent = new Intent(context, PlayActivity.class);
        intent.putExtra("fileName", savePath);
        context.startActivity(intent);
    }
}
