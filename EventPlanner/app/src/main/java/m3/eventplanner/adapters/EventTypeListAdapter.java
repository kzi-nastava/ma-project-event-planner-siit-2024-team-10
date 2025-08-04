package m3.eventplanner.adapters;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import m3.eventplanner.R;
import m3.eventplanner.fragments.eventtype.EventTypeFormFragment;
import m3.eventplanner.fragments.eventtype.EventTypesFragment;
import m3.eventplanner.fragments.eventtype.EventTypesViewModel;
import m3.eventplanner.models.AgendaItem;
import m3.eventplanner.models.GetEventTypeDTO;
import m3.eventplanner.models.GetOfferingCategoryDTO;

public class EventTypeListAdapter extends RecyclerView.Adapter<EventTypeListAdapter.EventTypeViewHolder>{
    private List<GetEventTypeDTO> eventTypeList;
    private EventTypesViewModel eventTypesViewModel;
    private EventTypesFragment eventTypesFragment;

    public EventTypeListAdapter(List<GetEventTypeDTO> eventTypeList, EventTypesFragment fragment) {
        this.eventTypesFragment=fragment;
        eventTypeList.sort(Comparator.comparing(GetEventTypeDTO::getName));
        this.eventTypeList = eventTypeList;
        this.eventTypesViewModel = new ViewModelProvider(fragment).get(EventTypesViewModel.class);
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

        holder.activateButton.setOnClickListener(v -> {
            eventTypesViewModel.activateEventType(eventType.getId());
        });

        holder.deactivateButton.setOnClickListener(v -> {
            eventTypesViewModel.deactivateEventType(eventType.getId());
        });

        holder.editButton.setOnClickListener(v ->{
            Bundle bundle = new Bundle();
            bundle.putParcelable("selectedEventType", eventType);
            EventTypeFormFragment dialog = new EventTypeFormFragment();
            dialog.setArguments(bundle);
            dialog.show(eventTypesFragment.getChildFragmentManager(), "EventTypeFormFragment");
        });
    }

    @Override
    public int getItemCount() {
        return eventTypeList.size();
    }
}
