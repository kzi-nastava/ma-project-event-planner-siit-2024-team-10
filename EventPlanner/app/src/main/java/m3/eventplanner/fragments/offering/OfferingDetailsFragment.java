package m3.eventplanner.fragments.offering;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import com.squareup.picasso.Picasso;

import m3.eventplanner.R;
import m3.eventplanner.auth.TokenManager;
import m3.eventplanner.databinding.FragmentOfferingDetailsBinding;
import m3.eventplanner.models.GetOfferingDTO;
import m3.eventplanner.models.GetProviderDTO;
import m3.eventplanner.models.GetServiceDTO;

public class OfferingDetailsFragment extends Fragment {

    private GetOfferingDTO offering;
    private FragmentOfferingDetailsBinding binding;
    private OfferingDetailsViewModel viewModel;
    private boolean isOwner;
    private boolean isAdmin;

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
        binding = FragmentOfferingDetailsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(OfferingDetailsViewModel.class);
        viewModel.initialize(requireContext());

        setupObservers();
        setupClickListeners();

        // Load initial data
        if (getArguments() != null) {
            int offeringId = getArguments().getInt("selectedServiceId");
            TokenManager tokenManager = new TokenManager(requireContext());
            int accountId = tokenManager.getAccountId();
            int userId = tokenManager.getUserId();
            isAdmin = tokenManager.getRole()!=null && tokenManager.getRole().equals("ADMIN");

            boolean isOrganizer = "EVENT_ORGANIZER".equals(tokenManager.getRole());
            binding.btnContactProvider.setVisibility(isOrganizer ? View.VISIBLE : View.GONE);

            if(accountId==0)
                binding.favouriteButton.setVisibility(View.GONE);

            viewModel.loadOfferingDetails(offeringId, accountId, userId);
        }
    }

    private void setupObservers() {
        viewModel.getOffering().observe(getViewLifecycleOwner(), this::populateOfferingDetails);

        viewModel.getIsFavourite().observe(getViewLifecycleOwner(), isFavourite ->
                binding.favouriteButton.setImageResource(isFavourite ?
                        R.drawable.heart_filled : R.drawable.heart_outline)
        );

        viewModel.getNavigateHome().observe(getViewLifecycleOwner(), navigateHome ->
                {
                    if(navigateHome){
                        Navigation.findNavController(binding.getRoot()).navigate(R.id.homeScreenFragment);
                    }
                }
        );

        viewModel.getError().observe(getViewLifecycleOwner(), error ->
                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show()
        );

        viewModel.getSuccessMessage().observe(getViewLifecycleOwner(), message ->
                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show()
        );

        viewModel.getIsOwner().observe(getViewLifecycleOwner(), isOwner->
        {
            this.isOwner=isOwner;
            if(isOwner || isAdmin){
                binding.deleteOfferingButton.setVisibility(View.VISIBLE);
                binding.editOfferingButton.setVisibility(View.VISIBLE);
            } else {
                binding.deleteOfferingButton.setVisibility(View.GONE);
                binding.editOfferingButton.setVisibility(View.GONE);
            }
        });
    }

    private void setupClickListeners() {
        binding.btnContactProvider.setOnClickListener(v -> {
            if (offering != null && offering.getProvider() != null) {
                int receiverId = offering.getProvider().getAccountId();
                Bundle bundle = new Bundle();
                bundle.putInt("receiverId", receiverId);
                Navigation.findNavController(v).navigate(R.id.chatFragment, bundle);
            } else {
                Toast.makeText(getContext(), "Provider info unavailable", Toast.LENGTH_SHORT).show();
            }
        });

        binding.favouriteButton.setOnClickListener(v -> {
            if (getArguments() != null) {
                int accountId = new TokenManager(requireContext()).getAccountId();
                viewModel.toggleFavourite(accountId);
            }
        });

        binding.deleteOfferingButton.setOnClickListener(v->{
            if (!isOwner && !isAdmin) {
                Toast.makeText(getContext(), "You cannot delete this", Toast.LENGTH_SHORT).show();
                return;
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());

            builder.setTitle("Confirm delete")
                    .setMessage("Are you sure you want to delete this offering?")
                    .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            viewModel.deleteOffering();
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            builder.create().show();
        });

        binding.editOfferingButton.setOnClickListener(v->{
            if (!isOwner && !isAdmin) {
                Toast.makeText(getContext(), "You cannot edit this", Toast.LENGTH_SHORT).show();
                return;
            }

            Bundle bundle = new Bundle();
            bundle.putInt("selectedServiceId", offering.getId());
            Navigation.findNavController(v).navigate(R.id.editServiceFragment, bundle);
        });
    }

    private void populateOfferingDetails(GetOfferingDTO offeringDTO) {
        this.offering = offeringDTO;

        // Service-specifična polja
        if (offeringDTO instanceof GetServiceDTO) {
            GetServiceDTO service = (GetServiceDTO) offeringDTO;
            binding.cancellationDeadline.setVisibility(View.VISIBLE);
            binding.reservationDeadline.setVisibility(View.VISIBLE);

            binding.cancellationDeadline.setText(String.valueOf(service.getCancellationPeriod())+"hours");
            binding.reservationDeadline.setText(String.valueOf(service.getReservationPeriod())+"hours");

            binding.specification.setText(service.getSpecification());
            if (service.isAutoConfirm()) {
                binding.duration.setText(String.valueOf(service.getMinDuration())+"hours");
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

            // Set category label styling (default blue/gray styling)
            binding.category.setBackgroundResource(R.drawable.category_label_background);
            binding.category.setTextColor(ContextCompat.getColor(requireContext(), R.color.category_text_color));
        } else {
            binding.category.setVisibility(View.GONE);
        }

        binding.description.setText(offeringDTO.getDescription());

        binding.originalPrice.setText(String.format("%.2f $", offeringDTO.getPrice()));
        binding.priceWithDiscount.setText(String.format("%.2f $", offeringDTO.getPrice() * (1 - offeringDTO.getDiscount() / 100)));
        binding.discount.setText(String.format("(%.2f%%)", offeringDTO.getDiscount()));
        TokenManager tokenManager = new TokenManager(requireContext());
        boolean isOrganizer = "EVENT_ORGANIZER".equals(tokenManager.getRole());

        if (offeringDTO.isAvailable()) {
            binding.isAvailable.setVisibility(View.VISIBLE);
            binding.isNotAvailable.setVisibility(View.GONE);

            binding.isAvailable.setTextColor(ContextCompat.getColor(requireContext(), R.color.green_dark));
            binding.isAvailable.setBackgroundResource(R.drawable.availability_label_background);

            if (isOrganizer) {
                binding.bookNowButton.setVisibility(View.VISIBLE);
                binding.bookNowButton.setEnabled(true);
                binding.bookNowButton.setBackgroundTintList(ColorStateList.valueOf(
                        ContextCompat.getColor(requireContext(), R.color.brand_purple)
                ));
                binding.bookNowButton.setTextColor(ContextCompat.getColor(requireContext(), R.color.chocolate_brown));
            } else {
                binding.bookNowButton.setVisibility(View.GONE);
            }
        } else {
            binding.isAvailable.setVisibility(View.GONE);
            binding.isNotAvailable.setVisibility(View.VISIBLE);

            binding.isNotAvailable.setTextColor(ContextCompat.getColor(requireContext(), R.color.red_dark));
            binding.isNotAvailable.setBackgroundResource(R.drawable.unavailability_label_background);

            binding.bookNowButton.setVisibility(View.GONE);
        }


        GetProviderDTO provider = offeringDTO.getProvider();

        binding.providerName.setText(provider.getCompany().getName());

        if (provider.getLocation() != null) {
            binding.providerAddress.setText(String.format("%s, %s",
                    provider.getLocation().getCity(), provider.getLocation().getCountry()));
        } else {
            binding.providerAddress.setText("N/A");
        }


        binding.providerEmail.setText(provider.getEmail());
        binding.providerPhone.setText(provider.getPhoneNumber());

        if (provider.getProfilePhoto() != null) {
            Picasso.get()
                    .load(provider.getProfilePhoto())
                    .into(binding.providerProfilePhoto);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}