package m3.eventplanner.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.Collection;

import m3.eventplanner.R;
import m3.eventplanner.models.GetOfferingDTO;

public class OfferingListAdapter extends RecyclerView.Adapter<OfferingListAdapter.OfferingViewHolder> {

    private Collection<GetOfferingDTO> offeringList;

    public OfferingListAdapter(Collection<GetOfferingDTO> offeringList) {
        this.offeringList = offeringList;
    }

    @Override
    public int getItemCount() {
        return offeringList.size();
    }

    @Override
    public OfferingViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.offering_card, parent, false);
        return new OfferingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(OfferingViewHolder holder, int position) {
        GetOfferingDTO offering = (GetOfferingDTO) offeringList.toArray()[position];
        holder.bind(offering);
    }

    public static class OfferingViewHolder extends RecyclerView.ViewHolder {

        TextView title, rating, price, provider, type, category;
        ImageView picture;

        public OfferingViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.offering_card_title);
            rating = itemView.findViewById(R.id.offering_card_rating);
            price = itemView.findViewById(R.id.offering_card_price);
            provider = itemView.findViewById(R.id.offering_card_provider);
            type = itemView.findViewById(R.id.offering_card_type);
            category = itemView.findViewById(R.id.offering_card_category);
            picture = itemView.findViewById(R.id.offering_card_image);
        }

        public void bind(GetOfferingDTO offering) {
            title.setText(offering.getName());
            rating.setText(offering.getAverageRating() + "★");
            price.setText(offering.getPrice() + "€");
            provider.setText(offering.getProvider().getCompany().getName());
            category.setText(offering.getCategory().getName().toUpperCase());

            if (offering.isProduct()) {
                type.setText("PRODUCT");
                picture.setImageResource(R.drawable.flowers);
            } else {
                type.setText("SERVICE");
                picture.setImageResource(R.drawable.makeup);
            }
        }
    }
}
