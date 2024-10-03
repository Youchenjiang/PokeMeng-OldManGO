package com.PokeMeng.OldManGO.Game;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.PokeMeng.OldManGO.R;

import java.util.Random;

public class ColorGame extends AppCompatActivity {
    private int correctButtonIndex;
    private int currentLevel;
    private int lives = 3; // 追蹤失誤次數
    private int score;

    private ImageView heart1, heart2, heart3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_color_game);

        currentLevel = 1;
        score = 0; // 初始化分數
        updateScoreTextView(); // 更新分數顯示
        generateColors();
        heart1 = findViewById(R.id.heart1);
        heart2 = findViewById(R.id.heart2);
        heart3 = findViewById(R.id.heart3);

        showInstructionsDialog(); // 顯示說明對話框
    }
    private void showInstructionsDialog() {
        // 創建對話框
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // 加載對話框佈局
        View dialogView = getLayoutInflater().inflate(R.layout.game_dialog_instructions, null);
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
        String videoPath = "android.resource://" + getPackageName() + "/" + R.raw.colorgametalk; // 替換為你的視頻文件
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

    public void onColorButtonClick(View view) {
        Button clickedButton = (Button) view;
        int clickedButtonIndex = Integer.parseInt(clickedButton.getTag().toString());

        if (clickedButtonIndex == correctButtonIndex) {
            Toast.makeText(this, "答對！進入下一關.", Toast.LENGTH_SHORT).show();
            score += 10; // 過一關得到10分
            updateScoreTextView(); // 更新分數顯示
            currentLevel++;
            generateColors();
        } else {
            Toast.makeText(this, "Incorrect! Try again.", Toast.LENGTH_SHORT).show();

            decreaseLife();
        }
    }

    private void generateColors() {
//        int numButtons;
//
//        // Determine number of buttons based on current level
//        if (currentLevel >= 1 && currentLevel <= 3) {
//            numButtons = 4;
//        } else if (currentLevel >= 4 && currentLevel <= 6) {
//            numButtons = 6;
//        } else {
//            numButtons = 9;
//        }
//
//        // Set visibility for buttons based on current level
//        for (int i = 1; i <= 9; i++) {
//            int buttonId = getResources().getIdentifier("btn" + i, "id", getPackageName());
//            Button button = findViewById(buttonId);
//            button.setVisibility(i <= numButtons ? View.VISIBLE : View.GONE);
//        }
//
//        // Generate same color for all visible buttons
//        int sameColor = getRandomColor();
//        for (int i = 1; i <= numButtons; i++) {
//            int buttonId = getResources().getIdentifier("btn" + i, "id", getPackageName());
//            Button button = findViewById(buttonId);
//            button.setBackgroundColor(sameColor);
//            button.setTag(i);
//        }
//
//        // Set different color for one button
//        correctButtonIndex = new Random().nextInt(numButtons) + 1;
//        Button correctButton = findViewById(getResources().getIdentifier("btn" + correctButtonIndex, "id", getPackageName()));
//        int differentColor;
//
//        // Apply different color adjustment only from level 11 onwards
//        if (currentLevel >= 11) {
//            differentColor = getDifferentColor(sameColor);
//        } else {
//            differentColor = getRandomColor();
//        }
//        correctButton.setBackgroundColor(differentColor);
//
//        TextView levelTextView = findViewById(R.id.textView);
//        levelTextView.setText("第" + currentLevel + "關");
        int numButtons = 9; // 固定顯示九個按鈕

        // 設置所有按鈕可見
        for (int i = 1; i <= numButtons; i++) {
            int buttonId = getResources().getIdentifier("btn" + i, "id", getPackageName());
            Button button = findViewById(buttonId);
            button.setVisibility(View.VISIBLE);
        }

        // 生成顏色
        int sameColor = getRandomColor();
        for (int i = 1; i <= numButtons; i++) {
            int buttonId = getResources().getIdentifier("btn" + i, "id", getPackageName());
            Button button = findViewById(buttonId);
            button.setBackgroundColor(sameColor);
            button.setTag(i);
        }

        // 設置一個按鈕顏色與其他不同
        correctButtonIndex = new Random().nextInt(numButtons) + 1;
        Button correctButton = findViewById(getResources().getIdentifier("btn" + correctButtonIndex, "id", getPackageName()));
        int differentColor = getDifferentColor(sameColor);
        correctButton.setBackgroundColor(differentColor);

        TextView levelTextView = findViewById(R.id.textView);
        levelTextView.setText("第" + currentLevel + "關");
    }

    private int getRandomColor() {
        Random random = new Random();
        return Color.rgb(random.nextInt(256), random.nextInt(256), random.nextInt(256));
    }

    private int getDifferentColor(int baseColor) {
        Random random = new Random();
        int red = Color.red(baseColor);
        int green = Color.green(baseColor);
        int blue = Color.blue(baseColor);

        // Adjust one of the RGB components slightly
        switch (random.nextInt(3)) {
            case 0:
                red += random.nextInt(50) - 90;
                break;
            case 1:
                green += random.nextInt(50) - 90;
                break;
            case 2:
                blue += random.nextInt(50) - 90;
                break;
        }

        return Color.rgb(Math.max(0, Math.min(255, red)), Math.max(0, Math.min(255, green)), Math.max(0, Math.min(255, blue)));
    }
    private void updateScoreTextView() {
        TextView scoreTextView = findViewById(R.id.scoreTextView);
        scoreTextView.setText("分數: " + score);
    }

    private void decreaseLife() {
        lives--; // 生命值減一
        updateHearts(); // 更新ImageView顯示
        if (lives == 0) {
            showGameOverDialog(); // 顯示遊戲結束的提醒視窗
        }
    }
    private void showGameOverDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("您已回到第一關")
                .setPositiveButton("確定", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        resetGame(); // 點擊確定按鈕後重置遊戲
                    }
                });

        // 設置字體大小
        final TextView messageView = new TextView(this);
        messageView.setText("　　挑戰失敗！再接再厲~~");
        messageView.setTextSize(24); // 設置字體大小為24sp
        builder.setView(messageView);

        // 創建並顯示提醒視窗
        AlertDialog dialog = builder.create();
        dialog.show();
    }
    private void updateHearts() {
        // 根據生命值更新ImageView顯示
        switch (lives) {
            case 3:
                heart1.setImageResource(R.drawable.game_heart1);
                heart2.setImageResource(R.drawable.game_heart1);
                heart3.setImageResource(R.drawable.game_heart1);
                break;
            case 2:
                heart1.setImageResource(R.drawable.game_heart1);
                heart2.setImageResource(R.drawable.game_heart1);
                heart3.setImageResource(R.drawable.game_heart0);
                break;
            case 1:
                heart1.setImageResource(R.drawable.game_heart1);
                heart2.setImageResource(R.drawable.game_heart0);
                heart3.setImageResource(R.drawable.game_heart0);
                break;
            case 0:
                heart1.setImageResource(R.drawable.game_heart0);
                heart2.setImageResource(R.drawable.game_heart0);
                heart3.setImageResource(R.drawable.game_heart0);
                break;
        }
    }

    private void gameOver() {
        Toast.makeText(this, "You lost! Game over.", Toast.LENGTH_SHORT).show();
        // 在這裡可以添加遊戲結束的相關邏輯，例如顯示分數、重新開始遊戲等
        // 這裡我們僅示範重置遊戲
        resetGame();
    }
    private void resetGame() {
        currentLevel = 1;
        lives = 3; // 重置生命值
        updateHearts(); // 更新ImageView顯示
        score = 0;
        updateScoreTextView();
        generateColors();
    }

    public void gotomain (View v){
        Intent it=new Intent(this, GameMain.class);
        startActivity(it);
    }
}
