package m3.eventplanner.fragments.user;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.applandeo.materialcalendarview.CalendarDay;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import m3.eventplanner.R;
import m3.eventplanner.databinding.FragmentCalendarBinding;
import m3.eventplanner.models.GetCalendarItemDTO;
import m3.eventplanner.utils.MultiDotDrawable;

public class CalendarFragment extends Fragment {

    private FragmentCalendarBinding binding;

    public CalendarFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentCalendarBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        List<CalendarDay> calendarDays = new ArrayList<>();
    }

    private int getCalendarItemColor(String itemType) {
        switch (itemType) {
            case "ACCEPTED_EVENT":
                return Color.RED;
            case "CREATED_EVENT":
                return Color.GREEN;
            case "RESERVATION":
                return Color.BLUE;
            default:
                return Color.BLACK;
        }
    }
}