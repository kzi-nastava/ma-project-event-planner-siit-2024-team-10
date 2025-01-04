package m3.eventplanner.fragments.home;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.slider.RangeSlider;
import com.google.android.material.slider.Slider;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import m3.eventplanner.R;
import m3.eventplanner.adapters.EventListAdapter;
import m3.eventplanner.adapters.OfferingListAdapter;
import m3.eventplanner.clients.ClientUtils;
import m3.eventplanner.models.GetEventTypeDTO;
import m3.eventplanner.models.GetOfferingCategoryDTO;

public class HomeScreenFragment extends Fragment {
    private MaterialButtonToggleGroup toggleGroup;
    private View eventSearchBar, offeringSearchBar, paginationBar;
    private RecyclerView contentRecyclerView;
    private TextView noCardsFoundTextView, topEventsTextView, topOfferingsTextView, pageNumber, totalNumberOfElements;
    private HomeEventsViewModel eventsViewModel;
    private HomeOfferingViewModel offeringsViewModel;
    private EventListAdapter eventAdapter;
    private OfferingListAdapter offeringAdapter;
    private SearchView searchEventView, searchOfferingView;
    private ClientUtils clientUtils;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        clientUtils = new ClientUtils(requireContext());
        eventsViewModel = new ViewModelProvider(this).get(HomeEventsViewModel.class);
        eventsViewModel.initialize(clientUtils);
        offeringsViewModel = new ViewModelProvider(this).get(HomeOfferingViewModel.class);
        offeringsViewModel.initialize(clientUtils);
        View rootView = inflater.inflate(R.layout.fragment_homescreen, container, false);
        initializeUIElements(rootView);
        setUpRecyclerView();
        eventsViewModel.fetchEventTypes();
        offeringsViewModel.fetchCategories();
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        eventsViewModel.getTopData().observe(getViewLifecycleOwner(), events -> {
            if (events != null && !events.isEmpty()) {
                eventAdapter = new EventListAdapter(events);
                contentRecyclerView.setAdapter(eventAdapter);
                eventAdapter.notifyDataSetChanged();
                contentRecyclerView.setVisibility(View.VISIBLE);
                noCardsFoundTextView.setVisibility(View.GONE);
                topEventsTextView.setVisibility(View.VISIBLE);
                topOfferingsTextView.setVisibility(View.GONE);
            } else {
                handleNoDataFound(true);
            }
        });

        offeringsViewModel.getTopData().observe(getViewLifecycleOwner(), offerings -> {
            if (offerings != null) {
                offeringAdapter = new OfferingListAdapter(offerings);
                contentRecyclerView.setAdapter(offeringAdapter);
                offeringAdapter.notifyDataSetChanged();
                contentRecyclerView.setVisibility(View.VISIBLE);
                noCardsFoundTextView.setVisibility(View.GONE);
                topEventsTextView.setVisibility(View.GONE);
                topOfferingsTextView.setVisibility(View.VISIBLE);
            } else {
                handleNoDataFound(false);
            }
        });

        eventsViewModel.getPagedData().observe(getViewLifecycleOwner(), pagedEvents -> {
            if (pagedEvents != null && !pagedEvents.getContent().equals(new ArrayList<>())) {
                eventAdapter = new EventListAdapter(pagedEvents.getContent());
                contentRecyclerView.setAdapter(eventAdapter);
                eventAdapter.notifyDataSetChanged();
                contentRecyclerView.setVisibility(View.VISIBLE);
                noCardsFoundTextView.setVisibility(View.GONE);
                paginationBar.setVisibility(View.VISIBLE);
                topEventsTextView.setVisibility(View.GONE);
                topOfferingsTextView.setVisibility(View.GONE);
                pageNumber.setText(String.valueOf("Page "+ (eventsViewModel.currentPage + 1) +" of "+ eventsViewModel.totalPages));
                totalNumberOfElements.setText(String.format("Total Elements: %d", eventsViewModel.totalElements));
            } else {
                handleNoDataFound(true);
            }
        });

