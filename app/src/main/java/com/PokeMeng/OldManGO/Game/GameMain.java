package com.PokeMeng.OldManGO.Game;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.PokeMeng.OldManGO.MainActivity;
import com.PokeMeng.OldManGO.R;
import com.PokeMeng.OldManGO.TaskManager;
import com.PokeMeng.OldManGO.Video.video_main;
import com.google.firebase.firestore.FirebaseFirestore;

public class GameMain extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_main);
        TaskManager taskManager = new TaskManager(FirebaseFirestore.getInstance(), "your_user_id");
        taskManager.checkAndCompleteTask("PlayedGame", result -> {
            if (!result) {
                Log.d("FireStore", "ChallengeCompleted not completed yet.");
                taskManager.updateTaskStatusForSteps(5);
                taskManager.markTaskAsCompleted("PlayedGame");
            }
            else Log.d("FireStore", "ChallengeCompleted already completed for today.");
        });
    }
    public void gotoMain(View v){
        Intent it=new Intent(this, MainActivity.class);
        startActivity(it);
    }
    public void gotoEye(View v){
        Intent it=new Intent(this, ColorGame.class);
        startActivity(it);
    }
    public void gotoFruit(View v){
        Intent it=new Intent(this, GameView.class);
        startActivity(it);
    }
    public void gotoWash(View v){
        Intent it=new Intent(this, WashGames.class);
        startActivity(it);
    }
    public void gotoCard(View v){
        Intent it=new Intent(this, CardGame.class);
        startActivity(it);
    }
    public void gotoVideo(View v){
        Intent it=new Intent(this, video_main.class);
        startActivity(it);
    }
}