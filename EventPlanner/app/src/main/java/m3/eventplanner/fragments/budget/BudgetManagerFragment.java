package m3.eventplanner.fragments.budget;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

import m3.eventplanner.R;
import m3.eventplanner.auth.TokenManager;
import m3.eventplanner.clients.ClientUtils;
import m3.eventplanner.databinding.FragmentBudgetManagerBinding;
import m3.eventplanner.models.GetEventDTO;
import m3.eventplanner.models.GetOfferingCategoryDTO;

public class BudgetManagerFragment extends Fragment {
    private BudgetManagerViewModel viewModel;
    private ClientUtils clientUtils;
    private FragmentBudgetManagerBinding binding;

    private List<GetEventDTO> eventList = new ArrayList<>();
    private List<GetOfferingCategoryDTO> categoryList = new ArrayList<>();

    public BudgetManagerFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentBudgetManagerBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(BudgetManagerViewModel.class);
        clientUtils = new ClientUtils(requireContext());
        viewModel.initialize(clientUtils);

        TokenManager tokenManager = new TokenManager(requireContext());
        int accountId = tokenManager.getAccountId();

        Spinner spinnerEvents = binding.spinnerEvents;
        Button buttonAdd = binding.buttonAddBudgetItem;

        // Posmatraj evente i updateuj spinner
        viewModel.getEvents().observe(getViewLifecycleOwner(), events -> {
            eventList = events;

            List<String> eventNames = new ArrayList<>();
            for (GetEventDTO event : events) {
                eventNames.add(event.getName());
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                    requireContext(),
                    android.R.layout.simple_spinner_item,
                    eventNames
            );
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerEvents.setAdapter(adapter);
        });

        viewModel.getCategories().observe(getViewLifecycleOwner(), categories -> {
            categoryList = categories;
        });

        buttonAdd.setOnClickListener(v -> showAddBudgetItemDialog());

        viewModel.loadOrganizersEvents(accountId);

        viewModel.loadCategories();
    }

    private void showAddBudgetItemDialog() {
        LayoutInflater inflater = LayoutInflater.from(requireContext());
        View dialogView = inflater.inflate(R.layout.dialog_add_budget_item, null);

        Spinner spinnerCategories = dialogView.findViewById(R.id.spinnerCategories);
        EditText editTextAmount = dialogView.findViewById(R.id.editTextAmount);

        List<String> categoryNames = new ArrayList<>();
        for (GetOfferingCategoryDTO category : categoryList) {
            categoryNames.add(category.getName());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                categoryNames
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategories.setAdapter(adapter);

        new AlertDialog.Builder(requireContext())
                .setTitle("Add Budget Item")
                .setView(dialogView)
                .setPositiveButton("Add", (dialog, which) -> {
                    String amountStr = editTextAmount.getText().toString();
                    int selectedEventIndex = binding.spinnerEvents.getSelectedItemPosition();
                    int selectedCategoryIndex = spinnerCategories.getSelectedItemPosition();

                    if (!amountStr.isEmpty() && selectedEventIndex >= 0 && selectedCategoryIndex >= 0) {
                        int amount = Integer.parseInt(amountStr);
                        GetEventDTO selectedEvent = eventList.get(selectedEventIndex);
                        GetOfferingCategoryDTO selectedCategory = categoryList.get(selectedCategoryIndex);

                        viewModel.addBudgetItem(selectedEvent.getId(), selectedCategory.getId(), amount);
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}
