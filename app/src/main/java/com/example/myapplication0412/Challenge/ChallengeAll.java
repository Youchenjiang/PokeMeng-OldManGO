package com.example.myapplication0412.Challenge;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ServiceInfo;
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
import androidx.core.app.NotificationCompat;
import androidx.core.app.ServiceCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.myapplication0412.R;
import com.example.myapplication0412.hey337973.TinyDB;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ChallengeAll extends AppCompatActivity implements SensorEventListener{
    String TAG = "計步器";
    int mSteps = 0;
    ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> { });
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.challenge_all);
        setupWindowInsets();
        getNowStep();
        checkSensors();
        registerSensors();
    }
    private void initNotification() {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, "CurrentStep");
        mBuilder.setContentTitle(getResources().getString(R.string.app_name))
                .setContentText("今日步数" + mSteps + " 步")
                .setContentIntent(PendingIntent.getActivity(this, 1, new Intent(), PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE))
                .setWhen(System.currentTimeMillis())//通知产生的时间，会在通知信息里显示
                .setPriority(Notification.DEFAULT_ALL)//设置该通知优先级
                .setAutoCancel(false)//设置这个标志当用户单击面板就可以让通知将自动取消
                .setOngoing(true)//ture，设置他为一个正在进行的通知。他们通常是用来表示一个后台任务,用户积极参与(如播放音乐)或以某种方式正在等待,因此占用设备(如一个文件下载,同步操作,主动网络连接)
                .setSmallIcon(R.mipmap.ic_launcher);
        Notification notification = mBuilder.build();
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ServiceCompat.startForeground((Service) getApplicationContext(), 1, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_MANIFEST);
        }
        Log.d(TAG, "initNotification()");
    }
    private void setupWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        findViewById(R.id.challenge_nowLayout).setOnClickListener(v ->startActivity(new Intent(this, ChallengeNow.class).putExtra("steps", mSteps)));
        findViewById(R.id.challenge_returnButton).setOnClickListener(v ->finish());
    }
    private void getNowStep(){
        List<ChallengeHistoryStep> taskList = getStepList();
        Calendar nowCalendar = Calendar.getInstance();
        nowCalendar.setTimeInMillis(System.currentTimeMillis());
        int currentDay = nowCalendar.get(Calendar.DAY_OF_YEAR),currentYear = nowCalendar.get(Calendar.YEAR);
        for (ChallengeHistoryStep challengeHistoryStep : taskList){
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(challengeHistoryStep.getStepDate());
            if (calendar.get(Calendar.DAY_OF_YEAR) == currentDay && calendar.get(Calendar.YEAR) == currentYear) {
                mSteps = challengeHistoryStep.getStepNumber();
                break;
            }
        }
        updateStepText();
    }
    private void updateStepText() {
        ((TextView) findViewById(R.id.challenge_myStepText)).setText(getResources().getString(R.string.challenge_myStepText, mSteps));
    }
    private List<ChallengeHistoryStep> getStepList() { //自定義SharedPreferences元件：https://github.com/kcochibili/TinyDB--Android-Shared-Preferences-Turbo/tree/master
        ArrayList<Object> taskListObject = new TinyDB(this).getListObject("StepList", ChallengeHistoryStep.class);   //TinyDB取得方法：https://stackoverflow.com/questions/35101437/android-shared-preference-tinydb-putlistobject-function
        List<ChallengeHistoryStep> taskList = new ArrayList<>();
        if (taskListObject != null) for (Object object : taskListObject) taskList.add((ChallengeHistoryStep) object);
        return taskList;
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
        else Log.e(TAG, "No sensor found for type: " + sensorType);
    }
    private void updateStepList(){
        List<ChallengeHistoryStep> taskList = getStepList();
        long currentDate = System.currentTimeMillis();
        Calendar nowCalendar = Calendar.getInstance();
        nowCalendar.setTimeInMillis(currentDate);
        int currentDay = nowCalendar.get(Calendar.DAY_OF_YEAR),currentYear = nowCalendar.get(Calendar.YEAR);
        taskList.removeIf(step -> {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(step.getStepDate());
            return calendar.get(Calendar.DAY_OF_YEAR) == currentDay && calendar.get(Calendar.YEAR) == currentYear;
        });
        taskList.add(new ChallengeHistoryStep(currentDate, mSteps));
        new TinyDB(this).putListObject("StepList", new ArrayList<>(taskList));   //TinyDB發送方法：https://stackoverflow.com/questions/35101437/android-shared-preference-tinydb-putlistobject-function
    }
    @Override
    public void onSensorChanged(SensorEvent event) { // 實現SensorEventListener回檔介面，在sensor改變時，會回檔該介面
        if (event.values[0] == 1.0f) mSteps++; // 並將結果通過event回傳給app處理
        updateStepText();
        updateStepList();
        sendBroadcast(new Intent("com.example.sportnew.STEP_UPDATE").putExtra("steps", mSteps));
        Log.i(TAG,"Detected step changes:"+event.values[0]);
    }
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        Log.i(TAG,"onAccuracyChanged");
    }
}

