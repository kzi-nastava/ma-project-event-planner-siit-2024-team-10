package m3.eventplanner.fragments.user;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.applandeo.materialcalendarview.CalendarDay;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import m3.eventplanner.R;
import m3.eventplanner.adapters.CalendarItemAdapter;
import m3.eventplanner.auth.TokenManager;
import m3.eventplanner.clients.ClientUtils;
import m3.eventplanner.databinding.FragmentCalendarBinding;
import m3.eventplanner.fragments.event.EventDetailsViewModel;
import m3.eventplanner.models.GetCalendarItemDTO;
import m3.eventplanner.utils.MultiDotDrawable;

public class CalendarFragment extends Fragment {

    private FragmentCalendarBinding binding;
    private CalendarViewModel viewModel;
    private CalendarItemAdapter calendarItemAdapter;
    private Map<LocalDate, List<GetCalendarItemDTO>> calendarMap = new HashMap<>();

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
        viewModel = new ViewModelProvider(this).get(CalendarViewModel.class);
        viewModel.initialize(new ClientUtils(requireContext()));

        viewModel.getCalendarItems().observe(getViewLifecycleOwner(), this::populateCalendar);

        viewModel.getError().observe(getViewLifecycleOwner(), error ->
                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show()
        );

        TokenManager tokenManager = new TokenManager(requireContext());

        binding.calendarItemRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        calendarItemAdapter = new CalendarItemAdapter(new ArrayList<>(), calendarItem -> {
            Bundle bundle = new Bundle();
            bundle.putInt("selectedEventId", calendarItem.getEventId());
            Navigation.findNavController(binding.getRoot()).navigate(R.id.eventDetailsFragment, bundle);
        });
        binding.calendarItemRecyclerView.setAdapter(calendarItemAdapter);

        String role = tokenManager.getRole();
        if (!"EVENT_ORGANIZER".equals(role)) {
            binding.createdEventLegend.setVisibility(View.GONE);
        }
        if (!"PROVIDER".equals(role)) {
            binding.reservationLegend.setVisibility(View.GONE);
        }


        binding.calendarView.setOnDayClickListener(eventDay -> {
            Calendar clickedDate = eventDay.getCalendar();
            LocalDate selectedDate = LocalDate.of(
                    clickedDate.get(Calendar.YEAR),
                    clickedDate.get(Calendar.MONTH) + 1,
                    clickedDate.get(Calendar.DAY_OF_MONTH)
            );
            binding.selectedDateTextView.setText(selectedDate.toString());
            calendarItemAdapter.updateCalendarItems(calendarMap.get(selectedDate)!=null?calendarMap.get(selectedDate):new ArrayList<>());
        });


        viewModel.loadCalendar(tokenManager.getAccountId());
    }

    private void populateCalendar(Collection<GetCalendarItemDTO> calendarItems){
        calendarMap = new HashMap<>();
        Map<LocalDate, Set<Integer>> dotsByDate = new HashMap<>();

        for (GetCalendarItemDTO item : calendarItems) {
            LocalDate date = LocalDateTime.parse(item.getStartTime()).toLocalDate();
            calendarMap.computeIfAbsent(date, k -> new ArrayList<>()).add(item);
            int color = getCalendarItemColor(item.getType());
            dotsByDate.computeIfAbsent(date, k -> new HashSet<>()).add(color);
        }


        List<CalendarDay> calendarDays = new ArrayList<>();

        for (Map.Entry<LocalDate, Set<Integer>> entry : dotsByDate.entrySet()) {
            LocalDate date = entry.getKey();
            Set<Integer> colorSet = entry.getValue();

            Calendar calendar = Calendar.getInstance();
            calendar.set(date.getYear(), date.getMonthValue() - 1, date.getDayOfMonth());

            CalendarDay calendarDay = new CalendarDay(calendar);
            calendarDay.setImageDrawable(new MultiDotDrawable(new ArrayList<>(colorSet), 10));
            calendarDays.add(calendarDay);
        }

        binding.calendarView.setCalendarDays(calendarDays);
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