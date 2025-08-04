package m3.eventplanner.fragments.chat;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import m3.eventplanner.adapters.ChatAdapter;
import m3.eventplanner.adapters.ChatContactAdapter;
import m3.eventplanner.auth.TokenManager;
import m3.eventplanner.databinding.FragmentChatBinding;
import m3.eventplanner.fragments.block.BlockDialogFragment;
import m3.eventplanner.fragments.report.CreateReportFragment;

import java.util.ArrayList;

public class ChatFragment extends Fragment {

    private FragmentChatBinding binding;
    private ChatViewModel viewModel;
    private ChatAdapter adapter;
    private ChatContactAdapter contactAdapter;

    private int senderId;
    private int receiverId;
    private Boolean currentIsBlocking = null;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null && getArguments().containsKey("receiverId")) {
            receiverId = getArguments().getInt("receiverId");
        } else {
            receiverId = -1;
        }
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentChatBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        contactAdapter = new ChatContactAdapter(contact -> {
            receiverId = contact.getUser();
            adapter.clear();
            viewModel.loadMessages(senderId, receiverId);
            viewModel.subscribeToSocket(receiverId);
            viewModel.checkBlockStatus(senderId, receiverId);
        });

        binding.contactRecyclerView.setLayoutManager(
                new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        binding.contactRecyclerView.setAdapter(contactAdapter);

        viewModel = new ViewModelProvider(this).get(ChatViewModel.class);
        viewModel.initialize(new m3.eventplanner.clients.ClientUtils(requireContext()));

        senderId = new TokenManager(requireContext()).getAccountId();

        adapter = new ChatAdapter(new ArrayList<>(), senderId);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerView.setAdapter(adapter);

        setupObservers();
        setupListeners();

        viewModel.loadContacts(senderId);

        if (receiverId != -1) {
            viewModel.loadMessages(senderId, receiverId);
            viewModel.subscribeToSocket(receiverId);
            viewModel.checkBlockStatus(senderId, receiverId);
        }

        getParentFragmentManager().setFragmentResultListener("block_result", getViewLifecycleOwner(),
                (requestKey, bundle) -> {
                    boolean changed = bundle.getBoolean("changed", false);
                    if (changed) {
                        viewModel.checkBlockStatus(senderId, receiverId);
                    }
                });
    }

    private void setupObservers() {
        viewModel.getContacts().observe(getViewLifecycleOwner(), contactAdapter::setContacts);

        viewModel.getMessages().observe(getViewLifecycleOwner(), messages -> {
            adapter.setMessages(messages);
            binding.recyclerView.scrollToPosition(messages.size() - 1);
        });

        viewModel.getNewMessage().observe(getViewLifecycleOwner(), message -> {
            adapter.addMessage(message);
            binding.recyclerView.scrollToPosition(adapter.getItemCount() - 1);
            viewModel.loadContacts(senderId);
        });

        viewModel.getError().observe(getViewLifecycleOwner(), error ->
                Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show()
        );

        viewModel.getBlockStatus().observe(getViewLifecycleOwner(), blockStatus -> {
            currentIsBlocking = blockStatus.isBlockingThem();
            if (blockStatus.isBlockedByThem()) {
                binding.blockedOverlay.setVisibility(View.VISIBLE);
                binding.messageInput.setEnabled(false);
                binding.sendButton.setEnabled(false);
                binding.blockedTxt.setText("You have been blocked by this user.");
            } else if (blockStatus.isBlockingThem()) {
                binding.blockedOverlay.setVisibility(View.VISIBLE);
                binding.messageInput.setEnabled(false);
                binding.sendButton.setEnabled(false);
                binding.blockedTxt.setText("You have blocked this user.");
            } else {
                binding.blockedOverlay.setVisibility(View.GONE);
                binding.messageInput.setEnabled(true);
                binding.sendButton.setEnabled(true);
            }
        });
    }
    private void setupListeners() {
        binding.sendButton.setOnClickListener(v -> {
            String text = binding.messageInput.getText().toString().trim();
            if (!TextUtils.isEmpty(text)) {
                viewModel.sendMessage(senderId, receiverId, text);
                binding.messageInput.setText("");
                viewModel.loadContacts(senderId);
            }
        });

        binding.reportBtn.setOnClickListener(v -> {
            if (receiverId == -1) {
                Toast.makeText(requireContext(), "No user selected to report.", Toast.LENGTH_SHORT).show();
                return;
            }

            CreateReportFragment dialog = CreateReportFragment.newInstance(senderId, receiverId);
            dialog.show(getParentFragmentManager(), "report_dialog");
        });
        binding.blockBtn.setOnClickListener(v -> {
            if (receiverId == -1) {
                Toast.makeText(requireContext(), "No user selected to block.", Toast.LENGTH_SHORT).show();
                return;
            }

            BlockDialogFragment dialog = BlockDialogFragment.newInstance(senderId, receiverId, currentIsBlocking);
            dialog.show(getParentFragmentManager(), "block_dialog");
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
