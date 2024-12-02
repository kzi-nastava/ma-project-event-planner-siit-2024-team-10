package m3.eventplanner.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.card.MaterialCardView;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.List;
import m3.eventplanner.R;
import m3.eventplanner.models.Notification;

public class NotificationListAdapter extends RecyclerView.Adapter<NotificationListAdapter.NotificationViewHolder> {

    private List<Notification> notificationItemList;

    public NotificationListAdapter(List<Notification> notificationItemList) {
        this.notificationItemList = notificationItemList;
    }

    public static class NotificationViewHolder extends RecyclerView.ViewHolder {
        public MaterialCardView notificationCard;
        public TextView titleTextView;
        public TextView descriptionTextView;
        public TextView dateAndTimeTextView;

        public NotificationViewHolder(View view) {
            super(view);
            notificationCard = view.findViewById(R.id.notification_card);
            titleTextView = view.findViewById(R.id.notification_title);
            descriptionTextView = view.findViewById(R.id.notification_description);
            dateAndTimeTextView = view.findViewById(R.id.notification_date_time);
        }
    }

    @NonNull
    @Override
    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.notification, parent, false);
        return new NotificationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position) {
        Notification notification = notificationItemList.get(position);

        holder.titleTextView.setText(notification.getTitle());
        holder.descriptionTextView.setText(notification.getContent());
        String formattedDate = formatDateTime(notification.getDate());
        holder.dateAndTimeTextView.setText(formattedDate);
    }
    private String formatDateTime(LocalDateTime dateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM);
        return dateTime.format(formatter);
    }

    @Override
    public int getItemCount() {
        return notificationItemList.size();
    }
}