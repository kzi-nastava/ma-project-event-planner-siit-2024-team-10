package m3.eventplanner.adapters;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.card.MaterialCardView;
import java.util.List;
import m3.eventplanner.R;
import m3.eventplanner.models.Event;
import androidx.navigation.Navigation;

public class EventListAdapter extends RecyclerView.Adapter<EventListAdapter.EventViewHolder> {

    private List<Event> eventItemList;

    public EventListAdapter(List<Event> eventItemList) {
        this.eventItemList = eventItemList;
    }

    public static class EventViewHolder extends RecyclerView.ViewHolder {
        public MaterialCardView eventCard;
        public TextView titleTextView;
        public TextView organizerTextView;
        public TextView ratingTextView;
        public TextView locationAndDateTextView;

        public EventViewHolder(View view) {
            super(view);
            eventCard = view.findViewById(R.id.event_card);
            titleTextView = view.findViewById(R.id.event_card_title);
            organizerTextView = view.findViewById(R.id.event_card_organizer);
            ratingTextView = view.findViewById(R.id.event_card_rating);
            locationAndDateTextView = view.findViewById(R.id.event_card_location_date);
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
        Event event = eventItemList.get(position);

        holder.titleTextView.setText(event.getTitle());
        holder.organizerTextView.setText("Organizer: " + event.getOrganizer());
        holder.ratingTextView.setText(event.getRating()+"★");
        holder.locationAndDateTextView.setText(event.getLocation()+" at "+event.getDate());

        holder.eventCard.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putParcelable("selectedEvent", event);
            Navigation.findNavController(v).navigate(R.id.action_homeScreenFragment_to_eventDetailsFragment, bundle);
        });
    }

    @Override
    public int getItemCount() {
        return eventItemList.size();
    }
}