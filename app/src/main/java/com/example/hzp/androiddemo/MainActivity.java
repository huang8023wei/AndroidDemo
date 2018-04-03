package com.example.hzp.androiddemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private Button mScrollVerticalView;
    private Button mScrollHorizontalView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mScrollHorizontalView = findViewById(R.id.bt_horizontal);
        mScrollVerticalView = findViewById(R.id.bt_vertical);
        mScrollHorizontalView.setOnClickListener(this);
        mScrollVerticalView.setOnClickListener(this);

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.bt_horizontal:
                startActivity(new Intent(this,ScrollHorizontalActivity.class));
                break;
            case R.id.bt_vertical:
                startActivity(new Intent(this,ScrollerVerticalActivity.class));
                break;
        }
    }
}
