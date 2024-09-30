package com.PokeMeng.OldManGO.medicined;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.NumberPicker;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.PokeMeng.OldManGO.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class DayInterval extends BottomSheetDialogFragment {

    public interface OnIntervalSelectedListener {
        void onIntervalSelected(String interval);
    }

    private Map<Integer, NumberPicker> numberPickerMap;
    private Map<Integer, String> radioButtonMap;
    private ImageButton backButton;
    private Button checkButton;
    private OnIntervalSelectedListener listener;

    public void setOnIntervalSelectedListener(OnIntervalSelectedListener listener) {
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.day_interval, container, false);
        setupViews(view);
        return view;
    }

    private void setupViews(View view) {
        NumberPicker npday = view.findViewById(R.id.numberPicker_day);
        NumberPicker npweek = view.findViewById(R.id.numberPicker_week);
        NumberPicker npmonth = view.findViewById(R.id.numberPicker_month);
        RadioGroup radioGroup = view.findViewById(R.id.radioGroup);
        backButton = view.findViewById(R.id.imageButton9);
        checkButton = view.findViewById(R.id.checkButton);

        // 初始化 NumberPicker Map
        numberPickerMap = new HashMap<>();
        numberPickerMap.put(R.id.radioButton, npday);
        numberPickerMap.put(R.id.radioButton2, npweek);
        numberPickerMap.put(R.id.radioButton3, npmonth);

        // 初始化 RadioButton Map
        radioButtonMap = new HashMap<>();
        radioButtonMap.put(R.id.radioButton, "天");
        radioButtonMap.put(R.id.radioButton2, "週");
        radioButtonMap.put(R.id.radioButton3, "月");

        // 設置 NumberPickers
        Date date = new Date();
        npday.setMaxValue(365);
        npday.setMinValue(1);
        npday.setValue(Integer.parseInt(new SimpleDateFormat("dd").format(date)));

        npweek.setMaxValue(52);
        npweek.setMinValue(1);
        npweek.setValue(Integer.parseInt(new SimpleDateFormat("w").format(date))); // 使用 'w' 來獲取週數

        npmonth.setMaxValue(12);
        npmonth.setMinValue(1);
        npmonth.setValue(Integer.parseInt(new SimpleDateFormat("MM").format(date)));

        // 初始隱藏所有 NumberPickers
        npday.setVisibility(View.GONE);
        npweek.setVisibility(View.GONE);
        npmonth.setVisibility(View.GONE);

        // 設置 RadioGroup 監聽器
        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            for (Map.Entry<Integer, NumberPicker> entry : numberPickerMap.entrySet()) {
                NumberPicker picker = entry.getValue();
                if (entry.getKey().equals(checkedId)) {
                    picker.setVisibility(View.VISIBLE);
                } else {
                    picker.setVisibility(View.GONE);
                }
            }
        });

        // 設置 backButton 點擊事件
        backButton.setOnClickListener(v -> dismiss());

        // 設置 checkButton 點擊事件
        checkButton.setOnClickListener(v -> {
            NumberPicker selectedPicker = null;
            String unit = "";
            for (Map.Entry<Integer, NumberPicker> entry : numberPickerMap.entrySet()) {
                if (entry.getValue().getVisibility() == View.VISIBLE) {
                    selectedPicker = entry.getValue();
                    unit = radioButtonMap.get(entry.getKey());
                    break;
                }
            }
            if (selectedPicker != null && listener != null) {
                int value = selectedPicker.getValue();
                String interval = "每 "+ value + " " + unit + "一次";
                listener.onIntervalSelected(interval);
            }
            dismiss();
        });
    }
}
