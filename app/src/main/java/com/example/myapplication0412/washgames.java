package com.example.myapplication0412;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Rect;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.content.SharedPreferences;
import android.widget.VideoView;

public class washgames extends AppCompatActivity {

    private ImageView plateDirt, bubbles, scrubSponge;
    private TextView scoreText, highScoreText, timeText;
    private int score = 0;
    private int highScore = 0;
    private int cleaningAttempts = 0;
    private Handler handler = new Handler();
    private Runnable runnable;
    private long startTime;
    private float lastX, lastY;
    private float totalDistance = 0;
    private static final float CIRCLE_DISTANCE = 1000; // 擦一圈要多少
    private static final int TOTAL_TIME = 30;
    private SharedPreferences preferences;
    private static final String PREFS_NAME = "WashGamePrefs";
    private static final String HIGH_SCORE_KEY = "HighScore";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_washgames);

        plateDirt = findViewById(R.id.plate_dirt);
        bubbles = findViewById(R.id.bubbles);
        scrubSponge = findViewById(R.id.scrub_sponge);
        scoreText = findViewById(R.id.score_text);
        highScoreText = findViewById(R.id.high_score_text);
        timeText = findViewById(R.id.time_text);

        preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        highScore = preferences.getInt(HIGH_SCORE_KEY, 0);
        highScoreText.setText("歷史最高分數: " + highScore);

        scrubSponge.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        lastX = event.getRawX();
                        lastY = event.getRawY();
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        float dx = event.getRawX() - lastX;
                        float dy = event.getRawY() - lastY;
                        scrubSponge.setX(scrubSponge.getX() + dx);
                        scrubSponge.setY(scrubSponge.getY() + dy);
                        lastX = event.getRawX();
                        lastY = event.getRawY();

                        // 增加移动的总距离
                        totalDistance += Math.sqrt(dx * dx + dy * dy);

                        // 检查菜瓜布是否在盘子上
                        if (isScrubSpongeOnPlate()) {
                            // 碰到盘子才增加刷洗次数和显示泡泡效果
                            if (totalDistance >= CIRCLE_DISTANCE) {
                                cleaningAttempts++;
                                totalDistance = 0; // 重置总距离
                                bubbles.setVisibility(View.VISIBLE);
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        bubbles.setVisibility(View.GONE);
                                    }
                                }, 1000); // 泡泡显示时间缩短为1秒

                                // 更新污渍透明度
                                plateDirt.setAlpha(1.0f - (cleaningAttempts * 0.2f));

                                // 刷满5次后清除污渍
                                if (cleaningAttempts == 5) {
                                    score++;
                                    scoreText.setText("分數: " + score);
                                    plateDirt.setAlpha(1.0f);
                                    cleaningAttempts = 0; // 重置刷洗次数
                                }
                            }
                        }
                        return true;
                }
                return false;
            }
        });

        startTime = System.currentTimeMillis();
        runnable = new Runnable() {
            @Override
            public void run() {
                long elapsedTime = System.currentTimeMillis() - startTime;
                int remainingTime = TOTAL_TIME - (int) (elapsedTime / 1000);
                if (remainingTime >= 0) {
                    timeText.setText("倒數計時: " + remainingTime);
                    handler.postDelayed(runnable, 1000);
                } else {
                    handler.removeCallbacks(runnable);
                    if (score > highScore) {
                        highScore = score;
                        highScoreText.setText("歷史最高分數: " + highScore);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putInt(HIGH_SCORE_KEY, highScore);
                        editor.apply();
                    }
                    showTimeUpDialog();
                }
            }
        };
        handler.post(runnable);
        showInstructionsDialog();
    }
    private void showInstructionsDialog() {
        // 創建對話框
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // 加載對話框佈局
        View dialogView = getLayoutInflater().inflate(R.layout.activity_dialog_instructions, null);
        builder.setView(dialogView);

        // 設置對話框按鈕
        builder.setPositiveButton("開始遊戲", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // 開始遊戲
            }
        });

        // 設置對話框標題
        builder.setTitle("遊戲說明");

        // 獲取對話框內的 VideoView 和 TextView
        VideoView videoView = dialogView.findViewById(R.id.videoView);
        TextView textViewInstructions = dialogView.findViewById(R.id.textViewInstructions);

        // 設置 VideoView 播放的視頻
        String videoPath = "android.resource://" + getPackageName() + "/" + R.raw.washgametalk; // 替換為你的視頻文件
        videoView.setVideoURI(Uri.parse(videoPath));
        videoView.start();

        // 設置說明文字
        textViewInstructions.setText("這是一個顏色分辨遊戲。請根據提示選擇正確的顏色按鈕。說明文字很長，這裡只是示例。請添加完整的遊戲說明文字。");
        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mp.start(); // 重新開始播放
            }
        });
        // 創建並顯示對話框
        AlertDialog dialog = builder.create();
        dialog.show();
    }
    private boolean isScrubSpongeOnPlate() {
        // 检查菜瓜布是否在盘子上
        int[] spongeLocation = new int[2];
        scrubSponge.getLocationOnScreen(spongeLocation);
        int[] plateLocation = new int[2];
        plateDirt.getLocationOnScreen(plateLocation);

        Rect spongeRect = new Rect(
                spongeLocation[0], spongeLocation[1],
                spongeLocation[0] + scrubSponge.getWidth(),
                spongeLocation[1] + scrubSponge.getHeight()
        );
        Rect plateRect = new Rect(
                plateLocation[0], plateLocation[1],
                plateLocation[0] + plateDirt.getWidth(),
                plateLocation[1] + plateDirt.getHeight()
        );

        return Rect.intersects(spongeRect, plateRect);
    }

    private void showTimeUpDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(washgames.this);
        builder.setTitle("時間到!!!")
                .setMessage("你刷乾淨了 " + score + " 個盤子!")
                .setPositiveButton("重玩一次", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // 重新启动活动
                        score = 0;
                        cleaningAttempts = 0;
                        plateDirt.setAlpha(1.0f);
                        scoreText.setText("分數: " + score);
                        startTime = System.currentTimeMillis();
                        handler.post(runnable);
                    }
                })
                .setNegativeButton("回上一頁", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        finish();
                    }
                })
                .show();
    }
    public void gotomain (View v){
        Intent it=new Intent(this,Main2GamemainActivity_gamemain.class);
        startActivity(it);
    }
}
