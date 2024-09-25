package com.PokeMeng.OldManGO.location;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import com.PokeMeng.OldManGO.R;
import com.google.android.material.button.MaterialButton;

public class login extends AppCompatActivity {

    private MaterialButton parentLoginBtn;
    private MaterialButton elderlyLoginBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        parentLoginBtn = findViewById(R.id.parentLoginBtn);
        elderlyLoginBtn = findViewById(R.id.elderlyLoginBtn);

        // 家長登入按鈕
        parentLoginBtn.setOnClickListener(v -> {
            Intent intent = new Intent(login.this, MapMainActivity.class);
            intent.putExtra("isParent", true);  // 傳遞家長身份
            startActivity(intent);
        });

        // 老人登入按鈕
        elderlyLoginBtn.setOnClickListener(v -> {
            Intent intent = new Intent(login.this, MapMainActivity.class);
            intent.putExtra("isParent", false);  // 傳遞老人身份
            startActivity(intent);
        });
    }
}
