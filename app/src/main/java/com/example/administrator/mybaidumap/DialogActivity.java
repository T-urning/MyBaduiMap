package com.example.administrator.mybaidumap;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class DialogActivity extends AppCompatActivity {
    private  TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialog);
        textView = (TextView) findViewById(R.id.point_data_text);
        Intent intent = getIntent();
        String pointDate = intent.getStringExtra("pointDate");
        textView.setText("该点记录时间为：\n" + pointDate);
    }
}
