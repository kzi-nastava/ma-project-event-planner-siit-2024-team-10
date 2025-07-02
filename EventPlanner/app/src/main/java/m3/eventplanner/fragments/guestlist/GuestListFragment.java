package m3.eventplanner.fragments.guestlist;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import java.util.ArrayList;
import java.util.List;

import m3.eventplanner.clients.ClientUtils;
import m3.eventplanner.databinding.FragmentGuestListBinding;

public class GuestListFragment extends Fragment {
    private FragmentGuestListBinding binding;
    private GuestListViewModel viewModel;
    private ClientUtils clientUtils;
    private List<EditText> emailFields = new ArrayList<>();
    private int maxParticipants = 0;
    private int eventId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentGuestListBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(GuestListViewModel.class);
        clientUtils = new ClientUtils(requireContext());

        Bundle args = getArguments();
        if (args != null) {
            eventId = args.getInt("eventId");
        }

        viewModel.initialize(clientUtils, eventId);
        viewModel.getEventById(eventId);
        viewModel.loadGuestList();

        setUpObservers();

        binding.toggleFormBtn.setOnClickListener(v -> toggleFormVisibility());
        binding.addGuestBtn.setOnClickListener(v -> addGuestField());
        binding.submitBtn.setOnClickListener(v -> submitGuestList());

        addGuestField();
    }

    private void setUpObservers() {
        viewModel.getEvent().observe(getViewLifecycleOwner(), event -> {
            maxParticipants = event.getMaxParticipants();
        });

        viewModel.getCurrentGuestList().observe(getViewLifecycleOwner(), guests -> {
            String display = guests.getGuests().isEmpty() ? "None yet" : TextUtils.join(", ", guests.getGuests());
            binding.currentGuestListText.setText("Current guest list: " + display);
        });

        viewModel.getError().observe(getViewLifecycleOwner(), error -> {
            Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
        });

        viewModel.getSuccessMessage().observe(getViewLifecycleOwner(), msg -> {
            Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
            requireActivity().onBackPressed();
        });
    }

    private void toggleFormVisibility() {
        boolean isVisible = binding.guestContainer.getVisibility() == View.VISIBLE;
        binding.guestContainer.setVisibility(isVisible ? View.GONE : View.VISIBLE);
        binding.toggleFormBtn.setText(isVisible ? "Add guests" : "Hide guest form");
    }

    private void addGuestField() {
        int alreadyInvited = viewModel.getCurrentGuestList().getValue() != null
                ? viewModel.getCurrentGuestList().getValue().getGuests().size() : 0;

        if (emailFields.size() + alreadyInvited >= maxParticipants) {
            Toast.makeText(requireContext(), "Max " + maxParticipants + " guests allowed.", Toast.LENGTH_SHORT).show();
            return;
        }

        View guestView = LayoutInflater.from(requireContext()).inflate(m3.eventplanner.R.layout.item_guest_field, binding.guestContainer, false);
        EditText emailInput = guestView.findViewById(m3.eventplanner.R.id.emailInput);
        ImageButton removeBtn = guestView.findViewById(m3.eventplanner.R.id.removeBtn);

        removeBtn.setOnClickListener(v -> {
            binding.guestContainer.removeView(guestView);
            emailFields.remove(emailInput);
        });

        emailFields.add(emailInput);
        binding.guestContainer.addView(guestView);
    }

    private void submitGuestList() {
        List<String> emails = new ArrayList<>();
        for (EditText field : emailFields) {
            String email = field.getText().toString().trim();
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                field.setError("Invalid email");
                return;
            }
            emails.add(email);
        }

        viewModel.sendGuestInvites(eventId, emails);
    }
}
