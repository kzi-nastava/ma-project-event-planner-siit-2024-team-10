package m3.eventplanner.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import m3.eventplanner.R;
import m3.eventplanner.models.GetOfferingDTO;

public class OfferingItemAdapter extends RecyclerView.Adapter<OfferingItemAdapter.OfferingViewHolder> {
    private List<GetOfferingDTO> offerings;

    public OfferingItemAdapter(List<GetOfferingDTO> offerings) {
        this.offerings = offerings;
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
        holder.bind(offering);
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
        private TextView offeringName;
        private TextView offeringPrice;
        private TextView offeringType;

        public OfferingViewHolder(@NonNull View itemView) {
            super(itemView);
            offeringName = itemView.findViewById(R.id.offering_name);
            offeringPrice = itemView.findViewById(R.id.offering_price);
            offeringType = itemView.findViewById(R.id.offering_type);
        }

        public void bind(GetOfferingDTO offering) {
            offeringName.setText(offering.getName());
            offeringPrice.setText(String.format("$%.2f", offering.getPrice()));
        }
    }
}