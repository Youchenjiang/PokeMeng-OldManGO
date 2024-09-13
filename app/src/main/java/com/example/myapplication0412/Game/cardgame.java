package com.example.myapplication0412.Game;

import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication0412.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class cardgame extends AppCompatActivity {
    private GridLayout gridLayout;
    private ImageButton[] buttons;
    private int[] images = {R.drawable.game_card_image1, R.drawable.game_card_image2, R.drawable.game_card_image3, R.drawable.game_card_image4,
            R.drawable.game_card_image5, R.drawable.game_card_image6, R.drawable.game_card_image7, R.drawable.game_card_image8};
    private int[] cardImages;
    private boolean[] cardFlipped;
    private int firstCard = -1, secondCard = -1;
    private boolean isProcessing = false;
    private int matchesFound = 0;
    private final int TOTAL_CARDS = 16;
    private double score = 0.0;
    private TextView scoreTextView;
    private Button hintButton;
    private Button backButton;
    private boolean hintActive = false;
    private int hintCount = 0;
    private final int MAX_HINTS = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cardgame);

        gridLayout = findViewById(R.id.gridLayout);
        scoreTextView = findViewById(R.id.scoreTextView);
        hintButton = findViewById(R.id.hintButton);
        backButton = findViewById(R.id.backButton);
        buttons = new ImageButton[TOTAL_CARDS];
        cardFlipped = new boolean[TOTAL_CARDS];
        cardImages = new int[TOTAL_CARDS];

        hintButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (hintCount < MAX_HINTS && !hintActive) {
                    showHint();
                    hintCount++;
                    if (hintCount >= MAX_HINTS) {
                        hintButton.setEnabled(false);
                    }
                }
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        setupGame();
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
        String videoPath = "android.resource://" + getPackageName() + "/" + R.raw.cardgametalk; // 替換為你的視頻文件
        videoView.setVideoURI(Uri.parse(videoPath));
        videoView.start();

        // 設置說明文字
        textViewInstructions.setText("這是一個記憶卡牌，請添加完整的遊戲說明文字。");
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

    private void setupGame() {
        List<Integer> imageList = new ArrayList<>();
        for (int i = 0; i < TOTAL_CARDS / 2; i++) {
            imageList.add(images[i]);
            imageList.add(images[i]);
        }
        Collections.shuffle(imageList);

        for (int i = 0; i < TOTAL_CARDS; i++) {
            cardImages[i] = imageList.get(i);
        }

        for (int i = 0; i < TOTAL_CARDS; i++) {
            if (buttons[i] == null) {
                buttons[i] = new ImageButton(this);
                buttons[i].setLayoutParams(new GridLayout.LayoutParams());
                GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                params.width = 210;
                params.height = 400;
                params.setMargins(4, 4, 4, 4);
                buttons[i].setLayoutParams(params);
                gridLayout.addView(buttons[i]);
            }
            buttons[i].setImageResource(R.drawable.game_card_back);
            buttons[i].setOnClickListener(new CardClickListener(i));
        }
    }

    private void showHint() {
        if (isProcessing || hintActive) return;

        hintActive = true;
        hintButton.setEnabled(false); // Disable hint button during hint
        disableButtons();

        for (int i = 0; i < TOTAL_CARDS; i++) {
            if (!cardFlipped[i]) {
                buttons[i].setImageResource(cardImages[i]);
            }
        }

        new Handler().postDelayed(() -> {
            for (int i = 0; i < TOTAL_CARDS; i++) {
                if (!cardFlipped[i]) {
                    buttons[i].setImageResource(R.drawable.game_card_back);
                }
            }
            hintActive = false;
            enableButtons();
            if (hintCount < MAX_HINTS) {
                hintButton.setEnabled(true); // Re-enable hint button after hint
            }
        }, 2000); // Hint duration: 2 seconds
    }

    private class CardClickListener implements View.OnClickListener {
        private int index;

        public CardClickListener(int index) {
            this.index = index;
        }

        @Override
        public void onClick(View view) {
            if (isProcessing || cardFlipped[index]) return;

            buttons[index].setImageResource(cardImages[index]);

            if (firstCard == -1) {
                firstCard = index;
            } else if (secondCard == -1) {
                secondCard = index;
                isProcessing = true;
                disableButtons();

                new Handler().postDelayed(() -> {
                    if (cardImages[firstCard] == cardImages[secondCard]) {
                        cardFlipped[firstCard] = true;
                        cardFlipped[secondCard] = true;
                        matchesFound += 2;
                        score += 12.5;
                        updateScore();
                        if (score >= 100) {
                            showEndGameDialog();
                        }
                    } else {
                        buttons[firstCard].setImageResource(R.drawable.game_card_back);
                        buttons[secondCard].setImageResource(R.drawable.game_card_back);
                    }
                    firstCard = -1;
                    secondCard = -1;
                    isProcessing = false;
                    enableButtons();
                }, 500);
            }
        }
    }

    private void updateScore() {
        scoreTextView.setText("分數: " + score);
    }

    private void showEndGameDialog() {
        new AlertDialog.Builder(this)
                .setTitle("恭喜過關!!記憶高手!!!")
                .setMessage("分數已達100分，還要繼續遊玩嗎?")
                .setPositiveButton("再一次", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        resetGame();
                    }
                })
                .setNegativeButton("回上一頁", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .setCancelable(false)
                .show();
    }

    private void resetGame() {
        score = 0.0;
        matchesFound = 0;
        firstCard = -1;
        secondCard = -1;
        isProcessing = false;
        hintCount = 0;
        hintButton.setEnabled(true);
        for (int i = 0; i < TOTAL_CARDS; i++) {
            cardFlipped[i] = false;
        }
        setupGame();
        updateScore();
    }

    private void disableButtons() {
        for (ImageButton button : buttons) {
            button.setClickable(false);
        }
    }

    private void enableButtons() {
        if (!hintActive) {
            for (ImageButton button : buttons) {
                button.setClickable(true);
            }
        }
    }
}
