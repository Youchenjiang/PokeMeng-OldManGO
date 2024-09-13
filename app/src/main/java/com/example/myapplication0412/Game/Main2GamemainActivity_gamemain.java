package com.example.myapplication0412.Game;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.myapplication0412.MainActivity;
import com.example.myapplication0412.R;
import com.example.myapplication0412.Video.video_main;

public class Main2GamemainActivity_gamemain extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2_gamemain);
    }
    public void gotomain (View v){
        Intent it=new Intent(this, MainActivity.class);
        startActivity(it);
    }
    public void gotoeye (View v){
        Intent it=new Intent(this, Colorgame.class);
        startActivity(it);
    }
    public void gotofruit (View v){
        Intent it=new Intent(this, GameView.class);
        startActivity(it);
    }
    public void gotowash (View v){
        Intent it=new Intent(this, washgames.class);
        startActivity(it);
    }
    public void gotocard (View v){
        Intent it=new Intent(this, cardgame.class);
        startActivity(it);
    }
    public void gotovideo (View v){
        Intent it=new Intent(this, video_main.class);
        startActivity(it);
    }
}