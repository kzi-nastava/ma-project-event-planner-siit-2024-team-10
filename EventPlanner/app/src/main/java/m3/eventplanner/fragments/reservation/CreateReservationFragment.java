package m3.eventplanner.fragments.reservation;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import m3.eventplanner.R;
import m3.eventplanner.auth.TokenManager;
import m3.eventplanner.clients.ClientUtils;
import m3.eventplanner.databinding.FragmentCreateReservationBinding;
import m3.eventplanner.fragments.event.CreateEventFragment;
import m3.eventplanner.models.CreateReservationDTO;
import m3.eventplanner.models.GetEventDTO;
import m3.eventplanner.models.GetEventTypeDTO;
import m3.eventplanner.models.GetServiceDTO;

public class CreateReservationFragment extends Fragment {
    private FragmentCreateReservationBinding binding;
    private ClientUtils clientUtils;
    private CreateReservationViewModel viewModel;
    private boolean isFixedDuration = false;
    private int minDuration = 0;
    private int maxDuration = 0;
    private GetServiceDTO service;
    private int organizerId, eventId;
    private ArrayAdapter<GetEventDTO> eventAdapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentCreateReservationBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(CreateReservationViewModel.class);
        clientUtils = new ClientUtils(requireContext());
        viewModel.initialize(clientUtils);

        setupObservers();

        Bundle args = getArguments();
        if (args != null) {
            int serviceId = args.getInt("selectedServiceId");
            TokenManager tokenManager = new TokenManager(requireContext());
            organizerId = tokenManager.getAccountId();
            viewModel.getServiceById(serviceId);
        }

        binding.selectStartTimeButton.setOnClickListener(v -> showTimePicker(true));
        binding.selectEndTimeButton.setOnClickListener(v -> showTimePicker(false));

        binding.submit.setOnClickListener(v->{
            String startTime = binding.selectedStartTime.getText().toString();
            String endTime = binding.selectedEndTime.getText().toString();
            CreateReservationDTO reservationDTO = new CreateReservationDTO(startTime, endTime, eventId, service.getId());
            viewModel.createReservation(reservationDTO);
        });
        binding.cancel.setOnClickListener(v->{
            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());

            builder.setTitle("Close the form")
                    .setMessage("Are you sure you want to close the reservation form? You'll lose all your progress.")
                    .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            NavController navController = Navigation.findNavController(requireView());
                            navController.navigateUp();
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            builder.create().show();
        });
    }
    private void setupObservers() {
        viewModel.getService().observe(getViewLifecycleOwner(), serviceDTO -> {
            if (serviceDTO != null) {
                service = serviceDTO;
                minDuration = service.getMinDuration();
                maxDuration = service.getMaxDuration();
                isFixedDuration = minDuration == maxDuration;

                setupTimeSelection();
                setupServiceData();
                setupEventSpinner();
            }
        });
        viewModel.getError().observe(getViewLifecycleOwner(), error -> {
            binding.error.setText(error);
            binding.error.setVisibility(View.VISIBLE);
        });
        viewModel.getNotification().observe(getViewLifecycleOwner(), notification -> {
            Toast.makeText(getContext(), notification, Toast.LENGTH_SHORT).show();
        });
        viewModel.getSuccessMessage().observe(getViewLifecycleOwner(), message ->{
            binding.error.setVisibility(View.GONE);
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
            try {
                NavController navController = NavHostFragment.findNavController(CreateReservationFragment.this);
                navController.navigate(R.id.homeScreenFragment);
            } catch (Exception e) {
                Log.e("NavigationError", "Failed to navigate", e);
            }
                }
        );

    }
        private void setupEventSpinner() {
        eventAdapter = new ArrayAdapter<GetEventDTO>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                new ArrayList<>()
        ) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                GetEventDTO event = getItem(position);
                if (event != null) {
                    android.widget.TextView text = (android.widget.TextView) view;
                    text.setText(event.getName());
                }
                return view;
            }

            @Override
            public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                GetEventDTO event = getItem(position);
                if (event != null) {
                    android.widget.TextView text = (android.widget.TextView) view;
                    text.setText(event.getName());
                }
                return view;
            }
        };

        eventAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.eventSpinner.setAdapter(eventAdapter);

        binding.eventSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                GetEventDTO selectedEvent = (GetEventDTO) parent.getItemAtPosition(position);
                binding.eventDate.setVisibility(View.VISIBLE);
                binding.eventDate.setText("Event Date: " + selectedEvent.getDate());
                eventId = selectedEvent.getId();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                binding.eventDate.setVisibility(View.GONE);
            }
        });

        viewModel.loadEvents(organizerId);
        viewModel.getEvents().observe(getViewLifecycleOwner(), events -> {
            eventAdapter.clear();
            if (events != null) {
                eventAdapter.addAll(events);
            }
            eventAdapter.notifyDataSetChanged();
        });
    }

    private void setupTimeSelection() {
        if (isFixedDuration) {
            binding.durationInfo.setText("This service's fixed duration is " + minDuration + " hours");
        } else {
            binding.durationInfo.setText("This service's duration is between " + minDuration +
                    " and " + maxDuration + " hours");
        }
    }
    private void setupServiceData(){
        binding.reservationPeriod.setText("Reservation period: "+service.getReservationPeriod()+" hours before the event");
        binding.serviceName.setText("Booking the service "+service.getName());
    }

    private void showTimePicker(boolean isStartTime) {
        if(isFixedDuration && !isStartTime){
            return;
        }
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
