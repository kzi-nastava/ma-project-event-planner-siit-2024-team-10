package m3.eventplanner.adapters;
import android.app.AlertDialog;
import android.content.DialogInterface;
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

import java.util.Comparator;
import java.util.List;
import m3.eventplanner.R;
import m3.eventplanner.fragments.event.AgendaItemFormFragment;
import m3.eventplanner.fragments.event.EventDetailsFragment;
import m3.eventplanner.fragments.event.EventDetailsViewModel;
import m3.eventplanner.fragments.eventtype.EventTypeFormFragment;
import m3.eventplanner.fragments.eventtype.EventTypesViewModel;
import m3.eventplanner.models.AgendaItem;
import m3.eventplanner.models.GetAgendaItemDTO;
import m3.eventplanner.models.GetEventTypeDTO;

import androidx.navigation.Navigation;

public class AgendaItemListAdapter extends RecyclerView.Adapter<AgendaItemListAdapter.AgendaViewHolder> {

    private List<GetAgendaItemDTO> agendaItemList;
    private EventDetailsFragment eventDetailsFragment;
    private EventDetailsViewModel eventDetailsViewModel;
    private boolean isOwner;

    public AgendaItemListAdapter(List<GetAgendaItemDTO> agendaItemList, EventDetailsFragment fragment, boolean isOwner) {
        this.agendaItemList = agendaItemList;
        this.eventDetailsFragment=fragment;
        this.eventDetailsViewModel = new ViewModelProvider(fragment).get(EventDetailsViewModel.class);
        this.isOwner=isOwner;
    }

    public static class AgendaViewHolder extends RecyclerView.ViewHolder {
        public MaterialCardView agendaCard;
        public TextView nameTextView;
        public TextView descriptionTextView;
        public TextView startTimeTextView;
        public TextView endTimeTextView;
        public TextView locationTextView;
        public ImageButton editButton;
        public ImageButton deleteButton;

        public AgendaViewHolder(View view) {
            super(view);
            agendaCard = view.findViewById(R.id.agendaItemCard);
            nameTextView = view.findViewById(R.id.agendaItemName);
            descriptionTextView = view.findViewById(R.id.agendaItemDescription);
            startTimeTextView = view.findViewById(R.id.agendaItemStartTime);
            endTimeTextView = view.findViewById(R.id.agendaItemEndTime);
            locationTextView = view.findViewById(R.id.agendaItemLocation);
            editButton=view.findViewById(R.id.edit_agenda_item_button);
            deleteButton=view.findViewById(R.id.delete_agenda_item_button);
        }
    }

    @NonNull
    @Override
    public AgendaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.agenda_item_card, parent, false);
        return new AgendaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AgendaViewHolder holder, int position) {
        GetAgendaItemDTO agendaItem = agendaItemList.get(position);

        holder.nameTextView.setText(agendaItem.getName());
        holder.descriptionTextView.setText(agendaItem.getDescription());
        holder.startTimeTextView.setText(agendaItem.getStartTime().toString());
        holder.endTimeTextView.setText(agendaItem.getEndTime().toString());
        holder.locationTextView.setText(agendaItem.getLocation());
        if(isOwner){
            holder.editButton.setVisibility(View.VISIBLE);
            holder.editButton.setOnClickListener(v ->{
                Bundle bundle = new Bundle();
                bundle.putParcelable("selectedAgendaItem", agendaItem);
                AgendaItemFormFragment dialog = new AgendaItemFormFragment();
                dialog.setArguments(bundle);
                dialog.show(eventDetailsFragment.getChildFragmentManager(), "AgendaItemFormFragment");
            });


        }
    }

    @Override
    public int getItemCount() {
        return agendaItemList.size();
    }
}
