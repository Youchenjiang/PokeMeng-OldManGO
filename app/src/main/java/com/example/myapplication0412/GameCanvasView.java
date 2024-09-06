package com.example.myapplication0412;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameCanvasView extends View {

    private Paint paint;
    private Handler handler;
    private Runnable runnable;
    private int score = 0;
    private int missedFruits = 0;
    private int highScore = 0;
    private long startTime;
    private Player player;
    private List<Fruit> fruits;
    private Paint scorePaint;
    private Bitmap fruitBitmap;
    private Bitmap playerBitmap;
    private int speed = 5;
    private int fruitProbability = 2;
    private SharedPreferences sharedPreferences;
    private boolean gameStarted = false; // 新增標誌

    public GameCanvasView(Context context, AttributeSet attrs) {
        super(context, attrs);
        paint = new Paint();
        player = new Player();
        fruits = new ArrayList<>();
        handler = new Handler();
        scorePaint = new Paint();
        scorePaint.setColor(Color.BLACK);
        scorePaint.setTextSize(80);

        sharedPreferences = context.getSharedPreferences("GamePrefs", Context.MODE_PRIVATE);
        highScore = sharedPreferences.getInt("highScore", 0);

        BitmapFactory.Options fruitOptions = new BitmapFactory.Options();
        fruitOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(getResources(), R.drawable.apple, fruitOptions);

        int fruitScaleFactor = Math.min(fruitOptions.outWidth / 50, fruitOptions.outHeight / 50);

        fruitOptions.inJustDecodeBounds = false;
        fruitOptions.inSampleSize = fruitScaleFactor;

        fruitBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.apple, fruitOptions);

        BitmapFactory.Options playerOptions = new BitmapFactory.Options();
        playerOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(getResources(), R.drawable.bag, playerOptions);

        int playerScaleFactor = Math.min(playerOptions.outWidth / 100, playerOptions.outHeight / 100);

        playerOptions.inJustDecodeBounds = false;
        playerOptions.inSampleSize = playerScaleFactor;

        playerBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.bag, playerOptions);

        player.width = playerBitmap.getWidth();
        player.height = playerBitmap.getHeight();

        startTime = SystemClock.elapsedRealtime();

        runnable = new Runnable() {
            @Override
            public void run() {
                invalidate();
            }
        };
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // 背景
        canvas.drawColor(Color.WHITE);

        // 繪製玩家
        canvas.drawBitmap(playerBitmap, player.x, player.y, paint);

        if (gameStarted) {
            // 繪製水果
            for (Fruit fruit : fruits) {
                canvas.drawBitmap(fruit.bitmap, fruit.x, fruit.y, paint);
                fruit.y += fruit.speed;

                if (fruit.y > getHeight()) {
                    missedFruits++;
                    fruits.remove(fruit);
                    break;
                }

                if (fruit.y + fruit.bitmap.getHeight() > player.y &&
                        fruit.x < player.x + player.width &&
                        fruit.x + fruit.bitmap.getWidth() > player.x) {
                    score++;
                    fruits.remove(fruit);
                    break;
                }
            }

            canvas.drawText("分數: " + score, 50, 100, scorePaint);
            canvas.drawText("歷史最高: " + highScore, 50, 200, scorePaint);
            canvas.drawText("失去的蘋果: " + missedFruits, 50, 300, scorePaint);

            if (missedFruits >= 3) {
                showGameOverDialog();
                return;
            }

            long currentTime = SystemClock.elapsedRealtime();
            if ((currentTime - startTime) / 1000 > 30) {
                speed += 2;
                fruitProbability += 1;
                startTime = currentTime;
            }

            if (new Random().nextInt(100) < fruitProbability) {
                fruits.add(new Fruit(getWidth(), fruitBitmap, speed));
            }

            handler.postDelayed(runnable, 30);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE:
                player.x = (int) event.getX() - player.width / 2;
                break;
        }
        return true;
    }

    public void startGame() {
        gameStarted = true;
        handler.postDelayed(runnable, 30);
    }

    private void showGameOverDialog() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if (score > highScore) {
            highScore = score;
            editor.putInt("highScore", highScore);
            editor.apply();
        }

        new AlertDialog.Builder(getContext())
                .setTitle("遊戲結束 \n" + "你失去太多蘋果了~你還要遊玩嗎?")
                .setPositiveButton("重玩一次", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        resetGame();
                    }
                })
                .setNegativeButton("回上一頁", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ((GameView) getContext()).finish();
                    }
                })
                .setCancelable(false)
                .show();
    }

    private void resetGame() {
        if (score > highScore) {
            highScore = score;
        }
        score = 0;
        missedFruits = 0;
        fruits.clear();
        startTime = SystemClock.elapsedRealtime();
        gameStarted = false;
        handler.removeCallbacks(runnable);
    }

    private static class Player {
        int x, y, width, height;

        Player() {
            x = 0;
            y = 1000;
            width = 200;
            height = 200;
        }
    }

    private static class Fruit {
        int x, y, speed;
        Bitmap bitmap;

        Fruit(int screenWidth, Bitmap bitmap, int speed) {
            this.x = new Random().nextInt(screenWidth - bitmap.getWidth());
            this.y = 0;
            this.speed = speed;
            this.bitmap = bitmap;
        }
    }
}
