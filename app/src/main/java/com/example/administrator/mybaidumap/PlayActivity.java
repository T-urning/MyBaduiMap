package com.example.administrator.mybaidumap;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBar;
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
        videoView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN |
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE );
        videoView.start();

        if (getActionBar() != null ) {
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }

    }


    public static void actionStart(Context context, String savePath){
        Intent intent = new Intent(context, PlayActivity.class);
        intent.putExtra("fileName", savePath);
        context.startActivity(intent);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        if(getActionBar() != null &&getActionBar().isShowing()){
            videoView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN |
                    View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE );
            //getActionBar().hide();
        }
    }
}
