package com.example.myapplication0412.Task;

import android.content.Context;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.myapplication0412.R;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.hdev.calendar.bean.DateInfo;
import com.hdev.calendar.view.MultiCalendarView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

public class TaskScheduled extends AppCompatActivity {
    class TaskAdapter extends ArrayAdapter<String> {
        List<String> tasksList;
        public TaskAdapter(@NonNull Context context, List<String> list) {
            super(context, R.layout.task_scheduled_listview, list);
            tasksList = list;
        }
        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            if (convertView == null)
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.task_scheduled_listview, parent, false);
            ((CheckedTextView)convertView.findViewById(R.id.ScheduledList_checkedText)).setText(tasksList.get(position));
            ((CheckedTextView)convertView.findViewById(R.id.ScheduledList_checkedText)).setChecked(((ListView) parent).isItemChecked(position));
            convertView.setOnClickListener(v -> ((ListView) parent).setItemChecked(position, !((ListView) parent).isItemChecked(position)));
            convertView.findViewById(R.id.ScheduledList_chooseImage).setOnClickListener(v -> showChooseDate(position));
            return convertView;
        }
    }
    TaskAdapter adapter;
    ArrayList<String> stringList = new ArrayList<>();
    ArrayList<ArrayList<DateInfo>> dateList = new ArrayList<>();
    ArrayList<String> documentIds = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.task_scheduled);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        ListView listView = findViewById(R.id.TaskScheduled_allList);
        adapter = new TaskAdapter(this, stringList);
        listView.setAdapter(adapter);
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        findViewById(R.id.TaskScheduled_removeImage).setOnClickListener(v -> deleteTask());
        findViewById(R.id.TaskScheduled_returnButton).setOnClickListener(v -> finish());
        loadScheduledTasks();
    }

    private void loadScheduledTasks() {
        FirebaseFirestore.getInstance().collection("Tasks").get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String taskName = document.getString("任務名稱");
                            List<Timestamp> timestamps = (List<Timestamp>) document.get("任務日期");
                            List<DateInfo> dateInfoList = new ArrayList<>();
                            for (Timestamp timestamp : Objects.requireNonNull(timestamps)) {
                                Calendar calendar = Calendar.getInstance();
                                calendar.setTimeInMillis(timestamp.toDate().getTime());
                                dateInfoList.add(new DateInfo(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH)));
                            }
                            stringList.add(taskName != null ? taskName : "null task name");
                            dateList.add(new ArrayList<>(dateInfoList));
                            documentIds.add(document.getId());
                        }
                        adapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(this, "Error getting tasks: " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void showChooseDate(int position) {
        List<DateInfo> oldSelected = dateList.get(position);
        View dialogView = getLayoutInflater().inflate(R.layout.task_add_calendar, null);
        MultiCalendarView mCalendarView = dialogView.findViewById(R.id.TaskAdd_CalendarView);
        mCalendarView.setDateRange(System.currentTimeMillis(), System.currentTimeMillis() + 365L * 24 * 60 * 60 * 1000, System.currentTimeMillis());
        mCalendarView.setSelectedDateList(oldSelected);
        mCalendarView.setOnMultiDateSelectedListener((view, selectedDays, selectedDates) -> {
            ((AlertDialog)view.getTag()).setMessage(selectedDates.isEmpty() ? "請選擇日期" : "已選擇" + selectedDates.size() + "天");
            return null;
        });
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("選擇日期")
                .setView(dialogView)
                .setMessage(oldSelected.isEmpty() ? "請選擇日期" : "已選擇" + oldSelected.size() + "天")
                .setPositiveButton("確定", null)
                .setCancelable(false)
                .create();
        dialog.setOnShowListener(dialogInterface -> dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(view -> {
            List<DateInfo> dateInfoList = mCalendarView.getSelectedDateList();
            if (!dateInfoList.isEmpty()) {
                oldSelected.clear();
                oldSelected.addAll(dateInfoList);
                updateFireStore(position, dateInfoList);
                dialog.dismiss();
            } else
                Toast.makeText(getApplicationContext(), "至少選擇一個日期", Toast.LENGTH_SHORT).show();
        }));
        mCalendarView.setTag(dialog);
        dialog.show();
    }

    private void updateFireStore(int position, List<DateInfo> newDates) {
        String documentId = documentIds.get(position);
        List<Timestamp> dateTimestamps = new ArrayList<>();
        for (DateInfo dateInfo : newDates) {
            Calendar calendar = Calendar.getInstance();
            calendar.set(dateInfo.getYear(), dateInfo.getMonth() - 1, dateInfo.getDay(), 0, 0, 0);
            long millis = calendar.getTimeInMillis();
            dateTimestamps.add(new Timestamp(millis / 1000, (int) (millis % 1000) * 1000000));
        }
        FirebaseFirestore.getInstance().collection("Tasks").document(documentId)
                .update("任務日期", dateTimestamps)
                .addOnSuccessListener(aVoid -> Toast.makeText(this, "Task updated successfully!", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(this, "Error updating task: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void deleteTask() {
        ListView listView = findViewById(R.id.TaskScheduled_allList);
        SparseBooleanArray checkedItemPositions = listView.getCheckedItemPositions();
        final int[] removedCount = {0};
        for (int i = listView.getCount() - 1; i >= 0; i--) {
            if (checkedItemPositions.get(i)) {
                String documentId = documentIds.get(i);
                int finalI = i;
                FirebaseFirestore.getInstance().collection("Tasks").document(documentId)
                        .delete()
                        .addOnSuccessListener(aVoid -> {
                            stringList.remove(finalI);
                            dateList.remove(finalI);
                            documentIds.remove(finalI);
                            removedCount[0]++;
                            adapter.notifyDataSetChanged();
                            Toast.makeText(this, removedCount[0] + " tasks removed.", Toast.LENGTH_LONG).show();
                        })
                        .addOnFailureListener(e -> Toast.makeText(this, "Error deleting task: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }
        }
        checkedItemPositions.clear();
    }
}