package m3.eventplanner.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;

import java.util.List;
import java.util.stream.Collectors;

import m3.eventplanner.R;
import m3.eventplanner.models.AgendaItem;
import m3.eventplanner.models.GetEventTypeDTO;
import m3.eventplanner.models.GetOfferingCategoryDTO;

public class EventTypeListAdapter extends RecyclerView.Adapter<EventTypeListAdapter.EventTypeViewHolder>{
    private List<GetEventTypeDTO> eventTypeList;

    public EventTypeListAdapter(List<GetEventTypeDTO> eventTypeList) {
        this.eventTypeList = eventTypeList;
    }

    public static class EventTypeViewHolder extends RecyclerView.ViewHolder {
        public MaterialCardView eventTypeCard;
        public TextView nameTextView;
        public TextView descriptionTextView;
        public TextView categoriesTextView;
        public ImageButton editButton, activateButton, deactivateButton;

        public EventTypeViewHolder(View view) {
            super(view);
            eventTypeCard = view.findViewById(R.id.eventTypeCard);
            nameTextView = view.findViewById(R.id.eventTypeName);
            descriptionTextView = view.findViewById(R.id.eventTypeDescription);
            categoriesTextView = view.findViewById(R.id.eventTypeCategories);
            editButton = view.findViewById(R.id.edit_event_type_button);
            activateButton = view.findViewById(R.id.activate_event_type_button);
            deactivateButton = view.findViewById(R.id.deactivate_event_type_button);
        }
    }

    @NonNull
    @Override
    public EventTypeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.event_type_card, parent, false);
        return new EventTypeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventTypeViewHolder holder, int position) {
        GetEventTypeDTO eventType = eventTypeList.get(position);

        holder.nameTextView.setText(eventType.getName());
        holder.descriptionTextView.setText(eventType.getDescription());
        String categories = eventType.getRecommendedCategories().stream()
                .map(GetOfferingCategoryDTO::getName)
                .collect(Collectors.joining(", "));
        holder.categoriesTextView.setText(categories);

        if(eventType.isActive())
            holder.activateButton.setVisibility(View.GONE);
        else
            holder.deactivateButton.setVisibility(View.GONE);
    }

    @Override
    public int getItemCount() {
        return eventTypeList.size();
    }
}
