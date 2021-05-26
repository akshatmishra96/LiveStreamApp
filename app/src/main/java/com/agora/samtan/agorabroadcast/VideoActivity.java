package com.agora.samtan.agorabroadcast;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.agora.samtan.agorabroadcast.activity.LoginActivity;
import com.agora.samtan.agorabroadcast.activity.MessageActivity;

import java.util.ArrayList;
import java.util.List;

import io.agora.rtc.Constants;
import io.agora.rtc.IRtcEngineEventHandler;
import io.agora.rtc.RtcEngine;
import io.agora.rtc.video.VideoCanvas;
import io.agora.rtc.video.VideoEncoderConfiguration;

public class VideoActivity extends AppCompatActivity {
    private static final String STATE_IN_PERMISSION_REQUEST = "in_permission_request";
    private boolean mInPermissionRequest = false;
    private static final int START_PERMISSIONS_CODE = 2;
    private RtcEngine mRtcEngine;
    private String channelName;
    private int channelProfile;
    public static final String LOGIN_MESSAGE = "com.agora.samtan.agorabroadcast.CHANNEL_LOGIN";
    private IRtcEngineEventHandler mRtcEventHandler = new IRtcEngineEventHandler() {

        @Override
        public void onFirstRemoteVideoDecoded(final int uid, int width, int height, int elapsed) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    setupRemoteVideo(uid);
                }
            });
        }

        @Override
        public void onUserOffline(int uid, int reason) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    onRemoteUserLeft();
                }
            });
        }

        @Override
        public void onUserMuteVideo(final int uid, final boolean muted) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    onRemoteUserVideoMuted(uid, muted);
                }
            });
        }
    };

    public void onLocalAudioMuteClicked(View view) {
        ImageView iv = (ImageView) view;
        if (iv.isSelected()) {
            iv.setSelected(false);
            iv.clearColorFilter();
        } else {
            iv.setSelected(true);
            iv.setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.MULTIPLY);
        }

        mRtcEngine.muteLocalAudioStream(iv.isSelected());
    }

    private void requestPermissions(List<String> missingPermissions, int code) {
        mInPermissionRequest = true;
        String[] permissions = missingPermissions.toArray(new String[missingPermissions.size()]);
        try {
            getClass().getMethod("requestPermissions", String[].class, Integer.TYPE).invoke(this, permissions, code);
        } catch (Exception ignored) {}
    }
    private boolean hasPermission(String permission) {
        try {
            int result = (Integer) getClass().getMethod("checkSelfPermission", String.class).invoke(this, permission);
            return result == PackageManager.PERMISSION_GRANTED;
        } catch (Exception ignored) {}
        return true;
    }


    private void onRemoteUserVideoMuted(int uid, boolean muted) {
        FrameLayout container = (FrameLayout) findViewById(R.id.remote_video_view_container);

        SurfaceView surfaceView = (SurfaceView) container.getChildAt(0);


        Object tag = surfaceView.getTag();
        if (tag != null && (Integer) tag == uid) {
            surfaceView.setVisibility(muted ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mInPermissionRequest = savedInstanceState != null && savedInstanceState.getBoolean(STATE_IN_PERMISSION_REQUEST);
        setContentView(R.layout.activity_video);

        Intent intent = getIntent();
        channelName = intent.getStringExtra(MainActivity.channelMessage);
        channelProfile = intent.getIntExtra(MainActivity.profileMessage, -1);

        if (channelProfile == -1) {
            Log.e("TAG: ", "No profile");
        }

        initAgoraEngineAndJoinChannel();
        if (!mInPermissionRequest) {
            final List<String> missingPermissions = new ArrayList<>();
            if (!hasPermission(Manifest.permission.CAMERA))
                missingPermissions.add(Manifest.permission.CAMERA);
            if (!hasPermission(Manifest.permission.RECORD_AUDIO))
                missingPermissions.add(Manifest.permission.RECORD_AUDIO);
            if (!hasPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE))
                missingPermissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (missingPermissions.size() > 0)
                requestPermissions(missingPermissions, START_PERMISSIONS_CODE);
        }
    }

    public void onLocalVideoMuteClicked(View view) {
        ImageView iv = (ImageView) view;
        if (iv.isSelected()) {
            iv.setSelected(false);
            iv.clearColorFilter();
        } else {
            iv.setSelected(true);
            iv.setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.MULTIPLY);
        }

        mRtcEngine.muteLocalVideoStream(iv.isSelected());

        FrameLayout container = (FrameLayout) findViewById(R.id.local_video_view_container);
        SurfaceView surfaceView = (SurfaceView) container.getChildAt(0);
        surfaceView.setZOrderMediaOverlay(!iv.isSelected());
        surfaceView.setVisibility(iv.isSelected() ? View.GONE : View.VISIBLE);
    }

    private void setupRemoteVideo(int uid) {
        FrameLayout container = (FrameLayout) findViewById(R.id.remote_video_view_container);
//        if (container.getChildCount() > 1) {
//            return;
//        }

        SurfaceView surfaceView = RtcEngine.CreateRendererView(getBaseContext());
        container.addView(surfaceView);
        mRtcEngine.setupRemoteVideo(new VideoCanvas(surfaceView, VideoCanvas.RENDER_MODE_FIT, uid));
        if(channelProfile==Constants.CLIENT_ROLE_BROADCASTER)
        {
            container.setVisibility(View.GONE);
container.removeAllViews();
            surfaceView.setVisibility(View.GONE);
        }
    }

    private void onRemoteUserLeft() {
        FrameLayout container = (FrameLayout) findViewById(R.id.remote_video_view_container);
        container.removeAllViews();
    }

    private void initAgoraEngineAndJoinChannel() {
        initalizeAgoraEngine();
        mRtcEngine.setChannelProfile(Constants.CHANNEL_PROFILE_LIVE_BROADCASTING);
        mRtcEngine.setClientRole(channelProfile);
        setupVideoProfile();
        setupLocalVideo();
        joinChannel();

    }

    private void initalizeAgoraEngine() {
        try {
            mRtcEngine = RtcEngine.create(getBaseContext(), getString(R.string.private_app_id), mRtcEventHandler);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setupVideoProfile() {
        mRtcEngine.enableVideo();

        mRtcEngine.setVideoEncoderConfiguration(new VideoEncoderConfiguration(VideoEncoderConfiguration.VD_640x480, VideoEncoderConfiguration.FRAME_RATE.FRAME_RATE_FPS_15,
                VideoEncoderConfiguration.STANDARD_BITRATE,
                VideoEncoderConfiguration.ORIENTATION_MODE.ORIENTATION_MODE_FIXED_PORTRAIT));
    }

    private void setupLocalVideo() {
        FrameLayout container = (FrameLayout) findViewById(R.id.local_video_view_container);
        SurfaceView surfaceView = RtcEngine.CreateRendererView(getBaseContext());
        surfaceView.setZOrderMediaOverlay(true);
        container.addView(surfaceView);
        mRtcEngine.setupLocalVideo(new VideoCanvas(surfaceView, VideoCanvas.RENDER_MODE_FIT, 0));
    if(channelProfile==Constants.CLIENT_ROLE_AUDIENCE)
    {

        container.setVisibility(View.GONE);
        surfaceView.setVisibility(View.GONE);
    }
    }

    private void joinChannel() {
        mRtcEngine.joinChannel(null, channelName, "Optional Data", 0);
    }

    private void leaveChannel() {
        mRtcEngine.leaveChannel();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        leaveChannel();
        RtcEngine.destroy();
        mRtcEngine = null;
    }

    public void onSwitchCameraClicked(View view) {
        mRtcEngine.switchCamera();
    }

    public void onEndCallClicked(View view) {
        finish();
    }

    public void onChatClicked(View view) {
        Intent loginIntent = new Intent(this, LoginActivity.class);
        loginIntent.putExtra(LOGIN_MESSAGE, channelName);
        startActivity(loginIntent);
        
    }
}
