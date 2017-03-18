package com.example.camerademo.activity;

import android.os.Bundle;
import android.widget.Button;

import com.example.camerademo.R;
import com.example.camerademo.rx.Rx;

public class MainActivity extends BaseActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    Button btnBack;
    Button btnFront;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnBack = (Button) findViewById(R.id.btn_back);
        btnFront = (Button) findViewById(R.id.btn_front);

        Rx.clicks(btnBack, v -> startActivity(CameraActivity.getNewIntent(false)));
        Rx.clicks(btnFront, v -> startActivity(CameraActivity.getNewIntent(true)));

    }
}
