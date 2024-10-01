package com.PokeMeng.OldManGO.Medicine;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.PokeMeng.OldManGO.R;
import com.PokeMeng.OldManGO.databinding.ActivityMain5Binding;
import com.google.firebase.FirebaseApp;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;


import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity5 extends AppCompatActivity {

    private ActivityMain5Binding binding;

    // 权限请求代码
    // 权限请求代码
    private static final int REQUEST_CODE_SCHEDULE_EXACT_ALARM = 1;
    private static final int REQUEST_CODE_POST_NOTIFICATIONS = 2;

    // SharedPreferences 关键字
    private static final String PREFS_NAME = "prefs";
    private static final String PREFS_NOTIFICATION_PERMISSION_REQUESTED = "notification_permission_requested";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMain5Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);
        FirebaseApp.initializeApp(this);




        BottomNavigationView navView = findViewById(R.id.nav_view);
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main5);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);

        createNotificationChannel();

        // 请求精确闹钟权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.SCHEDULE_EXACT_ALARM) != PackageManager.PERMISSION_GRANTED) {
                // 请求权限
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.SCHEDULE_EXACT_ALARM},
                        REQUEST_CODE_SCHEDULE_EXACT_ALARM);
            }
        }

        // 检查是否已经请求过通知权限
        if (shouldRequestNotificationPermission()) {
            requestNotificationPermissions();
        }

        // 处理 Intent 以打开特定 Fragment
        if (savedInstanceState == null) {
            handleIntent(getIntent());
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        handleIntent(intent);
    }


    private boolean shouldRequestNotificationPermission() {
        // 检查 SharedPreferences
        return getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
                .getBoolean(PREFS_NOTIFICATION_PERMISSION_REQUESTED, false) == false;
    }

    private void requestNotificationPermissions() {
        // 请求精确闹钟权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.SCHEDULE_EXACT_ALARM) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.SCHEDULE_EXACT_ALARM},
                        REQUEST_CODE_SCHEDULE_EXACT_ALARM);
            }
        }

        // 请求通知权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, REQUEST_CODE_POST_NOTIFICATIONS);
            }
        }

        // 更新 SharedPreferences，标记权限已请求
        getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit()
                .putBoolean(PREFS_NOTIFICATION_PERMISSION_REQUESTED, true)
                .apply();
    }



    private void handleIntent(Intent intent) {
        if (intent != null && intent.hasExtra("fragment")) {
            String fragmentToOpen = intent.getStringExtra("fragment");
            Log.d("MainActivity5", "Opening fragment: " + fragmentToOpen);
            NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main5);

            if ("DashboardFragment".equals(fragmentToOpen)) {
                navController.navigate(R.id.navigation_dashboard, null);
            } else {
                navController.navigate(R.id.navigation_home, null);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_SCHEDULE_EXACT_ALARM) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("MainActivity5", "允許設置鬧鐘提醒");
            } else {
                Log.d("MainActivity5", "鬧鐘提醒權限已被拒絕");
            }
        } else if (requestCode == REQUEST_CODE_POST_NOTIFICATIONS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("MainActivity5", "可以發送通知");
            } else {
                Log.d("MainActivity5", "通知權限已被拒絕");
            }
        }
    }

    private void createNotificationChannel() {
        Log.d("MainActivity5", "Notification channel created");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    "medicine_reminder_channel",
                    "Medicine Reminders",
                    NotificationManager.IMPORTANCE_HIGH
            );
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

        @Override
        protected void onResume() {
            super.onResume();
            // 确保每次活动恢复时都不会意外重置 Fragment
            if (getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_activity_main5) == null) {
                // 这里可以不需要再次加载 HomeFragment，Fragment 管理应该由 Navigation 控制
            }
        }

}

