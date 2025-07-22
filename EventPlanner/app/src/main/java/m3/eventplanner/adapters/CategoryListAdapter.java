package m3.eventplanner.adapters;

import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import m3.eventplanner.R;
import m3.eventplanner.fragments.category.CategoryFormFragment;
import m3.eventplanner.models.GetOfferingCategoryDTO;
import m3.eventplanner.models.GetOfferingDTO;

public class CategoryListAdapter extends RecyclerView.Adapter<CategoryListAdapter.CategoryViewHolder> {
    private List<GetOfferingCategoryDTO> categories;
    private CategoryFormFragment.OnCategoryFormFilledListener listener;
    private OnCategoryActionListener actionListener;

    public interface OnCategoryActionListener {
        void onApproveCategory(GetOfferingCategoryDTO category);
        void onEditCategory(GetOfferingCategoryDTO category);
        void onDeleteCategory(GetOfferingCategoryDTO category);
        void onChangeCategoryOfferings(GetOfferingCategoryDTO selectedCategory, GetOfferingDTO offering);
        void loadOfferingsForCategory(int categoryId, OfferingItemAdapter adapter, TextView noOfferingsText);
    }

    public CategoryListAdapter(List<GetOfferingCategoryDTO> categories,
                               CategoryFormFragment.OnCategoryFormFilledListener listener,
                               OnCategoryActionListener actionListener) {
        this.categories = categories;
        this.listener = listener;
        this.actionListener = actionListener;
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
        GetOfferingCategoryDTO category = categories.get(position);
        holder.bind(category);
    }

    @Override
    public int getItemCount() {
        return categories != null ? categories.size() : 0;
    }

    public void updateCategories(List<GetOfferingCategoryDTO> newCategories) {
        this.categories = newCategories;
        notifyDataSetChanged();
    }

    class CategoryViewHolder extends RecyclerView.ViewHolder {
        private TextView categoryName;
        private TextView categoryDescription;
        private ImageButton approveButton;
        private ImageButton editButton;
        private ImageButton deleteButton;
        private ImageButton changeOfferingsButton;
        private RecyclerView offeringsRecyclerView;
        private TextView noOfferingsText;
        private OfferingItemAdapter offeringAdapter;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            categoryName = itemView.findViewById(R.id.categoryName);
            categoryDescription = itemView.findViewById(R.id.categoryDescription);
            approveButton = itemView.findViewById(R.id.approve_category_button);
            editButton = itemView.findViewById(R.id.edit_category_button);
            deleteButton = itemView.findViewById(R.id.delete_category_button);
            changeOfferingsButton = itemView.findViewById(R.id.change_category_offerings_button);
            offeringsRecyclerView = itemView.findViewById(R.id.offeringsRecyclerView);
            noOfferingsText = itemView.findViewById(R.id.noOfferingsText);

            setupRecyclerView();
        }

        private void setupRecyclerView() {
            LinearLayoutManager layoutManager = new LinearLayoutManager(itemView.getContext());
            offeringsRecyclerView.setLayoutManager(layoutManager);
            offeringAdapter = new OfferingItemAdapter(null);
            offeringsRecyclerView.setAdapter(offeringAdapter);
        }

        public void bind(GetOfferingCategoryDTO category) {
            categoryName.setText(category.getName());
            categoryDescription.setText(category.getDescription());

            // Load offerings for this category
            if (actionListener != null) {
                actionListener.loadOfferingsForCategory(category.getId(), offeringAdapter, noOfferingsText);
            }

            if (!category.isPending()) {
                approveButton.setVisibility(View.GONE);
                changeOfferingsButton.setEnabled(false);
                changeOfferingsButton.setAlpha(0.4f);
            } else {
                approveButton.setVisibility(View.VISIBLE);
                changeOfferingsButton.setEnabled(true);
                changeOfferingsButton.setAlpha(1f);
            }

            // Set up button click listeners
            approveButton.setOnClickListener(v -> {
                if (actionListener != null) {
                    actionListener.onApproveCategory(category);
                }
            });

            editButton.setOnClickListener(v -> {
                if (actionListener != null) {
                    actionListener.onEditCategory(category);
                }
            });

            deleteButton.setOnClickListener(v -> {
                if (actionListener != null) {
                    actionListener.onDeleteCategory(category);
                }
            });

            changeOfferingsButton.setOnClickListener(v -> {
                if (actionListener != null && changeOfferingsButton.isEnabled()) {
                    // Get the current offerings for this category from the adapter
                    List<GetOfferingDTO> currentOfferings = offeringAdapter.getOfferings();

                    if (currentOfferings == null || currentOfferings.isEmpty()) {
                        Toast.makeText(itemView.getContext(), "No offerings in this category to move", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (currentOfferings.size() == 1) {
                        // If there's only one offering, move it directly
                        actionListener.onChangeCategoryOfferings(category, currentOfferings.get(0));
                    } else {
                        // If there are multiple offerings, show selection dialog
                        String[] offeringNames = currentOfferings.stream()
                                .map(GetOfferingDTO::getName)
                                .toArray(String[]::new);

                        final int[] selectedIndex = {0};

                        new AlertDialog.Builder(itemView.getContext())
                                .setTitle("Select offering to move")
                                .setSingleChoiceItems(offeringNames, 0, (dialog, which) -> selectedIndex[0] = which)
                                .setPositiveButton("Move", (dialog, which) -> {
                                    GetOfferingDTO selectedOffering = currentOfferings.get(selectedIndex[0]);
                                    actionListener.onChangeCategoryOfferings(category, selectedOffering);
                                })
                                .setNegativeButton("Cancel", null)
                                .show();
                    }
                }
            });
        }
    }
}