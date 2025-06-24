package m3.eventplanner.fragments.user;

import android.media.session.MediaSession;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;

import m3.eventplanner.R;
import m3.eventplanner.adapters.EventListAdapter;
import m3.eventplanner.adapters.OfferingListAdapter;
import m3.eventplanner.auth.TokenManager;
import m3.eventplanner.clients.ClientUtils;
import m3.eventplanner.databinding.FragmentFavouritesBinding;
import m3.eventplanner.databinding.FragmentUserDetailsBinding;
import m3.eventplanner.fragments.event.EventDetailsViewModel;

public class FavouritesFragment extends Fragment {
    private FragmentFavouritesBinding binding;
    private FavouritesViewModel viewModel;
    private ClientUtils clientUtils;
    private EventListAdapter eventAdapter;
    private OfferingListAdapter offeringAdapter;

    public FavouritesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentFavouritesBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(FavouritesViewModel.class);
        TokenManager tokenManager = new TokenManager(requireContext());
        this.clientUtils = new ClientUtils(requireContext());
        viewModel.initialize(clientUtils,tokenManager.getAccountId());

        binding.eventRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.offeringRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        viewModel.getFavouriteEvents().observe(getViewLifecycleOwner(), pagedEvents -> {
            if (pagedEvents != null && !pagedEvents.getContent().equals(new ArrayList<>())) {
                eventAdapter = new EventListAdapter(pagedEvents.getContent());
                binding.eventRecyclerView.setAdapter(eventAdapter);
                eventAdapter.notifyDataSetChanged();
                binding.eventRecyclerView.setVisibility(View.VISIBLE);
                binding.noEventsFoundTextView.setVisibility(View.GONE);
                binding.eventPaginationBar.paginationCurrentPage.setText(String.valueOf("Page "+ (viewModel.currentEventPage + 1) +" of "+ viewModel.totalEventPages));
                binding.eventPaginationBar.totalNumberOfElements.setText(String.format("Total Elements: %d", viewModel.totalEventElements));
            } else {
                binding.noEventsFoundTextView.setVisibility(View.VISIBLE);
                binding.eventRecyclerView.setVisibility(View.GONE);
                binding.eventPaginationBar.getRoot().setVisibility(View.GONE);
            }
        });

        viewModel.getError().observe(getViewLifecycleOwner(), errorMessage -> {
            if (errorMessage != null && !errorMessage.isEmpty()) {
                Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
            }
        });

        binding.eventPaginationBar.paginationForwardButton.setOnClickListener(v -> {
            viewModel.fetchNextEventPage();
        });

        binding.eventPaginationBar.paginationBackButton.setOnClickListener(v -> {
            viewModel.fetchPreviousEventPage();
        });

        viewModel.fetchEventPage(0);
    }
}