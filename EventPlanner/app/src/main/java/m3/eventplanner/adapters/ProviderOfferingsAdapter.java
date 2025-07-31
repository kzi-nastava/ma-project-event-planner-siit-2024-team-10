package m3.eventplanner.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DecimalFormat;
import java.util.List;

import m3.eventplanner.R;
import m3.eventplanner.fragments.offering.ProviderOfferingsViewModel;
import m3.eventplanner.models.GetCommentDTO;
import m3.eventplanner.models.GetOfferingDTO;

public class ProviderOfferingsAdapter extends RecyclerView.Adapter<ProviderOfferingsAdapter.OfferingViewHolder> {

    private List<GetOfferingDTO> offerings;
    private Context context;
    private DecimalFormat priceFormat = new DecimalFormat("#,##0.00");
    private ProviderOfferingsViewModel viewModel;

    public ProviderOfferingsAdapter(List<GetOfferingDTO> offerings, Context context, ProviderOfferingsViewModel viewModel) {
        this.offerings = offerings;
        this.context = context;
        this.viewModel = viewModel;
    }

    @NonNull
    @Override
    public OfferingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_offering, parent, false);
        return new OfferingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OfferingViewHolder holder, int position) {
        GetOfferingDTO offering = offerings.get(position);
        holder.bind(offering);
    }

    @Override
    public int getItemCount() {
        return offerings.size();
    }

    public class OfferingViewHolder extends RecyclerView.ViewHolder {
        private TextView tvName, tvCategory, tvDescription, tvPrice, tvDiscount, tvDiscountedPrice, tvRating;
        private RecyclerView recyclerComments;
        private TextView tvNoComments;
        private CommentAdapter commentAdapter;

        public OfferingViewHolder(@NonNull View itemView) {
            super(itemView);

            tvName = itemView.findViewById(R.id.tv_offering_name);
            tvCategory = itemView.findViewById(R.id.tv_offering_category);
            tvDescription = itemView.findViewById(R.id.tv_offering_description);
            tvPrice = itemView.findViewById(R.id.tv_offering_price);
            tvDiscount = itemView.findViewById(R.id.tv_offering_discount);
            tvDiscountedPrice = itemView.findViewById(R.id.tv_offering_discounted_price);
            tvRating = itemView.findViewById(R.id.tv_offering_rating);
            recyclerComments = itemView.findViewById(R.id.recycler_comments);
            tvNoComments = itemView.findViewById(R.id.tv_no_comments);

            commentAdapter = new CommentAdapter();
            recyclerComments.setLayoutManager(new LinearLayoutManager(context));
            recyclerComments.setAdapter(commentAdapter);
        }

        public void bind(GetOfferingDTO offering) {
            tvName.setText(offering.getName());

            if (offering.getCategory() != null) {
                tvCategory.setText(offering.getCategory().getName());
            } else {
                tvCategory.setText("Nepoznata kategorija");
            }

            tvDescription.setText(offering.getDescription());

            double originalPrice = offering.getPrice();
            double discount = offering.getDiscount();

            tvPrice.setText(priceFormat.format(originalPrice) + " $");

            if (discount > 0) {
                tvDiscount.setVisibility(View.VISIBLE);
                tvDiscountedPrice.setVisibility(View.VISIBLE);

                tvDiscount.setText("-" + (int) discount + "%");
                double discountedPrice = originalPrice * (1 - discount / 100);
                tvDiscountedPrice.setText(priceFormat.format(discountedPrice) + " $");
            } else {
                tvDiscount.setVisibility(View.GONE);
                tvDiscountedPrice.setVisibility(View.GONE);
            }

            if (offering.getAverageRating() > 0) {
                tvRating.setText("★ " + String.format("%.1f", offering.getAverageRating()));
                tvRating.setVisibility(View.VISIBLE);
            } else {
                tvRating.setVisibility(View.GONE);
            }

            loadComments(offering.getId());
        }

        private void loadComments(int offeringId) {
            viewModel.loadCommentsForOffering(offeringId, new ProviderOfferingsViewModel.CommentsCallback() {
                @Override
                public void onSuccess(List<GetCommentDTO> loadedComments) {
                    if (loadedComments.isEmpty()) {
                        tvNoComments.setVisibility(View.VISIBLE);
                        recyclerComments.setVisibility(View.GONE);
                    } else {
                        tvNoComments.setVisibility(View.GONE);
                        recyclerComments.setVisibility(View.VISIBLE);
                        commentAdapter.setComments(loadedComments);
                    }
                }

                @Override
                public void onError(String error) {
                    tvNoComments.setVisibility(View.VISIBLE);
                    recyclerComments.setVisibility(View.GONE);
                    tvNoComments.setText("Error loading");
                }
            });
        }
    }
}
