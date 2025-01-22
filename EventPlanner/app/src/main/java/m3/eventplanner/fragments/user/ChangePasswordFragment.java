package m3.eventplanner.fragments.user;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;
import java.util.stream.Collectors;

import m3.eventplanner.R;
import m3.eventplanner.auth.TokenManager;
import m3.eventplanner.clients.ClientUtils;
import m3.eventplanner.databinding.FragmentChangePasswordBinding;
import m3.eventplanner.databinding.FragmentEditCompanyBinding;
import m3.eventplanner.fragments.eventtype.EventTypeFormViewModel;
import m3.eventplanner.models.GetOfferingCategoryDTO;

public class ChangePasswordFragment extends DialogFragment {
    private FragmentChangePasswordBinding binding;
    private ClientUtils clientUtils;
    private ChangePasswordViewModel viewModel;

    public ChangePasswordFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(ChangePasswordViewModel.class);
        clientUtils = new ClientUtils(requireContext());
        viewModel.initialize(clientUtils, new TokenManager(requireContext()));
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        binding = FragmentChangePasswordBinding.inflate(inflater);


        builder.setView(binding.getRoot())
                .setTitle("Change password")
                .setPositiveButton("Submit", (dialog, id) -> {

                })
                .setNegativeButton("Cancel", (dialog, id) -> dialog.dismiss());

        return builder.create();
    }
}