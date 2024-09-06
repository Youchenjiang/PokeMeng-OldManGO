package com.example.myapplication0412.Task;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.myapplication0412.R;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.IgnoreExtraProperties;
import com.google.firebase.firestore.PropertyName;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.hdev.calendar.bean.DateInfo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class TaskAll extends AppCompatActivity {
    private DateInfo nowChooseDate;
    private ActivityResultLauncher<Intent> createTask;
    private ArrayList<String> stringList;
    private ArrayList<DateInfo> dateList;
    TaskAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.task_all);
        initFields();
        setupWindowInsets();
        setupButtons();
        loadTodaySchedule();
        setupDateSpinner();
        //tryConnectFirebase();
    }
    private void initFields() {
        nowChooseDate = new DateInfo(Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH) + 1, Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
        createTask = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> Toast.makeText(this, result.getResultCode() == Activity.RESULT_OK ? "成功新增" : "取消新增", Toast.LENGTH_LONG).show());
        stringList = new ArrayList<>();
    }
    private void setupWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
    private void setupButtons() {
        findViewById(R.id.TaskAll_returnButton).setOnClickListener(v -> finish());
        findViewById(R.id.TaskAll_addImage).setOnClickListener(v -> createTask.launch(new Intent(this, TaskAdd.class)));
        findViewById(R.id.TaskAll_scheduledButton).setOnClickListener(v -> startActivity(new Intent(this, TaskScheduled.class)));
    }
    private void setupDateSpinner() {
        loadDateSpinner();
        ((Spinner)findViewById(R.id.TaskAll_dateSpinner)).setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                stringList.clear();
                nowChooseDate = dateList.get(position);
                loadScheduledTasks(nowChooseDate);
                checkAndLoadTaskStatus();
            }
            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {}
        });
    }
    /*private void tryConnectFirebase() {
        try {
            //FirebaseDatabase.getInstance().setPersistenceEnabled(true);
            Map<String,String> data = new HashMap<>();
            data.put()
        } catch (Exception e) {
            Toast.makeText(this, "無法連接Firebase", Toast.LENGTH_SHORT).show();
        }
    }*/
    /*private void loadDateSpinner() {
        dateList = new ArrayList<>();
        ArrayList<Object> tasksSet = new TinyDB(this).getListObject("TaskList", TaskClass.class);
        if (tasksSet == null) tasksSet = new ArrayList<>();
        ArrayList<TaskClass> tasks = new ArrayList<>();
        for (Object taskObject : tasksSet)
            if (taskObject instanceof TaskClass) // Ensure safe casting
                tasks.add((TaskClass) taskObject);
        for (TaskClass task : tasks)
            for (DateInfo dateInfo : task.getTaskDate())
                if (!dateList.contains(dateInfo))
                    dateList.add(dateInfo);
        if (!dateList.contains(nowChooseDate))
            dateList.add(nowChooseDate);
        dateList.sort(this::compareDates);
        ArrayList<String> dateListString = new ArrayList<>();
        for (DateInfo dateInfo : dateList)
            dateListString.add(dateInfo.getYear() + "/" + dateInfo.getMonth() + "/" + dateInfo.getDay());
        Spinner dateSpinner = findViewById(R.id.TaskAll_dateSpinner);
        dateSpinner.setAdapter(new CustomSpinnerAdapter(this, dateListString, dateList.indexOf(nowChooseDate), Color.WHITE));
        dateSpinner.setSelection(dateList.indexOf(nowChooseDate));
    }*/
    private void loadDateSpinner() {
        dateList = new ArrayList<>();
        FirebaseFirestore.getInstance().collection("Tasks").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    List<Timestamp> taskDates = (List<Timestamp>) document.get("任務日期");
                    if (taskDates != null) {
                        for (Timestamp timestamp : taskDates) {
                            Calendar calendar = Calendar.getInstance();
                            calendar.setTime(timestamp.toDate());
                            DateInfo dateInfo = new DateInfo(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH));
                            if (!dateList.contains(dateInfo)) {
                                dateList.add(dateInfo);
                            }
                        }
                    }
                }
                if (!dateList.contains(nowChooseDate)) {
                    dateList.add(nowChooseDate);
                }
                dateList.sort(this::compareDates);
                ArrayList<String> dateListString = new ArrayList<>();
                for (DateInfo dateInfo : dateList) {
                    dateListString.add(dateInfo.getYear() + "/" + dateInfo.getMonth() + "/" + dateInfo.getDay());
                }
                Spinner dateSpinner = findViewById(R.id.TaskAll_dateSpinner);
                dateSpinner.setAdapter(new CustomSpinnerAdapter(this, dateListString, dateList.indexOf(nowChooseDate), Color.WHITE));
                dateSpinner.setSelection(dateList.indexOf(nowChooseDate));
            } else {
                Log.w("TaskRead", "Error getting documents.", task.getException());
            }
        });
    }
    private int compareDates(DateInfo d1, DateInfo d2) {
        if (d1.getYear() != d2.getYear()) return d1.getYear() - d2.getYear();
        if (d1.getMonth() != d2.getMonth()) return d1.getMonth() - d2.getMonth();
        return d1.getDay() - d2.getDay();
    }
    /*private void checkAndLoadTaskStatus() {
        TaskStatus savedStatus = new TinyDB(this).getObject("TaskStatus_" + nowChooseDate.toString(), TaskStatus.class);
        if (savedStatus != null)
            adapter.taskStatus = savedStatus;
        else {
            boolean[] defaultStatus = new boolean[stringList.size()];
            Arrays.fill(defaultStatus, false);
            adapter.taskStatus = new TaskStatus(nowChooseDate, stringList.toArray(new String[0]), defaultStatus);
        }
        adapter.notifyDataSetChanged();
    }*/
    private void checkAndLoadTaskStatus() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("TaskStatus").document("TaskStatus_" + nowChooseDate.toString()).get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null && task.getResult().exists()) {
                TaskStatus savedStatus = task.getResult().toObject(TaskStatus.class);
                if (savedStatus != null) {
                    adapter.taskStatus = savedStatus;
                } else {
                    initializeDefaultTaskStatus();
                }
            } else {
                initializeDefaultTaskStatus();
            }
            adapter.notifyDataSetChanged();
        });
    }

    private void initializeDefaultTaskStatus() {
        List<String> taskNames = new ArrayList<>(Arrays.asList(stringList.toArray(new String[0])));
        List<Boolean> defaultStatus = new ArrayList<>(Collections.nCopies(stringList.size(), false));
        adapter.taskStatus = new TaskStatus(nowChooseDate, taskNames, defaultStatus);
    }
    private void loadTodaySchedule() {
        DateInfo today = new DateInfo(Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH) + 1, Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
        ListView listView = findViewById(R.id.TaskAll_todayList);
        adapter = new TaskAdapter(this, stringList);
        listView.setAdapter(adapter);
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        loadScheduledTasks(today);
    }
    /*private void loadScheduledTasks(DateInfo compareDate) {
        stringList.addAll(Arrays.asList("每日健走150步","每日簽到","用藥提醒查看","今日已完成用藥","觀看運動影片","玩遊戲(防失智)","查看運動挑戰")); //固定任務
        ArrayList<Object> tasksSet = new TinyDB(this).getListObject("TaskList", TaskClass.class);
        if (tasksSet == null) tasksSet = new ArrayList<>();
        ArrayList<TaskClass> tasks = new ArrayList<>();
        for (Object taskObject : tasksSet)
            if (taskObject instanceof TaskClass) // Ensure safe casting
                tasks.add((TaskClass) taskObject);
        for (TaskClass task : tasks)
            if (compareDateInfo(new ArrayList<>(task.getTaskDate()), compareDate))
                stringList.add(task.getTaskName() != null ? task.getTaskName() : "null task name");
        adapter.notifyDataSetChanged();
    }*/
    private void loadScheduledTasks(DateInfo compareDate) {
        stringList.addAll(Arrays.asList("每日健走150步", "每日簽到", "用藥提醒查看", "今日已完成用藥", "觀看運動影片", "玩遊戲(防失智)", "查看運動挑戰")); // 固定任務

        FirebaseFirestore.getInstance().collection("Tasks").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    String taskName = document.getString("任務名稱");
                    List<Timestamp> taskDates = (List<Timestamp>) document.get("任務日期");
                    if (taskDates != null) {
                        for (Timestamp timestamp : taskDates) {
                            Calendar calendar = Calendar.getInstance();
                            calendar.setTime(timestamp.toDate());
                            DateInfo dateInfo = new DateInfo(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH));
                            if (compareDateInfo(Collections.singletonList(dateInfo), compareDate)) {
                                stringList.add(taskName != null ? taskName : "null task name");
                                break;
                            }
                        }
                    }
                }
                adapter.notifyDataSetChanged();
            } else {
                Log.w("TaskRead", "Error getting documents.", task.getException());
            }
        });
    }
    public boolean compareDateInfo(List<DateInfo> dateInfoList, DateInfo compareDate) {
        for (DateInfo dateInfo : dateInfoList)  //日期格式化：http://www.cftea.com/c/2017/03/6865.asp
            if (dateInfo.getYear() == compareDate.getYear() && dateInfo.getMonth() == compareDate.getMonth() && dateInfo.getDay() == compareDate.getDay())
                return true;
        return false;
    }
    @Override
    protected void onResume() {
        super.onResume();
        loadDateSpinner();
        checkAndLoadTaskStatus();
    }
    class TaskAdapter extends ArrayAdapter<String> {
        List<String> tasksList;
        TaskStatus taskStatus;
        public TaskAdapter(@NonNull Context context, List<String> list) {
            super(context, R.layout.task_all_listview, list);
            tasksList = list;
        }
        @NonNull
        @Override
        public View getView(int position, View convertView, @NonNull ViewGroup parent) {
            convertView = convertView == null ? LayoutInflater.from(getContext()).inflate(R.layout.task_all_listview, parent, false) : convertView;
            CheckedTextView checkedTextView = convertView.findViewById(R.id.AllList_checkedText);
            boolean isChecked = isTaskChecked(tasksList.get(position));
            checkedTextView.setText(tasksList.get(position));
            checkedTextView.setChecked(isChecked);
            checkedTextView.setBackgroundResource(isChecked ? R.drawable.task_all_listview_background_strikethrough : 0);
            convertView.findViewById(R.id.AllList_chooseImage).setVisibility(position >= 7 ? View.GONE : View.VISIBLE);
            convertView.setOnClickListener(v -> handleItemClick(position));
            return convertView;
        }

        /*private boolean isTaskChecked(String taskName) {
            if (taskStatus == null) return false;
            int index = Arrays.asList(taskStatus.getTaskName()).indexOf(taskName);
            return index != -1 && taskStatus.getTaskStatus().get(index);
        }*/
        private boolean isTaskChecked(String taskName) {
            if (taskStatus == null) return false;
            int index = taskStatus.getTaskName().indexOf(taskName);
            return index != -1 && taskStatus.getTaskStatus().get(index);
        }
        private void handleItemClick(int position) {
            DateInfo today = new DateInfo(Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH) + 1, Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
            if (position < 7)
                Toast.makeText(getContext(), "此為固定任務", Toast.LENGTH_SHORT).show();
            else if(!compareDateInfo(Collections.singletonList(dateList.get(((Spinner)findViewById(R.id.TaskAll_dateSpinner)).getSelectedItemPosition())),today))
                Toast.makeText(getContext(), "只能勾選當日完成的任務喔", Toast.LENGTH_SHORT).show();
            else{
                updateTaskStatus(position);
                notifyDataSetChanged();
            }
        }

        /*private void updateTaskStatus(int position) {
            if (!taskStatus.getDateInfo().equals(nowChooseDate))
                taskStatus = new TaskStatus(nowChooseDate, new String[0], new boolean[0]);
            String taskName = tasksList.get(position);
            int taskIndex = Arrays.asList(taskStatus.getTaskName()).indexOf(taskName);
            if (taskIndex == -1) {
                List<String> taskNames = new ArrayList<>(Arrays.asList(taskStatus.getTaskName()));
                taskNames.add(taskName);
                boolean[] newTaskStatuses = Arrays.copyOf(taskStatus.getTaskStatus(), taskStatus.getTaskStatus().length + 1);
                newTaskStatuses[newTaskStatuses.length - 1] = true;
                taskStatus.setTaskName(taskNames.toArray(new String[0]));
                taskStatus.setTaskStatus(newTaskStatuses);
            } else
                taskStatus.getTaskStatus()[taskIndex] = !taskStatus.getTaskStatus()[taskIndex];
            new TinyDB(getContext()).putObject("TaskStatus_" + nowChooseDate.toString(), taskStatus);
        }*/
        private void updateTaskStatus(int position) {
            Log.d("TaskAdapter", "Updating task status for position: " + position);
            if (!taskStatus.getDateInfo().equals(nowChooseDate))
                taskStatus = new TaskStatus(nowChooseDate, new ArrayList<>(), new ArrayList<>());
            String taskName = tasksList.get(position);
            int taskIndex = taskStatus.getTaskName().indexOf(taskName);
            if (taskIndex == -1) {
                List<String> taskNames = new ArrayList<>(taskStatus.getTaskName());
                taskNames.add(taskName);
                List<Boolean> newTaskStatuses = new ArrayList<>(taskStatus.getTaskStatus());
                newTaskStatuses.add(true);
                taskStatus.setTaskName(taskNames);
                taskStatus.setTaskStatus(newTaskStatuses);
            } else {
                List<Boolean> taskStatuses = new ArrayList<>(taskStatus.getTaskStatus());
                taskStatuses.set(taskIndex, !taskStatuses.get(taskIndex));
                taskStatus.setTaskStatus(taskStatuses);
            }
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("TaskStatus").document("TaskStatus_" + nowChooseDate.toString())
                    .set(taskStatus)
                    .addOnSuccessListener(aVoid -> Log.d("FireStore", "TaskStatus successfully updated!"))
                    .addOnFailureListener(e -> Log.w("FireStore", "Error updating TaskStatus", e));
        }
    }
    //加的這些黃色的鬼東西是序列化的屬性，加了之後Firebase才知道包含哪些屬性
    @IgnoreExtraProperties
    public static class TaskStatus {
        @PropertyName("date_info")
        private DateInfo dateInfo;
        @PropertyName("task_name")
        private List<String> taskName;
        @PropertyName("is_done")
        private List<Boolean> isDone;

        @SuppressWarnings("unused")
        // 呼叫 DataSnapshot.getValue(TaskStatus.class) 所需的預設建構子(簡單說就是刪了會壞掉)
        public TaskStatus() {}

        public TaskStatus(DateInfo dateInfo, List<String> taskName, List<Boolean> isDone) {
            this.dateInfo = dateInfo;
            this.taskName = taskName;
            this.isDone = isDone;
        }

        @PropertyName("date_info")
        public DateInfo getDateInfo() {
            return dateInfo;
        }

        @PropertyName("task_name")
        public List<String> getTaskName() {
            return taskName;
        }

        @PropertyName("is_done")
        public List<Boolean> getTaskStatus() {
            return isDone;
        }

        @PropertyName("task_name")
        public void setTaskName(List<String> taskName) {
            this.taskName = taskName;
        }

        @PropertyName("is_done")
        public void setTaskStatus(List<Boolean> done) {
            isDone = done;
        }
    }
    static class CustomSpinnerAdapter extends ArrayAdapter<String> {
        Context context;
        List<String> items;
        int specialItemIndex,specialItemColor;
        public CustomSpinnerAdapter(@NonNull Context context, @NonNull List<String> items, int specialItemIndex, int specialItemColor) {
            super(context, android.R.layout.simple_spinner_item, items);
            this.context = context;
            this.items = items;
            this.specialItemIndex = specialItemIndex;
            this.specialItemColor = specialItemColor;
        }
        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            return Objects.requireNonNull(createView(position, convertView));
        }
        @Override
        public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            return createView(position, convertView);
        }
        private View createView(int position, @Nullable View convertView) {
            TextView textView;
            if (convertView == null) {
                textView = new TextView(context);
                textView.setPadding(16, 16, 16, 16);
                textView.setTextSize(18);
                textView.setTextColor(Color.BLACK);
            } else
                textView = (TextView) convertView;
            textView.setText(items.get(position));
            textView.setBackgroundColor(position == specialItemIndex ? specialItemColor : 0);
            return textView;
        }
    }
}
