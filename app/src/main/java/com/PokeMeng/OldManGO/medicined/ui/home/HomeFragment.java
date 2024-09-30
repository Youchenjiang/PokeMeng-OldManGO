package com.PokeMeng.OldManGO.medicined.ui.home;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.PokeMeng.OldManGO.R;
import com.PokeMeng.OldManGO.medicined.MedicineAdapter;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.PokeMeng.OldManGO.medicined.Medicine;
import com.PokeMeng.OldManGO.medicined.ui.SharedViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private RecyclerView recyclerView;
    private MedicineAdapter medicineAdapter;
    private List<Medicine> medicines = new ArrayList<>();
    private TextView textView;
    private SharedViewModel sharedViewModel;
    private MedicineAdapter adapter;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // 恢复药物列表
        if (savedInstanceState != null) {
            medicines = savedInstanceState.getParcelableArrayList("medicines");
        }

        // 初始化 RecyclerView 和 Adapter
        recyclerView = view.findViewById(R.id.recyclerViewMedicines);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        textView = view.findViewById(R.id.textView);
        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        // 初始化 MedicineAdapter
        medicineAdapter = new MedicineAdapter(getContext(), medicines, new MedicineAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Medicine medicine) {
                navigateToBlankFragment(medicine); // 处理点击事件
            }
        }, false, false, sharedViewModel);

        recyclerView.setAdapter(medicineAdapter);

        // 观察药物列表的变化
        sharedViewModel.getMedicines().observe(getViewLifecycleOwner(), medicines -> {
            if (medicines != null) {
                medicineAdapter.updateMedicines(medicines); // 更新 Adapter
            } else {
                Log.e("HomeFragment", "Medicines list is null");
            }
        });

        // 点击 header_section 来新增药物
        view.findViewById(R.id.header_section).setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_activity_main5);
            navController.navigate(R.id.action_homeFragment_to_blankFragment);
        });

        // 设置 FragmentResultListener 来接收编辑结果
        getParentFragmentManager().setFragmentResultListener("editResult", this, (requestKey, bundle) -> {
            String updatedName = bundle.getString("updatedName");
            String updatedFrequency = bundle.getString("updatedFrequency");
            ArrayList<String> updatedTimes = bundle.getStringArrayList("updatedTimes");
            String updatedDosage = bundle.getString("updatedDosage");
            int updatedStock = bundle.getInt("updatedStock");
            int updatedStock2 = bundle.getInt("updatedStock2");
            String updatedSpinner2Value = bundle.getString("updatedSpinner2Value");
            int medicineId = bundle.getInt("medicineId", -1);
            String updatedStartDate = bundle.getString("updatedStartDate");

            if (isInputValid(updatedName, updatedFrequency, updatedTimes, updatedDosage, updatedStock, updatedStock2)) {
                if (medicineId != -1) {
                    updateRecyclerView(updatedName, updatedFrequency, updatedTimes, updatedDosage, updatedStock, updatedStock2, updatedSpinner2Value, medicineId, updatedStartDate);
                } else {
                    Log.e("HomeFragment", "Medicine ID is invalid");
                }
            } else {
                Toast.makeText(getContext(), "请填写所有字段", Toast.LENGTH_SHORT).show();
            }
        });

        // 设置 FragmentResultListener 来接收新增药物结果
        getParentFragmentManager().setFragmentResultListener("addMedicineResult", this, (requestKey, result) -> {
            String name = result.getString("medicineName");
            String frequency = result.getString("medicineFrequency");
            ArrayList<String> times = result.getStringArrayList("medicineTimes");
            String dosage = result.getString("dosage");
            int stock = result.getInt("stock");
            int stock2 = result.getInt("stock2");
            String imageUrl = result.getString("medicineImageUrl");
            String spinner2Value = result.getString("spinner2Value");
            int medicineId = generateUniqueId();
            String startDate = result.getString("startDate");

            if (isInputValid(name, frequency, times, dosage, stock, stock2)) {
                Medicine newMedicine = new Medicine(name, frequency, times, dosage, stock, stock2, imageUrl, spinner2Value, medicineId, startDate);
                addOrUpdateMedicine(newMedicine);
            } else {
                Toast.makeText(getContext(), "请填写所有字段", Toast.LENGTH_SHORT).show();
            }
        });

        getParentFragmentManager().setFragmentResultListener("deleteMedicineResult", this, (requestKey, bundle) -> {
            int deletedMedicineId = bundle.getInt("deletedMedicineId");
            deleteMedicine(deletedMedicineId);
            sharedViewModel.removeMedicine(deletedMedicineId);
        });

        updateEmptyView();

        return view; // 返回已初始化的视图
    }

    private boolean isInputValid(String name, String frequency, ArrayList<String> times, String dosage, int stock, int stock2) {
        return !(name == null || name.isEmpty() ||
                frequency == null || frequency.isEmpty() ||
                times == null || times.isEmpty() ||
                dosage == null || dosage.isEmpty() ||
                stock < 0 || stock2 < 0);
    }

    private void deleteMedicine(int medicineId) {
        int position = getMedicinePositionById(medicineId);
        if (position != -1) {
            medicines.remove(position);
            medicineAdapter.notifyItemRemoved(position);
            updateEmptyView();
        }
    }

    private void updateRecyclerView(String name, String frequency, ArrayList<String> times, String dosage, int stock, int stock2, String spinner2Value, int medicineId, String startDate) {
        int position = getMedicinePositionById(medicineId);
        if (position != -1) {
            Medicine existingMedicine = medicines.get(position);
            existingMedicine.setName(name);
            existingMedicine.setFrequency(frequency);
            existingMedicine.setTimes(times);
            existingMedicine.setDosage(dosage);
            existingMedicine.setStock(stock);
            existingMedicine.setStock2(stock2);
            existingMedicine.setSpinner2Value(spinner2Value);
            existingMedicine.setStartDate(startDate);

            resetButtonStates(medicineId, times);
            medicineAdapter.notifyItemChanged(position);
        }
    }

    private int getMedicinePositionById(int medicineId) {
        for (int i = 0; i < medicines.size(); i++) {
            if (medicines.get(i).getId() == medicineId) {
                return i;
            }
        }
        return -1;
    }

    private void addOrUpdateMedicine(Medicine newMedicine) {
        int position = getMedicinePositionById(newMedicine.getId());
        if (position != -1) {
            medicines.set(position, newMedicine);
            medicineAdapter.notifyItemChanged(position);
        } else {
            medicines.add(newMedicine);
            medicineAdapter.notifyItemInserted(medicines.size() - 1);
        }

        resetButtonStates(newMedicine.getId(), newMedicine.getTimes());
        sharedViewModel.addMedicine(newMedicine);
        updateEmptyView();
    }

    private void resetButtonStates(int medicineId, ArrayList<String> times) {
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("button_prefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        for (String time : times) {
            editor.putBoolean("button_clicked_" + medicineId + "_" + time, false);
        }
        editor.apply();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("medicines", new ArrayList<>(medicines));
    }

    private void updateEmptyView() {
        textView.setVisibility(medicines.isEmpty() ? View.VISIBLE : View.GONE);
    }

    private void navigateToBlankFragment(Medicine medicine) {
        Log.d("HomeFragment", "Navigating with medicine ID: " + medicine.getId());
        Bundle bundle = new Bundle();
        bundle.putString("editTextText", medicine.getName());
        bundle.putString("textView3", medicine.getFrequency());
        bundle.putStringArrayList("timeContainer", medicine.getTimes());
        bundle.putString("textView9", medicine.getDosage());
        bundle.putInt("editTextNumber", medicine.getStock());
        bundle.putInt("editTextNumber2", medicine.getStock2());
        bundle.putString("imageView4", medicine.getImageUrl());
        bundle.putString("spinner2Value", medicine.getSpinner2Value());
        bundle.putString("startDate", medicine.getStartDate());
        bundle.putInt("medicineId", medicine.getId());
        bundle.putBoolean("isEdit", true);

        NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_activity_main5);
        navController.navigate(R.id.action_homeFragment_to_blankFragment, bundle);
    }

    private int generateUniqueId() {
        // 这里生成唯一 ID 的逻辑，可以根据您的需求进行调整
        return medicines.size() + 1; // 示例：简单的 ID 生成方式
    }

    @Override
    public void onResume() {
        super.onResume();
        BottomNavigationView bottomNavigationView = getActivity().findViewById(R.id.nav_view);
        if (bottomNavigationView != null) {
            bottomNavigationView.setVisibility(View.VISIBLE);
        }
    }
}
