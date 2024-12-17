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
import m3.eventplanner.models.GetOfferingDTO;
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

    private int currentOfferingPage = 0;
    private final int offeringPageSize = 3;
    private int totalOfferingPages = 0;

    private enum PaginationContext {
        EVENTS, OFFERINGS
    }

    private PaginationContext currentPaginationContext = PaginationContext.EVENTS;
    private ClientUtils clientUtils;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        clientUtils = new ClientUtils(requireContext());
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
        Call<Collection<GetEventDTO>> call = clientUtils.getEventService().getTopEvents();
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
        currentPaginationContext = PaginationContext.EVENTS;
        Call<PagedResponse<GetEventDTO>> call = clientUtils.getEventService().getEvents(currentEventPage, eventPageSize, null,null,null,null,null,null,null);
        call.enqueue(new Callback<PagedResponse<GetEventDTO>>() {
            @Override
            public void onResponse(Call<PagedResponse<GetEventDTO>> call, Response<PagedResponse<GetEventDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    updateRecyclerView(response.body().getContent(), new EventListAdapter(response.body().getContent()));
                    totalEventPages = response.body().getTotalPages();
                    pageNumber.setText("Page "+ (currentEventPage+1)+" of "+totalEventPages);
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
        Call<Collection<GetOfferingDTO>> call = clientUtils.getOfferingService().getTopOfferings();
        call.enqueue(new Callback<Collection<GetOfferingDTO>>() {
            @Override
            public void onResponse(Call<Collection<GetOfferingDTO>> call, Response<Collection<GetOfferingDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Collection<GetOfferingDTO> topOfferings = response.body();
                    updateRecyclerView(topOfferings, new OfferingListAdapter(topOfferings));
                } else {
                    handleNoDataFound();
                }
            }

            @Override
            public void onFailure(Call<Collection<GetOfferingDTO>> call, Throwable t) {
                Log.d("Offerings", t.getMessage() != null ? t.getMessage() : "error");
            }
        });
    }


    private void showAllOfferings() {
        toggleVisibility(View.GONE, View.GONE, View.GONE, View.VISIBLE, View.VISIBLE);
        currentPaginationContext = PaginationContext.OFFERINGS;
        Call<PagedResponse<GetOfferingDTO>> call = clientUtils.getOfferingService().getOfferings(currentOfferingPage, offeringPageSize, null, null, null, null, null, null, null, null,null,null,null, null,null);
        call.enqueue(new Callback<PagedResponse<GetOfferingDTO>>() {
            @Override
            public void onResponse(Call<PagedResponse<GetOfferingDTO>> call, Response<PagedResponse<GetOfferingDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    updateRecyclerView(response.body().getContent(), new OfferingListAdapter(response.body().getContent()));
                    totalOfferingPages = response.body().getTotalPages();
                    pageNumber.setText("Page " + (currentOfferingPage + 1) + " of " + totalOfferingPages);
                    totalNumberOfElements.setText("Number of results: " + response.body().getTotalElements());
                } else {
                    handleNoDataFound();
                }
            }

            @Override
            public void onFailure(Call<PagedResponse<GetOfferingDTO>> call, Throwable t) {
                Log.e("OfferingsPagination", "Failed to fetch data: " + t.getMessage());
            }
        });
    }


    private void loadNextPage() {
        switch (currentPaginationContext){
            case EVENTS:
                if (currentEventPage+1<totalEventPages) {
                    currentEventPage++;
                    showAllEvents();
                }
                break;
            case OFFERINGS:
                if(currentOfferingPage+1 <totalOfferingPages){
                    currentOfferingPage++;
                    showAllOfferings();
                }
                break;
        }
    }

    private void loadPreviousPage() {

        switch (currentPaginationContext){
            case EVENTS:
                if (currentEventPage > 0) {
                    currentEventPage--;
                    showAllEvents();
                }
                break;
            case OFFERINGS:
                if (currentOfferingPage > 0) {
                    currentOfferingPage--;
                    showAllOfferings();
                }
                break;
        }
    }

    private void loadNextOfferingPage() {
        if (currentOfferingPage + 1 < totalOfferingPages) {
            currentOfferingPage++;
            showAllOfferings();
        }
    }

    private void loadPreviousOfferingPage() {
        if (currentOfferingPage > 0) {
            currentOfferingPage--;
            showAllOfferings();
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
}