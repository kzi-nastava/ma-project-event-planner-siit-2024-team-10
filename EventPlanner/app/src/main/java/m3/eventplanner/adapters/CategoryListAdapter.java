package m3.eventplanner.adapters;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import m3.eventplanner.R;
import m3.eventplanner.fragments.category.CategoryFormFragment;
import m3.eventplanner.fragments.category.CategoryFragment;
import m3.eventplanner.fragments.category.CategoryViewModel;
import m3.eventplanner.fragments.eventtype.EventTypeFormFragment;
import m3.eventplanner.models.GetEventTypeDTO;
import m3.eventplanner.models.GetOfferingCategoryDTO;

public class CategoryListAdapter extends RecyclerView.Adapter<CategoryListAdapter.CategoryViewHolder>{
    private List<GetOfferingCategoryDTO> categoryList;
    private CategoryViewModel categoryViewModel;
    private CategoryFragment categoryFragment;

    public CategoryListAdapter(List<GetOfferingCategoryDTO> categoryList, CategoryFragment fragment) {
        this.categoryFragment=fragment;
        categoryList.sort(Comparator.comparing(GetOfferingCategoryDTO::getName));
        this.categoryList = categoryList;
        this.categoryViewModel = new ViewModelProvider(fragment).get(CategoryViewModel.class);
    }

    public static class CategoryViewHolder extends RecyclerView.ViewHolder {
        public MaterialCardView eventTypeCard;
        public TextView nameTextView;
        public TextView descriptionTextView;
        public ImageButton editButton, approveButton, deleteButton;

        public CategoryViewHolder(View view) {
            super(view);
            eventTypeCard = view.findViewById(R.id.categoryCard);
            nameTextView = view.findViewById(R.id.categoryName);
            descriptionTextView = view.findViewById(R.id.categoryDescription);
            editButton = view.findViewById(R.id.edit_category_button);
            approveButton = view.findViewById(R.id.approve_category_button);
            deleteButton = view.findViewById(R.id.delete_category_button);
        }
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.category_card, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        GetOfferingCategoryDTO category = categoryList.get(position);

        holder.nameTextView.setText(category.getName());
        holder.descriptionTextView.setText(category.getDescription());

        if(!category.isPending())
            holder.approveButton.setVisibility(View.GONE);

        holder.approveButton.setOnClickListener(v -> {
            categoryViewModel.approveCategory(category.getId());
        });

        holder.deleteButton.setOnClickListener(v -> {
            categoryViewModel.deleteCategory(category.getId());
        });

        holder.editButton.setOnClickListener(v ->{
            Bundle bundle = new Bundle();
            bundle.putParcelable("selectedCategory", category);
            CategoryFormFragment dialog = new CategoryFormFragment();
            dialog.setArguments(bundle);
            dialog.show(categoryFragment.getChildFragmentManager(), "CategoryFormFragment");
        });
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }
}
