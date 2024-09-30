package com.PokeMeng.OldManGO.medicined;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.PokeMeng.OldManGO.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class BottomSheet extends BottomSheetDialogFragment {

    private OnFrequencySelectedListener mListener;

    public interface OnFrequencySelectedListener {
        void onFrequencySelected(String frequency);
    }

    public void setOnFrequencySelectedListener(OnFrequencySelectedListener listener) {
        mListener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bottom_date, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button weekdayButton = view.findViewById(R.id.weekdayButton);
        Button intervalButton = view.findViewById(R.id.intervalButton);
        Button asNeededButton = view.findViewById(R.id.asNeededButton);

        weekdayButton.setOnClickListener(v -> {
            BottomWeekdaySheet bottomWeekdaySheet = new BottomWeekdaySheet();

            bottomWeekdaySheet.setOnWeekdaySelectedListener(selectedDays -> {
                if (mListener != null) {
                    mListener.onFrequencySelected(selectedDays);
                }
            });

            bottomWeekdaySheet.show(getChildFragmentManager(), bottomWeekdaySheet.getTag());
        });

        intervalButton.setOnClickListener(v -> {
            DayInterval dayIntervalSheet = new DayInterval();

            dayIntervalSheet.setOnIntervalSelectedListener(interval -> {
                if (mListener != null) {
                    mListener.onFrequencySelected(interval);
                }
            });

            dayIntervalSheet.show(getChildFragmentManager(), dayIntervalSheet.getTag());
        });

        asNeededButton.setOnClickListener(v -> {
            if (mListener != null) {
                mListener.onFrequencySelected(asNeededButton.getText().toString());
            }
            dismiss();
        });
    }
}
