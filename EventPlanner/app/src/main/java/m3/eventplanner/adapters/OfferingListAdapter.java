package m3.eventplanner.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import m3.eventplanner.R;
import m3.eventplanner.models.Offering;
import m3.eventplanner.models.Product;
import m3.eventplanner.models.Service;

import java.util.List;

public class OfferingListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Offering> offeringList;

    private static final int TYPE_PRODUCT = 1;
    private static final int TYPE_SERVICE = 2;

    public OfferingListAdapter(List<Offering> offeringList) {
        this.offeringList = offeringList;
    }

    @Override
    public int getItemViewType(int position) {
        Offering offering = offeringList.get(position);
        if (offering instanceof Product) {
            return TYPE_PRODUCT;
        } else if (offering instanceof Service) {
            return TYPE_SERVICE;
        } else {
            return super.getItemViewType(position);
        }
    }

    @Override
    public int getItemCount() {
        return offeringList.size();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if (viewType == TYPE_PRODUCT) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_card, parent, false);
            return new ProductViewHolder(view);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.service_card, parent, false);
            return new ServiceViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Offering offering = offeringList.get(position);

        if (holder instanceof ProductViewHolder) {
            Product product = (Product) offering;
            ((ProductViewHolder) holder).bind(product);
        } else if (holder instanceof ServiceViewHolder) {
            Service service = (Service) offering;
            ((ServiceViewHolder) holder).bind(service);
        }
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        TextView title, description, price, provider;

        public ProductViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.product_card_title);
            description = itemView.findViewById(R.id.product_card_description);
            price = itemView.findViewById(R.id.product_card_price);
            provider = itemView.findViewById(R.id.product_card_provider);
        }

        public void bind(Product product) {
            title.setText(product.getTitle());
            description.setText(product.getDescription());
            price.setText(product.getPrice()+"€");
            provider.setText(product.getOrganizer());
        }
    }

    public static class ServiceViewHolder extends RecyclerView.ViewHolder {
        TextView title, description, price, provider;

        public ServiceViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.service_card_title);
            description = itemView.findViewById(R.id.service_card_description);
            price = itemView.findViewById(R.id.service_card_price);
            provider = itemView.findViewById(R.id.service_card_provider);
        }

        public void bind(Service service) {
            title.setText(service.getTitle());
            description.setText(service.getDescription());
            price.setText(service.getPrice()+"€");
            provider.setText(service.getOrganizer());
        }
    }
}
