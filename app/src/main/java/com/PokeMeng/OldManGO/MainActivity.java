package com.PokeMeng.OldManGO;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextClock;

import androidx.appcompat.app.AppCompatActivity;

import com.PokeMeng.OldManGO.Challenge.ChallengeAll;
import com.PokeMeng.OldManGO.Game.GameMain;
import com.PokeMeng.OldManGO.Task.TaskAll;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 設定台灣時區
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Taipei"));

        // 取得當前時間
        Calendar calendar = Calendar.getInstance();

        // 使用 SimpleDateFormat 取得中文的星期表示
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy / MM / dd E\nH:mm", Locale.CHINESE);
        String formattedDate = sdf.format(calendar.getTime());

        // 找到 TextClock
        TextClock textClock = findViewById(R.id.textClock2);

        // 設定 TextClock 的格式來顯示日期、時間和中文星期
        textClock.setFormat12Hour(formattedDate);
        textClock.setFormat24Hour(formattedDate);

        findViewById(R.id.imageButton).setOnClickListener(v -> startActivity(new Intent(this, TaskAll.class)));
        findViewById(R.id.button).setOnClickListener(v -> startActivity(new Intent(this, TaskAll.class)));
        findViewById(R.id.imageButton3).setOnClickListener(v -> startActivity(new Intent(this, GameMain.class)));
        findViewById(R.id.button12).setOnClickListener(v -> startActivity(new Intent(this, GameMain.class)));
        findViewById(R.id.imageButton4).setOnClickListener(v -> startActivity(new Intent(this, ChallengeAll.class)));
        findViewById(R.id.button13).setOnClickListener(v -> startActivity(new Intent(this, ChallengeAll.class)));
        findViewById(R.id.imageButton6).setOnClickListener(v -> startActivity(new Intent(this, prize.class)));
        findViewById(R.id.imageButton5).setOnClickListener(v -> startActivity(new Intent(this, login.class)));
    }
}