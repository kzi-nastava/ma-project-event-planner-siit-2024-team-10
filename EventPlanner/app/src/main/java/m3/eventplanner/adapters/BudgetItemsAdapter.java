package m3.eventplanner.adapters;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import m3.eventplanner.R;
import m3.eventplanner.models.GetBudgetItemDTO;
import m3.eventplanner.models.GetOfferingDTO;

public class BudgetItemsAdapter extends RecyclerView.Adapter<BudgetItemsAdapter.BudgetItemViewHolder> {

    private List<GetBudgetItemDTO> budgetItems;
    private Map<Integer, List<GetOfferingDTO>> offeringsMap = new HashMap<>();
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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.budget_item_card, parent, false);
        return new BudgetItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BudgetItemViewHolder holder, int position) {
        GetBudgetItemDTO budgetItem = budgetItems.get(position);
        List<GetOfferingDTO> offerings = new ArrayList<>();
        if (budgetItem.getServices() != null) {
            offerings.addAll(budgetItem.getServices());
        }
        if (budgetItem.getProducts() != null) {
            offerings.addAll(budgetItem.getProducts());
        }
        holder.bind(budgetItem, offerings);
    }

    @Override
    public int getItemCount() {
        return budgetItems != null ? budgetItems.size() : 0;
    }

    public void updateBudgetItems(List<GetBudgetItemDTO> newBudgetItems) {
        this.budgetItems = newBudgetItems;
        notifyDataSetChanged();
    }

    public void setOfferingsForBudgetItem(int budgetItemId, List<GetOfferingDTO> offerings) {
        offeringsMap.put(budgetItemId, offerings);
        notifyItemChanged(findPositionByBudgetItemId(budgetItemId));
    }

    private int findPositionByBudgetItemId(int id) {
        for (int i = 0; i < budgetItems.size(); i++) {
            if (budgetItems.get(i).getId() == id) return i;
        }
        return -1;
    }

    class BudgetItemViewHolder extends RecyclerView.ViewHolder {
        private TextView textViewCategory;
        private EditText editTextAmount;
        private RecyclerView recyclerViewOfferings;
        private ImageButton buttonDelete;
        private OfferingItemAdapter offeringsAdapter;

        public BudgetItemViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewCategory = itemView.findViewById(R.id.textViewCategory);
            editTextAmount = itemView.findViewById(R.id.editTextAmount);
            recyclerViewOfferings = itemView.findViewById(R.id.recyclerViewOfferings);
            buttonDelete = itemView.findViewById(R.id.buttonDelete);

            recyclerViewOfferings.setLayoutManager(new LinearLayoutManager(
                    itemView.getContext(), LinearLayoutManager.HORIZONTAL, false));
        }
        public void bind(GetBudgetItemDTO budgetItem, List<GetOfferingDTO> ignoredOfferings) {
            textViewCategory.setText(budgetItem.getCategory().getName());
            editTextAmount.setText(String.valueOf((int) budgetItem.getAmount()));

            // TextWatcher da detektuje promene u amount polju
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
                    } catch (NumberFormatException ignored) {
                        // silently ignore invalid input
                    }
                }
            });

            // Delete dugme
            buttonDelete.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onDeleteBudgetItem(budgetItem.getId());
                }
            });

            List<GetOfferingDTO> offerings = new ArrayList<>();
            if (budgetItem.getServices() != null) {
                offerings.addAll(budgetItem.getServices());
            }
            if (budgetItem.getProducts() != null) {
                offerings.addAll(budgetItem.getProducts());
            }

            if (!offerings.isEmpty()) {
                offeringsAdapter = new OfferingItemAdapter(offerings, true);
                recyclerViewOfferings.setAdapter(offeringsAdapter);
                recyclerViewOfferings.setVisibility(View.VISIBLE);
                itemView.findViewById(R.id.textViewNoOfferings).setVisibility(View.GONE);
            } else {
                recyclerViewOfferings.setAdapter(null);
                recyclerViewOfferings.setVisibility(View.GONE);
                itemView.findViewById(R.id.textViewNoOfferings).setVisibility(View.VISIBLE);
            }

        }
    }
}
