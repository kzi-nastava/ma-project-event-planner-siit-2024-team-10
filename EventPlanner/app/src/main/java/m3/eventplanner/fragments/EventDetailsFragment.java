package m3.eventplanner.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import m3.eventplanner.R;
import m3.eventplanner.adapters.AgendaItemListAdapter;
import m3.eventplanner.adapters.EventListAdapter;
import m3.eventplanner.auth.TokenManager;
import m3.eventplanner.clients.ClientUtils;
import m3.eventplanner.models.AddFavouriteEventDTO;
import m3.eventplanner.models.AgendaItem;
import m3.eventplanner.models.Event;
import m3.eventplanner.models.GetAgendaItemDTO;
import m3.eventplanner.models.GetEventDTO;
import m3.eventplanner.models.GetOrganizerDTO;
import m3.eventplanner.models.Organizer;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EventDetailsFragment extends Fragment {
    private TextView eventName, eventType, eventDescription, eventLocation, eventDate;
    private RecyclerView agendaRecyclerView;
    private ImageView eventOrganizerProfilePhoto;
    private TextView eventOrganizerName, eventOrganizerLocation, eventOrganizerEmail, eventOrganizerPhone;
    private ImageButton favouriteButton;

    private int eventId;
    private GetEventDTO event;
    private Collection<GetAgendaItemDTO> agenda;
    private ClientUtils clientUtils;
    private boolean isFavourite = false;


    public EventDetailsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_event_details, container, false);

        clientUtils = new ClientUtils(requireContext());

        InitializeUIComponents(view);

        favouriteButton.setOnClickListener(v -> {
            int accountId=new TokenManager(requireContext()).getAccountId();
            if(isFavourite){
                clientUtils.getAccountService().removeEventFromFavourites(accountId, eventId).enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        isFavourite=!isFavourite;
                        RefreshFavouriteButton();
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        // Handle failure
                    }
                });
            }
            else{
                clientUtils.getAccountService().addEventToFavourites(accountId, new AddFavouriteEventDTO(eventId)).enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        isFavourite=!isFavourite;
                        RefreshFavouriteButton();
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        // Handle failure
                    }
                });
            }

        });

        if (getArguments() != null) {
            eventId = getArguments().getInt("selectedEventId");
            Call<GetEventDTO> eventCall = clientUtils.getEventService().getEvent(eventId);
            eventCall.enqueue(new Callback<GetEventDTO>() {
                @Override
                public void onResponse(Call<GetEventDTO> call, Response<GetEventDTO> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        event = response.body();
                        PopulateEventDetails();
                    } else {

                        return;
                    }
                }

                @Override
                public void onFailure(Call<GetEventDTO> call, Throwable t) {
                    Log.d("REZ", t.getMessage() != null ? t.getMessage() : "error");
                }
            });

            Call<Collection<GetAgendaItemDTO>> agendaCall = clientUtils.getEventService().getEventAgenda(eventId);
            agendaCall.enqueue(new Callback<Collection<GetAgendaItemDTO>>() {
                @Override
                public void onResponse(Call<Collection<GetAgendaItemDTO>> call, Response<Collection<GetAgendaItemDTO>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        agenda = response.body();
                        PopulateAgenda();
                    } else {
                        return;
                    }
                }

                @Override
                public void onFailure(Call<Collection<GetAgendaItemDTO>> call, Throwable t) {
                    Log.d("REZ", t.getMessage() != null ? t.getMessage() : "error");
                }
            });

            Call<Collection<GetEventDTO>> favouritesCall = clientUtils.getAccountService().getFavouriteEvents(new TokenManager(requireContext()).getAccountId());
            favouritesCall.enqueue(new Callback<Collection<GetEventDTO>>() {
                @Override
                public void onResponse(Call<Collection<GetEventDTO>> call, Response<Collection<GetEventDTO>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        isFavourite = response.body().stream().anyMatch(event -> event.getId() == eventId);
                        RefreshFavouriteButton();
                    } else {
                        return;
                    }
                }

                @Override
                public void onFailure(Call<Collection<GetEventDTO>> call, Throwable t) {
                    Log.d("REZ", t.getMessage() != null ? t.getMessage() : "error");
                }
            });
        }
        return view;
    }

    private void InitializeUIComponents(View view) {
        eventName = view.findViewById(R.id.eventName);
        eventType = view.findViewById(R.id.eventType);
        eventDescription = view.findViewById(R.id.eventDescription);
        eventLocation = view.findViewById(R.id.eventLocation);
        eventDate = view.findViewById(R.id.eventDate);
        agendaRecyclerView = view.findViewById(R.id.agendaRecyclerView);
        eventOrganizerProfilePhoto = view.findViewById(R.id.eventOrganizerProfilePhoto);
        eventOrganizerName = view.findViewById(R.id.eventOrganizerName);
        eventOrganizerLocation = view.findViewById(R.id.eventOrganizerLocation);
        eventOrganizerEmail = view.findViewById(R.id.eventOrganizerEmail);
        eventOrganizerPhone = view.findViewById(R.id.eventOrganizerPhone);
        favouriteButton = view.findViewById(R.id.favourite_button);
    }

    private void PopulateEventDetails() {
        eventName.setText(event.getName());
        eventType.setText(event.getEventType().getName());
        eventDescription.setText(event.getDescription());
        eventLocation.setText(event.getLocation().toString());
        eventDate.setText(event.getDate());

        // Populate the organizer details
        GetOrganizerDTO organizer = event.getOrganizer();
        eventOrganizerName.setText(organizer.getFirstName() + " " + organizer.getLastName());
        eventOrganizerLocation.setText(organizer.getLocation().getCity() + ", " + organizer.getLocation().getCountry());
        eventOrganizerEmail.setText(organizer.getEmail());
        eventOrganizerPhone.setText(organizer.getPhoneNumber());

        if (event.getOrganizer().getProfilePhoto() != null)
            Picasso.get().load(event.getOrganizer().getProfilePhoto()).into(eventOrganizerProfilePhoto);
    }

    private void PopulateAgenda() {
        agendaRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        agendaRecyclerView.setAdapter(new AgendaItemListAdapter((List<GetAgendaItemDTO>) agenda));
    }

    private void RefreshFavouriteButton() {
        favouriteButton.setImageResource(isFavourite ? R.drawable.heart_filled : R.drawable.heart_outline);
    }
}