package com.example.welink;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

public class Splashscreen extends AppCompatActivity {

    ImageView imageView;
    TextView nameTV, name2TV;
    long animTime = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splashscreen);

        imageView = findViewById(R.id.iv_logo_splash);
        nameTV = findViewById(R.id.tv_splash_name);
        name2TV = findViewById(R.id.tv_splash_name2);
        ObjectAnimator animatorY = ObjectAnimator.ofFloat(imageView,"y",800f);
        ObjectAnimator animatorName = ObjectAnimator.ofFloat(nameTV,"x",400f);
        animatorY.setDuration(animTime);
        animatorName.setDuration(animTime);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(animatorY,animatorName);
        animatorSet.start();

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(Splashscreen.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        },4000);

    }
}