/*
public class ChallengeAll extends AppCompatActivity implements View.OnClickListener, SensorEventListener{
    private SensorManager sensorManager;
    private Sensor sensor;
    private TextView textView_step;
    private Button button_start;
    private int step;
    private double original_value;
    private double last_value;
    private double current_value;
    private boolean motionState=true; //是否处于运动状态
    private boolean processState=false;  //是否已经开始计步
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.challenge_all);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        findViewById(R.id.challenge_nowLayout).setOnClickListener(v ->startActivity(new Intent(this, ChallengeNow.class)));
        findViewById(R.id.challenge_returnButton).setOnClickListener(v ->finish());
        step=0;
        original_value=0;
        last_value =0;
        current_value =0;
        sensorManager=(SensorManager) getSystemService(SENSOR_SERVICE);
        sensor=sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER); //获取传感器，在计步器中需要使用的是加速度传感器
        sensorManager.registerListener(this,sensor,sensorManager.SENSOR_DELAY_UI);
        textView_step=findViewById(R.id.challenge_tryText);
        button_start=findViewById((R.id.tryButton));
        button_start.setOnClickListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        double range=1; //设置一个精度范围
        float[] value=event.values;
        current_value =magnitude(value[0],value[1],value[2]); //计算当前的模
        if(motionState){ //向上加速的状态
            if (current_value >= last_value)
                last_value = current_value;
            else {
                //检测到一次峰值
                if(Math.abs(current_value-last_value)>range){
                    original_value=current_value;
                    motionState=false;
                }
            }
        }
        if(!motionState){ //向下加速的状态
            if (current_value <= last_value)
                last_value = current_value;
            else {
                if(Math.abs(current_value-last_value)>range){ //检测到一次峰值
                    original_value=current_value;
                    if (processState){
                        step++; //检测到开始记录，步数加1
                        textView_step.setText(step+""); //更新读数
                    }
                    motionState=true;
                }
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onClick(View v) {
        step=0;
        textView_step.setText("0");
        if (processState){
            button_start.setText("出发！GO！");
            processState=false;
        }else{
            button_start.setText("走不动了歇一会~");
            processState=true;
        }
    }

    private double magnitude(float x, float y, float z) {
        return Math.sqrt(x*x+y*y+z*z);
    }
}*//*
public class ChallengeAll extends AppCompatActivity implements SensorEventListener {

    private TextView stepCountTextView;
    private TextView distanceTextView;
    private TextView timeTextView;
    private Button pauseButton;
    private SensorManager sensorManager;
    private Sensor stepCounterSensor;
    private ProgressBar progressBar;
    private boolean isPause = false;
    private long timePaused = 0;
    private float stepLengthInMeter = 0.762f;
    private long startTime = 0;
    private int stepCountTarget = 5000;
    private TextView stepCountTargetTextView;
    private Handler timeHandler = new Handler();
    private Runnable timeRunnable = new Runnable() {
        @Override
        public void run() {
            long timeInMilliseconds = System.currentTimeMillis() - startTime - timePaused;
            int seconds = (int) (timeInMilliseconds / 1000);
            int minutes = seconds / 60;
            seconds = seconds % 60;
            timeTextView.setText(String.format(Locale.getDefault(), "%d:%02d", minutes, seconds));
            if (!isPause) timeTextView.postDelayed(this, 1000);
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.newTry);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        //findViewById(R.id.challenge_nowLayout).setOnClickListener(v -> startActivity(new Intent(this, ChallengeNow.class)));
        //findViewById(R.id.challenge_returnButton).setOnClickListener(v -> finish());
        stepCountTextView = findViewById(R.id.stepCountTextView);
        distanceTextView = findViewById(R.id.distanceTextView);
        timeTextView = findViewById(R.id.timeTextView);
        pauseButton = findViewById(R.id.pauseButton);
        pauseButton.setOnClickListener(v -> onPauseButtonClicked());
        stepCountTargetTextView = findViewById(R.id.stepCountTargetTextView);
        progressBar = findViewById(R.id.progressBar);
        startTime = System.currentTimeMillis();
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        stepCounterSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        progressBar.setMax(stepCountTarget);
        stepCountTargetTextView.setText("Step Goal: " + stepCountTarget);
        if(stepCounterSensor == null) stepCountTargetTextView.setText("Step Counter Sensor Not Available");
    }
    @Override
    protected void onResume() {
        super.onResume();
        if(stepCounterSensor != null){
            sensorManager.registerListener((SensorEventListener) this, stepCounterSensor, SensorManager.SENSOR_DELAY_NORMAL);
            timeHandler.postDelayed(timeRunnable, 0);
        }
    }
    @Override
    protected void onStop() {
        super.onStop();
        if(stepCounterSensor != null){
            sensorManager.unregisterListener((SensorEventListener) this, stepCounterSensor);
            timeHandler.removeCallbacks(timeRunnable);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if(sensorEvent.sensor.getType() == Sensor.TYPE_STEP_COUNTER){
            int stepCount = (int) sensorEvent.values[0];
            stepCountTextView.setText("Step Count: " + stepCount);
            progressBar.setProgress(stepCount);
            if(stepCount >= stepCountTarget) stepCountTargetTextView.setText("Step Goal Achieved");
            float distanceInKM = stepCount * stepLengthInMeter / 1000;
            distanceTextView.setText(String.format(Locale.getDefault(), "Distance: %.2f km", distanceInKM));

        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
    private void onPauseButtonClicked(){
        if(isPause){
            isPause = false;
            startTime += System.currentTimeMillis() - timePaused;
            pauseButton.setText("Pause");
        }else{
            isPause = true;
            timeHandler.removeCallbacks(timeRunnable);
            timePaused = System.currentTimeMillis() - startTime;
            pauseButton.setText("Resume");
        }
    }
}*/