package com.agora.samtan.agorabroadcast;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class SplashScreen extends AppCompatActivity {
    Handler handler;
    ImageView iv;
    TextView tv;

Animation bounce,blink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splashfile);
iv=findViewById(R.id.logo_id);
tv=findViewById(R.id.tvsp);

bounce= AnimationUtils.loadAnimation(getApplicationContext(),R.anim.bounce);
blink= AnimationUtils.loadAnimation(getApplicationContext(),R.anim.blink);
iv.setAnimation(bounce);
tv.setAnimation(blink);

        handler=new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent=new Intent(SplashScreen.this,SplashScreen2.class);
                startActivity(intent);
                finish();
            }
        },4000);

    }
}

