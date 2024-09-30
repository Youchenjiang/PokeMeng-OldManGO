package com.PokeMeng.OldManGO.Challenge;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.PokeMeng.OldManGO.R;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class ChallengeAll extends AppCompatActivity implements SensorEventListener{
    String TAG = "計步器";
    int mSteps;
    ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> { });
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.challenge_all);
        setupWindowInsets();
        getNowStep();
        checkSensors();
        registerSensors();
    }/*
    private void initNotification() {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, "CurrentStep");
        mBuilder.setContentTitle(getResources().getString(R.string.app_name))
                .setContentText("今日步數" + mSteps + " 步")
                .setContentIntent(PendingIntent.getActivity(this, 1, new Intent(), PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE))
                .setWhen(System.currentTimeMillis())//通知產生的時間，會在通知訊息中顯示
                .setPriority(Notification.DEFAULT_ALL)//設定該通知優先權
                .setAutoCancel(false)//設定這個標誌當使用者點擊面板就可以讓通知將自動取消
                .setOngoing(true)//ture，設定他為一個正在進行的通知。他們通常是用來表示一個後台任務,用戶積極參與(如播放音樂)或以某種方式正在等待,因此佔用設備(如一個文件下載,同步操作,主動網絡連接)
                .setSmallIcon(R.mipmap.ic_launcher);
        Notification notification = mBuilder.build();
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ServiceCompat.startForeground((Service) getApplicationContext(), 1, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_MANIFEST);
        }
        Log.d(TAG, "initNotification()");
    }*/
    private void setupWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        findViewById(R.id.challenge_nowLayout).setOnClickListener(v ->startActivity(new Intent(this, ChallengeNow.class).putExtra("steps", mSteps)));
        findViewById(R.id.challenge_returnButton).setOnClickListener(v ->finish());
    }
    private void getNowStep() {
        String formattedDate = new SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(System.currentTimeMillis());
        String userId = "your_user_id"; // Replace with actual user ID

        db.collection("Users").document(userId).collection("StepList").document(formattedDate).get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null && task.getResult().exists()) {
                ChallengeHistoryStep challengeHistoryStep = task.getResult().toObject(ChallengeHistoryStep.class);
                if (challengeHistoryStep != null) {
                    Log.d(TAG, "Step number: " + challengeHistoryStep.getStepNumber());
                    mSteps = challengeHistoryStep.getStepNumber();
                }
            } else {
                Log.w(TAG, "Error getting document or document does not exist.", task.getException());
            }
            updateStepText();
        });
    }
    private void updateStepText() {
        ((TextView) findViewById(R.id.challenge_myStepText)).setText(getResources().getString(R.string.challenge_myStepText, mSteps));
    }
    private void checkSensors(){ //一個簡單的Android計步器：https://blog.csdn.net/TDSSS/article/details/125879573
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) { // Handle devices with API level larger than 29
            String[] ACTIVITY_RECOGNITION_PERMISSION = { Manifest.permission.ACTIVITY_RECOGNITION }; //檢查許可權
            if (ContextCompat.checkSelfPermission(this, ACTIVITY_RECOGNITION_PERMISSION[0]) != PackageManager.PERMISSION_GRANTED) // 許可權是否已經 授權 GRANTED---授權  DENIED---拒絕
                ActivityCompat.requestPermissions(this, ACTIVITY_RECOGNITION_PERMISSION, 321); // 如果沒有授予該許可權，就去提示用戶請求自動開啟許可權
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 321 && grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED)
            showPermissionDialog();
    }
    private void showPermissionDialog() {
        new AlertDialog.Builder(this) //提示用戶手動開啟許可權
                .setTitle("健康運動許可權").setMessage("健康運動許可權不可用")
                .setPositiveButton("立即開啟", (dialog, which) -> {
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.fromParts("package", getPackageName(), null)); // 跳轉到應用設置介面
                    activityResultLauncher.launch(intent);
                }).setNegativeButton("取消", (dialog, which) -> {
                    Toast.makeText(getApplicationContext(), "沒有獲得許可權，應用無法運行！", Toast.LENGTH_SHORT).show();
                    finish();
                }).setCancelable(false).show();
    }
    private void registerSensors() {
        SensorManager sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE); // 獲取SensorManager管理器實例
        registerSensor(sensorManager, Sensor.TYPE_STEP_COUNTER);
        registerSensor(sensorManager, Sensor.TYPE_STEP_DETECTOR);
    }
    private void registerSensor(SensorManager sensorManager, int sensorType) {
        Sensor sensor = sensorManager.getDefaultSensor(sensorType); // 獲取計步器sensor
        if (sensor != null) sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
        else {
            mSteps = -1;
            Log.e(TAG, "No sensor found for type: " + sensorType);
        }
    }
    private void updateStepList() {
        String userId = "your_user_id"; // Replace with actual user ID
        String formattedDate = new SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(System.currentTimeMillis());
        ChallengeHistoryStep newStep = new ChallengeHistoryStep(mSteps);
        db.collection("Users").document(userId).collection("StepList").document(formattedDate).set(newStep)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "Step successfully updated!"))
                .addOnFailureListener(e -> Log.w(TAG, "Error updating step", e));
    }
    @Override
    public void onSensorChanged(SensorEvent event) { // 實現SensorEventListener回檔介面，在sensor改變時，會回檔該介面
        if (event.values[0] == 1.0f) mSteps++; // 並將結果通過event回傳給app處理
        updateStepText();
        if(mSteps != 0) updateStepList();
        sendBroadcast(new Intent("com.PokeMeng.OldManGO.STEP_UPDATE").putExtra("steps", mSteps));
        Log.i(TAG,"Detected step changes:"+event.values[0]);
    }
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        Log.i(TAG,"onAccuracyChanged");
    }




    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart called");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume called");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause called");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop called");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy called");
    }
}