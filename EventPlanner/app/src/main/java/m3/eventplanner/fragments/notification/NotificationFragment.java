package m3.eventplanner.fragments.notification;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import m3.eventplanner.R;
import m3.eventplanner.adapters.NotificationListAdapter;
import m3.eventplanner.auth.TokenManager;
import m3.eventplanner.clients.ClientUtils;

public class NotificationFragment extends Fragment {
    private SwitchCompat toggle;
    private RecyclerView contentRecyclerView;
    private TextView noCardsFoundTextView, pageNumber, totalNumberOfElements;
    private View paginationView;
    private NotificationViewModel notificationViewModel;
    private NotificationListAdapter notificationListAdapter;
    private ClientUtils clientUtils;
    private Integer accountId;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        clientUtils = new ClientUtils(requireContext());
        accountId = new TokenManager(requireContext()).getAccountId();
        notificationViewModel = new ViewModelProvider(this).get(NotificationViewModel.class);
        notificationViewModel.initialize(clientUtils, accountId);
        View rootView = inflater.inflate(R.layout.fragment_notification_panel, container, false);
        initializeUIElements(rootView);
        contentRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        notificationViewModel.getNotifications().observe(getViewLifecycleOwner(), pagedNotifications -> {
            if (pagedNotifications != null && !pagedNotifications.getContent().equals(new ArrayList<>())) {
                notificationListAdapter = new NotificationListAdapter(pagedNotifications.getContent());
                contentRecyclerView.setAdapter(notificationListAdapter);
                notificationListAdapter.notifyDataSetChanged();
                contentRecyclerView.setVisibility(View.VISIBLE);
                noCardsFoundTextView.setVisibility(View.GONE);
                paginationView.setVisibility(View.VISIBLE);
                pageNumber.setText(String.valueOf("Page " + (notificationViewModel.currentPage + 1) + " of " + notificationViewModel.totalPages));
                totalNumberOfElements.setText(String.format("Total Elements : %d", notificationViewModel.totalElements));
            } else {
                handleNoDataFound();
            }
        });

        notificationViewModel.getError().observe(getViewLifecycleOwner(), errorMessage -> {
            if (errorMessage != null && !errorMessage.isEmpty()) {
                Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
            }
        });

        notificationViewModel.fetchPage(0);

        setUpPaginationButtons(view);
    }

    private void initializeUIElements(View rootView) {
        contentRecyclerView = rootView.findViewById(R.id.notification_list);
        noCardsFoundTextView = rootView.findViewById(R.id.noNotificationsFoundTextView);
        paginationView = rootView.findViewById(R.id.notification_pagination);
        toggle = rootView.findViewById(R.id.toggle_notifications);
    }

    private void setUpPaginationButtons(View rootView) {
        Button paginationForwardButton = rootView.findViewById(R.id.paginationForwardButton);
        Button paginationBackButton = rootView.findViewById(R.id.paginationBackButton);
        pageNumber = rootView.findViewById(R.id.paginationCurrentPage);
        totalNumberOfElements = rootView.findViewById(R.id.totalNumberOfElements);

        paginationForwardButton.setOnClickListener(v -> {
            notificationViewModel.fetchNextPage();
        });

        paginationBackButton.setOnClickListener(v -> {
            notificationViewModel.fetchPreviousPage();
        });
    }

    private void handleNoDataFound() {
        noCardsFoundTextView.setText(R.string.no_notifications);
        noCardsFoundTextView.setVisibility(View.VISIBLE);
        contentRecyclerView.setVisibility(View.GONE);
        paginationView.setVisibility(View.GONE);
    }

}
