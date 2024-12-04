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

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import m3.eventplanner.R;
import m3.eventplanner.adapters.EventListAdapter;
import m3.eventplanner.adapters.OfferingListAdapter;
import m3.eventplanner.models.AgendaItem;
import m3.eventplanner.models.Event;
import m3.eventplanner.models.EventType;
import m3.eventplanner.models.Location;
import m3.eventplanner.models.Offering;
import m3.eventplanner.models.Organizer;
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

        List<Event> events = new ArrayList<>();

        events.add(new Event(1, new EventType(1, "Wedding"), "Mary And Josh's Wedding", 2.5,
                new Organizer(1, "John", "Doe", new Location(1, "Serbia", "Beograd", "Main Street", "10"),
                        "john.doe@example.com", "123456789", "https://media.istockphoto.com/id/1437816897/photo/business-woman-manager-or-human-resources-portrait-for-career-success-company-we-are-hiring.jpg?s=612x612&w=0&k=20&c=tyLvtzutRh22j9GqSGI33Z4HpIwv9vL_MZw_xOE19NQ="),
                new Location(2, "Serbia", "Beograd", "Wedding Venue", "5"),
                LocalDate.of(2024, 12, 12), "A beautiful winter wedding.", 150, true,
                new ArrayList<AgendaItem>() {{
                    add(new AgendaItem(1, "Ceremony", "Wedding ceremony at the main hall.", "12:03", "12:30", "Main Hall"));
                    add(new AgendaItem(2, "Reception", "Wedding reception with dinner and music.", "12:30", "14:00", "Banquet Hall"));
                }}
        ));

        events.add(new Event(2, new EventType(1, "Wedding"), "Mary And Josh's Wedding", 5.0,
                new Organizer(2, "John", "Doe", new Location(1, "Serbia", "Novi Sad", "Main Street", "10"),
                        "john.doe@example.com", "123456789", null),
                new Location(3, "Serbia", "Novi Sad", "Wedding Venue", "10"),
                LocalDate.of(2024, 12, 12), "A magical wedding reception.", 200, true,
                new ArrayList<AgendaItem>() {{
                    add(new AgendaItem(1, "Ceremony", "Wedding ceremony at the main hall.", "12:03", "12:30", "Main Hall"));
                    add(new AgendaItem(2, "Reception", "Wedding reception with dinner and music.", "12:30", "14:00", "Banquet Hall"));
                }}
        ));

        events.add(new Event(3, new EventType(1, "Wedding"), "Mary And Josh's Wedding", 1.5,
                new Organizer(3, "John", "Doe", new Location(1, "Serbia", "Arilje", "Main Street", "10"),
                        "john.doe@example.com", "123456789", null),
                new Location(4, "Serbia", "Arilje", "Wedding Venue", "15"),
                LocalDate.of(2024, 12, 12), "A serene countryside wedding.", 100, true,
                new ArrayList<AgendaItem>() {{
                    add(new AgendaItem(1, "Ceremony", "Wedding ceremony at the main hall.", "12:03", "12:30", "Main Hall"));
                    add(new AgendaItem(2, "Reception", "Wedding reception with dinner and music.", "12:30", "14:00", "Banquet Hall"));
                }}
        ));

        events.add(new Event(4, new EventType(2, "Conference"), "Tech Conference 2024", 4.0,
                new Organizer(4, "Alice", "Smith", new Location(5, "Serbia", "Belgrade", "Tech Hub", "20"),
                        "alice.smith@example.com", "987654321", null),
                new Location(6, "Serbia", "Belgrade", "Conference Center", "30"),
                LocalDate.of(2024, 1, 14), "A gathering for tech enthusiasts.", 300, true,
                new ArrayList<AgendaItem>() {{
                    add(new AgendaItem(1, "Keynote", "Opening keynote by the CEO.", "09:00", "10:00", "Main Stage"));
                    add(new AgendaItem(2, "Panel Discussion", "Discussion on the future of tech.", "10:15", "11:00", "Room A"));
                    add(new AgendaItem(3, "Networking", "Networking session for all attendees.", "11:15", "12:00", "Lounge"));
                }}
        ));

        events.add(new Event(5, new EventType(3, "Festival"), "Food Festival", 3.7,
                new Organizer(5, "Emily", "Brown", new Location(7, "Serbia", "Novi Sad", "Food Street", "5"),
                        "emily.brown@example.com", "1122334455", null),
                new Location(8, "Serbia", "Novi Sad", "Festival Grounds", "40"),
                LocalDate.of(2024, 2, 16), "A delicious culinary journey.", 500, true,
                new ArrayList<AgendaItem>() {{
                    add(new AgendaItem(1, "Food Tasting", "Taste a variety of cuisines.", "11:00", "13:00", "Food Court"));
                    add(new AgendaItem(2, "Cooking Demo", "Live cooking demonstrations.", "13:15", "14:30", "Cooking Stage"));
                    add(new AgendaItem(3, "Chef Talk", "Meet renowned chefs and learn their secrets.", "14:45", "16:00", "Chef's Corner"));
                }}
        ));

        return events;
    }

    private List<Event> getAllEvents() {
        List<Event> events = new ArrayList<>();

        events.add(new Event(1, new EventType(1, "Wedding"), "Mary And Josh's Wedding", 2.5,
                new Organizer(1, "John", "Doe", new Location(1, "Serbia", "Beograd", "Main Street", "10"),
                        "john.doe@example.com", "123456789", null),
                new Location(2, "Serbia", "Beograd", "Wedding Venue", "5"),
                LocalDate.of(2024, 12, 12), "A beautiful winter wedding.", 150, true,
                new ArrayList<AgendaItem>() {{
                    add(new AgendaItem(1, "Ceremony", "Wedding ceremony at the main hall.", "12:03", "12:30", "Main Hall"));
                    add(new AgendaItem(2, "Reception", "Wedding reception with dinner and music.", "12:30", "14:00", "Banquet Hall"));
                }}
        ));

        events.add(new Event(2, new EventType(1, "Wedding"), "Mary And Josh's Wedding", 5.0,
                new Organizer(2, "John", "Doe", new Location(1, "Serbia", "Novi Sad", "Main Street", "10"),
                        "john.doe@example.com", "123456789", null),
                new Location(3, "Serbia", "Novi Sad", "Wedding Venue", "10"),
                LocalDate.of(2024, 12, 12), "A magical wedding reception.", 200, true,
                new ArrayList<AgendaItem>() {{
                    add(new AgendaItem(1, "Ceremony", "Wedding ceremony at the main hall.", "12:03", "12:30", "Main Hall"));
                    add(new AgendaItem(2, "Reception", "Wedding reception with dinner and music.", "12:30", "14:00", "Banquet Hall"));
                }}
        ));

        events.add(new Event(3, new EventType(1, "Wedding"), "Mary And Josh's Wedding", 1.5,
                new Organizer(3, "John", "Doe", new Location(1, "Serbia", "Arilje", "Main Street", "10"),
                        "john.doe@example.com", "123456789", null),
                new Location(4, "Serbia", "Arilje", "Wedding Venue", "15"),
                LocalDate.of(2024, 12, 12), "A serene countryside wedding.", 100, true,
                new ArrayList<AgendaItem>() {{
                    add(new AgendaItem(1, "Ceremony", "Wedding ceremony at the main hall.", "12:03", "12:30", "Main Hall"));
                    add(new AgendaItem(2, "Reception", "Wedding reception with dinner and music.", "12:30", "14:00", "Banquet Hall"));
                }}
        ));

        events.add(new Event(4, new EventType(2, "Conference"), "Tech Conference 2024", 4.0,
                new Organizer(4, "Alice", "Smith", new Location(5, "Serbia", "Belgrade", "Tech Hub", "20"),
                        "alice.smith@example.com", "987654321", null),
                new Location(6, "Serbia", "Belgrade", "Conference Center", "30"),
                LocalDate.of(2024, 1, 14), "A gathering for tech enthusiasts.", 300, true,
                new ArrayList<AgendaItem>() {{
                    add(new AgendaItem(1, "Keynote", "Opening keynote by the CEO.", "09:00", "10:00", "Main Stage"));
                    add(new AgendaItem(2, "Panel Discussion", "Discussion on the future of tech.", "10:15", "11:00", "Room A"));
                    add(new AgendaItem(3, "Networking", "Networking session for all attendees.", "11:15", "12:00", "Lounge"));
                }}
        ));

        events.add(new Event(5, new EventType(3, "Festival"), "Food Festival", 3.7,
                new Organizer(5, "Emily", "Brown", new Location(7, "Serbia", "Novi Sad", "Food Street", "5"),
                        "emily.brown@example.com", "1122334455", null),
                new Location(8, "Serbia", "Novi Sad", "Festival Grounds", "40"),
                LocalDate.of(2024, 2, 16), "A delicious culinary journey.", 500, true,
                new ArrayList<AgendaItem>() {{
                    add(new AgendaItem(1, "Food Tasting", "Taste a variety of cuisines.", "11:00", "13:00", "Food Court"));
                    add(new AgendaItem(2, "Cooking Demo", "Live cooking demonstrations.", "13:15", "14:30", "Cooking Stage"));
                    add(new AgendaItem(3, "Chef Talk", "Meet renowned chefs and learn their secrets.", "14:45", "16:00", "Chef's Corner"));
                }}
        ));

        events.add(new Event(6, new EventType(3, "Festival"), "Spring Music Festival", 4.5,
                new Organizer(6, "Michael", "Johnson", new Location(9, "Serbia", "Kragujevac", "Music Avenue", "12"),
                        "michael.johnson@example.com", "6677889900", null),
                new Location(10, "Serbia", "Kragujevac", "Open Air Stage", "50"),
                LocalDate.of(2024, 3, 20), "A celebration of spring and music.", 700, true,
                new ArrayList<AgendaItem>() {{
                    add(new AgendaItem(1, "Live Performance", "Live band performance.", "17:00", "18:30", "Main Stage"));
                    add(new AgendaItem(2, "DJ Set", "Dance music set by DJ Mike.", "19:00", "20:30", "Side Stage"));
                }}
        ));

        events.add(new Event(7, new EventType(4, "Fashion"), "Fashion Week", 5.0,
                new Organizer(7, "Sarah", "Davis", new Location(11, "Serbia", "Zrenjanin", "Fashion Street", "18"),
                        "sarah.davis@example.com", "4433221100", null),
                new Location(12, "Serbia", "Zrenjanin", "Fashion Pavilion", "70"),
                LocalDate.of(2024, 4, 1), "Showcasing the latest trends.", 400, true,
                new ArrayList<AgendaItem>() {{
                    add(new AgendaItem(1, "Runway Show", "Fashion show featuring top designers.", "10:00", "12:00", "Main Stage"));
                    add(new AgendaItem(2, "Designer Meet and Greet", "Meet and talk to designers.", "12:30", "14:00", "VIP Room"));
                }}
        ));

        events.add(new Event(8, new EventType(5, "Conference"), "Digital Marketing Summit", 3.9,
                new Organizer(8, "Tom", "Lee", new Location(13, "Serbia", "Niš", "Marketing Hub", "22"),
                        "tom.lee@example.com", "2233445566", null),
                new Location(14, "Serbia", "Niš", "Summit Hall", "80"),
                LocalDate.of(2024, 5, 5), "Exploring digital marketing trends.", 350, true,
                new ArrayList<AgendaItem>() {{
                    add(new AgendaItem(1, "Social Media Marketing", "Strategies for success in social media marketing.", "09:00", "10:30", "Room A"));
                    add(new AgendaItem(2, "SEO and SEM", "Maximizing visibility through SEO and SEM.", "10:45", "12:00", "Room B"));
                }}
        ));

        events.add(new Event(9, new EventType(6, "Retreat"), "Yoga Retreat", 4.2,
                new Organizer(9, "Anna", "Green", new Location(15, "Serbia", "Subotica", "Zen Street", "7"),
                        "anna.green@example.com", "5566778899", null),
                new Location(16, "Serbia", "Subotica", "Yoga Center", "90"),
                LocalDate.of(2024, 6, 10), "A rejuvenating yoga experience.", 100, true,
                new ArrayList<AgendaItem>() {{
                    add(new AgendaItem(1, "Morning Yoga", "Start your day with a peaceful yoga session.", "08:00", "09:30", "Yoga Hall"));
                    add(new AgendaItem(2, "Meditation Session", "Relax and meditate for mental clarity.", "10:00", "11:00", "Meditation Room"));
                }}
        ));

        events.add(new Event(10, new EventType(7, "Festival"), "International Film Festival", 4.8,
                new Organizer(10, "David", "Moore", new Location(17, "Serbia", "Pancevo", "Cinema Street", "5"),
                        "david.moore@example.com", "9988776655", null),
                new Location(18, "Serbia", "Pancevo", "Festival Hall", "100"),
                LocalDate.of(2024, 7, 12), "A week of the best international films.", 600, true,
                new ArrayList<AgendaItem>() {{
                    add(new AgendaItem(1, "Opening Film", "Premiere of the opening film.", "18:00", "20:00", "Main Screen"));
                    add(new AgendaItem(2, "Short Films", "Screening of selected short films.", "20:15", "22:00", "Screen 2"));
                }}
        ));
        return events;
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