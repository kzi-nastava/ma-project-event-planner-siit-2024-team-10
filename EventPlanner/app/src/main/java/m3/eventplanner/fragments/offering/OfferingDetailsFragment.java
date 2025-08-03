package m3.eventplanner.fragments.offering;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import m3.eventplanner.R;
import m3.eventplanner.adapters.CommentAdapter;
import m3.eventplanner.auth.TokenManager;
import m3.eventplanner.databinding.FragmentOfferingDetailsBinding;
import m3.eventplanner.fragments.reservation.CreateReservationFragment;
import m3.eventplanner.fragments.reservation.CreateReservationViewModel;
import m3.eventplanner.models.CreateCommentDTO;
import m3.eventplanner.models.GetEventDTO;
import m3.eventplanner.models.GetOfferingDTO;
import m3.eventplanner.models.GetProviderDTO;
import m3.eventplanner.models.GetServiceDTO;

public class OfferingDetailsFragment extends Fragment {
    private CommentAdapter commentAdapter;
    private GetOfferingDTO offering;
    private FragmentOfferingDetailsBinding binding;
    private OfferingDetailsViewModel viewModel;
    private boolean isOwner;
    private boolean isAdmin;
    private int userId;

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

        if (getArguments() != null) {
            int offeringId = getArguments().getInt("selectedServiceId");
            TokenManager tokenManager = new TokenManager(requireContext());
            userId = tokenManager.getUserId();
            int accountId = tokenManager.getAccountId();
            isAdmin = tokenManager.getRole()!=null && tokenManager.getRole().equals("ADMIN");

            boolean isOrganizer = "EVENT_ORGANIZER".equals(tokenManager.getRole());
            binding.btnContactProvider.setVisibility(isOrganizer ? View.VISIBLE : View.GONE);

            if(accountId==0)
                binding.favouriteButton.setVisibility(View.GONE);
            viewModel.loadOfferingDetails(offeringId, accountId, userId);

            if (userId != 0) {
                viewModel.loadEventsForOrganizer(userId);
            }
        }
        // comments
        commentAdapter = new CommentAdapter();
        RecyclerView recyclerView = view.findViewById(R.id.comments_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(commentAdapter);

        if (getArguments() != null) {
            int offeringId = getArguments().getInt("selectedServiceId");
            viewModel.loadComments(offeringId);
        }

        Button submitButton = view.findViewById(R.id.submit_comment_button);
        submitButton.setOnClickListener(v -> {
            submitComment();
        });

        viewModel.getComments().observe(getViewLifecycleOwner(), comments -> {
            commentAdapter.setComments(comments);
        });

        viewModel.getError().observe(getViewLifecycleOwner(), errorMsg -> {
            if (errorMsg != null) {
                Toast.makeText(requireContext(), errorMsg, Toast.LENGTH_SHORT).show();
            }
        });

        viewModel.getSuccessMessage().observe(getViewLifecycleOwner(), message -> {
            if (message != null) {
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
            }
        });
        if (getArguments() != null) {
            int offeringId = getArguments().getInt("selectedServiceId");
            TokenManager tokenManager = new TokenManager(requireContext());
            int userId = tokenManager.getUserId();

            binding.addNewCommentSectionId.setVisibility(View.GONE);
            binding.submitCommentButton.setVisibility(View.GONE);
            binding.newCommentText.setVisibility(View.GONE);
            binding.newCommentRating.setVisibility(View.GONE);

            if("EVENT_ORGANIZER".equals(tokenManager.getRole())){
                viewModel.checkIfUserPurchasedOffering(userId, offeringId);
                viewModel.getHasPurchased().observe(getViewLifecycleOwner(), hasPurchased -> {
                    if (hasPurchased != null && hasPurchased) {
                        binding.addNewCommentSectionId.setVisibility(View.VISIBLE);
                        binding.submitCommentButton.setVisibility(View.VISIBLE);
                        binding.newCommentText.setVisibility(View.VISIBLE);
                        binding.newCommentRating.setVisibility(View.VISIBLE);
                    }
                });
            }
        }
    }

