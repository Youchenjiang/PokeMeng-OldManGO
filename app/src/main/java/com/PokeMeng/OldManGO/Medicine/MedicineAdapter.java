package com.PokeMeng.OldManGO.Medicine;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.app.AlertDialog;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.PokeMeng.OldManGO.R;

import java.util.List;
import com.PokeMeng.OldManGO.Medicine.ui.SharedViewModel;
import com.bumptech.glide.Glide;

public class MedicineAdapter extends RecyclerView.Adapter<MedicineAdapter.MedicineViewHolder> {
    private List<Medicine> medicines;
    private OnItemClickListener onItemClickListener;
    private boolean isDashboard;
    private Context context;
    private boolean isNotificationsFragment;
    private SharedViewModel sharedViewModel;

    // 统一 SharedPreferences 名称
    private static final String SHARED_PREFS_NAME = "MedicinePrefs";

    public MedicineAdapter(Context context, List<Medicine> medicines, OnItemClickListener onItemClickListener, boolean isDashboard, boolean isNotificationsFragment, SharedViewModel sharedViewModel) {
        this.context = context;
        this.medicines = medicines;
        this.onItemClickListener = onItemClickListener;
        this.isDashboard = isDashboard;
        this.isNotificationsFragment = isNotificationsFragment;
        this.sharedViewModel = sharedViewModel;
    }

    @NonNull
    @Override
    public MedicineViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_medicine, parent, false);
        return new MedicineViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MedicineViewHolder holder, int position) {
        Medicine medicine = medicines.get(position);
        holder.bind(medicine, onItemClickListener);
        handleButtonState(holder, medicine);
        Log.d("MedicineAdapter", "Binding medicine: " + medicine.getName() + ", " + medicine.getFrequency());
        if (isNotificationsFragment) {
            holder.checkmark.setVisibility(View.VISIBLE); // 在 NotificationsFragment 中显示打勾符号
        } else {
            holder.checkmark.setVisibility(View.GONE); // 在其他地方隐藏打勾符号
        }
    }

    private void handleButtonState(MedicineViewHolder holder, Medicine medicine) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE);
        String firstTime = medicine.getTimes().isEmpty() ? "" : medicine.getTimes().get(0);
        boolean isClicked = getButtonClickedState(sharedPreferences, medicine.getId(), firstTime);

        if (isDashboard) {
            holder.takenButton.setVisibility(View.VISIBLE);
            holder.takenButton.setEnabled(!isClicked);
            holder.takenButton.setBackgroundColor(isClicked ? Color.GRAY : Color.BLACK);

            if (!isClicked) {
                holder.takenButton.setOnClickListener(v -> showConfirmationDialog(holder, medicine, firstTime));
            }
        } else {
            holder.takenButton.setVisibility(View.GONE); // 在 HomeFragment 中隐藏按钮
        }
    }

    private void showConfirmationDialog(MedicineViewHolder holder, Medicine medicine, String firstTime) {
        new AlertDialog.Builder(context)
                .setTitle("確認")
                .setMessage("是否已服用藥物？")
                .setPositiveButton("是", (dialog, which) -> {
                    SharedPreferences.Editor editor = context.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE).edit();
                    setButtonClickedState(editor, medicine.getId(), firstTime);
                    medicine.setTaken(true); // 更新药物的 isTaken 状态
                    holder.takenButton.setEnabled(false);
                    holder.takenButton.setBackgroundColor(Color.GRAY);
                    sharedViewModel.addTakenMedicine(medicine); // 添加到已服用药物列表
                })
                .setNegativeButton("否", null)
                .show();
    }

    // 读取按钮点击状态
    private boolean getButtonClickedState(SharedPreferences sharedPreferences, int medicineId, String time) {
        return sharedPreferences.getBoolean("button_clicked_" + medicineId + "_" + time, false);
    }

    // 设置按钮点击状态
    private void setButtonClickedState(SharedPreferences.Editor editor, int medicineId, String time) {
        editor.putBoolean("button_clicked_" + medicineId + "_" + time, true);
        editor.apply();
    }

    @Override
    public int getItemCount() {
        return medicines.size();
    }

    public void updateMedicines(List<Medicine> newMedicines) {
        this.medicines = newMedicines;
        notifyDataSetChanged(); // 通知 RecyclerView 更新数据
    }

    class MedicineViewHolder extends RecyclerView.ViewHolder {
        private TextView nameTextView;
        private TextView frequencyTextView;
        private TextView timeTextView;
        private ImageView imageView;
        private Button takenButton;
        private ImageView checkmark;

        public MedicineViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.medicineName);
            frequencyTextView = itemView.findViewById(R.id.medicineFrequency);
            timeTextView = itemView.findViewById(R.id.medicineTime);
            imageView = itemView.findViewById(R.id.medicineImageView);
            takenButton = itemView.findViewById(R.id.btn_taken);
            checkmark = itemView.findViewById(R.id.checkmark);
        }

        public void bind(Medicine medicine, OnItemClickListener listener) {
            Log.d("MedicineAdapter", "Binding medicine: " + medicine.getName() + ", " + medicine.getFrequency());

            nameTextView.setText(medicine.getName());
            frequencyTextView.setText(medicine.getFrequency());

            List<String> times = medicine.getTimes();
            if (times != null && !times.isEmpty()) {
                String timeString = String.join(", ", times);
                timeTextView.setText(timeString);
            } else {
                timeTextView.setText("No times available");
            }

            // 确保 imageUrl 不为空
            if (medicine.getImageUrl() != null && !medicine.getImageUrl().isEmpty()) {
                Glide.with(imageView.getContext())
                        .load(medicine.getImageUrl())
                        .placeholder(R.drawable.mc_pt)
                        .error(R.drawable.mc_pt)
                        .into(imageView);
            } else {
                imageView.setImageResource(R.drawable.mc_pt);  // 使用默认图片
            }

            itemView.setOnClickListener(v -> listener.onItemClick(medicine));
        }
    }

    public interface OnItemClickListener {
        void onItemClick(Medicine medicine);
    }
}
