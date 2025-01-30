package m3.eventplanner.fragments.reservation;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;

import java.util.Locale;

import m3.eventplanner.databinding.FragmentCreateReservationBinding;

public class CreateReservationFragment extends Fragment {
    private FragmentCreateReservationBinding binding;
    private boolean isFixedDuration = false;
    private int minDuration = 0;
    private int maxDuration = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentCreateReservationBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle args = getArguments();
        if (args != null) {
            isFixedDuration = args.getBoolean("isFixedDuration", false);
            minDuration = args.getInt("minDuration", 0);
            maxDuration = args.getInt("maxDuration", 0);

            setupTimeSelection();
        }

        binding.selectStartTimeButton.setOnClickListener(v -> showTimePicker(true));
        binding.selectEndTimeButton.setOnClickListener(v -> showTimePicker(false));

        updateDurationInfo();
    }

    private void setupTimeSelection() {
        if (isFixedDuration) {
            binding.endTimeContainer.setVisibility(View.GONE);
            binding.durationInfo.setText("This service's fixed duration is " + minDuration + " hours");
        } else {
            binding.endTimeContainer.setVisibility(View.VISIBLE);
            binding.durationInfo.setText("This service's duration is between " + minDuration +
                    " and " + maxDuration + " hours");
        }
    }

    private void showTimePicker(boolean isStartTime) {
        MaterialTimePicker picker = new MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_24H)
                .setHour(12)
                .setMinute(0)
                .setTitleText(isStartTime ? "Select Start Time" : "Select End Time")
                .build();

        picker.addOnPositiveButtonClickListener(v -> {
            String timeStr = String.format(Locale.getDefault(), "%02d:%02d",
                    picker.getHour(), picker.getMinute());
            if (isStartTime) {
                binding.selectedStartTime.setText(timeStr);
                if (isFixedDuration) {
                    calculateEndTime(picker.getHour(), picker.getMinute());
                }
            } else {
                binding.selectedEndTime.setText(timeStr);
            }
        });

        picker.show(getChildFragmentManager(), "time_picker");
    }

    private void calculateEndTime(int startHour, int startMinute) {
        int endHour = startHour + minDuration;
        int endMinute = startMinute;

        if (endHour >= 24) {
            endHour -= 24;
        }

        String endTimeStr = String.format(Locale.getDefault(), "%02d:%02d", endHour, endMinute);
        binding.selectedEndTime.setText(endTimeStr);
    }

    private void updateDurationInfo() {
        if (isFixedDuration) {
            binding.durationInfo.setText("This service's fixed duration is " + minDuration + " hours");
        } else {
            binding.durationInfo.setText("This service's duration is between " + minDuration +
                    " and " + maxDuration + " hours");
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
