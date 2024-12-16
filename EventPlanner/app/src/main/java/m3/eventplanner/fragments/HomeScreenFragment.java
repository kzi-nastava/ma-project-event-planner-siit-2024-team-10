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
import java.util.Collection;
import m3.eventplanner.R;
import m3.eventplanner.adapters.EventListAdapter;
import m3.eventplanner.adapters.OfferingListAdapter;
import m3.eventplanner.clients.ClientUtils;
import m3.eventplanner.clients.EventService;
import m3.eventplanner.models.AgendaItem;
import m3.eventplanner.models.Event;
import m3.eventplanner.models.EventType;
import m3.eventplanner.models.GetEventDTO;
import m3.eventplanner.models.Location;
import m3.eventplanner.models.Offering;
import m3.eventplanner.models.Organizer;
import m3.eventplanner.models.PagedResponse;
import m3.eventplanner.models.Product;
import m3.eventplanner.models.Service;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeScreenFragment extends Fragment {
    private MaterialButtonToggleGroup toggleGroup;
    private View eventSearchBar, offeringSearchBar, paginationBar;
    private RecyclerView contentRecyclerView;
    private TextView noCardsFoundTextView, topEventsTextView, topOfferingsTextView, pageNumber, totalNumberOfElements;
    private int currentEventPage = 0;
    private final int eventPageSize = 3;
    private int totalEventPages = 0;


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
        setUpPaginationButtons(view);
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

    private void setUpPaginationButtons(View rootView) {
        Button paginationForwardButton = rootView.findViewById(R.id.paginationForwardButton);
        Button paginationBackButton = rootView.findViewById(R.id.paginationBackButton);
        pageNumber = rootView.findViewById(R.id.paginationCurrentPage);
        totalNumberOfElements = rootView.findViewById(R.id.totalNumberOfElements);

        paginationForwardButton.setOnClickListener(v -> loadNextPage());
        paginationBackButton.setOnClickListener(v -> loadPreviousPage());
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
        Collection<String> data = new ArrayList<>();
        String[] array = getResources().getStringArray(arrayResId);
        for (String item : array) {
            data.add(item);
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, new ArrayList<>(data));
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
        Call<Collection<GetEventDTO>> call = ClientUtils.eventService.getTopEvents();
        call.enqueue(new Callback<Collection<GetEventDTO>>() {
            @Override
            public void onResponse(Call<Collection<GetEventDTO>> call, Response<Collection<GetEventDTO>> response) {
                if (response.isSuccessful() && response.body() != null){
                    Collection<GetEventDTO> topEvents = response.body();
                    updateRecyclerView(topEvents, new EventListAdapter(topEvents));
                } else{
                    handleNoDataFound();
                }
            }

            @Override
            public void onFailure(Call<Collection<GetEventDTO>> call, Throwable t) {
                Log.d("REZ", t.getMessage() != null ? t.getMessage() : "error");
            }
        });
    }
    private void handleNoDataFound() {
        noCardsFoundTextView.setVisibility(View.VISIBLE);
        contentRecyclerView.setVisibility(View.GONE);
    }

    private void showAllEvents() {
        toggleVisibility(View.GONE, View.GONE, View.VISIBLE, View.GONE, View.VISIBLE);
        Call<PagedResponse<GetEventDTO>> call = ClientUtils.eventService.getEvents(currentEventPage, eventPageSize, null,null,null,null,null,null,null);
        call.enqueue(new Callback<PagedResponse<GetEventDTO>>() {
            @Override
            public void onResponse(Call<PagedResponse<GetEventDTO>> call, Response<PagedResponse<GetEventDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    updateRecyclerView(response.body().getContent(), new EventListAdapter(response.body().getContent()));
                    totalEventPages = response.body().getTotalPages();
                    pageNumber.setText(currentEventPage+1+" of "+totalEventPages);
                    totalNumberOfElements.setText("Number of results: "+response.body().getTotalElements());
                } else {
                    handleNoDataFound();
                }
            }

            @Override
            public void onFailure(Call<PagedResponse<GetEventDTO>> call, Throwable t) {
                Log.e("Pagination", "Failed to fetch data: " + t.getMessage());
            }
        });
    }

    private void showTopOfferings() {
        toggleVisibility(View.GONE, View.VISIBLE, View.GONE, View.GONE, View.GONE);
        Collection<Offering> topOfferings = getTopOfferings();
        updateRecyclerView(topOfferings, new OfferingListAdapter(topOfferings));
    }

    private void showAllOfferings() {
        toggleVisibility(View.GONE, View.GONE, View.GONE, View.VISIBLE, View.VISIBLE);
        Collection<Offering> allOfferings = getAllOfferings();
        updateRecyclerView(allOfferings, new OfferingListAdapter(allOfferings));
    }

    private void loadNextPage() {
        if (currentEventPage+1<totalEventPages) {
            currentEventPage++;
            showAllEvents();
        }
    }

    private void loadPreviousPage() {
        if (currentEventPage > 0) {
            currentEventPage--;
            showAllEvents();
        }
    }


    private void toggleVisibility(int topEventsVisibility, int topOfferingsVisibility, int eventSearchVisibility, int offeringSearchVisibility, int paginationVisibility) {
        topEventsTextView.setVisibility(topEventsVisibility);
        topOfferingsTextView.setVisibility(topOfferingsVisibility);
        eventSearchBar.setVisibility(eventSearchVisibility);
        offeringSearchBar.setVisibility(offeringSearchVisibility);
        paginationBar.setVisibility(paginationVisibility);
    }

    private <T> void updateRecyclerView(Collection<T> data, RecyclerView.Adapter<?> adapter) {
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
    private Collection<Event> getTopEvents() {

        Collection<Event> events = new ArrayList<>();

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

    private Collection<Offering> getTopOfferings() {
        Collection<Offering> list = new ArrayList<Offering>();

        list.add(new Product(11L, "Luxury Wedding Shoes", 4.4, "Provider 11", 80));
        list.add(new Service(12L, "Hair Styling for Wedding", 4.1, "Provider 12", 1500));
        list.add(new Product(13L, "Wedding Party Favors", 3.9, "Provider 13", 70));
        list.add(new Service(14L, "Officiant for Wedding", 4.7, "Provider 14", 1000));
        list.add(new Product(15L, "Wedding Decor", 4.3, "Provider 15", 500));

        return list;
    }

    private Collection<Offering> getAllOfferings() {
        Collection<Offering> list = new ArrayList<Offering>();

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