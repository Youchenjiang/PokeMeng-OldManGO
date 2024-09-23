package com.example.myapplication0412;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.util.Calendar;
import com.nlf.calendar.Lunar;
import com.nlf.calendar.Solar;

import android.content.res.Configuration;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import com.hankcs.hanlp.HanLP;


public class Ca extends AppCompatActivity {

    private CalendarView calendarView;
    private Button button;
    private String selectedDate;
    private DatabaseReference databaseReference;
    private TextView dateDisplay;


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

        databaseReference= FirebaseDatabase.getInstance().getReference("CalendarView");

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                markAsCheckedIn();
            }
        });

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


    private void markAsCheckedIn() {
        if (selectedDate != null) {
            databaseReference.child(selectedDate).setValue("今日已簽到")
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(Ca.this, "签到成功", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(Ca.this, "签到失败: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        }
    }
}