    private void setupObservers() {
        viewModel.getOffering().observe(getViewLifecycleOwner(), this::populateOfferingDetails);

        viewModel.getIsFavourite().observe(getViewLifecycleOwner(), isFavourite ->
                binding.favouriteButton.setImageResource(isFavourite ?
                        R.drawable.heart_filled : R.drawable.heart_outline)
        );

        viewModel.getNavigateHome().observe(getViewLifecycleOwner(), navigateHome -> {
            if (navigateHome) {
                Navigation.findNavController(binding.getRoot()).navigate(R.id.homeScreenFragment);
            }
        });

        viewModel.getError().observe(getViewLifecycleOwner(), error ->
                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show()
        );

        viewModel.getSuccessMessage().observe(getViewLifecycleOwner(), message ->
                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show()
        );

        viewModel.getIsOwner().observe(getViewLifecycleOwner(), isOwner -> {
            this.isOwner = isOwner;
            if (isOwner || isAdmin) {
                binding.deleteOfferingButton.setVisibility(View.VISIBLE);
                binding.editOfferingButton.setVisibility(View.VISIBLE);
            } else {
                binding.deleteOfferingButton.setVisibility(View.GONE);
                binding.editOfferingButton.setVisibility(View.GONE);
            }
        });
    }

    private void setupClickListeners() {
        binding.btnViewProvider.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putParcelable("provider", offering.getProvider());

            Navigation.findNavController(v).navigate(R.id.providerOfferingsFragment, bundle);
        });
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

        binding.bookNowButton.setOnClickListener(v -> {
            if (offering instanceof GetServiceDTO) {
                CreateReservationFragment dialog = new CreateReservationFragment();
                Bundle bundle = new Bundle();
                bundle.putInt("selectedServiceId", offering.getId());
                dialog.setArguments(bundle);
                dialog.show(getParentFragmentManager(), "create_reservation_dialog");
                return;
            }

            List<GetEventDTO> events = viewModel.getEvents().getValue();
            if (events == null || events.isEmpty()) {
                Toast.makeText(requireContext(), "No events found for booking", Toast.LENGTH_SHORT).show();
                return;
            }

            List<String> eventNames = new ArrayList<>();
            for (GetEventDTO event : events) {
                eventNames.add(event.getName());
            }

            View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_event_selection, null);
            Spinner spinner = dialogView.findViewById(R.id.spinnerEvents);

            ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, eventNames);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(adapter);

            new AlertDialog.Builder(requireContext())
                    .setTitle("Select Event for Booking")
                    .setView(dialogView)
                    .setPositiveButton("Book", (dialog, which) -> {
                        int selectedPosition = spinner.getSelectedItemPosition();
                        if (selectedPosition >= 0 && selectedPosition < events.size()) {
                            int selectedEventId = events.get(selectedPosition).getId();
                            viewModel.buyOffering(selectedEventId);
                        } else {
                            Toast.makeText(requireContext(), "Invalid event selected", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
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
            if(offering instanceof GetServiceDTO) {
                bundle.putInt("selectedServiceId", offering.getId());
                Navigation.findNavController(v).navigate(R.id.editServiceFragment, bundle);
            }
            else{
                bundle.putInt("selectedProductId", offering.getId());
                Navigation.findNavController(v).navigate(R.id.editProductFragment, bundle);
            }

        });
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
    private void submitComment() {
        RatingBar ratingBar = requireView().findViewById(R.id.new_comment_rating);
        TextInputEditText commentTextInput = requireView().findViewById(R.id.new_comment_text);

        int rating = (int) ratingBar.getRating();
        String commentText = commentTextInput.getText() != null ? commentTextInput.getText().toString().trim() : "";

        if (commentText.isEmpty()) {
            Toast.makeText(requireContext(), "Please enter a comment", Toast.LENGTH_SHORT).show();
            return;
        }

        if (rating == 0) {
            Toast.makeText(requireContext(), "Please provide a rating", Toast.LENGTH_SHORT).show();
            return;
        }

        if (getArguments() != null) {
            int offeringId = getArguments().getInt("selectedServiceId");
            CreateCommentDTO commentDTO = new CreateCommentDTO(commentText,userId,rating);

            viewModel.createComment(offeringId, commentDTO);

            ratingBar.setRating(0);
            commentTextInput.setText("");
        }
    }

}
