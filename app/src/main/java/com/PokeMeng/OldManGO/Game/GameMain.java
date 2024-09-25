package com.PokeMeng.OldManGO.Game;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.PokeMeng.OldManGO.MainActivity;
import com.PokeMeng.OldManGO.R;
import com.PokeMeng.OldManGO.Video.video_main;

public class GameMain extends AppCompatActivity {

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
        Intent it=new Intent(this, ColorGame.class);
        startActivity(it);
    }
    public void gotofruit (View v){
        Intent it=new Intent(this, GameView.class);
        startActivity(it);
    }
    public void gotowash (View v){
        Intent it=new Intent(this, WashGames.class);
        startActivity(it);
    }
    public void gotocard (View v){
        Intent it=new Intent(this, CardGame.class);
        startActivity(it);
    }
    public void gotovideo (View v){
        Intent it=new Intent(this, video_main.class);
        startActivity(it);
    }
}