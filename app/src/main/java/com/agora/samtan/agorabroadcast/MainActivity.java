package com.agora.samtan.agorabroadcast;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import io.agora.rtc.Constants;

public class MainActivity extends AppCompatActivity {

    Animation fadein,move,blink,zoomin;
    TextView tv,tv2;
    RadioButton h,a;
    RadioGroup rg;


    int channelProfile;
    public static final String channelMessage = "com.agora.samtan.agorabroadcast.CHANNEL";
    public static final String profileMessage = "com.agora.samtan.agorabroadcast.PROFILE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv=findViewById(R.id.textview);
        tv2=findViewById(R.id.textview2);
        fadein= AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fade_in);
        move= AnimationUtils.loadAnimation(getApplicationContext(),R.anim.move);
        blink= AnimationUtils.loadAnimation(getApplicationContext(),R.anim.blink);
        zoomin= AnimationUtils.loadAnimation(getApplicationContext(),R.anim.zoom_in);
        tv.setAnimation(fadein);
        tv2.setAnimation(blink);
        h=findViewById(R.id.host);
        a=findViewById(R.id.audience);
        rg=findViewById(R.id.radio);


    }

    public void onRadioButtonClicked(View view) {
        boolean checked = ((RadioButton) view).isChecked();
        switch (view.getId()) {
            case R.id.host:
                if (checked) {
                    h.setAnimation(blink);
                    a.setAnimation(move);
                    channelProfile = Constants.CLIENT_ROLE_BROADCASTER;
                }
                break;
            case R.id.audience:
                if (checked) {
                    a.setAnimation(blink);
                    h.setAnimation(move);
                    channelProfile = Constants.CLIENT_ROLE_AUDIENCE;
                }
                break;
        }
    }

    public void onSubmit(View view) {
        a.clearAnimation();
        h.clearAnimation();
        EditText channel = (EditText) findViewById(R.id.channel);
        String channelName = channel.getText().toString();
        Intent intent = new Intent(this, VideoActivity.class);
        intent.putExtra(channelMessage, channelName);
        intent.putExtra(profileMessage, channelProfile);
        startActivity(intent);
    }
}
