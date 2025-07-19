package m3.eventplanner.adapters;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;

import com.bumptech.glide.Glide;

import java.io.File;
import java.util.Collection;
import java.util.List;

import m3.eventplanner.BuildConfig;
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
        holder.offeringCard.setOnClickListener(v->{
            if (!offering.isProduct()){
                Bundle bundle = new Bundle();
                bundle.putInt("selectedServiceId", offering.getId());
                Navigation.findNavController(v).navigate(R.id.action_manageOfferingsFragment_to_serviceDetailsFragment,bundle);
            }
        });

    }

    public static class OfferingViewHolder extends RecyclerView.ViewHolder {
        public MaterialCardView offeringCard;
        TextView title, rating, price, provider, type, category;
        ImageView picture;

        public OfferingViewHolder(View itemView) {
            super(itemView);
            offeringCard = itemView.findViewById(R.id.offering_card);
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

            List<String> photos = offering.getPhotos();
            if (photos != null && !photos.isEmpty()) {
                String imageUrl = "http://" + BuildConfig.IP_ADDR + ":8080/api/images/" + photos.get(0).substring(photos.get(0).lastIndexOf(File.separator) + 1);
                Glide.with(itemView.getContext())
                        .load(imageUrl)
                        .placeholder(R.drawable.placeholder)
                        .error(R.drawable.placeholder)
                        .into(picture);
                System.out.println(imageUrl);
            } else {
                picture.setImageResource(R.drawable.placeholder);
            }
        }
    }
}
