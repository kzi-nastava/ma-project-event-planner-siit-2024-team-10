package m3.eventplanner.adapters;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.card.MaterialCardView;

import java.util.Collection;
import m3.eventplanner.R;
import m3.eventplanner.models.GetEventDTO;

import androidx.navigation.Navigation;

public class EventListAdapter extends RecyclerView.Adapter<EventListAdapter.EventViewHolder> {

    private Collection<GetEventDTO> eventItemCollection;

    public EventListAdapter(Collection<GetEventDTO> eventItemCollection){
        this.eventItemCollection = eventItemCollection;
    }

    public static class EventViewHolder extends RecyclerView.ViewHolder {
        public MaterialCardView eventCard;
        public TextView titleTextView;
        public TextView organizerTextView;
        public TextView ratingTextView;
        public TextView locationAndDateTextView;
        public TextView eventTypeTextView;

        public EventViewHolder(View view) {
            super(view);
            eventCard = view.findViewById(R.id.event_card);
            titleTextView = view.findViewById(R.id.event_card_title);
            organizerTextView = view.findViewById(R.id.event_card_organizer);
            ratingTextView = view.findViewById(R.id.event_card_rating);
            locationAndDateTextView = view.findViewById(R.id.event_card_location_date);
            eventTypeTextView = view.findViewById(R.id.event_card_type);
        }
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.event_card, parent, false);
        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        GetEventDTO event = (GetEventDTO) eventItemCollection.toArray()[position];

        holder.titleTextView.setText(event.getName());
        holder.organizerTextView.setText("Organizer: " + event.getOrganizer().getFirstName() + " " + event.getOrganizer().getLastName());
        holder.ratingTextView.setText(event.getAverageRating() + "★");
        holder.locationAndDateTextView.setText(event.getLocation().getCity() + ", " + event.getLocation().getCountry() + " at " + event.getDate());
        if(event.getEventType()!=null)
            holder.eventTypeTextView.setText(event.getEventType().getName().toUpperCase());
        else
            holder.eventTypeTextView.setVisibility(View.GONE);

        holder.eventCard.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putInt("selectedEventId", event.getId());
            Navigation.findNavController(v).navigate(R.id.action_homeScreenFragment_to_eventDetailsFragment, bundle);
        });
    }

    @Override
    public int getItemCount() {
        return eventItemCollection.size();
    }
}