package com.example.hospital;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;


public class SplashActivity extends AppCompatActivity {

    ImageView img;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);


//        img = findViewById(R.id.img);
//        Glide.with(this)
//                .asGif()
//                .override(1000, 1000) // Load a lower resolution version of the GIF
//                .load(R.drawable.hospital_splash)
//                .into(img);
        //mover
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashActivity.this, LanguageActivity.class);
                startActivity(intent);
                finish();
            }
        }, 2000);


    }
}
