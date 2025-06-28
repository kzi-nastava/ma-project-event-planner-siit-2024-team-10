package m3.eventplanner.fragments.notification;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import m3.eventplanner.R;
import m3.eventplanner.adapters.NotificationListAdapter;
import m3.eventplanner.models.Notification;

public class NotificationFragment extends Fragment {

    private RecyclerView contentRecyclerView;
    private TextView noCardsFoundTextView;
    private View paginationView;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_notification_panel, container, false);
        initializeUIElements(rootView);
        setUpRecyclerView();
        displayNotifications();
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private void initializeUIElements(View rootView) {
        contentRecyclerView = rootView.findViewById(R.id.notification_list);
        noCardsFoundTextView = rootView.findViewById(R.id.noNotificationsFoundTextView);
        paginationView = rootView.findViewById(R.id.notification_pagination);
    }

    private void setUpRecyclerView() {
        contentRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    private void displayNotifications() {
        List<Notification> notifications = getNotifications();
        NotificationListAdapter adapter = new NotificationListAdapter(notifications);
        updateRecyclerView(notifications, adapter);
    }

    private <T> void updateRecyclerView(List<T> data, RecyclerView.Adapter<?> adapter) {
        if (data == null || data.isEmpty()) {
            noCardsFoundTextView.setVisibility(View.VISIBLE);
            contentRecyclerView.setVisibility(View.GONE);
            paginationView.setVisibility(View.GONE);
        } else {
            noCardsFoundTextView.setVisibility(View.GONE);
            contentRecyclerView.setVisibility(View.VISIBLE);
            contentRecyclerView.setAdapter(adapter);
            paginationView.setVisibility(View.VISIBLE);
        }
    }

    private List<Notification> getNotifications() {
        List<Notification> notifications = new ArrayList<>();

        notifications.add(new Notification(1, "Rated Event", false, "John J. rated 'Mary and John's wedding' 4 stars.", LocalDateTime.now().minusDays(1)));
        notifications.add(new Notification(2, "Rated Event", false, "Kim L. rated 'Popular band concert' 2 stars.", LocalDateTime.now().minusHours(2)));

        notifications.add(new Notification(3, "New Comment", false, "Mia O. commented 'Tech innovations convention': 'This convention was great!'", LocalDateTime.now().minusMinutes(30)));
        notifications.add(new Notification(4, "New Comment", false, "Alexander P. commented 'Popular band concert':'The music was horrible.", LocalDateTime.now().minusDays(3)));

        notifications.add(new Notification(5, "Service Reminder", false, "Your booked service 'Digital Marketing Workshop' starts in 1 hour.", LocalDateTime.now().minusMinutes(45)));
        notifications.add(new Notification(6, "Service Reminder", true, "Your booked service 'Personal Trainer' starts in 1 hour.", LocalDateTime.now().minusMinutes(50)));

        notifications.add(new Notification(7, "Service Reminder", true, "Your booked service 'Digital Marketing Workshop' starts in 1 hour.", LocalDateTime.now().minusMinutes(45)));
        notifications.add(new Notification(8, "Service Reminder", true, "Your booked service 'Personal Trainer' starts in 1 hour.", LocalDateTime.now().minusMinutes(50)));

        return notifications;
    }
}
