package m3.eventplanner.fragments.category;

import android.app.AlertDialog;
import android.os.Bundle;
import android.widget.TextView;

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
import java.util.stream.Collectors;

import m3.eventplanner.adapters.CategoryListAdapter;
import m3.eventplanner.adapters.OfferingItemAdapter;
import m3.eventplanner.clients.ClientUtils;
import m3.eventplanner.databinding.FragmentCategoryBinding;
import m3.eventplanner.models.GetOfferingCategoryDTO;
import m3.eventplanner.models.GetOfferingDTO;

public class CategoryFragment extends Fragment
        implements CategoryFormFragment.OnCategoryFormFilledListener,
        CategoryListAdapter.OnCategoryActionListener {

    private CategoryViewModel viewModel;
    private ClientUtils clientUtils;
    private FragmentCategoryBinding binding;
    private CategoryListAdapter adapter;

    public CategoryFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentCategoryBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(CategoryViewModel.class);
        clientUtils = new ClientUtils(requireContext());
        viewModel.initialize(clientUtils);

        setupObservers();
        viewModel.loadCategories();

    }

    private void setupObservers() {
        viewModel.getCategories().observe(getViewLifecycleOwner(), categories -> {
            if (adapter == null) {
                binding.categoryRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                adapter = new CategoryListAdapter(categories, this, this);
                binding.categoryRecyclerView.setAdapter(adapter);
            } else {
                adapter.updateCategories(categories);
            }
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

        binding.createCategoryButton.setOnClickListener(v -> {
            CategoryFormFragment dialog = new CategoryFormFragment();
            dialog.show(getChildFragmentManager(), "CategoryFormFragment");
        });
    }

    @Override
    public void onCategoryFormFilled(int id, String name, String description, boolean edit) {
        if (edit) {
            viewModel.editCategory(id, name, description);
        } else {
            viewModel.createCategory(name, description);
        }
    }

    @Override
    public void onApproveCategory(GetOfferingCategoryDTO category) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Approve Category")
                .setMessage("Are you sure you want to approve the category '" + category.getName() + "'?")
                .setPositiveButton("Approve", (dialog, which) -> {
                    viewModel.approveCategory(category.getId());
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    public void onEditCategory(GetOfferingCategoryDTO category) {
        CategoryFormFragment dialog = new CategoryFormFragment();
        Bundle args = new Bundle();
        args.putParcelable("selectedCategory", category);
        dialog.setArguments(args);
        dialog.show(getChildFragmentManager(), "CategoryFormFragment");
    }

    @Override
    public void onDeleteCategory(GetOfferingCategoryDTO category) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Delete Category")
                .setMessage("Are you sure you want to delete the category '" + category.getName() + "'? This action cannot be undone.")
                .setPositiveButton("Delete", (dialog, which) -> {
                    viewModel.deleteCategory(category.getId());
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
    @Override
    public void onChangeCategoryOfferings(GetOfferingCategoryDTO selectedCategory, GetOfferingDTO offering) {
        List<GetOfferingCategoryDTO> allCategories = viewModel.getCategories().getValue();
        if (allCategories == null) return;

        List<GetOfferingCategoryDTO> otherCategories = allCategories.stream()
                .filter(cat -> cat.getId() != selectedCategory.getId())
                .collect(Collectors.toList());

        if (otherCategories.isEmpty()) {
            Toast.makeText(getContext(), "No other categories available", Toast.LENGTH_SHORT).show();
            return;
        }

        String[] categoryNames = otherCategories.stream()
                .map(GetOfferingCategoryDTO::getName)
                .toArray(String[]::new);

        final int[] selectedIndex = {0};

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Move offering '" + offering.getName() + "' to category");

        builder.setSingleChoiceItems(categoryNames, 0, (dialog, which) -> {
            selectedIndex[0] = which;
        });

        builder.setPositiveButton("Move", (dialog, which) -> {
            GetOfferingCategoryDTO targetCategory = otherCategories.get(selectedIndex[0]);

            viewModel.changeCategory(offering.getId(), targetCategory.getId());
        });

        builder.setNegativeButton("Cancel", null);

        builder.show();
    }

    @Override
    public void loadOfferingsForCategory(int categoryId, OfferingItemAdapter adapter, TextView noOfferingsText) {
        viewModel.loadOfferingsForCategory(categoryId, adapter, noOfferingsText);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}