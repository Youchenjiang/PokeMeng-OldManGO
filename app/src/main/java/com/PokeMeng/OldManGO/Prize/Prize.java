package com.PokeMeng.OldManGO.Prize;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.PokeMeng.OldManGO.R;

public class Prize extends AppCompatActivity {

    private static final int REQUEST_CODE_REWARD_DETAILS = 1; // 請求碼
    private int currentPoints = 40100; // 模擬用戶當前積分
    String pointsExpiryDate = "2024年12月31日"; // 積分到期日

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.prize);

        // 設置顯示積分的 TextView
        TextView pointsTextView = findViewById(R.id.pointsTextView);
        pointsTextView.setText("當前積分: " + currentPoints);

        // 設置積分到期日
        TextView expiryDateTextView = findViewById(R.id.expiryDateTextView);
        expiryDateTextView.setText("積分有效期至: " + pointsExpiryDate);



        // 設置第一個獎品的點擊事件
        ImageView rewardImageView1 = findViewById(R.id.rewardImageView1);
        rewardImageView1.setOnClickListener(v -> {
            openRewardDetails(1, 21000); // 獎品1所需積分為21000
        });

        // 設置第二個獎品的點擊事件
        ImageView rewardImageView2 = findViewById(R.id.rewardImageView2);
        rewardImageView2.setOnClickListener(v -> {
            openRewardDetails(2, 15000); // 獎品2所需積分為15000
        });

        // 設置查看兌換記錄按鈕的點擊事件
        Button viewHistoryButton = findViewById(R.id.viewHistoryButton);
        viewHistoryButton.setOnClickListener(v -> {
            Intent intent = new Intent(Prize.this, ExchangeHistory.class);
            startActivity(intent);
        });

        // 设置button10和button9的点击事件
        Button button10 = findViewById(R.id.button10);
        Button button9 = findViewById(R.id.button9);

        button10.setOnClickListener(v -> {
            // 示例功能: 进行相应操作
            handleRedeem(21000); // 假设button10对应21000积分的兑换
        });

        button9.setOnClickListener(v -> {
            // 示例功能: 进行相应操作
            handleRedeem(15000); // 假设button9对应15000积分的兑换
        });
    }

    private void handleRedeem(int requiredPoints) {
        if (currentPoints >= requiredPoints) {
            openRewardDetails(1, requiredPoints); // 调用打开奖励详情的方法
        } else {
            // 显示提示用户积分不足
            // 您可以使用Toast或AlertDialog来提示
        }
    }

    // 跳转到详细页面的方法





    // 跳轉到詳細頁面的方法
    private void openRewardDetails(int rewardId, int requiredPoints) {
        Intent intent = new Intent(this, RewardDetails.class);
        intent.putExtra("userPoints", currentPoints); // 傳遞當前積分到 reward_details 活動
        intent.putExtra("rewardId", rewardId); // 傳遞獎品 ID
        intent.putExtra("requiredPoints", requiredPoints); // 傳遞所需積分
        startActivityForResult(intent, REQUEST_CODE_REWARD_DETAILS);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_REWARD_DETAILS && resultCode == RESULT_OK) {
            // 獲取更新後的積分
            currentPoints = data.getIntExtra("updatedPoints", currentPoints);

            // 更新顯示的積分
            TextView pointsTextView = findViewById(R.id.pointsTextView);
            pointsTextView.setText("當前積分: " + currentPoints);
        }
    }

}
