package m3.eventplanner.fragments.invitation;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import m3.eventplanner.auth.TokenManager;
import m3.eventplanner.clients.ClientUtils;
import m3.eventplanner.databinding.FragmentInvitationBinding;

import m3.eventplanner.R;

public class InvitationFragment extends Fragment {

    private FragmentInvitationBinding binding;
    private InvitationViewModel viewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentInvitationBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(InvitationViewModel.class);
        viewModel.initialize(new ClientUtils(requireContext()));

        String token = getArguments() != null ? getArguments().getString("invitation-token") : null;
        if (token == null) {
            Toast.makeText(requireContext(), "No invitation token provided", Toast.LENGTH_SHORT).show();
            return;
        }

        TokenManager tokenManager = new TokenManager(requireContext());
        String email = tokenManager.getEmail();

        if (email == null) {
            Bundle bundle = new Bundle();
            bundle.putString("invitation-token", token);
            NavHostFragment.findNavController(this).navigate(R.id.loginFragment, bundle);
        } else {
            viewModel.processInvitation(token, email);

            viewModel.getSuccessMessage().observe(getViewLifecycleOwner(), msg -> {
                Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show();
                NavHostFragment.findNavController(this).navigate(R.id.userDetailsFragment);
            });

            viewModel.getError().observe(getViewLifecycleOwner(), err -> {
                Toast.makeText(requireContext(), err, Toast.LENGTH_LONG).show();
                if (err.contains("401") || err.contains("Unauthorized")) {
                    tokenManager.clearToken();
                    Bundle bundle = new Bundle();
                    bundle.putString("invitation-token", token);
                    NavHostFragment.findNavController(this).navigate(R.id.loginFragment, bundle);
                }
            });
        }
    }
}
