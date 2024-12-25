package m3.eventplanner.fragments.event;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.Collection;
import java.util.List;

import m3.eventplanner.R;
import m3.eventplanner.adapters.AgendaItemListAdapter;
import m3.eventplanner.auth.TokenManager;
import m3.eventplanner.clients.ClientUtils;
import m3.eventplanner.databinding.FragmentEventDetailsBinding;
import m3.eventplanner.models.AddFavouriteEventDTO;
import m3.eventplanner.models.GetAgendaItemDTO;
import m3.eventplanner.models.GetEventDTO;
import m3.eventplanner.models.GetOrganizerDTO;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EventDetailsFragment extends Fragment {
    private FragmentEventDetailsBinding binding;
    private EventDetailsViewModel viewModel;
    private ClientUtils clientUtils;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentEventDetailsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(EventDetailsViewModel.class);
        clientUtils = new ClientUtils(requireContext());
        viewModel.initialize(clientUtils);

        setupObservers();
        setupClickListeners();

        // Load initial data
        if (getArguments() != null) {
            int eventId = getArguments().getInt("selectedEventId");
            int accountId = new TokenManager(requireContext()).getAccountId();
            if(accountId==0)
                binding.favouriteButton.setVisibility(View.GONE);
            viewModel.loadEventDetails(eventId, accountId);
        }
    }

    private void setupObservers() {
        viewModel.getEvent().observe(getViewLifecycleOwner(), this::populateEventDetails);

        viewModel.getAgenda().observe(getViewLifecycleOwner(), agendaItems -> {
            binding.agendaRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            binding.agendaRecyclerView.setAdapter(new AgendaItemListAdapter(agendaItems));
        });

        viewModel.getIsFavourite().observe(getViewLifecycleOwner(), isFavourite ->
                binding.favouriteButton.setImageResource(isFavourite ?
                        R.drawable.heart_filled : R.drawable.heart_outline)
        );

        viewModel.getError().observe(getViewLifecycleOwner(), error ->
                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show()
        );
    }

    private void setupClickListeners() {
        binding.favouriteButton.setOnClickListener(v -> {
            if (getArguments() != null) {
                int eventId = getArguments().getInt("selectedEventId");
                int accountId = new TokenManager(requireContext()).getAccountId();
                viewModel.toggleFavourite(accountId, eventId);
            }
        });

        binding.submitCommentButton.setOnClickListener(v -> {
            int rating = (int) binding.newRating.getRating();
            if (rating > 0) {
                int eventId = getArguments().getInt("selectedEventId");
                viewModel.submitRating(eventId, rating);
                Toast.makeText(getContext(), "Rating submitted successfully", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "Please select a rating", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void populateEventDetails(GetEventDTO event) {
        binding.eventName.setText(event.getName());
        binding.eventType.setText(event.getEventType().getName());
        binding.eventDescription.setText(event.getDescription());
        binding.eventLocation.setText(event.getLocation().toString());
        binding.eventDate.setText(event.getDate());
        binding.averageRating.setText("★ "+event.getAverageRating());

        // Populate organizer details
        GetOrganizerDTO organizer = event.getOrganizer();
        binding.eventOrganizerName.setText(String.format("%s %s",
                organizer.getFirstName(), organizer.getLastName()));
        binding.eventOrganizerLocation.setText(String.format("%s, %s",
                organizer.getLocation().getCity(), organizer.getLocation().getCountry()));
        binding.eventOrganizerEmail.setText(organizer.getEmail());
        binding.eventOrganizerPhone.setText(organizer.getPhoneNumber());

        if (organizer.getProfilePhoto() != null) {
            Picasso.get().load(organizer.getProfilePhoto())
                    .into(binding.eventOrganizerProfilePhoto);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}