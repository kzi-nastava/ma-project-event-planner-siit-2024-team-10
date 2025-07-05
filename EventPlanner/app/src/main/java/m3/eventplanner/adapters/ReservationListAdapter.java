package m3.eventplanner.adapters;

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
import m3.eventplanner.fragments.reservation.ReservationConfirmationFragment;
import m3.eventplanner.fragments.reservation.ReservationConfirmationViewModel;
import m3.eventplanner.models.GetReservationDTO;

public class ReservationListAdapter extends RecyclerView.Adapter<ReservationListAdapter.ReservationViewHolder> {

    private List<GetReservationDTO> reservationList;
    private ReservationConfirmationViewModel reservationsViewModel;

    public ReservationListAdapter(List<GetReservationDTO> reservationList, ReservationConfirmationFragment fragment) {
        this.reservationList = reservationList;
        this.reservationList.sort(Comparator.comparing(r -> r.getService().getName())); // Sort by service name
        this.reservationsViewModel = new ViewModelProvider(fragment).get(ReservationConfirmationViewModel.class);
    }

    public static class ReservationViewHolder extends RecyclerView.ViewHolder {
        MaterialCardView reservationCard;
        TextView serviceNameTextView;
        TextView eventInfoTextView;
        TextView startTimeTextView;
        TextView endTimeTextView;
        ImageButton acceptButton;
        ImageButton denyButton;

        public ReservationViewHolder(View view) {
            super(view);
            reservationCard = view.findViewById(R.id.reservationCard);
            serviceNameTextView = view.findViewById(R.id.serviceName);
            eventInfoTextView = view.findViewById(R.id.eventInfo);
            startTimeTextView = view.findViewById(R.id.startTime);
            endTimeTextView = view.findViewById(R.id.endTime);
            acceptButton = view.findViewById(R.id.acceptButton);
            denyButton = view.findViewById(R.id.denyButton);
        }
    }

    @NonNull
    @Override
    public ReservationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_reservation, parent, false);
        return new ReservationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReservationViewHolder holder, int position) {
        GetReservationDTO reservation = reservationList.get(position);

        holder.serviceNameTextView.setText(reservation.getService().getName());
        holder.eventInfoTextView.setText(reservation.getEvent().getName() + " • " + reservation.getEvent().getDate());
        holder.startTimeTextView.setText("Start: " + reservation.getStartTime());
        holder.endTimeTextView.setText("End: " + reservation.getEndTime());

        holder.acceptButton.setOnClickListener(v -> {
            reservationsViewModel.acceptReservation(reservation.getId(), null);
        });

        holder.denyButton.setOnClickListener(v -> {
            reservationsViewModel.denyReservation(reservation.getId(), null);
        });
    }

    @Override
    public int getItemCount() {
        return reservationList.size();
    }
}
