package m3.eventplanner.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.datepicker.MaterialDatePicker;
import java.util.ArrayList;
import java.util.List;
import m3.eventplanner.R;
import m3.eventplanner.adapters.EventListAdapter;
import m3.eventplanner.adapters.OfferingListAdapter;
import m3.eventplanner.models.Event;
import m3.eventplanner.models.Offering;
import m3.eventplanner.models.Product;
import m3.eventplanner.models.Service;

public class HomeScreenFragment extends Fragment {

    private MaterialButtonToggleGroup toggleGroup;
    private View eventSearchBar, offeringSearchBar, paginationBar;
    private RecyclerView contentRecyclerView;
    private TextView noCardsFoundTextView, topEventsTextView, topOfferingsTextView;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_homescreen, container, false);
        initializeUIElements(rootView);
        setUpRecyclerView();
        setUpToggleGroup();
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setUpSortSpinners(view);
        setUpFilterButtons(view);
    }

    private void initializeUIElements(View rootView) {
        toggleGroup = rootView.findViewById(R.id.toggleButton);
        eventSearchBar = rootView.findViewById(R.id.eventSearchBar);
        offeringSearchBar = rootView.findViewById(R.id.offeringSearchBar);
        paginationBar = rootView.findViewById(R.id.paginationBar);
        contentRecyclerView = rootView.findViewById(R.id.contentRecyclerView);
        noCardsFoundTextView = rootView.findViewById(R.id.noCardsFoundTextView);
        topEventsTextView = rootView.findViewById(R.id.top_events_text);
        topOfferingsTextView = rootView.findViewById(R.id.top_offerings_text);
    }

    private void setUpRecyclerView() {
        contentRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    private void setUpToggleGroup() {
        showTopEvents();
        toggleGroup.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (isChecked) {
                if (checkedId == R.id.tabTopEvents) {
                    showTopEvents();
                } else if (checkedId == R.id.tabAllEvents) {
                    showAllEvents();
                } else if (checkedId == R.id.tabTopOfferings) {
                    showTopOfferings();
                } else if (checkedId == R.id.tabAllOfferings) {
                    showAllOfferings();
                }
            }
        });
    }


    private void setUpSortSpinners(View view) {
        setUpEventSortSpinners(view);
        setUpOfferingSortSpinners(view);
    }

    private void setUpEventSortSpinners(View view) {
        Spinner eventSortBySpinner = view.findViewById(R.id.btn_sort_events_by);
        eventSortBySpinner.setAdapter(createSpinnerAdapter(R.array.event_sort_criteria));

        Spinner eventSortDirectionSpinner = view.findViewById(R.id.btn_sort_events_direction);
        eventSortDirectionSpinner.setAdapter(createSpinnerAdapter(R.array.sort_directions));
    }

    private void setUpOfferingSortSpinners(View view) {
        Spinner offeringsSortBySpinner = view.findViewById(R.id.btn_sort_offerings_by);
        offeringsSortBySpinner.setAdapter(createSpinnerAdapter(R.array.offering_sort_criteria));

        Spinner offeringSortDirectionSpinner = view.findViewById(R.id.btn_sort_offering_direction);
        offeringSortDirectionSpinner.setAdapter(createSpinnerAdapter(R.array.sort_directions));
    }

    private ArrayAdapter<String> createSpinnerAdapter(int arrayResId) {
        List<String> data = new ArrayList<>();
        String[] array = getResources().getStringArray(arrayResId);
        for (String item : array) {
            data.add(item);
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, data);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        return adapter;
    }

    private void setUpFilterButtons(View view) {
        Button btnFilters = view.findViewById(R.id.filter_events_button);
        btnFilters.setOnClickListener(v -> showEventFilterBottomSheet());

        Button btnOfferingFilters = view.findViewById(R.id.filter_offerings_button);
        btnOfferingFilters.setOnClickListener(v -> {
            String selectedText = getSelectedOfferingType(view);
            if (selectedText != null) {
                showOfferingFilterBottomSheet(selectedText);
            }
        });
    }

    private String getSelectedOfferingType(View view) {
        RadioGroup offeringRadioGroup = view.findViewById(R.id.offering_radio_group);
        int selectedId = offeringRadioGroup.getCheckedRadioButtonId();
        if (selectedId != -1) {
            return ((RadioButton) view.findViewById(selectedId)).getText().toString();
        } else {
            Log.e("HomeScreenFragment", "No radio button selected.");
            return null;
        }
    }

    private void showTopEvents() {
        toggleVisibility(View.VISIBLE, View.GONE, View.GONE, View.GONE, View.GONE);
        List<Event> topEvents = getTopEvents();
        updateRecyclerView(topEvents, new EventListAdapter(topEvents));
    }

    private void showAllEvents() {
        toggleVisibility( View.GONE, View.GONE,View.VISIBLE, View.GONE, View.VISIBLE);
        List<Event> allEvents = getAllEvents();
        updateRecyclerView(allEvents, new EventListAdapter(allEvents));
    }

    private void showTopOfferings() {
        toggleVisibility( View.GONE,  View.VISIBLE,View.GONE, View.GONE, View.GONE);
        List<Offering> topOfferings = getTopOfferings();
        updateRecyclerView(topOfferings, new OfferingListAdapter(topOfferings));
    }

    private void showAllOfferings() {
        toggleVisibility( View.GONE,  View.GONE, View.GONE, View.VISIBLE, View.VISIBLE);
        List<Offering> allOfferings = getAllOfferings();
        updateRecyclerView(allOfferings, new OfferingListAdapter(allOfferings));
    }

    private void toggleVisibility(int topEventsVisibility, int topOfferingsVisibility, int eventSearchVisibility, int offeringSearchVisibility, int paginationVisibility) {
        topEventsTextView.setVisibility(topEventsVisibility);
        topOfferingsTextView.setVisibility(topOfferingsVisibility);
        eventSearchBar.setVisibility(eventSearchVisibility);
        offeringSearchBar.setVisibility(offeringSearchVisibility);
        paginationBar.setVisibility(paginationVisibility);
    }

    private <T> void updateRecyclerView(List<T> data, RecyclerView.Adapter<?> adapter) {
        if (data == null || data.isEmpty()) {
            noCardsFoundTextView.setVisibility(View.VISIBLE);
            contentRecyclerView.setVisibility(View.GONE);
        } else {
            noCardsFoundTextView.setVisibility(View.GONE);
            contentRecyclerView.setVisibility(View.VISIBLE);
            contentRecyclerView.setAdapter(adapter);
        }
    }

    private void showEventFilterBottomSheet() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(requireContext());
        View bottomSheetView = getLayoutInflater().inflate(R.layout.filter_events, null);
        bottomSheetDialog.setContentView(bottomSheetView);
        setUpEventFilterDateRangePicker(bottomSheetView);
        setUpEventTypeSpinner(bottomSheetView);
        bottomSheetDialog.show();
    }

    private void showOfferingFilterBottomSheet(String selected) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(requireContext());
        View bottomSheetView = getLayoutInflater().inflate(R.layout.homepage_filter_offerings, null);
        bottomSheetDialog.setContentView(bottomSheetView);
        setUpOfferingFilterDateRangePicker(bottomSheetView);
        setUpCategorySpinner(bottomSheetView);
        setUpEventTypeSpinner(bottomSheetView);
        setUpVisibilityForOfferingType(bottomSheetView, selected);
        bottomSheetDialog.show();
    }

    private void setUpEventFilterDateRangePicker(View bottomSheetView) {
        Button dateRangeButton = bottomSheetView.findViewById(R.id.date_range_button);
        TextView selectedDatesTextView = bottomSheetView.findViewById(R.id.selected_dates);
        MaterialDatePicker<Pair<Long, Long>> picker = MaterialDatePicker.Builder.dateRangePicker().setTitleText("Select Date Range").build();
        dateRangeButton.setOnClickListener(v -> picker.show(getParentFragmentManager(), "DATE_PICKER"));
        picker.addOnPositiveButtonClickListener(selection -> updateSelectedDateRange(selection, selectedDatesTextView));
    }

    private void setUpOfferingFilterDateRangePicker(View bottomSheetView) {
        Button dateRangeButton = bottomSheetView.findViewById(R.id.date_range_button);
        TextView selectedDatesTextView = bottomSheetView.findViewById(R.id.selected_dates);
        MaterialDatePicker<Pair<Long, Long>> picker = MaterialDatePicker.Builder.dateRangePicker().setTitleText("Select Date Range").build();
        dateRangeButton.setOnClickListener(v -> picker.show(getParentFragmentManager(), "DATE_PICKER"));
        picker.addOnPositiveButtonClickListener(selection -> updateSelectedDateRange(selection, selectedDatesTextView));
    }

    private void setUpEventTypeSpinner(View bottomSheetView) {
        Spinner eventTypeSpinner = bottomSheetView.findViewById(R.id.event_type_spinner);
        eventTypeSpinner.setAdapter(createSpinnerAdapter(R.array.event_types));
    }

    private void setUpCategorySpinner(View bottomSheetView) {
        Spinner categorySpinner = bottomSheetView.findViewById(R.id.category_spinner);
        categorySpinner.setAdapter(createSpinnerAdapter(R.array.categories));
    }

    private void setUpVisibilityForOfferingType(View bottomSheetView, String selected) {
        View serviceDurationView = bottomSheetView.findViewById(R.id.service_duration);
        Button dateRangePickerButton = bottomSheetView.findViewById(R.id.date_range_button);
        TextView dateRangeTextView = bottomSheetView.findViewById(R.id.selected_dates);
        if ("SERVICE".equals(selected)) {
            serviceDurationView.setVisibility(View.VISIBLE);
            dateRangePickerButton.setVisibility(View.VISIBLE);
            dateRangeTextView.setVisibility(View.VISIBLE);
        } else {
            serviceDurationView.setVisibility(View.GONE);
            dateRangePickerButton.setVisibility(View.GONE);
            dateRangeTextView.setVisibility(View.GONE);
        }
    }

    private void updateSelectedDateRange(Pair<Long, Long> selection, TextView selectedDatesTextView) {
        String formattedDates = formatDate(selection.first) + " - " + formatDate(selection.second);
        selectedDatesTextView.setText(formattedDates);
    }

    private String formatDate(Long dateInMillis) {
        return java.text.DateFormat.getDateInstance().format(new java.util.Date(dateInMillis));
    }
    // Example data fetching methods
    private List<Event> getTopEvents() {
        List<Event> list = new ArrayList<>();

        list.add(new Event(11L, "Outdoor Adventure Expo", 3.6, "Outdoor Life", "Senta, Serbia", "25.08.2024. 09:00"));
        list.add(new Event(12L, "Tech Startup Pitch", 4.1, "Tech Innovators", "Novi Sad, Serbia", "15.09.2024. 10:00"));
        list.add(new Event(13L, "Art Gallery Opening", 4.3, "Art Lovers", "Belgrade, Serbia", "22.10.2024. 19:00"));
        list.add(new Event(14L, "Science Fair", 4.4, "STEM Serbia", "Kragujevac, Serbia", "05.11.2024. 10:30"));
        list.add(new Event(15L, "Winter Wonderland Festival", 4.9, "Winter Fest", "Niš, Serbia", "15.12.2024. 16:00"));

        return list;
    }

    private List<Event> getAllEvents() {
        List<Event> list = new ArrayList<>();

        list.add(new Event(1L, "Mary And Josh's Wedding", 2.5, "Organizer 1", "Beograd, Serbia", "12.12.2024. 12:03"));
        list.add(new Event(2L, "Mary And Josh's Wedding", 5, "Organizer 1", "Novi Sad, Serbia", "12.12.2024. 12:03"));
        list.add(new Event(3L, "Mary And Josh's Wedding", 1.5, "Organizer 1", "Arilje, Serbia", "12.12.2024. 12:03"));
        list.add(new Event(4L, "Tech Conference 2024", 4, "Tech Corp", "Belgrade, Serbia", "14.01.2024. 09:00"));
        list.add(new Event(5L, "Food Festival", 3.7, "Foodies Ltd.", "Novi Sad, Serbia", "16.02.2024. 11:00"));
        list.add(new Event(6L, "Spring Music Festival", 4.5, "Spring Tunes", "Kragujevac, Serbia", "20.03.2024. 17:00"));
        list.add(new Event(7L, "Fashion Week", 5, "Fashion Events", "Zrenjanin, Serbia", "01.04.2024. 10:00"));
        list.add(new Event(8L, "Digital Marketing Summit", 3.9, "Marketing Hub", "Niš, Serbia", "05.05.2024. 14:30"));
        list.add(new Event(9L, "Yoga Retreat", 4.2, "ZenLife", "Subotica, Serbia", "10.06.2024. 08:00"));
        list.add(new Event(10L, "International Film Festival", 4.8, "Film Association", "Pancevo, Serbia", "12.07.2024. 18:00"));

        return list;
    }

    private List<Offering> getTopOfferings() {
        List<Offering> list = new ArrayList<Offering>();

        list.add(new Product(11L, "Luxury Wedding Shoes", 4.4, "Provider 11", 80));
        list.add(new Service(12L, "Hair Styling for Wedding", 4.1, "Provider 12", 1500));
        list.add(new Product(13L, "Wedding Party Favors", 3.9, "Provider 13", 70));
        list.add(new Service(14L, "Officiant for Wedding", 4.7, "Provider 14", 1000));
        list.add(new Product(15L, "Wedding Decor", 4.3, "Provider 15", 500));

        return list;
    }

    private List<Offering> getAllOfferings() {
        List<Offering> list = new ArrayList<Offering>();

        list.add(new Product(1L, "Wedding Flowers", 2.5, "Provider 1", 90));
        list.add(new Service(2L, "Wedding Makeup", 4, "Provider 2", 2500));
        list.add(new Product(3L, "Custom Wedding Invitations", 4.2, "Provider 3", 50));
        list.add(new Service(4L, "DJ for Wedding", 5, "Provider 4", 5000));
        list.add(new Product(5L, "Bridal Gown", 4.8, "Provider 5", 200));
        list.add(new Service(6L, "Wedding Photography", 4.9, "Provider 6", 3000));
        list.add(new Product(7L, "Wedding Cake", 3.7, "Provider 7", 120));
        list.add(new Service(8L, "Floral Arrangement for Wedding", 4.3, "Provider 8", 1500));
        list.add(new Product(9L, "Bridal Jewelry Set", 4.6, "Provider 9", 250));
        list.add(new Service(10L, "Wedding Video Editing", 4.5, "Provider 10", 1200));

        return list;
    }

}