        offeringsViewModel.getPagedData().observe(getViewLifecycleOwner(), pagedOfferings -> {
            if (pagedOfferings != null && !pagedOfferings.getContent().equals(new ArrayList<>())) {
                offeringAdapter = new OfferingListAdapter(pagedOfferings.getContent());
                contentRecyclerView.setAdapter(offeringAdapter);
                offeringAdapter.notifyDataSetChanged();
                contentRecyclerView.setVisibility(View.VISIBLE);
                noCardsFoundTextView.setVisibility(View.GONE);
                paginationBar.setVisibility(View.VISIBLE);
                topEventsTextView.setVisibility(View.GONE);
                topOfferingsTextView.setVisibility(View.GONE);
                pageNumber.setText(String.valueOf("Page "+ (offeringsViewModel.currentPage + 1) +" of "+ offeringsViewModel.totalPages));
                totalNumberOfElements.setText(String.format("Total Elements: %d", offeringsViewModel.totalElements));
            } else {
                handleNoDataFound(false);
            }
        });

        eventsViewModel.getError().observe(getViewLifecycleOwner(), errorMessage -> {
            if (errorMessage != null && !errorMessage.isEmpty()) {
                Toast.makeText(getContext(), errorMessage, Toast.LENGTH_LONG).show();
            }
        });

        eventsViewModel.loadTopEvents();

        setUpSortSpinners(view);
        setUpFilterButtons(view);
        setUpPaginationButtons(view);
        setUpSearchBar(view);
        setUpOfferingRadioGroup(view);

        setUpToggleGroup(view);
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

        paginationForwardButton.setOnClickListener(v -> {
            if (toggleGroup.getCheckedButtonId() == R.id.tabAllEvents) {
                eventsViewModel.fetchNextPage();
            } else if (toggleGroup.getCheckedButtonId() == R.id.tabAllOfferings) {
                offeringsViewModel.fetchNextPage();
            }
        });

