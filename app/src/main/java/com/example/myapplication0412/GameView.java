package com.example.myapplication0412;

import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class GameView extends AppCompatActivity {

    private GameCanvasView gameCanvasView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_view);

        gameCanvasView = findViewById(R.id.gameCanvasView);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        showInstructionsDialog();
    }

    private void showInstructionsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.activity_dialog_instructions, null);
        builder.setView(dialogView);

        builder.setPositiveButton("開始遊戲", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // 開始遊戲
                gameCanvasView.startGame();
            }
        });

        builder.setTitle("遊戲說明");

        VideoView videoView = dialogView.findViewById(R.id.videoView);
        TextView textViewInstructions = dialogView.findViewById(R.id.textViewInstructions);

        String videoPath = "android.resource://" + getPackageName() + "/" + R.raw.fruitgametalk; // 替換為你的視頻文件
        videoView.setVideoURI(Uri.parse(videoPath));
        videoView.start();

        textViewInstructions.setText("這是一個接水果遊戲。利用籃子去跳戰接越多蘋果吧!!!");
        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mp.start(); // 重新開始播放
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void gotomain(View v) {
        Intent it = new Intent(this, Main2GamemainActivity_gamemain.class);
        startActivity(it);
    }
}
