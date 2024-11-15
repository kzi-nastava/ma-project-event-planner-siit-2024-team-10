package m3.eventplanner.adapters;

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

public class EventListAdapter extends RecyclerView.Adapter<EventListAdapter.EventViewHolder> {

    private List<Event> eventItemList;

    public EventListAdapter(List<Event> eventItemList) {
        this.eventItemList = eventItemList;
    }

    public static class EventViewHolder extends RecyclerView.ViewHolder {
        public MaterialCardView eventCard;
        public TextView titleTextView;
        public TextView organizerTextView;
        public TextView descriptionTextView;

        public EventViewHolder(View view) {
            super(view);
            eventCard = view.findViewById(R.id.event_card);
            titleTextView = view.findViewById(R.id.event_card_title);
            organizerTextView = view.findViewById(R.id.event_card_organizer);
            descriptionTextView = view.findViewById(R.id.event_card_description);
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
        holder.descriptionTextView.setText(event.getDescription());

        holder.eventCard.setOnClickListener(v -> {
        });
    }

    @Override
    public int getItemCount() {
        return eventItemList.size();
    }
}
