package com.example.myapplication0412.Dailycheckin;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication0412.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Map;

import java.util.Calendar;
import com.nlf.calendar.Lunar;
import com.nlf.calendar.Solar;

import android.content.res.Configuration;

import java.util.Locale;

import com.hankcs.hanlp.HanLP;


public class Ca extends AppCompatActivity {

    private CalendarView calendarView;
    private Button button;
    private String selectedDate;
    private DatabaseReference databaseReference;
    private TextView dateDisplay;
    private FirebaseAuth auth;
    private FirebaseFirestore firestore;
    private CollectionReference checkinCollection;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 設置應用全局的Locale為繁體中文
        Locale locale = Locale.TAIWAN;
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getResources().updateConfiguration(config, getResources().getDisplayMetrics());

        //EdgeToEdge.enable(this);
        setContentView(R.layout.activity_ca);

        calendarView = findViewById(R.id.calendarView);
        button = findViewById(R.id.button);
        dateDisplay = findViewById(R.id.dateDisplay);

        // 初始化 Firebase 和 Firestore
        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        // 指定 Firestore 集合名稱
        checkinCollection = firestore.collection("DailyCheckin");

        // 獲取當前用戶
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            // 初始化簽到集合引用
            checkinCollection = firestore.collection("DailyCheckin")
                    .document(currentUser.getUid())
                    .collection("Checkins");
        } else {
            Toast.makeText(this, "請先登入", Toast.LENGTH_SHORT).show();
        }

        Calendar currentCalendar = Calendar.getInstance();
        int year = currentCalendar.get(Calendar.YEAR);
        int month = currentCalendar.get(Calendar.MONTH);
        int dayOfMonth = currentCalendar.get(Calendar.DAY_OF_MONTH);
        updateDateDisplay(year, month, dayOfMonth);

        // 初始化按鈕狀態
        if (isToday(year, month, dayOfMonth)) {
            button.setEnabled(true);  // 今天可以簽到
        } else {
            button.setEnabled(false); // 其他日子不能簽到
        }

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                selectedDate=Integer.toString(year)+Integer.toString(month+1)+Integer.toString(dayOfMonth);
                updateDateDisplay(year, month , dayOfMonth);

                // 檢查選擇的日期是否為今天
                if (isToday(year, month, dayOfMonth)) {
                    button.setEnabled(true);  // 今天可以簽到
                } else {
                    button.setEnabled(false); // 其他日子不能簽到
                    Toast.makeText(Ca.this, "無法對過去或未來的日期簽到", Toast.LENGTH_SHORT).show();
                }


                selectedDate = year + "-" + (month + 1) + "-" + dayOfMonth;
                updateDateDisplay(year, month , dayOfMonth);
            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedDate != null) {
                    checkIfAlreadyCheckedIn(selectedDate); // 檢查是否已簽到
                } else {
                    Toast.makeText(Ca.this, "請先選擇日期", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //databaseReference= FirebaseDatabase.getInstance().getReference("CalendarView");



    }
    private void checkIfAlreadyCheckedIn(String date) {
        // 檢查 Firestore 中是否已有該日期的簽到紀錄
        if (checkinCollection != null) {
            checkinCollection.document(date).get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    // 如果該日期的紀錄已存在，提示已經簽到過
                    Toast.makeText(Ca.this, "今天已經簽到過", Toast.LENGTH_SHORT).show();
                } else {
                    // 如果該日期的紀錄不存在，進行簽到
                    markAsCheckedIn(date);
                }
            }).addOnFailureListener(e -> {
                Toast.makeText(Ca.this, "檢查失敗: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            });
        } else {
            Toast.makeText(this, "請先登入", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateDateDisplay(int year, int month, int dayOfMonth) {
        selectedDate = year + "-" + (month + 1) + "-" + dayOfMonth;

        // 使用lunar-java庫進行公曆轉農曆
        Solar solar = new Solar(year, month+1, dayOfMonth);
        Lunar lunar = solar.getLunar();  // 轉換為農曆日期

        String lunarFestival = lunar.getFestivals().toString();
        String solarFestival = solar.getFestivals().toString();
        String jieQi = lunar.getJieQi();

        // 如果 lunar.toString() 也是簡體中文，則轉換
        String lunarDate = HanLP.convertToTraditionalChinese(lunar.toString());

        // 使用 HanLP 進行簡繁轉換
        lunarFestival = HanLP.convertToTraditionalChinese(lunarFestival);
        solarFestival = HanLP.convertToTraditionalChinese(solarFestival);
        jieQi = HanLP.convertToTraditionalChinese(jieQi);

        // 自定義節日修改
        if (month == 4 && dayOfMonth == 1) { // 如果是 4/1
            solarFestival = "愚人節"; // 修改為愚人節
        }
        if (month == 3 && dayOfMonth == 4) { // 如果是 4/4
            solarFestival = "兒童節"; // 修改為兒童節
        }
        if (month == 4 && dayOfMonth == 1) { // 如果是 5/1
            solarFestival = "勞動節"; // 修改為勞動節
        }
        if (month == 4 && dayOfMonth == 4) { // 如果是 5/4
            solarFestival = "青年節"; // 修改為青年節
        }
        if (month == 8 && dayOfMonth == 28) { // 如果是 9/28
            solarFestival = "教師節"; // 修改為教師節
        }
        if (month == 9 && dayOfMonth == 10) { // 如果是 10/10
            solarFestival = "國慶節"; // 修改為國慶節
        }
        if (month == 10 && dayOfMonth == 31) { // 如果是 10/31
            solarFestival = "萬聖節前夜"; // 修改為萬聖節前夜
        }
        if (month == 11 && dayOfMonth == 1) { // 如果是 11/1
            solarFestival = "萬聖節"; // 修改為萬聖節
        }
        if (month == 12 && dayOfMonth == 24) { // 如果是 12/24
            solarFestival = "平安夜"; // 修改為平安夜
        }
        if (month == 12 && dayOfMonth == 25) { // 如果是 12/25
            solarFestival = "聖誕節"; // 修改為聖誕節
        }
        else if (month == 8 && dayOfMonth == 10) { // 如果是 8/10
            solarFestival = ""; // 清空或顯示其他資訊
        } else if (month == 5 && dayOfMonth == 1) { // 如果是 6/1
            solarFestival = ""; // 不顯示兒童節
        } else if (month == 6 && dayOfMonth == 1) { // 如果是 7/1
            solarFestival = ""; // 不顯示建黨節
        } else if (month == 7 && dayOfMonth == 1) { // 如果是 8/1
            solarFestival = ""; // 不顯示建軍節
        } else if (month == 9 && dayOfMonth == 1) { // 如果是 8/1
            solarFestival = ""; // 不顯示建軍節
        }

// 構建顯示字串
        String displayText = "公曆: " + selectedDate + "\n農曆: " + lunar.toString();

// 如果有農曆節日則顯示
        if (!lunarFestival.isEmpty()) {
            displayText += "\n農曆節日: " + lunarFestival;
        }
        // 檢查節氣是否為清明
        if ("清明".equals(jieQi)) {
            displayText += "\n農曆節日: [清明節]"; // 在農曆節日中顯示清明
        }

// 如果有公曆節日則顯示
        if (!solarFestival.isEmpty()) {
            displayText += "\n公曆節日: " + solarFestival;
        } else {
            displayText += "\n公曆節日: []"; // 顯示空的公曆節日
        }


// 如果有節氣則顯示
        if (!jieQi.isEmpty()) {
            displayText += "\n節氣: " + jieQi;
        }

// 更新TextView顯示結果
        dateDisplay.setText(displayText);

    }

    // 檢查所選日期是否為今天
    private boolean isToday(int year, int month, int dayOfMonth) {
        Calendar today = Calendar.getInstance();
        return (year == today.get(Calendar.YEAR)) &&
                (month == today.get(Calendar.MONTH)) &&
                (dayOfMonth == today.get(Calendar.DAY_OF_MONTH));
    }


    // 標記為已簽到
    private void markAsCheckedIn(String date) {
        // 以傳入的日期參數進行簽到邏輯
        if (checkinCollection != null) {
            // 獲取當前時間
            Calendar calendar = Calendar.getInstance();
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);
            int second = calendar.get(Calendar.SECOND);
            String checkTime = String.format("%02d:%02d:%02d", hour, minute, second); // 轉換成 "HH:mm:ss" 格式

            // 建立簽到資料
            Map<String, Object> checkinData = new HashMap<>();
            checkinData.put("date", date); // 簽到日期
            checkinData.put("timestamp", FieldValue.serverTimestamp()); // 伺服器時間戳記
            checkinData.put("status", "已簽到"); // 簽到狀態
            checkinData.put("check_time", checkTime); // 簽到時間

            // 將資料寫入 Firestore
            checkinCollection.document(date).set(checkinData)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(Ca.this, "簽到成功", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(Ca.this, "簽到失敗: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        }
    }




}
