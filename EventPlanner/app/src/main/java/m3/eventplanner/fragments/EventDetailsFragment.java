package m3.eventplanner.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import m3.eventplanner.R;
import m3.eventplanner.adapters.AgendaItemListAdapter;
import m3.eventplanner.models.AgendaItem;
import m3.eventplanner.models.Event;
import m3.eventplanner.models.Organizer;

public class EventDetailsFragment extends Fragment {
    private TextView eventName, eventType, eventDescription, eventLocation, eventDate;
    private RecyclerView agendaRecyclerView;
    private ImageView eventOrganizerProfilePhoto;
    private TextView eventOrganizerName, eventOrganizerLocation, eventOrganizerEmail, eventOrganizerPhone;

    private Event event;


    public EventDetailsFragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_event_details, container, false);

        InitializeUIComponents(view);

        if (getArguments() != null) {
            event = getArguments().getParcelable("selectedEvent");
            PopulateEventDetails();
        }
        return view;
    }

    private void InitializeUIComponents(View view){
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
    }

    private void PopulateEventDetails(){
        eventName.setText(event.getName());
        eventType.setText(event.getEventType().getName());
        eventDescription.setText(event.getDescription());
        eventLocation.setText(event.getLocation().toString());
        eventDate.setText(event.getDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));

        // Populate the organizer details
        Organizer organizer = event.getOrganizer();
        eventOrganizerName.setText(organizer.getFirstName()+" "+organizer.getLastName());
        eventOrganizerLocation.setText(organizer.getLocation().getCity()+", "+organizer.getLocation().getCountry());
        eventOrganizerEmail.setText(organizer.getEmail());
        eventOrganizerPhone.setText(organizer.getPhoneNumber());

        if(event.getOrganizer().getProfilePhoto()!=null)
            Picasso.get().load(event.getOrganizer().getProfilePhoto()).into(eventOrganizerProfilePhoto);

        agendaRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        agendaRecyclerView.setAdapter(new AgendaItemListAdapter(event.getAgenda()));

    }
}