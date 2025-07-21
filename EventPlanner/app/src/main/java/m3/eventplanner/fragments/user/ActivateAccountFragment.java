package m3.eventplanner.fragments.user;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavOptions;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import m3.eventplanner.R;
import m3.eventplanner.activities.MainActivity;
import m3.eventplanner.auth.TokenManager;
import m3.eventplanner.clients.ClientUtils;
import m3.eventplanner.fragments.invitation.InvitationViewModel;

public class ActivateAccountFragment extends Fragment {
    private ActivateAccountViewModel viewModel;

    public ActivateAccountFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_activate_account, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(ActivateAccountViewModel.class);
        viewModel.initialize(new ClientUtils(requireContext()));

        String token = getArguments() != null ? getArguments().getString("activation-token") : null;
        if (token == null) {
            Toast.makeText(requireContext(), "No activation token provided", Toast.LENGTH_SHORT).show();
            return;
        }

        TokenManager tokenManager = new TokenManager(requireContext());
        String email = tokenManager.getEmail();

        viewModel.getSuccessMessage().observe(getViewLifecycleOwner(), msg -> {
            Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show();
            NavOptions navOptions = new NavOptions.Builder()
                    .setPopUpTo(NavHostFragment.findNavController(this).getGraph().getStartDestinationId(), true)
                    .build();

            NavHostFragment.findNavController(this).navigate(R.id.loginFragment, null, navOptions);
        });

        viewModel.getError().observe(getViewLifecycleOwner(), err -> {
            Toast.makeText(requireContext(), err, Toast.LENGTH_LONG).show();
            NavOptions navOptions = new NavOptions.Builder()
                    .setPopUpTo(NavHostFragment.findNavController(this).getGraph().getStartDestinationId(), true)
                    .build();

            NavHostFragment.findNavController(this).navigate(R.id.homeScreenFragment, null, navOptions);
        });

        viewModel.activate(token);

    }
}