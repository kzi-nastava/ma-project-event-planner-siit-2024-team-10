package m3.eventplanner.adapters;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import m3.eventplanner.R;
import m3.eventplanner.models.GetPricelistItemDTO;

public class PricelistItemAdapter extends RecyclerView.Adapter<PricelistItemAdapter.ViewHolder> {

    public interface OnUpdateClickListener {
        void onUpdateClick(int offeringId, double newPrice, double newDiscount);
    }

    private List<GetPricelistItemDTO> items;
    private OnUpdateClickListener listener;

    public PricelistItemAdapter(List<GetPricelistItemDTO> items, OnUpdateClickListener listener) {
        this.items = items;
        this.listener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView textName;
        public EditText editPrice, editDiscount;
        public Button buttonUpdate;

        public ViewHolder(View itemView) {
            super(itemView);
            textName = itemView.findViewById(R.id.textName);
            editPrice = itemView.findViewById(R.id.editPrice);
            editDiscount = itemView.findViewById(R.id.editDiscount);
            buttonUpdate = itemView.findViewById(R.id.buttonUpdate);
        }
    }

    @NonNull
    @Override
    public PricelistItemAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.pricelist_item_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PricelistItemAdapter.ViewHolder holder, int position) {
        GetPricelistItemDTO item = items.get(position);
        holder.textName.setText(item.getName());
        holder.editPrice.setText(String.valueOf(item.getPrice()));
        holder.editDiscount.setText(String.valueOf(item.getDiscount()));

        holder.buttonUpdate.setOnClickListener(v -> {
            try {
                double price = Double.parseDouble(holder.editPrice.getText().toString());
                double discount = Double.parseDouble(holder.editDiscount.getText().toString());
                listener.onUpdateClick(item.getOfferingId(), price, discount);
            } catch (NumberFormatException e) {
                Toast.makeText(holder.itemView.getContext(), "Try again", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void updateItems(List<GetPricelistItemDTO> newItems) {
        this.items = newItems;
        notifyDataSetChanged();
    }
}
