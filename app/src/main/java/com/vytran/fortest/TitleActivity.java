package com.vytran.fortest;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class TitleActivity extends AppCompatActivity {

    private Handler handler = new Handler();
    boolean quit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_title);

        quit = false;

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!quit) {
                    Intent intent = new Intent(TitleActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        }, 5000);
    }

    @Override
    public void onBackPressed() {
        quit = true;
        super.onBackPressed();
    }
}
