package m3.eventplanner.adapters;

import android.graphics.Color;
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
import java.util.Collection;
import m3.eventplanner.R;
import m3.eventplanner.models.GetNotificationDTO;

public class NotificationListAdapter extends RecyclerView.Adapter<NotificationListAdapter.NotificationViewHolder> {

    private Collection<GetNotificationDTO> notificationItemList;

    public NotificationListAdapter(Collection<GetNotificationDTO> notificationItemList) {
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
        GetNotificationDTO notification = (GetNotificationDTO) notificationItemList.toArray()[position];

        holder.titleTextView.setText(notification.getTitle());
        holder.descriptionTextView.setText(notification.getContent());
        String formattedDate;
        try {
            LocalDateTime dateTime = LocalDateTime.parse(notification.getDate());
            formattedDate = dateTime.format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM));
        } catch (Exception e) {
            formattedDate = notification.getDate();
        }
        holder.dateAndTimeTextView.setText(formattedDate);

        if (!notification.isRead()) {
            holder.notificationCard.setCardBackgroundColor(Color.WHITE);
        } else {
            holder.notificationCard.setCardBackgroundColor(Color.LTGRAY);
        }
    }

    @Override
    public int getItemCount() {
        return notificationItemList.size();
    }
}