package m3.eventplanner.fragments.budget;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.squareup.picasso.Picasso;

import m3.eventplanner.R;
import m3.eventplanner.auth.TokenManager;
import m3.eventplanner.databinding.FragmentOldOfferingDetailsBinding;
import m3.eventplanner.models.GetOfferingDTO;
import m3.eventplanner.models.GetProviderDTO;
import m3.eventplanner.models.GetServiceDTO;

public class OldOfferingDetailsFragment extends Fragment {

    private FragmentOldOfferingDetailsBinding binding;
    private GetOfferingDTO offering;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            offering = getArguments().getParcelable("offering");
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentOldOfferingDetailsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (offering != null) {
            populateOfferingDetails(offering);
        }
    }

    private void populateOfferingDetails(GetOfferingDTO offeringDTO) {
        this.offering = offeringDTO;

        if (offeringDTO instanceof GetServiceDTO) {
            GetServiceDTO service = (GetServiceDTO) offeringDTO;
            binding.cancellationDeadline.setVisibility(View.VISIBLE);
            binding.reservationDeadline.setVisibility(View.VISIBLE);

            binding.cancellationDeadline.setText(service.getCancellationPeriod() + " hours");
            binding.reservationDeadline.setText(service.getReservationPeriod() + " hours");

            binding.specification.setText(service.getSpecification());
            if (service.isAutoConfirm()) {
                binding.duration.setText(service.getMinDuration() + " hours");
            } else {
                binding.duration.setText(String.format("%dh - %dh", service.getMinDuration(), service.getMaxDuration()));
            }
        } else {
            binding.specificationLabel.setVisibility(View.GONE);
            binding.reservationDeadline.setVisibility(View.GONE);
            binding.cancellationDeadline.setVisibility(View.GONE);
            binding.reservationDeadlineLabel.setVisibility(View.GONE);
            binding.durationLabel.setVisibility(View.GONE);
            binding.duration.setVisibility(View.GONE);
            binding.cancellationDeadlineLabel.setVisibility(View.GONE);
        }

        binding.offeringName.setText(offeringDTO.getName());

        if (offeringDTO.getCategory() != null) {
            binding.category.setText(offeringDTO.getCategory().getName());
            binding.category.setVisibility(View.VISIBLE);
            binding.category.setBackgroundResource(R.drawable.category_label_background);
            binding.category.setTextColor(ContextCompat.getColor(requireContext(), R.color.category_text_color));
        } else {
            binding.category.setVisibility(View.GONE);
        }

        binding.description.setText(offeringDTO.getDescription());

        binding.originalPrice.setText(String.format("%.2f $", offeringDTO.getPrice()));
        binding.priceWithDiscount.setText(String.format("%.2f $", offeringDTO.getPrice() * (1 - offeringDTO.getDiscount() / 100)));
        binding.discount.setText(String.format("(%.2f%%)", offeringDTO.getDiscount()));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
