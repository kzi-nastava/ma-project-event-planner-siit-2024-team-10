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
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Add Budget Item");

        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_add_budget_item, null);
        EditText editTextAmount = dialogView.findViewById(R.id.editTextAmount);
        Spinner categorySpinner = dialogView.findViewById(R.id.spinnerCategories);

        List<GetBudgetItemDTO> currentItems = viewModel.getBudgetItems().getValue();
        List<Integer> usedCategoryIds = new ArrayList<>();
        if (currentItems != null) {
            for (GetBudgetItemDTO item : currentItems) {
                if (item.getCategory() != null) {
                    usedCategoryIds.add(item.getCategory().getId());
                }
            }
        }

        List<GetOfferingCategoryDTO> availableCategories = new ArrayList<>();
        for (GetOfferingCategoryDTO category : categoryList) {
            if (!usedCategoryIds.contains(category.getId())) {
                availableCategories.add(category);
            }
        }

        List<String> categoryNames = new ArrayList<>();
        for (GetOfferingCategoryDTO category : availableCategories) {
            categoryNames.add(category.getName());
        }

        if (availableCategories.isEmpty()) {
            Toast.makeText(requireContext(), "All categories are already added.", Toast.LENGTH_SHORT).show();
            return;
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, categoryNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(adapter);

        builder.setView(dialogView);

        builder.setPositiveButton("Add", (dialog, which) -> {
            String amountStr = editTextAmount.getText().toString().trim();
            if (!amountStr.isEmpty()) {
                int amount = Integer.parseInt(amountStr);
                int selectedCategoryIndex = categorySpinner.getSelectedItemPosition();
                GetOfferingCategoryDTO selectedCategory = availableCategories.get(selectedCategoryIndex);
                viewModel.addBudgetItem(currentEventId, selectedCategory.getId(), amount);
            } else {
                Toast.makeText(requireContext(), "Please enter an amount", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        builder.show();
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
