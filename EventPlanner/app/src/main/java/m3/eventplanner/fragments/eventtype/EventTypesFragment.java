package m3.eventplanner.fragments.eventtype;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.List;

import m3.eventplanner.R;
import m3.eventplanner.adapters.EventTypeListAdapter;
import m3.eventplanner.clients.ClientUtils;
import m3.eventplanner.databinding.FragmentEventTypesBinding;
import m3.eventplanner.models.GetEventTypeDTO;


public class EventTypesFragment extends Fragment implements EventTypeFormFragment.OnEventTypeFormFilledListener {
    private EventTypesViewModel viewModel;
    private ClientUtils clientUtils;
    private FragmentEventTypesBinding binding;

    public EventTypesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentEventTypesBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(EventTypesViewModel.class);
        clientUtils = new ClientUtils(requireContext());
        viewModel.initialize(clientUtils);

        setupObservers();

        // Load initial data
        viewModel.loadEventTypes();
    }

    private void setupObservers() {
        viewModel.getEventTypes().observe(getViewLifecycleOwner(), this::populateEventTypes);

        viewModel.getEventTypes().observe(getViewLifecycleOwner(), eventTypes -> {
            binding.eventTypesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            binding.eventTypesRecyclerView.setAdapter(new EventTypeListAdapter(eventTypes,this));
        });

        viewModel.getError().observe(getViewLifecycleOwner(), errorMessage -> {
            if (errorMessage != null && !errorMessage.isEmpty()) {
                Toast.makeText(getContext(), errorMessage, Toast.LENGTH_LONG).show();
            }
        });

        viewModel.getSuccessMessage().observe(getViewLifecycleOwner(), successMessage -> {
            if (successMessage != null && !successMessage.isEmpty()) {
                Toast.makeText(getContext(), successMessage, Toast.LENGTH_SHORT).show();
            }
        });

        binding.createEventTypeButton.setOnClickListener(v -> {
            EventTypeFormFragment dialog = new EventTypeFormFragment();
            dialog.show(getChildFragmentManager(), "EventTypeFormFragment");
        });
    }

    private void populateEventTypes(List<GetEventTypeDTO> eventTypes) {

    }

    @Override
    public void onEventTypeFormFilled(String name, String description, List<Integer> recommendedCategoryIds) {
        String a ="aaaa";
    }

}