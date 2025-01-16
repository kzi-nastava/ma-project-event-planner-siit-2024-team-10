package m3.eventplanner.fragments.category;

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

import m3.eventplanner.adapters.CategoryListAdapter;
import m3.eventplanner.clients.ClientUtils;
import m3.eventplanner.databinding.FragmentCategoryBinding;

public class CategoryFragment extends Fragment implements CategoryFormFragment.OnCategoryFormFilledListener {
    private CategoryViewModel viewModel;
    private ClientUtils clientUtils;
    private FragmentCategoryBinding binding;
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
            binding.categoryRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            binding.categoryRecyclerView.setAdapter(new CategoryListAdapter(categories,this));
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
        if(edit)
            viewModel.editCategory(id,name,description);
        else
            viewModel.createCategory(name,description);
    }

}