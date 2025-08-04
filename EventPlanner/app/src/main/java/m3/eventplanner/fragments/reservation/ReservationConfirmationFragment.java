package m3.eventplanner.fragments.reservation;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import m3.eventplanner.adapters.ReservationListAdapter;
import m3.eventplanner.auth.TokenManager;
import m3.eventplanner.clients.ClientUtils;
import m3.eventplanner.databinding.FragmentReservationConfirmationBinding;

public class ReservationConfirmationFragment extends Fragment {
    private ReservationConfirmationViewModel viewModel;
    private ClientUtils clientUtils;
    private FragmentReservationConfirmationBinding binding;

    public ReservationConfirmationFragment() {
        // Required empty constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentReservationConfirmationBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(ReservationConfirmationViewModel.class);
        clientUtils = new ClientUtils(requireContext());
        int accountId = new TokenManager(requireContext()).getAccountId();;
        viewModel.initialize(clientUtils, accountId);

        setupObservers();
        viewModel.loadPendingReservations();
    }

    private void setupObservers() {
        viewModel.getReservations().observe(getViewLifecycleOwner(), reservations -> {
            if (reservations == null || reservations.isEmpty()) {
                binding.noReservationsText.setVisibility(View.VISIBLE);
                binding.reservationsRecyclerView.setVisibility(View.GONE);
            } else {
                binding.noReservationsText.setVisibility(View.GONE);
                binding.reservationsRecyclerView.setVisibility(View.VISIBLE);
                binding.reservationsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                binding.reservationsRecyclerView.setAdapter(new ReservationListAdapter(reservations, this));
            }
        });

        viewModel.getError().observe(getViewLifecycleOwner(), error -> {
            if (error != null && !error.isEmpty()) {
                Toast.makeText(getContext(), error, Toast.LENGTH_LONG).show();
            }
        });

        viewModel.getSuccessMessage().observe(getViewLifecycleOwner(), message -> {
            if (message != null && !message.isEmpty()) {
                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
