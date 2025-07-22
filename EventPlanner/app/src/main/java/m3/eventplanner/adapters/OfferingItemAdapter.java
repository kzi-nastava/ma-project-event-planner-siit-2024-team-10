package m3.eventplanner.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import m3.eventplanner.R;
import m3.eventplanner.models.GetOfferingDTO;

public class OfferingItemAdapter extends RecyclerView.Adapter<OfferingItemAdapter.OfferingViewHolder> {
    private List<GetOfferingDTO> offerings;
    private boolean isUsedInBudget = false;
    private OnOfferingClickListener offeringClickListener;


    public List<GetOfferingDTO> getOfferings() {
        return offerings;
    }

    // Constructor without budget flag (default use)
    public OfferingItemAdapter(List<GetOfferingDTO> offerings) {
        this.offerings = offerings;
    }

    public OfferingItemAdapter(List<GetOfferingDTO> offerings, boolean isUsedInBudget, OnOfferingClickListener offeringClickListener) {
        this.offerings = offerings;
        this.isUsedInBudget = isUsedInBudget;
        this.offeringClickListener = offeringClickListener;
    }

    // Constructor with budget flag
    public OfferingItemAdapter(List<GetOfferingDTO> offerings, boolean isUsedInBudget) {
        this.offerings = offerings;
        this.isUsedInBudget = isUsedInBudget;
    }

    public interface OnOfferingClickListener {
        void onOfferingClick(GetOfferingDTO offering);
    }

    @NonNull
    @Override
    public OfferingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_offering_small, parent, false);
        return new OfferingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OfferingViewHolder holder, int position) {
        GetOfferingDTO offering = offerings.get(position);
        holder.bind(offering, isUsedInBudget);
        holder.itemView.setOnClickListener(v -> {
            if (offeringClickListener != null) {
                offeringClickListener.onOfferingClick(offering);
            }
        });
    }

    @Override
    public int getItemCount() {
        return offerings != null ? offerings.size() : 0;
    }

    public void updateOfferings(List<GetOfferingDTO> newOfferings) {
        this.offerings = newOfferings;
        notifyDataSetChanged();
    }

    static class OfferingViewHolder extends RecyclerView.ViewHolder {
        private final TextView offeringName;
        private final LinearLayout offeringItem;
        private final TextView offeringPrice;

        public OfferingViewHolder(@NonNull View itemView) {
            super(itemView);
            offeringName = itemView.findViewById(R.id.offering_name);
            offeringItem = itemView.findViewById(R.id.offering_item);
            offeringPrice = itemView.findViewById(R.id.offering_price);
        }

        public void bind(GetOfferingDTO offering, boolean isUsedInBudget) {
            offeringName.setText(offering.getName());
            offeringPrice.setText(String.format("%.0f RSD", offering.getPrice()));

            // Set background color based on usage context
            if (isUsedInBudget) {
                offeringItem.setBackgroundColor(Color.parseColor("#fee9ce")); // light yellow
                offeringPrice.setTextColor(Color.parseColor("#c17664"));
                offeringPrice.setTextColor(Color.parseColor("#78c3a0"));
            }
        }
    }
}
