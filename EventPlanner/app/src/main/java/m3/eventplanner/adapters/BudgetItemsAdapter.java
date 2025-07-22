package m3.eventplanner.adapters;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import m3.eventplanner.R;
import m3.eventplanner.models.GetBudgetItemDTO;
import m3.eventplanner.models.GetOfferingDTO;
import m3.eventplanner.models.GetProductDTO;
import m3.eventplanner.models.GetServiceDTO;

public class BudgetItemsAdapter extends RecyclerView.Adapter<BudgetItemsAdapter.BudgetItemViewHolder> {

    private List<GetBudgetItemDTO> budgetItems;
    private final Map<Integer, List<GetOfferingDTO>> offeringsMap = new HashMap<>();
    private final OnBudgetItemActionListener listener;

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
        GetBudgetItemDTO budgetItem = budgetItems.get(position);List<GetOfferingDTO> offerings = new ArrayList<>();
        if (budgetItem.getServices() != null) {
            for (GetServiceDTO service : budgetItem.getServices()) {
                offerings.add(service);
            }
        }
        if (budgetItem.getProducts() != null) {
            for (GetProductDTO product : budgetItem.getProducts()) {
                offerings.add(product);
            }
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
        private final TextView textViewCategory;
        private final EditText editTextAmount;
        private final RecyclerView recyclerViewOfferings;
        private final ImageButton buttonDelete;
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

        public void bind(GetBudgetItemDTO budgetItem, List<GetOfferingDTO> offerings) {
            textViewCategory.setText(budgetItem.getCategory().getName());
            int originalAmount = (int) budgetItem.getAmount();
            editTextAmount.setText(String.valueOf(originalAmount));

            // Remove old listeners to prevent multiple triggers
            editTextAmount.setOnFocusChangeListener(null);
            editTextAmount.setOnEditorActionListener(null);

            // Focus lost - apply amount change
            editTextAmount.setOnFocusChangeListener((v, hasFocus) -> {
                if (!hasFocus) {
                    applyAmountChange(editTextAmount, budgetItem, originalAmount);
                }
            });

            // Enter key - apply amount change
            editTextAmount.setOnEditorActionListener((v, actionId, event) -> {
                if (actionId == EditorInfo.IME_ACTION_DONE ||
                        (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER &&
                                event.getAction() == KeyEvent.ACTION_DOWN)) {
                    editTextAmount.clearFocus(); // Triggers focus listener
                    return true;
                }
                return false;
            });

            buttonDelete.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onDeleteBudgetItem(budgetItem.getId());
                }
            });

            if (!offerings.isEmpty()) {
                offeringsAdapter = new OfferingItemAdapter(
                        offerings,
                        true,
                        offering -> {
                            Bundle bundle = new Bundle();
                            bundle.putParcelable("offering", offering);
                            NavController navController = Navigation.findNavController(itemView);
                            navController.navigate(R.id.action_budgetManagerFragment_to_oldOfferingDetailsFragment, bundle);
                        }
                );
                recyclerViewOfferings.setAdapter(offeringsAdapter);
                recyclerViewOfferings.setVisibility(View.VISIBLE);
                itemView.findViewById(R.id.textViewNoOfferings).setVisibility(View.GONE);
            } else {
                recyclerViewOfferings.setAdapter(null);
                recyclerViewOfferings.setVisibility(View.GONE);
                itemView.findViewById(R.id.textViewNoOfferings).setVisibility(View.VISIBLE);
            }
        }

        private void applyAmountChange(EditText editText, GetBudgetItemDTO budgetItem, int originalAmount) {
            String input = editText.getText().toString().trim();
            if (!input.isEmpty()) {
                try {
                    int newAmount = Integer.parseInt(input);
                    if (newAmount != originalAmount && listener != null) {
                        listener.onAmountChanged(budgetItem.getId(), newAmount);
                    }
                } catch (NumberFormatException e) {
                    Toast.makeText(editText.getContext(), "Invalid amount", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
