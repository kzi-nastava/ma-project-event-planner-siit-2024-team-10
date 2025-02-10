package m3.eventplanner.fragments.event;

import android.media.session.MediaSession;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.time.LocalTime;
import java.util.Collection;
import java.util.List;

import m3.eventplanner.R;
import m3.eventplanner.adapters.AgendaItemListAdapter;
import m3.eventplanner.auth.TokenManager;
import m3.eventplanner.clients.ClientUtils;
import m3.eventplanner.databinding.FragmentEventDetailsBinding;
import m3.eventplanner.fragments.eventtype.EventTypeFormFragment;
import m3.eventplanner.models.AddFavouriteEventDTO;
import m3.eventplanner.models.CreateAgendaItemDTO;
import m3.eventplanner.models.GetAgendaItemDTO;
import m3.eventplanner.models.GetEventDTO;
import m3.eventplanner.models.GetOrganizerDTO;
import m3.eventplanner.models.UpdateAgendaItemDTO;
import m3.eventplanner.models.UpdateEventTypeDTO;
import m3.eventplanner.utils.PdfUtils;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EventDetailsFragment extends Fragment implements AgendaItemFormFragment.OnAgendaItemFormFilledListener {
    private FragmentEventDetailsBinding binding;
    private EventDetailsViewModel viewModel;
    private ClientUtils clientUtils;
    private GetEventDTO event;
    private boolean isOwner;
    private boolean isAdmin;


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
        viewModel.initialize(requireContext());

        setupObservers();
        setupClickListeners();

        // Load initial data
        if (getArguments() != null) {
            int eventId = getArguments().getInt("selectedEventId");
            TokenManager tokenManager = new TokenManager(requireContext());
            int accountId = tokenManager.getAccountId();
            int userId = tokenManager.getUserId();
            isAdmin = tokenManager.getRole()!=null && tokenManager.getRole().equals("ADMIN");
            if(accountId==0)
                binding.favouriteButton.setVisibility(View.GONE);
            viewModel.loadEventDetails(eventId, accountId, userId);
        }
    }

    private void setupObservers() {
        viewModel.getEvent().observe(getViewLifecycleOwner(), this::populateEventDetails);

        viewModel.getAgenda().observe(getViewLifecycleOwner(), agendaItems -> {
            binding.agendaRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            binding.agendaRecyclerView.setAdapter(new AgendaItemListAdapter(agendaItems,this,isOwner));
        });

        viewModel.getIsFavourite().observe(getViewLifecycleOwner(), isFavourite ->
                binding.favouriteButton.setImageResource(isFavourite ?
                        R.drawable.heart_filled : R.drawable.heart_outline)
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
            if(isOwner){
                binding.addAgendaItemButton.setVisibility(View.VISIBLE);
            }

            if( (isOwner||isAdmin) && event.isOpen()) {
                binding.openEventReportButton.setVisibility(View.VISIBLE);
            }
            else {
                binding.openEventReportButton.setVisibility(View.GONE);
            }
        });

        viewModel.getIsParticipating().observe(getViewLifecycleOwner(), isParticipating->
        {
            binding.attendButton.setText("You're going!");
            binding.attendButton.setEnabled(false);
        });
    }

    private void setupClickListeners() {
        binding.favouriteButton.setOnClickListener(v -> {
            if (getArguments() != null) {
                int eventId = getArguments().getInt("selectedEventId");
                int accountId = new TokenManager(requireContext()).getAccountId();
                viewModel.toggleFavourite(accountId);
            }
        });

        binding.submitCommentButton.setOnClickListener(v -> {
            int rating = (int) binding.newRating.getRating();
            if (rating > 0) {
                int eventId = getArguments().getInt("selectedEventId");
                viewModel.submitRating(rating);
                Toast.makeText(getContext(), "Rating submitted successfully", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "Please select a rating", Toast.LENGTH_SHORT).show();
            }
        });

        binding.addAgendaItemButton.setOnClickListener(v->{
            AgendaItemFormFragment dialog = new AgendaItemFormFragment();
            dialog.show(getChildFragmentManager(), "AgendaItemFormFragment");
        });

        binding.openEventReportButton.setOnClickListener(v->{
            Bundle bundle = new Bundle();
            bundle.putInt("selectedEventId", event.getId());
            Navigation.findNavController(v).navigate(R.id.openEventReportFragment, bundle);
        });

        binding.attendButton.setOnClickListener(v->{
            viewModel.addParticipant();
        });

        binding.exportToPdfButton.setOnClickListener(v->{
            viewModel.exportToPdf();
        });
    }

    private void populateEventDetails(GetEventDTO event) {
        this.event=event;

        binding.eventName.setText(event.getName());
        if(event.getEventType()!=null)
            binding.eventType.setText(event.getEventType().getName());
        else {
            binding.eventTypeTitle.setVisibility(View.GONE);
            binding.eventType.setVisibility(View.GONE);
        }
        binding.eventDescription.setText(event.getDescription());
        binding.eventLocation.setText(event.getLocation().toString());
        binding.eventDate.setText(event.getDate());
        binding.averageRating.setText("★ "+event.getAverageRating());
        binding.participantsCount.setText(String.valueOf(event.getParticipantsCount()));

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

        if(!event.isOpen()){
            binding.participantsTitle.setVisibility(View.GONE);
            binding.participantsSection.setVisibility(View.GONE);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onAgendaItemFormFilled(int id, String name, String description, LocalTime startTime, LocalTime endTime, String location, boolean edit) {
        if(!edit){
            CreateAgendaItemDTO agendaItemDTO = new CreateAgendaItemDTO(name,description,location,startTime.toString(),endTime.toString());
            viewModel.addAgendaItem(agendaItemDTO);
        }
        else {
            UpdateAgendaItemDTO agendaItemDTO =new UpdateAgendaItemDTO(name,description,location,startTime.toString(),endTime.toString());
            viewModel.updateAgendaItem(id,agendaItemDTO);
        }
    }
}