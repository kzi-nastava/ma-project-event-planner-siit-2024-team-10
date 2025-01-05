package m3.eventplanner.fragments.event;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import m3.eventplanner.R;
import m3.eventplanner.databinding.FragmentEventDetailsBinding;
import m3.eventplanner.databinding.FragmentOpenEventReportBinding;

public class OpenEventReportFragment extends Fragment {
    private FragmentOpenEventReportBinding binding;
    private OpenEventReportViewModel viewModel;

    public OpenEventReportFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentOpenEventReportBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getArguments() != null) {
            int eventId = getArguments().getInt("selectedEventId");
            viewModel = new ViewModelProvider(this).get(OpenEventReportViewModel.class);
            viewModel.initialize(requireContext(),eventId);
        }

        binding.exportToPdfButton.setOnClickListener(v->{
            viewModel.exportToPdf();
        });

        viewModel.getError().observe(getViewLifecycleOwner(), error ->
                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show()
        );

        viewModel.getSuccessMessage().observe(getViewLifecycleOwner(), message ->
                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show()
        );
    }
}