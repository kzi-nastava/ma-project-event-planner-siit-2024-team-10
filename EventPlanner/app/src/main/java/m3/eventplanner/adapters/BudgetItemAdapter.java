package m3.eventplanner.adapters;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import m3.eventplanner.R;
import m3.eventplanner.models.GetBudgetItemDTO;

public class BudgetItemsAdapter extends RecyclerView.Adapter<BudgetItemsAdapter.BudgetItemViewHolder> {
    private List<GetBudgetItemDTO> budgetItems;
    private OnBudgetItemActionListener listener;

    public interface OnBudgetItemActionListener {
        void onDeleteBudgetItem(int budgetItemId);
        void onAmountChanged(int budgetItemId, int newAmount);
    }

    public BudgetItemsAdapter(List<GetBudgetItemDTO> budgetItems, OnBudgetItemActionListener listener) {
        this.budgetItems = budgetItems;
        this.listener = listener;
    }

    @NonNull
    @Override
    public BudgetItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.budget_item_card, parent, false);
        return new BudgetItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BudgetItemViewHolder holder, int position) {
        GetBudgetItemDTO budgetItem = budgetItems.get(position);
        holder.bind(budgetItem);
    }

    @Override
    public int getItemCount() {
        return budgetItems != null ? budgetItems.size() : 0;
    }

    public void updateBudgetItems(List<GetBudgetItemDTO> newBudgetItems) {
        this.budgetItems = newBudgetItems;
        notifyDataSetChanged();
    }

    class BudgetItemViewHolder extends RecyclerView.ViewHolder {
        private TextView textViewCategory;
        private EditText editTextAmount;
        private RecyclerView recyclerViewOfferings;
        private Button buttonDelete;
        private OfferingListAdapter offeringsAdapter;

        public BudgetItemViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewCategory = itemView.findViewById(R.id.textViewCategory);
            editTextAmount = itemView.findViewById(R.id.editTextAmount);
            recyclerViewOfferings = itemView.findViewById(R.id.recyclerViewOfferings);
            buttonDelete = itemView.findViewById(R.id.buttonDelete);

            setupOfferingsRecyclerView();
        }

        private void setupOfferingsRecyclerView() {
            LinearLayoutManager layoutManager = new LinearLayoutManager(itemView.getContext(),
                    LinearLayoutManager.HORIZONTAL, false);
            recyclerViewOfferings.setLayoutManager(layoutManager);
        }

        public void bind(GetBudgetItemDTO budgetItem) {
            // Set category name
            textViewCategory.setText(budgetItem.getCategory().getName());

            // Set amount
            editTextAmount.setText(String.valueOf(budgetItem.getAmount()));

            // Setup amount change listener
            editTextAmount.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {}

                @Override
                public void afterTextChanged(Editable s) {
                    try {
                        int newAmount = Integer.parseInt(s.toString());
                        if (listener != null) {
                            listener.onAmountChanged(budgetItem.getId(), newAmount);
                        }
                    } catch (NumberFormatException e) {
                        // Handle invalid input
                    }
                }
            });

            // Setup offerings adapter
            if (budgetItem.getOfferings() != null) {
                offeringsAdapter = new OfferingsAdapter(budgetItem.getOfferings());
                recyclerViewOfferings.setAdapter(offeringsAdapter);
            }

            // Setup delete button
            buttonDelete.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onDeleteBudgetItem(budgetItem.getId());
                }
            });
        }
    }
}