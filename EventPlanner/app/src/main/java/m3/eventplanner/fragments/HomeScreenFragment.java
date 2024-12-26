package m3.eventplanner.fragments;

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
import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.slider.Slider;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import m3.eventplanner.R;
import m3.eventplanner.adapters.EventListAdapter;
import m3.eventplanner.adapters.OfferingListAdapter;
import m3.eventplanner.clients.ClientUtils;
import m3.eventplanner.models.GetEventTypeDTO;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeScreenFragment extends Fragment {
    private MaterialButtonToggleGroup toggleGroup;
    private View eventSearchBar, offeringSearchBar, paginationBar;
    private RecyclerView contentRecyclerView;
    private TextView noCardsFoundTextView, topEventsTextView, topOfferingsTextView, pageNumber, totalNumberOfElements;
    private HomeScreenViewModel homeScreenViewModel;
    private EventListAdapter eventAdapter;
    private OfferingListAdapter offeringAdapter;


    private ClientUtils clientUtils;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        clientUtils = new ClientUtils(requireContext());
        homeScreenViewModel = new ViewModelProvider(this, new HomeScreenViewModelFactory(clientUtils)).get(HomeScreenViewModel.class);
        View rootView = inflater.inflate(R.layout.fragment_homescreen, container, false);
        initializeUIElements(rootView);
        setUpRecyclerView();
        setUpToggleGroup();
        homeScreenViewModel.fetchEventTypes();
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        homeScreenViewModel.getTopEvents().observe(getViewLifecycleOwner(), events -> {
            if (events != null && !events.isEmpty()) {
                eventAdapter = new EventListAdapter(events);
                contentRecyclerView.setAdapter(eventAdapter);
                eventAdapter.notifyDataSetChanged();
                contentRecyclerView.setVisibility(View.VISIBLE);
                noCardsFoundTextView.setVisibility(View.GONE);
            } else {
                handleNoDataFound();
            }
        });

        homeScreenViewModel.getTopOfferings().observe(getViewLifecycleOwner(), offerings -> {
            if (offerings != null) {
                offeringAdapter = new OfferingListAdapter(offerings);
                contentRecyclerView.setAdapter(offeringAdapter);
                offeringAdapter.notifyDataSetChanged();
                contentRecyclerView.setVisibility(View.VISIBLE);
                noCardsFoundTextView.setVisibility(View.GONE);
            } else {
                handleNoDataFound();
            }
        });

        homeScreenViewModel.getPagedEvents().observe(getViewLifecycleOwner(), pagedEvents -> {
            if (pagedEvents != null && pagedEvents.getContent() != null) {
                eventAdapter = new EventListAdapter(pagedEvents.getContent());
                contentRecyclerView.setAdapter(eventAdapter);
                eventAdapter.notifyDataSetChanged();
                contentRecyclerView.setVisibility(View.VISIBLE);
                noCardsFoundTextView.setVisibility(View.GONE);
                paginationBar.setVisibility(View.VISIBLE);
                pageNumber.setText(String.valueOf("Page "+ (homeScreenViewModel.getCurrentEventPage() + 1) +" of "+ homeScreenViewModel.getTotalEventPages()));
                totalNumberOfElements.setText(String.format("Total Elements: %d", homeScreenViewModel.getNumOfEvents()));
            } else {
                handleNoDataFound();
            }
        });

        homeScreenViewModel.getPagedOfferings().observe(getViewLifecycleOwner(), pagedOfferings -> {
            if (pagedOfferings != null && pagedOfferings.getContent() != null) {
                offeringAdapter = new OfferingListAdapter(pagedOfferings.getContent());
                contentRecyclerView.setAdapter(offeringAdapter);
                offeringAdapter.notifyDataSetChanged();
                contentRecyclerView.setVisibility(View.VISIBLE);
                noCardsFoundTextView.setVisibility(View.GONE);
                paginationBar.setVisibility(View.VISIBLE);
                pageNumber.setText(String.valueOf("Page "+ (homeScreenViewModel.getCurrentOfferingPage() + 1) +" of "+ homeScreenViewModel.getTotalOfferingPages()));
                totalNumberOfElements.setText(String.format("Total Elements: %d", homeScreenViewModel.getNumOfOfferings()));
            } else {
                handleNoDataFound();
            }
        });

        homeScreenViewModel.loadTopEvents();

        setUpSortSpinners(view);
        setUpFilterButtons(view);
        setUpPaginationButtons(view);

        setUpToggleGroup();
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

        paginationForwardButton.setOnClickListener(v -> homeScreenViewModel.loadNextPage());
        paginationBackButton.setOnClickListener(v -> homeScreenViewModel.loadPreviousPage());
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
    private void setUpToggleGroup() {
        toggleGroup.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (isChecked) {
                resetVisibilityForTab(checkedId);
                if (checkedId == R.id.tabTopEvents) {
                    homeScreenViewModel.loadTopEvents();
                } else if (checkedId == R.id.tabAllEvents) {
                    homeScreenViewModel.loadPagedEvents(0);
                } else if (checkedId == R.id.tabTopOfferings) {
                    homeScreenViewModel.loadTopOfferings();
                } else if (checkedId == R.id.tabAllOfferings) {
                    homeScreenViewModel.loadPagedOfferings(0);
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

    private void handleNoDataFound() {
        noCardsFoundTextView.setVisibility(View.VISIBLE);
        contentRecyclerView.setVisibility(View.GONE);
        paginationBar.setVisibility(View.GONE);
    }
    private void showEventFilterBottomSheet() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(requireContext());
        View bottomSheetView = getLayoutInflater().inflate(R.layout.filter_events, null);
        bottomSheetDialog.setContentView(bottomSheetView);
        setUpEventFilterDateRangePicker(bottomSheetView);

        homeScreenViewModel.getEventTypes().observe(getViewLifecycleOwner(), eventTypes -> {
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

            homeScreenViewModel.loadPagedEvents(0, eventTypeId, location, maxParticipants, minRating, startDateString, endDateString);

            bottomSheetDialog.dismiss();
        });

        restartEventFilterButton.setOnClickListener(v->{
            homeScreenViewModel.loadPagedEvents(0,null,null,null,null,null,null);

            bottomSheetDialog.dismiss();
        });

        bottomSheetDialog.show();
    }

    private void showOfferingFilterBottomSheet(String selected) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(requireContext());
        View bottomSheetView = getLayoutInflater().inflate(R.layout.homepage_filter_offerings, null);
        bottomSheetDialog.setContentView(bottomSheetView);
        setUpCategorySpinner(bottomSheetView);
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