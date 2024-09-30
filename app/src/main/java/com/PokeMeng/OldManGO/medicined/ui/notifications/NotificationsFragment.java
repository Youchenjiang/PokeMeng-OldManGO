package com.PokeMeng.OldManGO.medicined.ui.notifications;


import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.PokeMeng.OldManGO.databinding.FragmentNotificationsBinding;
import com.PokeMeng.OldManGO.medicined.Medicine;
import com.PokeMeng.OldManGO.medicined.MedicineAdapter;
import com.PokeMeng.OldManGO.R;

import com.PokeMeng.OldManGO.medicined.ui.SharedViewModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class NotificationsFragment extends Fragment {

    private FragmentNotificationsBinding binding;
    private RecyclerView recyclerView;
    private MedicineAdapter medicineAdapter;
    private List<Medicine> medicineList = new ArrayList<>();
    private SharedViewModel sharedViewModel;
    private Calendar selectedCalendar;
    private TextView textView12;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentNotificationsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        initializeRecyclerView();
        initializeDatePicker();

        // 加载历史药物列表
        loadHistoryMedicines();

        return root; // 确保返回的是 root，而不是新的视图
    }

    private void initializeRecyclerView() {
        recyclerView = binding.recyclerhistory;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        medicineAdapter = new MedicineAdapter(requireContext(), medicineList, medicine -> {
            // Handle click event (optional)
        }, false, true, sharedViewModel);
        recyclerView.setAdapter(medicineAdapter);
    }

    private void initializeDatePicker() {
        textView12 = binding.textView12;
        ImageButton pickButton = binding.pickButton;
        selectedCalendar = Calendar.getInstance();

        pickButton.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    requireContext(),
                    (view, year, monthOfYear, dayOfMonth) -> {
                        selectedCalendar.set(year, monthOfYear, dayOfMonth);
                        updateDateTextView();
                        filterMedicinesBySelectedDate();
                    },
                    selectedCalendar.get(Calendar.YEAR),
                    selectedCalendar.get(Calendar.MONTH),
                    selectedCalendar.get(Calendar.DAY_OF_MONTH)
            );
            datePickerDialog.show();
        });
    }

    private void updateDateTextView() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        textView12.setText(sdf.format(selectedCalendar.getTime()));
    }

    private void filterMedicinesBySelectedDate() {
        String selectedDate = textView12.getText().toString();
        sharedViewModel.getMedicinesByDate(selectedDate).observe(getViewLifecycleOwner(), medicines -> {
            medicineList.clear();
            medicineList.addAll(medicines);
            medicineAdapter.notifyDataSetChanged();
        });
    }

    private void loadHistoryMedicines() {
        sharedViewModel.getHistoryMedicines().observe(getViewLifecycleOwner(), medicines -> {
            medicineList.clear();
            medicineList.addAll(medicines);
            medicineAdapter.notifyDataSetChanged();
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
