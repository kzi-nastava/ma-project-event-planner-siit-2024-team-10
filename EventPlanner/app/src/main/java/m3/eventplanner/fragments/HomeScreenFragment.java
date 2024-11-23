package m3.eventplanner.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import m3.eventplanner.R;
import m3.eventplanner.adapters.EventListAdapter;
import m3.eventplanner.adapters.OfferingListAdapter;
import m3.eventplanner.models.Event;
import m3.eventplanner.models.Offering;
import m3.eventplanner.models.Product;
import m3.eventplanner.models.Service;

public class HomeScreenFragment extends Fragment {

    private MaterialButtonToggleGroup toggleGroup;
    private View eventSearchBar;
    private View offeringSearchBar;
    private View paginationBar;
    private RecyclerView contentRecyclerView;
    private TextView noCardsFoundTextView;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_homescreen, container, false);

        // Initialize UI elements
        toggleGroup = rootView.findViewById(R.id.toggleButton);
        eventSearchBar = rootView.findViewById(R.id.eventSearchBar);
        offeringSearchBar = rootView.findViewById(R.id.offeringSearchBar);
        paginationBar = rootView.findViewById(R.id.paginationBar);
        contentRecyclerView = rootView.findViewById(R.id.contentRecyclerView);
        noCardsFoundTextView = rootView.findViewById(R.id.noCardsFoundTextView);

        // Set up RecyclerView
        contentRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Set up default view (e.g., Top Events)
        if (savedInstanceState == null) {
            showTopEvents();
        }

        // Set up toggle button listener
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

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Spinner eventSortBySpinner = view.findViewById(R.id.btn_sort_events_by);
        List<String> eventSortCriteria = new ArrayList<>();
        eventSortCriteria.add("Any");
        eventSortCriteria.add("Name");
        eventSortCriteria.add("Rating");
        eventSortCriteria.add("Type Name");

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                eventSortCriteria
        );
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        eventSortBySpinner.setAdapter(spinnerAdapter);

        Spinner eventSortDirectionSpinner = view.findViewById(R.id.btn_sort_events_direction);
        List<String> eventSortDirection = new ArrayList<>();
        eventSortDirection.add("Ascending");
        eventSortDirection.add("Descending");

        ArrayAdapter<String> spinnerDirectionAdapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                eventSortDirection
        );
        spinnerDirectionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        eventSortDirectionSpinner.setAdapter(spinnerDirectionAdapter);


        // Button for opening filters
        Button btnFilters = view.findViewById(R.id.filter_events_button);
        btnFilters.setOnClickListener(v -> {
            showEventFilterBottomSheet();
        });
    }


    private void showTopEvents() {
        // Hide unnecessary elements
        eventSearchBar.setVisibility(View.GONE);
        offeringSearchBar.setVisibility(View.GONE);
        paginationBar.setVisibility(View.GONE);

        // Load data and set adapter
        List<Event> topEvents = getTopEvents();
        if (topEvents == null || topEvents.isEmpty()) {
            noCardsFoundTextView.setText(R.string.no_events);
            noCardsFoundTextView.setVisibility(View.VISIBLE);
            contentRecyclerView.setVisibility(View.GONE);
        } else {
            noCardsFoundTextView.setVisibility(View.GONE);
            contentRecyclerView.setVisibility(View.VISIBLE);
            EventListAdapter eventAdapter = new EventListAdapter(topEvents);
            contentRecyclerView.setAdapter(eventAdapter);
        }
    }

    private void showAllEvents() {
        // Show event search bar and pagination
        eventSearchBar.setVisibility(View.VISIBLE);
        offeringSearchBar.setVisibility(View.GONE);
        paginationBar.setVisibility(View.VISIBLE);

        // Load data and set adapter
        List<Event> allEvents = getAllEvents();
        if (allEvents == null || allEvents.isEmpty()) {
            noCardsFoundTextView.setText(R.string.no_events);
            noCardsFoundTextView.setVisibility(View.VISIBLE);
            contentRecyclerView.setVisibility(View.GONE);
        } else {
            noCardsFoundTextView.setVisibility(View.GONE);
            contentRecyclerView.setVisibility(View.VISIBLE);
            EventListAdapter eventAdapter = new EventListAdapter(allEvents);
            contentRecyclerView.setAdapter(eventAdapter);
        }
    }

    private void showTopOfferings() {
        // Hide unnecessary elements
        eventSearchBar.setVisibility(View.GONE);
        offeringSearchBar.setVisibility(View.GONE);
        paginationBar.setVisibility(View.GONE);

        // Load data and set adapter
        List<Offering> topOfferings = getTopOfferings();
        if (topOfferings == null || topOfferings.isEmpty()) {
            noCardsFoundTextView.setText(R.string.no_offerings);
            noCardsFoundTextView.setVisibility(View.VISIBLE);
            contentRecyclerView.setVisibility(View.GONE);
        } else {
            noCardsFoundTextView.setVisibility(View.GONE);
            contentRecyclerView.setVisibility(View.VISIBLE);
            OfferingListAdapter offeringAdapter = new OfferingListAdapter(topOfferings);
            contentRecyclerView.setAdapter(offeringAdapter);
        }
    }

    private void showAllOfferings() {
        // Show offering search bar and pagination
        eventSearchBar.setVisibility(View.GONE);
        offeringSearchBar.setVisibility(View.VISIBLE);
        paginationBar.setVisibility(View.VISIBLE);

        // Load data and set adapter
        List<Offering> allOfferings = getAllOfferings();
        if (allOfferings == null || allOfferings.isEmpty()) {
            noCardsFoundTextView.setText(R.string.no_offerings);
            noCardsFoundTextView.setVisibility(View.VISIBLE);
            contentRecyclerView.setVisibility(View.GONE);
        } else {
            noCardsFoundTextView.setVisibility(View.GONE);
            contentRecyclerView.setVisibility(View.VISIBLE);
            OfferingListAdapter offeringAdapter = new OfferingListAdapter(allOfferings);
            contentRecyclerView.setAdapter(offeringAdapter);
        }
    }

    // Example data fetching methods (replace with actual implementation)
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

    private void showEventFilterBottomSheet() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(requireContext());
        View bottomSheetView = getLayoutInflater().inflate(R.layout.filter_events, null);
        bottomSheetDialog.setContentView(bottomSheetView);

        MaterialDatePicker.Builder<Pair<Long, Long>> builder = MaterialDatePicker.Builder.dateRangePicker();
        builder.setTitleText("Select Date Range");
        final MaterialDatePicker<Pair<Long, Long>> picker = builder.build();

        // Find and set up the date range button
        Button dateRangeButton = bottomSheetView.findViewById(R.id.date_range_button);
        TextView selectedDatesTextView = bottomSheetView.findViewById(R.id.selected_dates);

        dateRangeButton.setOnClickListener(v -> picker.show(getParentFragmentManager(), "DATE_PICKER"));

        // Handle the date range picker result
        picker.addOnPositiveButtonClickListener(selection -> {
            // Extract the date range
            Pair<Long, Long> dateRange = picker.getSelection();
            if (dateRange != null) {
                Long startDate = dateRange.first; // Start date in milliseconds
                Long endDate = dateRange.second; // End date in milliseconds

                String formattedStartDate = formatDate(startDate);
                String formattedEndDate = formatDate(endDate);

                String selectedDatesText = getString(R.string.selected_date_range, formattedStartDate, formattedEndDate);
                selectedDatesTextView.setText(selectedDatesText);
            }
        });

        Spinner eventTypeSpinner = bottomSheetView.findViewById(R.id.event_type_spinner);
        List<String> eventTypes = new ArrayList<>();
        eventTypes.add("Any");
        eventTypes.add("Wedding");
        eventTypes.add("Concert");
        eventTypes.add("Convention");

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                eventTypes
        );
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        eventTypeSpinner.setAdapter(spinnerAdapter);

        bottomSheetDialog.show();
    }

    // Helper method to format date
    private String formatDate(Long dateInMillis) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
        return formatter.format(new Date(dateInMillis));
    }
}