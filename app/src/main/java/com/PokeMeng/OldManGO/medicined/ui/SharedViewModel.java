package com.PokeMeng.OldManGO.medicined.ui;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.SavedStateHandle;

import com.PokeMeng.OldManGO.medicined.Medicine;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Date;

public class SharedViewModel extends ViewModel {
    private final SavedStateHandle stateHandle;
    private final MutableLiveData<List<Medicine>> takenMedicines = new MutableLiveData<>(new ArrayList<>());
    private final Map<String, List<Medicine>> medicinesByDate = new HashMap<>();
    private final MutableLiveData<List<Medicine>> historyMedicines = new MutableLiveData<>(new ArrayList<>());
    private final List<Integer> clickedMedicineIds = new ArrayList<>();
    private final MutableLiveData<List<Medicine>> clickedMedicinesFromDashboard = new MutableLiveData<>(new ArrayList<>());

    private final DatabaseReference databaseReference; // Firebase Database reference

    public SharedViewModel(SavedStateHandle stateHandle) {
        this.stateHandle = stateHandle;
        this.databaseReference = FirebaseDatabase.getInstance().getReference("medicines"); // Initialize Firebase reference

        // Initialize medicines list
        if (stateHandle.get("medicines") == null) {
            stateHandle.set("medicines", new ArrayList<>()); // Initialize medicine list as empty
        }
    }

    public LiveData<List<Medicine>> getMedicines() {
        return stateHandle.getLiveData("medicines", new ArrayList<>());
    }

    // 添加药物
    public void addMedicine(Medicine medicine) {
        // 生成唯一的 Firebase ID 并哈希为整数
        String key = databaseReference.push().getKey();
        if (key != null) {
            medicine.setId(key.hashCode()); // 将 ID 设为哈希值
            List<Medicine> currentMedicines = getMedicines().getValue();
            if (currentMedicines != null) {
                currentMedicines.add(medicine);
                stateHandle.set("medicines", currentMedicines);
                databaseReference.child(key).setValue(medicine); // 将药物保存到 Firebase
                addClickedMedicineId(medicine.getId());
                Log.d("SharedViewModel", "Added medicine: " + medicine.getName());
            }
        }
    }

    // 更新药物信息
    public void updateMedicine(Medicine medicine) {
        List<Medicine> currentMedicines = getMedicines().getValue();
        if (currentMedicines != null) {
            for (int i = 0; i < currentMedicines.size(); i++) {
                if (currentMedicines.get(i).getId() == medicine.getId()) {
                    currentMedicines.set(i, medicine);
                    stateHandle.set("medicines", currentMedicines);
                    databaseReference.child(String.valueOf(medicine.getId())).setValue(medicine); // 更新 Firebase
                    addClickedMedicineId(medicine.getId());
                    break;
                }
            }
        }
    }

    // 删除药物
    public void removeMedicine(int id) {
        List<Medicine> currentMedicines = getMedicines().getValue();
        if (currentMedicines != null) {
            currentMedicines.removeIf(medicine -> medicine.getId() == id);
            stateHandle.set("medicines", currentMedicines);
            databaseReference.child(String.valueOf(id)).removeValue(); // 从 Firebase 删除
            clickedMedicineIds.removeIf(clickedId -> clickedId == id);
            Log.d("SharedViewModel", "Removed medicine with ID: " + id);
        }
    }

    private MutableLiveData<Date> clickedDate = new MutableLiveData<>(); // 用于保存点击日期

    // 设置点击的日期
    public void setClickedDate(Date date) {
        clickedDate.setValue(date);
    }

    // 获取点击的日期
    public LiveData<Date> getClickedDate() {
        return clickedDate;
    }

    // 添加点击的药物 ID
    public void addClickedMedicineId(int id) {
        if (!clickedMedicineIds.contains(id)) {
            clickedMedicineIds.add(id);
        }
    }

    // 获取点击的药物 ID 列表
    public List<Integer> getClickedMedicineIds() {
        return clickedMedicineIds;
    }

    // 添加点击的药物到从 Dashboard 点击的药物列表
    public void addClickedMedicineFromDashboard(Medicine medicine) {
        List<Medicine> currentClickedMedicines = clickedMedicinesFromDashboard.getValue();
        if (currentClickedMedicines != null) {
            currentClickedMedicines.add(medicine);
            clickedMedicinesFromDashboard.setValue(currentClickedMedicines);
        }
    }

    // 获取从 Dashboard 点击的药物列表
    public LiveData<List<Medicine>> getClickedMedicinesFromDashboard() {
        return clickedMedicinesFromDashboard;
    }

    public LiveData<List<Medicine>> getMedicinesByDateFromDashboard(String date) {
        MutableLiveData<List<Medicine>> medicinesByDate = new MutableLiveData<>();

        // 获取从 DashboardFragment 中点击的药物
        List<Medicine> currentClickedMedicines = clickedMedicinesFromDashboard.getValue();
        List<Medicine> filteredMedicines = new ArrayList<>();

        if (currentClickedMedicines != null) {
            for (Medicine medicine : currentClickedMedicines) {
                // 假设 Medicine 类有 getTakenDate 方法返回服药日期
                if (medicine.getTakenDate().equals(date)) {
                    filteredMedicines.add(medicine);
                }
            }
        }

        medicinesByDate.setValue(filteredMedicines);
        return medicinesByDate;
    }

    // 记录已服用药物
    public void addTakenMedicine(Medicine medicine) {
        List<Medicine> currentTaken = takenMedicines.getValue();
        if (currentTaken == null) {
            currentTaken = new ArrayList<>();
        }

        // 记录服用日期
        String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Calendar.getInstance().getTime());
        medicine.setTakenDate(currentDate); // 假设你已经在 Medicine 类中添加了这个方法
        currentTaken.add(medicine);
        takenMedicines.setValue(currentTaken); // 更新已服用药品列表

        // 将药物添加到历史记录
        List<Medicine> currentHistory = historyMedicines.getValue();
        if (currentHistory == null) {
            currentHistory = new ArrayList<>();
        }
        currentHistory.add(medicine);
        historyMedicines.setValue(currentHistory); // 更新历史记录

        Log.d("SharedViewModel", "Added taken medicine on " + currentDate + ": " + medicine.getName());

        // 更新药物按日期记录
        medicinesByDate.putIfAbsent(currentDate, new ArrayList<>());
        medicinesByDate.get(currentDate).add(medicine);

        // 记录点击的药物 ID
        addClickedMedicineId(medicine.getId());
    }

    // 获取已服用药物列表
    public LiveData<List<Medicine>> getTakenMedicines() {
        return takenMedicines;
    }

    // 获取历史记录药物列表
    public LiveData<List<Medicine>> getHistoryMedicines() {
        return historyMedicines;
    }

    // 根据日期获取药物列表
    public LiveData<List<Medicine>> getMedicinesByDate(String date) {
        MutableLiveData<List<Medicine>> liveData = new MutableLiveData<>();
        liveData.setValue(medicinesByDate.getOrDefault(date, new ArrayList<>()));
        return liveData;
    }
}
