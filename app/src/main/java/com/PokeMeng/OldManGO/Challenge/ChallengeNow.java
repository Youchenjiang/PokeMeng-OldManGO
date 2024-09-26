package com.PokeMeng.OldManGO.Challenge;

import static android.content.ContentValues.TAG;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.PokeMeng.OldManGO.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ChallengeNow extends AppCompatActivity {

    private StepUpdateReceiver stepUpdateReceiver;
    private boolean isReceiverRegistered = false;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate called");
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
        registerReceiver(stepUpdateReceiver, new IntentFilter("com.PokeMeng.OldManGO.STEP_UPDATE"), Context.RECEIVER_NOT_EXPORTED); // 註冊廣播接收器
        isReceiverRegistered = true;
    }
    private void setNowStep(int step_now, int duration){
        int step_goal = 500;
        ChallengeNowProgressBar challengeNowProgressBar = findViewById(R.id.now_nowCircularProgressBar);
        challengeNowProgressBar.setText("目前步數：" + step_now + "步" + "\n" + "目標步數：" + step_goal + "步");
        challengeNowProgressBar.setProgress((float) step_now / step_goal * 100);
        challengeNowProgressBar.setDuration(duration);
    }
    private void getStepList(FireStoreCallback callback) {
        String userId = "your_user_id"; // Replace with actual user ID

        db.collection("Users").document(userId).collection("StepList").get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                List<ChallengeHistoryStep> taskList = new ArrayList<>();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    ChallengeHistoryStep challengeHistoryStep = document.toObject(ChallengeHistoryStep.class);
                    challengeHistoryStep.setStepDate(document.getId()); // Set the document ID as the step date
                    taskList.add(challengeHistoryStep);
                }
                callback.onCallback(taskList);
            } else {
                Log.w("FireStore", "Error getting documents.", task.getException());
            }
        });
    }
    private void showHistory() {
        View dialogView = getLayoutInflater().inflate(R.layout.challenge_now_history, null);
        setHistory(dialogView);
        new AlertDialog.Builder(this).setTitle("歷史紀錄").setView(dialogView).setPositiveButton("確定", null).create().show();
    }
    private void setHistory(View dialogView) {
        getStepList(messages -> {
            ArrayAdapter<ChallengeHistoryStep> adapter = new ArrayAdapter<ChallengeHistoryStep>(this, R.layout.challenge_now_history_setview, messages) {
                @NonNull
                @Override
                public View getView(int position, View convertView, @NonNull ViewGroup parent) {
                    convertView = convertView == null ? LayoutInflater.from(getContext()).inflate(R.layout.challenge_now_history_setview, parent, false) : convertView;
                    ChallengeHistoryStep currentMessage = getItem(position); // Get the current message
                    ((TextView) convertView.findViewById(R.id.Listview_timeText)).setText(Objects.requireNonNull(currentMessage).getStepDate()); // Set the date text
                    ((TextView) convertView.findViewById(R.id.Listview_numberText)).setText(getString(R.string.challenge_DateStep, Objects.requireNonNull(currentMessage).getStepNumber())); // Set their text
                    return convertView;
                }
            };
            ((ListView) dialogView.findViewById(R.id.history_listview)).setAdapter(adapter);
        });
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isReceiverRegistered) unregisterReceiver(stepUpdateReceiver); // 取消註冊廣播接收器
    }
    private class StepUpdateReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null && "com.PokeMeng.OldManGO.STEP_UPDATE".equals(intent.getAction()))
                setNowStep(intent.getIntExtra("steps", 0), 1);
        }
    }
    private interface FireStoreCallback {
        void onCallback(List<ChallengeHistoryStep> list);
    }
    public static class ChallengeHistoryStep {
        private String stepDate; // Add this field
        int stepNumber;
        public ChallengeHistoryStep(int stepNumber) {
            this.stepNumber = stepNumber;
        }
        public int getStepNumber() { return stepNumber;}
        public String getStepDate() { return stepDate;}
        public void setStepDate(String stepDate) { this.stepDate = stepDate;}
    }




    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart called");
        // Start or resume any tasks that need to be done when the activity is visible
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume called");
        // Resume any paused tasks or refresh the UI
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause called");
        // Pause any ongoing tasks or release resources that are not needed when the activity is not in the foreground
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop called");
        // Stop any tasks that should not run when the activity is not visible
    }

}