package m3.eventplanner.fragments.report;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import m3.eventplanner.R;
import m3.eventplanner.clients.ClientUtils;
import m3.eventplanner.models.CreateAccountReportDTO;

public class ReportFragment extends DialogFragment {

    private EditText descriptionInput;
    private Button cancelButton, submitButton;
    private int reporterId, reporteeId;
    private ReportViewModel viewModel;
    private ClientUtils clientUtils;

    public static ReportFragment newInstance(int reporterId, int reporteeId) {
        ReportFragment fragment = new ReportFragment();
        Bundle args = new Bundle();
        args.putInt("reporterId", reporterId);
        args.putInt("reporteeId", reporteeId);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_report_user, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        viewModel = new ViewModelProvider(this).get(ReportViewModel.class);
        clientUtils = new ClientUtils(requireContext());
        viewModel.initialize(clientUtils);
        descriptionInput = view.findViewById(R.id.descriptionInput);
        cancelButton = view.findViewById(R.id.cancelBtn);
        submitButton = view.findViewById(R.id.submitReportBtn);

        if (getArguments() != null) {
            reporterId = getArguments().getInt("reporterId");
            reporteeId = getArguments().getInt("reporteeId");
        }

        viewModel.getSuccessMessage().observe(getViewLifecycleOwner(), message -> {
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
            dismiss();
        });

        viewModel.getErrorMessage().observe(getViewLifecycleOwner(), message -> {
            Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show();
            dismiss();
        });

        cancelButton.setOnClickListener(v -> dismiss());

        submitButton.setOnClickListener(v -> {
            String description = descriptionInput.getText().toString().trim();

            if (description.length() < 10) {
                descriptionInput.setError("Please enter at least 10 characters");
                return;
            }

            CreateAccountReportDTO dto = new CreateAccountReportDTO();
            dto.setDescription(description);
            dto.setReporterId(reporterId);
            dto.setReporteeId(reporteeId);
            viewModel.sendReport(dto);

        });
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null && dialog.getWindow() != null) {
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }
}
