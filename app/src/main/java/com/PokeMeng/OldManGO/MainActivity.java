package com.PokeMeng.OldManGO;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextClock;

import androidx.appcompat.app.AppCompatActivity;

import com.PokeMeng.OldManGO.Challenge.ChallengeAll;
import com.PokeMeng.OldManGO.Game.GameMain;
import com.PokeMeng.OldManGO.Task.TaskAll;
import com.PokeMeng.OldManGO.location.login;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;
import android.speech.tts.TextToSpeech;

public class MainActivity extends AppCompatActivity {
    private TextToSpeech tts;
    private AudioManager audioManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 初始化 AudioManager
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        // 設置最大音量
        int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, maxVolume, 0);

        // 初始化 TextToSpeech
        tts = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {
                int result = tts.setLanguage(Locale.TRADITIONAL_CHINESE);
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Log.e("TTS", "這個語言不支援");
                } else {
                    Log.i("TTS", "TTS 初始化成功");
                }
            } else {
                Log.e("TTS", "TTS 初始化失敗");
            }
        });

        tts.setSpeechRate(0.7f); // 調整語速，1.0 為正常速度
        tts.setPitch(1.2f); // 調整音調，1.0 為正常音調
        // 設定按鈕點擊語音
        findViewById(R.id.imageButton).setOnClickListener(v -> {
            speak("每日任務");
            startActivity(new Intent(this, TaskAll.class));
        });

        findViewById(R.id.imageButton3).setOnClickListener(v -> {
            speak("娛樂遊戲");
            startActivity(new Intent(this, GameMain.class));
        });

        findViewById(R.id.imageButton4).setOnClickListener(v -> {
            speak("運動挑戰");
            startActivity(new Intent(this, ChallengeAll.class));
        });

        findViewById(R.id.imageButton6).setOnClickListener(v -> {
            speak("獎勵兌換");
            startActivity(new Intent(this, prize.class));
        });

        findViewById(R.id.imageButton5).setOnClickListener(v -> {
            speak("定位系統");
            startActivity(new Intent(this, login.class));
        });

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
    }

    // 播放語音的功能
    private void speak(String text) {
        if (tts != null && !tts.isSpeaking()) {
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
        }
    }

    @Override
    protected void onDestroy() {
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }
}
