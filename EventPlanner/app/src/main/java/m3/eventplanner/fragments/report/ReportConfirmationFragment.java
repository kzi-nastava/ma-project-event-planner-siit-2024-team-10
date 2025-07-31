package m3.eventplanner.fragments.report;

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

import m3.eventplanner.adapters.ReportListAdapter;
import m3.eventplanner.auth.TokenManager;
import m3.eventplanner.clients.ClientUtils;
import m3.eventplanner.databinding.FragmentReportConfirmationBinding;

public class ReportConfirmationFragment extends Fragment {
    private ReportConfirmationViewModel viewModel;
    private ClientUtils clientUtils;
    private FragmentReportConfirmationBinding binding;

    public ReportConfirmationFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentReportConfirmationBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(ReportConfirmationViewModel.class);
        clientUtils = new ClientUtils(requireContext());
        int accountId = new TokenManager(requireContext()).getAccountId();
        viewModel.initialize(clientUtils, accountId);

        setupObservers();
        viewModel.loadPendingReports();
    }

    private void setupObservers() {
        viewModel.getReports().observe(getViewLifecycleOwner(), reports -> {
            if (reports == null || reports.isEmpty()) {
                binding.noReportsText.setVisibility(View.VISIBLE);
                binding.reportsRecyclerView.setVisibility(View.GONE);
            } else {
                binding.noReportsText.setVisibility(View.GONE);
                binding.reportsRecyclerView.setVisibility(View.VISIBLE);
                binding.reportsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                binding.reportsRecyclerView.setAdapter(new ReportListAdapter(reports, this));
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
