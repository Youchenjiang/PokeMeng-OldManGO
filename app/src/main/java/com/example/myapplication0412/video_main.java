package com.example.myapplication0412;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.MediaController;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

public class video_main extends AppCompatActivity {

    private VideoView videoView;
    private FrameLayout videoContainer;
    private Button exitButton;
    private Button fullscreenButton;

    private boolean isFullscreen = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_main);

        videoView = findViewById(R.id.videoView);
        videoContainer = findViewById(R.id.aspect_ratio_frame);
        exitButton = findViewById(R.id.exitButton);
        fullscreenButton = findViewById(R.id.fullscreenButton);

        ImageButton imageButton1 = findViewById(R.id.imageButton1);
        ImageButton imageButton2 = findViewById(R.id.imageButton2);
        ImageButton imageButton3 = findViewById(R.id.imageButton3);

        imageButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playVideo(R.raw.sport01);
            }
        });

        imageButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playVideo(R.raw.sport02);
            }
        });

        imageButton3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playVideo(R.raw.sport03);
            }
        });

        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exitVideo();
            }
        });

        fullscreenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleFullscreen();
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("isFullscreen", isFullscreen);
        if (isFullscreen) {
            // Save fullscreen mode details
            outState.putInt("videoViewWidth", videoView.getLayoutParams().width);
            outState.putInt("videoViewHeight", videoView.getLayoutParams().height);
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        isFullscreen = savedInstanceState.getBoolean("isFullscreen");
        if (isFullscreen) {
            // Restore fullscreen mode
            int width = savedInstanceState.getInt("videoViewWidth");
            int height = savedInstanceState.getInt("videoViewHeight");
            ViewGroup.LayoutParams params = videoView.getLayoutParams();
            params.width = width;
            params.height = height;
            videoView.setLayoutParams(params);
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
    }

    private void playVideo(int videoResId) {
        Uri videoUri = Uri.parse("android.resource://" + getPackageName() + "/" + videoResId);
        videoView.setVideoURI(videoUri);

        MediaController mediaController = new MediaController(this);
        mediaController.setAnchorView(videoView);
        videoView.setMediaController(mediaController);

        videoView.start();

        // 重置全屏按鈕文本
        fullscreenButton.setText("全屏");
        isFullscreen = false;

        enterFullscreen();

    }

    private void enterFullscreen() {
        videoContainer.setVisibility(View.VISIBLE);
        fullscreenButton.setVisibility(View.VISIBLE);
        exitButton.setVisibility(View.VISIBLE);

        // 設置 VideoView 為全螢幕模式
        ViewGroup.LayoutParams params = videoView.getLayoutParams();
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.height = ViewGroup.LayoutParams.MATCH_PARENT; // 全螢幕高度
        videoView.setLayoutParams(params);

        // 隱藏其他按鈕
        findViewById(R.id.button).setVisibility(View.GONE);
        findViewById(R.id.button11).setVisibility(View.GONE);
        findViewById(R.id.button12).setVisibility(View.GONE);
        findViewById(R.id.button13).setVisibility(View.GONE);
        findViewById(R.id.button14).setVisibility(View.GONE);
        findViewById(R.id.button2).setVisibility(View.GONE);

        // 將視頻容器和按鈕移到最上層
        videoContainer.bringToFront();
        fullscreenButton.bringToFront();
        exitButton.bringToFront();

    }

    private void exitVideo() {
        videoView.stopPlayback();
        videoContainer.setVisibility(View.GONE);
        fullscreenButton.setVisibility(View.GONE);
        exitButton.setVisibility(View.GONE);

        // 恢復其他按鈕的顯示
        findViewById(R.id.button).setVisibility(View.VISIBLE);
        findViewById(R.id.button11).setVisibility(View.VISIBLE);
        findViewById(R.id.button12).setVisibility(View.VISIBLE);
        findViewById(R.id.button13).setVisibility(View.VISIBLE);
        findViewById(R.id.button14).setVisibility(View.VISIBLE);
        findViewById(R.id.button2).setVisibility(View.VISIBLE);

        // 重設 VideoView 的尺寸
        ViewGroup.LayoutParams params = videoView.getLayoutParams();
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.height = (int) (300 * getResources().getDisplayMetrics().density); // 恢復到初始大小
        videoView.setLayoutParams(params);

        // 如果仍在全螢幕模式，退出全螢幕
        if (isFullscreen) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            isFullscreen = false;
        }

    }

    private void toggleFullscreen() {
        if (isFullscreen) {
            // Exit fullscreen
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            ViewGroup.LayoutParams params = videoView.getLayoutParams();
            params.width = ViewGroup.LayoutParams.MATCH_PARENT;
            params.height = (int) (300 * getResources().getDisplayMetrics().density); // Reset to initial size
            videoView.setLayoutParams(params);
            fullscreenButton.setText("全屏");
            isFullscreen = false;
            findViewById(R.id.button).setVisibility(View.GONE);
            findViewById(R.id.button11).setVisibility(View.GONE);
            findViewById(R.id.button12).setVisibility(View.GONE);
            findViewById(R.id.button13).setVisibility(View.GONE);
            findViewById(R.id.button14).setVisibility(View.GONE);
            findViewById(R.id.button2).setVisibility(View.GONE);
        } else {
            // Enter fullscreen
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            ViewGroup.LayoutParams params = videoView.getLayoutParams();
            params.width = ViewGroup.LayoutParams.MATCH_PARENT;
            params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            videoView.setLayoutParams(params);
            fullscreenButton.setText("退出全屏");
            isFullscreen = true;
            findViewById(R.id.button).setVisibility(View.GONE);
            findViewById(R.id.button11).setVisibility(View.GONE);
            findViewById(R.id.button12).setVisibility(View.GONE);
            findViewById(R.id.button13).setVisibility(View.GONE);
            findViewById(R.id.button14).setVisibility(View.GONE);
            findViewById(R.id.button2).setVisibility(View.GONE);

            int padding = (int) (12 * getResources().getDisplayMetrics().density); // 24dp from edges

            exitButton.setX(padding); // 24dp from left
            exitButton.setY(padding); // 24dp from top

            fullscreenButton.setX(getResources().getDisplayMetrics().widthPixels -
                    fullscreenButton.getWidth() - padding); // 24dp from right
            fullscreenButton.setY(padding); // 24dp from top

        }
    }
    public void gotogame (View v){
        Intent it=new Intent(this,Main2GamemainActivity_gamemain.class);
        startActivity(it);
    }
    public void gotomain (View v){
        Intent it=new Intent(this,MainActivity.class);
        startActivity(it);
    }
}
