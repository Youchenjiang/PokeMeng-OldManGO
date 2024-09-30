package com.PokeMeng.OldManGO.medicined;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.PokeMeng.OldManGO.R;
import com.PokeMeng.OldManGO.medicined.BottomSheet;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class BottomWeekdaySheet extends BottomSheetDialogFragment {

    private ToggleButton toggleSunday;
    private ToggleButton toggleMonday;
    private ToggleButton toggleTuesday;
    private ToggleButton toggleWednesday;
    private ToggleButton toggleThursday;
    private ToggleButton toggleFriday;
    private ToggleButton toggleSaturday;
    private Button selectAllButton, checkButton;
    private ImageButton backButton;

    // 回調接口
    public interface OnWeekdaySelectedListener {
        void onWeekdaySelected(String selectedDays);
    }

    private OnWeekdaySelectedListener listener;

    // 設置接口的方法
    public void setOnWeekdaySelectedListener(OnWeekdaySelectedListener listener) {
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bottom_weekday, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 初始化 ToggleButton
        toggleSunday = view.findViewById(R.id.toggleSunday);
        toggleMonday = view.findViewById(R.id.toggleMonday);
        toggleTuesday = view.findViewById(R.id.toggleTuesday);
        toggleWednesday = view.findViewById(R.id.toggleWednesday);
        toggleThursday = view.findViewById(R.id.toggleThursday);
        toggleFriday = view.findViewById(R.id.toggleFriday);
        toggleSaturday = view.findViewById(R.id.toggleSaturday);

        checkButton = view.findViewById(R.id.checkButton);
        backButton = view.findViewById(R.id.imageButton9);

        // 初始化 '每天' 按鈕
        selectAllButton = view.findViewById(R.id.button5);

        setupToggleButtons();

        selectAllButton.setOnClickListener(v -> {
            boolean allSelected = areAllToggleButtonsSelected();
            selectAllToggleButtons(!allSelected);
        });

        backButton.setOnClickListener(v -> dismiss()); // 點擊返回按鈕關閉 BottomSheet

        // 設置確認按鈕的點擊事件
        checkButton.setOnClickListener(v -> onDaysSelected());
    }

    // 設置每個 ToggleButton 的選擇變化
    private void setupToggleButtons() {
        setupToggleButton(toggleSunday);
        setupToggleButton(toggleMonday);
        setupToggleButton(toggleTuesday);
        setupToggleButton(toggleWednesday);
        setupToggleButton(toggleThursday);
        setupToggleButton(toggleFriday);
        setupToggleButton(toggleSaturday);
    }

    private void setupToggleButton(ToggleButton toggleButton) {
        toggleButton.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                toggleButton.setBackgroundResource(R.drawable.toggle_button_selected_background);
            } else {
                toggleButton.setBackgroundResource(R.drawable.toggle_button_unselected_background);
            }
        });
    }

    // 檢查是否所有的 ToggleButton 都已經選中
    private boolean areAllToggleButtonsSelected() {
        return toggleSunday.isChecked() && toggleMonday.isChecked() &&
                toggleTuesday.isChecked() && toggleWednesday.isChecked() &&
                toggleThursday.isChecked() && toggleFriday.isChecked() &&
                toggleSaturday.isChecked();
    }

    // 選擇或取消選擇所有的 ToggleButton
    private void selectAllToggleButtons(boolean isChecked) {
        toggleSunday.setChecked(isChecked);
        toggleMonday.setChecked(isChecked);
        toggleTuesday.setChecked(isChecked);
        toggleWednesday.setChecked(isChecked);
        toggleThursday.setChecked(isChecked);
        toggleFriday.setChecked(isChecked);
        toggleSaturday.setChecked(isChecked);
    }

    // 當點擊確認按鈕時，將選中的日期傳遞給 MainActivity2
    private void onDaysSelected() {
        StringBuilder selectedDays = new StringBuilder();

        if (toggleSunday.isChecked()) selectedDays.append("日");
        if (toggleMonday.isChecked()) selectedDays.append("一");
        if (toggleTuesday.isChecked()) selectedDays.append("二");
        if (toggleWednesday.isChecked()) selectedDays.append("三");
        if (toggleThursday.isChecked()) selectedDays.append("四");
        if (toggleFriday.isChecked()) selectedDays.append("五");
        if (toggleSaturday.isChecked()) selectedDays.append("六");

        // 通知 listener 傳遞選中的日期
        if (listener != null) {
            listener.onWeekdaySelected(selectedDays.toString().trim());
        }

        // 關閉 BottomWeekdaySheet
        dismiss();

        // 通知父層的 BottomSheet 一起關閉
        if (getParentFragment() instanceof BottomSheet) {
            ((BottomSheet) getParentFragment()).dismiss();
        }
    }

}
