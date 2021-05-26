package com.agora.samtan.agorabroadcast;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class SplashScreen2 extends AppCompatActivity {
    Button btn2;

    Animation slidedown,fadein;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splashfile2);

slidedown= AnimationUtils.loadAnimation(getApplicationContext(),R.anim.slide_down);

fadein= AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fade_in);



        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            private boolean useDiceOne;


            @Override
            public void run() {
                ImageView image = findViewById(R.id.logo_id);
                image.setAnimation(fadein);
                if (!useDiceOne) {
                    image.setImageResource(R.drawable.welcome);
                } else {
                    image.setImageResource(R.drawable.start);
                }
                useDiceOne = !useDiceOne;
                handler.postDelayed(this, 2000);

            }
        }, 500);


        btn2=findViewById(R.id.btn2);

        btn2.setAnimation(slidedown);


        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SplashScreen2.this,MainActivity.class));
            }
        });
    }
}
