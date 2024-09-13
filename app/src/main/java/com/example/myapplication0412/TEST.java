package com.example.myapplication0412;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;

import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

public class TEST extends AppCompatActivity {
    ImageView heartImageView;
    ImageView dollImageView;
    Bitmap[] hearts = new Bitmap[3];
    Bitmap[] dolls = new Bitmap[2];
    int currentHeartIndex = 0;
    int currentDollIndex = 0;
    Handler handler = new Handler();
    Runnable heartRunnable = new Runnable() {
        @Override
        public void run() {
            // 切換心形圖片索引
            currentHeartIndex = (currentHeartIndex + 1) % hearts.length;
            // 設置下一個心形圖片到 heartImageView
            heartImageView.setImageBitmap(hearts[currentHeartIndex]);
            handler.postDelayed(this, 400); // 每 100 毫秒切換一次
        }
    };
    Runnable dollRunnable = new Runnable() {
        @Override
        public void run() {
            // 切換玩偶圖片索引
            currentDollIndex = (currentDollIndex + 1) % dolls.length;
            // 設置下一個玩偶圖片到 dollImageView
            dollImageView.setImageBitmap(dolls[currentDollIndex]);
            handler.postDelayed(this, 800); // 每 100 毫秒切換一次
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        heartImageView = findViewById(R.id.heartImageView);
        dollImageView = findViewById(R.id.dollImageView);

        hearts[0] = BitmapFactory.decodeResource(getResources(), R.drawable.game_heart1);
        hearts[1] = BitmapFactory.decodeResource(getResources(), R.drawable.game_heart2);
        hearts[2] = BitmapFactory.decodeResource(getResources(), R.drawable.game_heart3);
        dolls[0] = BitmapFactory.decodeResource(getResources(), R.drawable.game_doll1);
        dolls[1] = BitmapFactory.decodeResource(getResources(), R.drawable.game_doll2);

        handler.post(heartRunnable); // 開始心形動畫
        handler.post(dollRunnable); // 開始玩偶動畫
    }

    public void onGet(View v) {
        Intent it = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(it, 100);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == 100) {
            Bundle extras = data.getExtras();
            Bitmap bmp = (Bitmap) extras.get("data");
            ImageView imv = findViewById(R.id.imageView);
            imv.setImageBitmap(bmp);
        } else {
            Toast.makeText(this, "沒有拍到照片", Toast.LENGTH_LONG).show();
        }
    }
}
