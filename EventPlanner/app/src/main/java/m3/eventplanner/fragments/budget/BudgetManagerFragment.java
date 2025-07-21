package m3.eventplanner.fragments.budget;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import m3.eventplanner.R;
import m3.eventplanner.adapters.BudgetItemsAdapter;
import m3.eventplanner.auth.TokenManager;
import m3.eventplanner.clients.ClientUtils;
import m3.eventplanner.databinding.FragmentBudgetManagerBinding;
import m3.eventplanner.models.GetBudgetItemDTO;
import m3.eventplanner.models.GetEventDTO;
import m3.eventplanner.models.GetOfferingCategoryDTO;

public class BudgetManagerFragment extends Fragment implements BudgetItemsAdapter.OnBudgetItemActionListener {
    private BudgetManagerViewModel viewModel;
    private ClientUtils clientUtils;
    private FragmentBudgetManagerBinding binding;
    private BudgetItemsAdapter budgetItemsAdapter;
    private RecyclerView recyclerViewBudgetItems;

    private List<GetEventDTO> eventList = new ArrayList<>();
    private List<GetOfferingCategoryDTO> categoryList = new ArrayList<>();
    private int currentEventId = -1;

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

        setupViews();
        setupObservers();

        viewModel.loadOrganizersEvents(accountId);
        viewModel.loadCategories();
    }

    private void setupViews() {
        TextView textViewRecommendedTitle = binding.textViewRecommendedTitle;
        LinearLayout layoutRecommendedCategories = binding.layoutRecommendedCategories;
        TextView textViewTotalBudget = binding.textViewTotalBudget;

        Spinner spinnerEvents = binding.spinnerEvents;
        Button buttonAdd = binding.buttonAddBudgetItem;
        recyclerViewBudgetItems = binding.recyclerViewBudgetItems;

        // Setup RecyclerView
        recyclerViewBudgetItems.setLayoutManager(new LinearLayoutManager(requireContext()));
        budgetItemsAdapter = new BudgetItemsAdapter(new ArrayList<>(), this);
        recyclerViewBudgetItems.setAdapter(budgetItemsAdapter);

        buttonAdd.setOnClickListener(v -> showAddBudgetItemDialog());

        // Listen to spinner selection to load budget items for selected event and update recommended categories
        spinnerEvents.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position >= 0 && eventList != null && position < eventList.size()) {
                    currentEventId = eventList.get(position).getId();
                    viewModel.loadBudgetItemsForEvent(currentEventId);

                    // Prikaz preporučenih kategorija
                    GetEventDTO selectedEvent = eventList.get(position);
                    List<GetOfferingCategoryDTO> recommended = selectedEvent.getEventType() != null
                            ? selectedEvent.getEventType().getRecommendedCategories()
                            : null;

                    layoutRecommendedCategories.removeAllViews();

                    if (recommended != null && !recommended.isEmpty()) {
                        textViewRecommendedTitle.setVisibility(View.VISIBLE);
                        layoutRecommendedCategories.setVisibility(View.VISIBLE);

                        for (GetOfferingCategoryDTO cat : recommended) {
                            TextView chip = new TextView(requireContext());
                            chip.setText(cat.getName());
                            chip.setBackgroundResource(R.drawable.chip_background); // Ako nemaš, napravi ili ukloni
                            chip.setPadding(16, 8, 16, 8);
                            chip.setTextColor(getResources().getColor(android.R.color.white));
                            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.WRAP_CONTENT,
                                    LinearLayout.LayoutParams.WRAP_CONTENT
                            );
                            params.setMargins(8, 4, 8, 4);
                            chip.setLayoutParams(params);
                            layoutRecommendedCategories.addView(chip);
                        }
                    } else {
                        textViewRecommendedTitle.setVisibility(View.GONE);
                        layoutRecommendedCategories.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                currentEventId = -1;
                budgetItemsAdapter.updateBudgetItems(new ArrayList<>());
                Toast.makeText(requireContext(), "No budget items", Toast.LENGTH_SHORT).show();

                // Sakrij preporučene kategorije
                textViewRecommendedTitle.setVisibility(View.GONE);
                layoutRecommendedCategories.setVisibility(View.GONE);
            }
        });
    }


    private void setupObservers() {
        // Observe events and update spinner
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
            binding.spinnerEvents.setAdapter(adapter);

            // Optionally select first event on load
            if (!events.isEmpty()) {
                currentEventId = events.get(0).getId();
                viewModel.loadBudgetItemsForEvent(currentEventId);
            }
        });

        // Observe categories
        viewModel.getCategories().observe(getViewLifecycleOwner(), categories -> {
            categoryList = categories;
        });

        // Observe budget items and show "No budget items" toast if empty
        viewModel.getBudgetItems().observe(getViewLifecycleOwner(), budgetItems -> {
            int totalBudget = 0;
            for (GetBudgetItemDTO item : budgetItems) {
                totalBudget += item.getAmount();
            }
            binding.textViewTotalBudget.setText("Total budget: " + totalBudget);

            if (budgetItems == null || budgetItems.isEmpty()) {
                budgetItemsAdapter.updateBudgetItems(new ArrayList<>());
                Toast.makeText(requireContext(), "No budget items", Toast.LENGTH_SHORT).show();
            } else {
                budgetItemsAdapter.updateBudgetItems(budgetItems);
            }
        });

        // Observe error messages
        viewModel.getError().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show();
                viewModel.clearMessages();
            }
        });

        // Observe success messages
        viewModel.getSuccessMessage().observe(getViewLifecycleOwner(), message -> {
            if (message != null) {
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
                viewModel.clearMessages();
            }
        });
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

                        currentEventId = selectedEvent.getId();
                        viewModel.addBudgetItem(selectedEvent.getId(), selectedCategory.getId(), amount);
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    public void onDeleteBudgetItem(int budgetItemId) {
        viewModel.deleteBudgetItem(budgetItemId, currentEventId);
    }

    @Override
    public void onAmountChanged(int budgetItemId, int newAmount) {
        viewModel.updateBudgetItemAmount(currentEventId, budgetItemId, newAmount);
    }
}