        paginationBackButton.setOnClickListener(v -> {
            if (toggleGroup.getCheckedButtonId() == R.id.tabAllEvents) {
                eventsViewModel.fetchPreviousPage();
            } else if (toggleGroup.getCheckedButtonId() == R.id.tabAllOfferings) {
                offeringsViewModel.fetchPreviousPage();
            }
        });
    }
    private void setUpSearchBar(View view) {
        searchEventView = view.findViewById(R.id.event_search_text);

        searchEventView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                eventsViewModel.loadSearchedEvents(0,query);
                return true;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }

        });

        searchOfferingView = view.findViewById(R.id.offering_search_text);

        searchOfferingView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                offeringsViewModel.loadSearchedOfferings(0,query);
                return true;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }

        });

    }
    private void setUpRecyclerView() {
        contentRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }
    private void resetVisibilityForTab(int tabId) {
        eventSearchBar.setVisibility(View.GONE);
        offeringSearchBar.setVisibility(View.GONE);
        paginationBar.setVisibility(View.GONE);
        contentRecyclerView.setVisibility(View.GONE);

        if (tabId == R.id.tabAllEvents) {
            eventSearchBar.setVisibility(View.VISIBLE);
            paginationBar.setVisibility(View.VISIBLE);
            contentRecyclerView.setVisibility(View.VISIBLE);
        } else if (tabId == R.id.tabAllOfferings) {
            offeringSearchBar.setVisibility(View.VISIBLE);
            paginationBar.setVisibility(View.VISIBLE);
            contentRecyclerView.setVisibility(View.VISIBLE);
        } else {
            contentRecyclerView.setVisibility(View.VISIBLE);
        }
    }
    private void setUpToggleGroup(View view) {
        toggleGroup.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (isChecked) {
                resetVisibilityForTab(checkedId);
                if (checkedId == R.id.tabTopEvents) {
                    eventsViewModel.loadTopEvents();
                } else if (checkedId == R.id.tabAllEvents) {
                    eventsViewModel.fetchPage(0);
                } else if (checkedId == R.id.tabTopOfferings) {
                    offeringsViewModel.loadTopOfferings();
                } else if (checkedId == R.id.tabAllOfferings) {
                    Boolean isService = getSelectedOfferingType(view);
                    offeringsViewModel.loadOfferings(0, isService);
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
        Spinner eventSortDirectionSpinner = view.findViewById(R.id.btn_sort_events_direction);

        eventSortBySpinner.setAdapter(createSpinnerAdapter(R.array.event_sort_criteria));
        eventSortDirectionSpinner.setAdapter(createSpinnerAdapter(R.array.sort_directions));

        Map<String, String> sortCriteriaMapping = new HashMap<>();
        sortCriteriaMapping.put("Any", null);
        sortCriteriaMapping.put("Name", "name");
        sortCriteriaMapping.put("Date", "date");
        sortCriteriaMapping.put("Average Rating", "averageRating");
        sortCriteriaMapping.put("City", "location.city");

        Map<String, String> sortDirectionMapping = new HashMap<>();
        sortDirectionMapping.put("Ascending", "ASC");
        sortDirectionMapping.put("Descending", "DESC");

        AdapterView.OnItemSelectedListener sortListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedSortBy = eventSortBySpinner.getSelectedItem().toString();
                String selectedSortDirection = eventSortDirectionSpinner.getSelectedItem().toString();

                String sortBy = sortCriteriaMapping.get(selectedSortBy);
                String sortDirection = sortDirectionMapping.get(selectedSortDirection);

                eventsViewModel.loadSortedEvents(0, sortBy, sortDirection);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        };

        eventSortBySpinner.setOnItemSelectedListener(sortListener);
        eventSortDirectionSpinner.setOnItemSelectedListener(sortListener);
    }



    private void setUpOfferingSortSpinners(View view) {
        Spinner offeringSortBySpinner = view.findViewById(R.id.btn_sort_offerings_by);
        Spinner offeringSortDirectionSpinner = view.findViewById(R.id.btn_sort_offering_direction);

        offeringSortBySpinner.setAdapter(createSpinnerAdapter(R.array.offering_sort_criteria));
        offeringSortDirectionSpinner.setAdapter(createSpinnerAdapter(R.array.sort_directions));

        Map<String, String> sortCriteriaMapping = new HashMap<>();
        sortCriteriaMapping.put("Any", null);
        sortCriteriaMapping.put("Name", "name");
        sortCriteriaMapping.put("Price", "price");
        sortCriteriaMapping.put("Average Rating", "averageRating");
        sortCriteriaMapping.put("City", "location.city");

        Map<String, String> sortDirectionMapping = new HashMap<>();
        sortDirectionMapping.put("Ascending", "ASC");
        sortDirectionMapping.put("Descending", "DESC");

        AdapterView.OnItemSelectedListener sortListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedSortBy = offeringSortBySpinner.getSelectedItem().toString();
                String selectedSortDirection = offeringSortDirectionSpinner.getSelectedItem().toString();

                String sortBy = sortCriteriaMapping.get(selectedSortBy);
                String sortDirection = sortDirectionMapping.get(selectedSortDirection);

                offeringsViewModel.loadSortedOfferings(0, sortBy, sortDirection);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        };

        offeringSortBySpinner.setOnItemSelectedListener(sortListener);
        offeringSortDirectionSpinner.setOnItemSelectedListener(sortListener);
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
            Boolean isService = getSelectedOfferingType(view);
            showOfferingFilterBottomSheet(isService);
        });
    }

    private Boolean getSelectedOfferingType(View view) {
        RadioGroup offeringRadioGroup = view.findViewById(R.id.offering_radio_group);
        int selectedId = offeringRadioGroup.getCheckedRadioButtonId();
        if (selectedId != -1) {
            String selectedText = ((RadioButton) view.findViewById(selectedId)).getText().toString();
            return "Service".equalsIgnoreCase(selectedText);
        } else {
            Log.e("HomeScreenFragment", "No radio button selected.");
            return null;
        }
    }

    private void setUpOfferingRadioGroup(View view) {
        RadioGroup offeringRadioGroup = view.findViewById(R.id.offering_radio_group);

        offeringRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            Boolean isService = getSelectedOfferingType(view);
            if (isService != null) {
                offeringsViewModel.loadOfferings(0, isService);
            } else {
                Log.e("HomeScreenFragment", "Invalid offering type selection.");
            }
        });
    }

    private void handleNoDataFound(Boolean isEvent) {
        if (isEvent){
            noCardsFoundTextView.setText("No Events Found");
        }else{
            noCardsFoundTextView.setText("No Offerings Found");
        }
        noCardsFoundTextView.setVisibility(View.VISIBLE);
        contentRecyclerView.setVisibility(View.GONE);
        paginationBar.setVisibility(View.GONE);
    }
    private void showEventFilterBottomSheet() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(requireContext());
        View bottomSheetView = getLayoutInflater().inflate(R.layout.filter_events, null);
        bottomSheetDialog.setContentView(bottomSheetView);
        setUpEventFilterDateRangePicker(bottomSheetView);

        eventsViewModel.getEventTypes().observe(getViewLifecycleOwner(), eventTypes -> {
            if (eventTypes != null && !eventTypes.isEmpty()) {
                setUpEventTypeSpinner(bottomSheetView, eventTypes);
            }
        });

        Spinner eventTypeSpinner = bottomSheetView.findViewById(R.id.event_type_spinner);

        Button eventFilterSendButton = bottomSheetView.findViewById(R.id.event_filter_send);
        Button restartEventFilterButton = bottomSheetView.findViewById(R.id.restart_event_filter);

        eventFilterSendButton.setOnClickListener(v -> {
            GetEventTypeDTO selectedEventType = (GetEventTypeDTO) eventTypeSpinner.getSelectedItem();

            Integer eventTypeId = null;
            if (selectedEventType != null && selectedEventType.getId() != -1) {
                eventTypeId = selectedEventType.getId();
            }

            String location = ((TextInputEditText) bottomSheetView.findViewById(R.id.locationTextField)).getText().toString();
            if (location.isEmpty()) {
                location = null;
            }

            Integer maxParticipants = null;
            TextInputEditText maxParticipantsEditText = bottomSheetView.findViewById(R.id.max_participants);
            String maxParticipantsText = maxParticipantsEditText.getText().toString().trim();
            if (!maxParticipantsText.isEmpty()) {
                try {
                    maxParticipants = Integer.parseInt(maxParticipantsText);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }

            Double minRating = (double) ((Slider) bottomSheetView.findViewById(R.id.minPriceSlider)).getValue();
            if (minRating == 0.0){
                minRating = null;
            }
            TextView selectedDatesTextView = bottomSheetView.findViewById(R.id.selected_dates);
            String dates = selectedDatesTextView.getText().toString().trim();
            String startDateString = null;
            String endDateString = null;
            String[] dateParts = dates.split(" - ");
            if (dateParts.length == 2) {
                String startDateStr = dateParts[0].trim();
                String endDateStr = dateParts[1].trim();

                SimpleDateFormat inputDateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.ENGLISH);

                try {
                    Date startDate = inputDateFormat.parse(startDateStr);
                    Date endDate = inputDateFormat.parse(endDateStr);

                    SimpleDateFormat outputDateFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.ENGLISH);

                    startDateString = outputDateFormat.format(startDate);
                    endDateString = outputDateFormat.format(endDate);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            eventsViewModel.loadFilteredEvents(0, eventTypeId, location, maxParticipants, minRating, startDateString, endDateString);
            bottomSheetDialog.dismiss();
        });

        restartEventFilterButton.setOnClickListener(v->{
            eventsViewModel.resetFilters();
            bottomSheetDialog.dismiss();
        });

        bottomSheetDialog.show();
    }

    private void showOfferingFilterBottomSheet(Boolean isService) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(requireContext());
        View bottomSheetView = getLayoutInflater().inflate(R.layout.homepage_filter_offerings, null);
        bottomSheetDialog.setContentView(bottomSheetView);

        RangeSlider priceRangeSlider = bottomSheetView.findViewById(R.id.price_range_slider);

        offeringsViewModel.getCategories().observe(getViewLifecycleOwner(), categories -> {
            if (categories != null && !categories.isEmpty()) {
                setUpCategorySpinner(bottomSheetView, categories);
            }
        });

        offeringsViewModel.getHighestPrice().observe(getViewLifecycleOwner(), highestPrice -> {
            if (highestPrice != null) {
                priceRangeSlider.setValueTo(highestPrice.floatValue());

                List<Float> currentValues = priceRangeSlider.getValues();
                List<Float> adjustedValues = new ArrayList<>();
                for (Float value : currentValues) {
                    adjustedValues.add(Math.min(value, highestPrice.floatValue()));
                }
                priceRangeSlider.setValues(adjustedValues);
            } else {
                Log.e("HighestPrice", "Failed to fetch highest price");
            }
        });
        offeringsViewModel.fetchHighestPrice(isService);

        setUpVisibilityForOfferingType(bottomSheetView, isService);

        bottomSheetDialog.show();
    }

    private void setUpEventFilterDateRangePicker(View bottomSheetView) {
        Button dateRangeButton = bottomSheetView.findViewById(R.id.date_range_button);
        TextView selectedDatesTextView = bottomSheetView.findViewById(R.id.selected_dates);
        MaterialDatePicker<Pair<Long, Long>> picker = MaterialDatePicker.Builder.dateRangePicker().setTitleText("Select Date Range").build();
        dateRangeButton.setOnClickListener(v -> picker.show(getParentFragmentManager(), "DATE_PICKER"));
        picker.addOnPositiveButtonClickListener(selection -> updateSelectedDateRange(selection, selectedDatesTextView));
    }

    private void setUpEventTypeSpinner(View bottomSheetView, List<GetEventTypeDTO> eventTypes) {
        Spinner eventTypeSpinner = bottomSheetView.findViewById(R.id.event_type_spinner);
        if (eventTypeSpinner != null) {
            List<GetEventTypeDTO> spinnerList = new ArrayList<>();
            GetEventTypeDTO anyOption = new GetEventTypeDTO();
            anyOption.setName("Any");
            anyOption.setId(-1);
            spinnerList.add(anyOption);
            spinnerList.addAll(eventTypes);

            ArrayAdapter<GetEventTypeDTO> adapter = new ArrayAdapter<GetEventTypeDTO>(requireContext(), android.R.layout.simple_spinner_item, spinnerList) {
                @Override
                public View getDropDownView(int position, View convertView, ViewGroup parent) {
                    View view = super.getDropDownView(position, convertView, parent);
                    TextView textView = (TextView) view;
                    textView.setText(spinnerList.get(position).getName());
                    return view;
                }

                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    TextView textView = (TextView) super.getView(position, convertView, parent);
                    textView.setText(spinnerList.get(position).getName());
                    return textView;
                }
            };

            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            eventTypeSpinner.setAdapter(adapter);
            eventTypeSpinner.setTag(spinnerList);

            eventTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if (spinnerList != null && !spinnerList.isEmpty()) {
                        GetEventTypeDTO selectedEventType = spinnerList.get(position);
                        if (selectedEventType != null && selectedEventType.getId() != -1) {
                            int selectedEventTypeId = selectedEventType.getId();
                        } else {
                        }
                    }
                }
                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        } else {
            Log.e("HomeScreenFragment", "Event Type Spinner is null.");
        }
    }
    private void setUpCategorySpinner(View bottomSheetView, List<GetOfferingCategoryDTO> categories) {
        Spinner categorySpinner = bottomSheetView.findViewById(R.id.category_spinner);
        if (categorySpinner != null) {
            List<GetOfferingCategoryDTO> spinnerList = new ArrayList<>();
            GetOfferingCategoryDTO anyOption = new GetOfferingCategoryDTO();
            anyOption.setName("Any");
            anyOption.setId(-1);
            spinnerList.add(anyOption);
            spinnerList.addAll(categories);

            ArrayAdapter<GetOfferingCategoryDTO> adapter = new ArrayAdapter<GetOfferingCategoryDTO>(requireContext(), android.R.layout.simple_spinner_item, spinnerList) {
                @Override
                public View getDropDownView(int position, View convertView, ViewGroup parent) {
                    View view = super.getDropDownView(position, convertView, parent);
                    TextView textView = (TextView) view;
                    textView.setText(spinnerList.get(position).getName());
                    return view;
                }

                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    TextView textView = (TextView) super.getView(position, convertView, parent);
                    textView.setText(spinnerList.get(position).getName());
                    return textView;
                }
            };

            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            categorySpinner.setAdapter(adapter);
            categorySpinner.setTag(spinnerList);

            categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if (spinnerList != null && !spinnerList.isEmpty()) {
                        GetOfferingCategoryDTO selectedCategory = spinnerList.get(position);
                        if (selectedCategory != null && selectedCategory.getId() != -1) {
                            int selectedCategoryTypeId = selectedCategory.getId();
                        } else {
                        }
                    }
                }
                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        } else {
            Log.e("HomeScreenFragment", "Category Spinner is null.");
        }
    }

    private void setUpVisibilityForOfferingType(View bottomSheetView, Boolean isService) {
        View serviceDurationView = bottomSheetView.findViewById(R.id.service_duration);
        if (isService) {
            serviceDurationView.setVisibility(View.VISIBLE);
        } else {
            serviceDurationView.setVisibility(View.GONE);
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