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
import m3.eventplanner.models.AgendaItem;
import androidx.navigation.Navigation;

public class AgendaItemListAdapter extends RecyclerView.Adapter<AgendaItemListAdapter.AgendaViewHolder> {

    private List<AgendaItem> agendaItemList;

    public AgendaItemListAdapter(List<AgendaItem> agendaItemList) {
        this.agendaItemList = agendaItemList;
    }

    public static class AgendaViewHolder extends RecyclerView.ViewHolder {
        public MaterialCardView agendaCard;
        public TextView nameTextView;
        public TextView descriptionTextView;
        public TextView startTimeTextView;
        public TextView endTimeTextView;
        public TextView locationTextView;

        public AgendaViewHolder(View view) {
            super(view);
            agendaCard = view.findViewById(R.id.agendaItemCard);
            nameTextView = view.findViewById(R.id.agendaItemName);
            descriptionTextView = view.findViewById(R.id.agendaItemDescription);
            startTimeTextView = view.findViewById(R.id.agendaItemStartTime);
            endTimeTextView = view.findViewById(R.id.agendaItemEndTime);
            locationTextView = view.findViewById(R.id.agendaItemLocation);
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
        AgendaItem agendaItem = agendaItemList.get(position);

        holder.nameTextView.setText(agendaItem.getName());
        holder.descriptionTextView.setText(agendaItem.getDescription());
        holder.startTimeTextView.setText(agendaItem.getStartTime());
        holder.endTimeTextView.setText(agendaItem.getEndTime());
        holder.locationTextView.setText(agendaItem.getLocation());
    }

    @Override
    public int getItemCount() {
        return agendaItemList.size();
    }
}
