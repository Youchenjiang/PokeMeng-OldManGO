package com.example.myapplication0412.Challenge;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.myapplication0412.R;
import com.example.myapplication0412.hey337973.TinyDB;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class ChallengeNow extends AppCompatActivity {

    private StepUpdateReceiver stepUpdateReceiver;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.challenge_now);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        findViewById(R.id.now_checkButton).setOnClickListener(v -> showHistory());
        findViewById(R.id.now_returnButton).setOnClickListener(v -> finish());
        setNowStep(getIntent().getIntExtra("steps", 0), 2000);
        stepUpdateReceiver = new StepUpdateReceiver();
        registerReceiver(stepUpdateReceiver, new IntentFilter("com.example.sportnew.STEP_UPDATE"), Context.RECEIVER_NOT_EXPORTED); // 註冊廣播接收器
    }
    private void setNowStep(int step_now, int duration){
        int step_goal = 500;
        ChallengeNowProgressBar challengeNowProgressBar = findViewById(R.id.now_nowCircularProgressBar);
        challengeNowProgressBar.setText("目前步數：" + step_now + "步" + "\n" + "目標步數：" + step_goal + "步");
        challengeNowProgressBar.setProgress((float) step_now / step_goal * 100);
        challengeNowProgressBar.setDuration(duration);
    }
    private List<ChallengeHistoryStep> getStepList() { //自定義SharedPreferences元件：https://github.com/kcochibili/TinyDB--Android-Shared-Preferences-Turbo/tree/master
        ArrayList<Object> taskListObject = new TinyDB(this).getListObject("StepList", ChallengeHistoryStep.class);   //TinyDB取得方法：https://stackoverflow.com/questions/35101437/android-shared-preference-tinydb-putlistobject-function
        List<ChallengeHistoryStep> taskList = new ArrayList<>();
        if (taskListObject != null) for (Object object : taskListObject) taskList.add((ChallengeHistoryStep) object);
        return taskList;
    }
    private void showHistory() {
        View dialogView = getLayoutInflater().inflate(R.layout.challenge_now_history, null);
        setHistory(dialogView);
        new AlertDialog.Builder(this).setTitle("歷史紀錄").setView(dialogView).setPositiveButton("確定", null).create().show();
    }
    public static long convertToTimestamp(String dateString) {
        try { //取得某個月有多少天：https://www.it72.com/7740.htm, https://stackoverflow.com/questions/58762347/how-to-get-number-of-days-of-all-month, https://blog.csdn.net/sp_wei/article/details/84654440
            return Objects.requireNonNull(new SimpleDateFormat("yyyy/MM/dd", Locale.TAIWAN).parse(dateString)).getTime();
        } catch (Exception e) { //取得該月第一天星期：https://blog.51cto.com/u_16175474/10332589
            Log.e("convertDateStringToTimestamp", Objects.requireNonNull(e.getMessage()));
            return 0;
        }
    }
    private void setHistory(View dialogView) {
        List<ChallengeHistoryStep> messages = getStepList();
        ArrayAdapter<ChallengeHistoryStep> adapter = new ArrayAdapter<ChallengeHistoryStep>(this, R.layout.challenge_now_history_setview, messages) {
            @NonNull @Override
            public View getView(int position, View convertView, @NonNull ViewGroup parent) {
                convertView = convertView == null ? LayoutInflater.from(getContext()).inflate(R.layout.challenge_now_history_setview, parent, false): convertView;
                ChallengeHistoryStep currentMessage = getItem(position);    // Get the current message
                ((TextView)convertView.findViewById(R.id.Listview_timeText)).setText(DateFormat.format("yyyy-MM-dd", Objects.requireNonNull(currentMessage).getStepDate()));  // Format the date before showing it
                ((TextView)convertView.findViewById(R.id.Listview_numberText)).setText(getString(R.string.challenge_DateStep, currentMessage.getStepNumber()));  // Set their text
                return convertView;
            }
        };
        ((ListView)dialogView.findViewById(R.id.history_listview)).setAdapter(adapter);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(stepUpdateReceiver); // 取消註冊廣播接收器
    }
    private class StepUpdateReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null && "com.example.sportnew.STEP_UPDATE".equals(intent.getAction()))
                setNowStep(intent.getIntExtra("steps", 0), 1);
        }
    }
}