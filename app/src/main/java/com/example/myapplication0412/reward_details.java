package com.example.myapplication0412;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class reward_details extends AppCompatActivity {

    private int requiredPoints; // 所需積分
    private int userPoints; // 使用者當前積分
    private int rewardId; // 獎品 ID

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reward_details);

        // 獲取從上級頁面傳遞的資料
        userPoints = getIntent().getIntExtra("userPoints", 0);
        requiredPoints = getIntent().getIntExtra("requiredPoints", 0); // 獲取所需積分
        rewardId = getIntent().getIntExtra("rewardId", 0); // 獲取獎品 ID

        // 顯示積分有效期
        TextView rewardExpiryDateTextView = findViewById(R.id.rewardExpiryDateTextView);
        rewardExpiryDateTextView.setText("兌換有效期: 2024年6月3日 - 2024年7月12日");

        // 顯示產品資訊
        TextView rewardProductInfoTextView = findViewById(R.id.rewardProductInfoTextView);
        ImageView rewardImageView = findViewById(R.id.rewardImageView);

        switch (rewardId) {
            case 1:
                rewardImageView.setImageResource(R.drawable.reward_image);
                rewardProductInfoTextView.setText("高品質不沾鍋，適合各類烹飪");
                break;
            case 2:
                rewardImageView.setImageResource(R.drawable.reward_image02);
                rewardProductInfoTextView.setText("高級清潔器，提升清潔效果");
                break;
            // 你可以添加更多的獎品資訊
        }

        // 顯示所需積分
        rewardExpiryDateTextView.setText("所需積分: " + requiredPoints + "分");

        // 設置兌換按鈕
        Button redeemButton = findViewById(R.id.redeemButton);
        if (userPoints >= requiredPoints) {
            redeemButton.setEnabled(true); // 使用者積分足夠時按鈕可用
            redeemButton.setOnClickListener(v -> {
                updatePointsAfterRedemption();
                saveExchangeHistory(); // 保存兌換記錄
                showSuccessDialog();
            });
        } else {
            redeemButton.setEnabled(false); // 積分不足時按鈕不可用
        }
    }

    // 更新積分並返回到前一個頁面
    private void updatePointsAfterRedemption() {
        Intent resultIntent = new Intent();
        resultIntent.putExtra("updatedPoints", userPoints - requiredPoints); // 返回更新後的積分
        setResult(RESULT_OK, resultIntent);
        finish(); // 關閉當前活動
    }

    // 保存兌換記錄到 SharedPreferences
    private void saveExchangeHistory() {
        SharedPreferences sharedPreferences = getSharedPreferences("ExchangeHistory", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        String record = getCurrentDateTime() + " - " + getRewardDescription() + " - " + requiredPoints + "分";
        String existingRecords = sharedPreferences.getString("history", "");
        editor.putString("history", existingRecords + "\n" + record);
        editor.apply();
    }

    private String getCurrentDateTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss", Locale.getDefault());
        return sdf.format(new Date()); // 獲取當前日期和時間
    }

    private String getRewardDescription() {
        switch (rewardId) {
            case 1:
                return "高品質不沾鍋";
            case 2:
                return "高級清潔器";
            default:
                return "未知獎品";
        }
    }

    // 顯示兌換成功的對話框
    private void showSuccessDialog() {
        new AlertDialog.Builder(this)
                .setTitle("兌換成功")
                .setMessage("恭喜！您已成功兌換獎勳。")
                .setPositiveButton("確定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        // 可在這裡處理其他邏輯，如返回主頁面或更新積分顯示等
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_info)
                .show();
    }
}
