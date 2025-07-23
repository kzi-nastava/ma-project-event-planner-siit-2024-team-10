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
import m3.eventplanner.auth.TokenManager;
import m3.eventplanner.databinding.FragmentChatBinding;
import m3.eventplanner.models.GetMessageDTO;

import java.util.ArrayList;

public class ChatFragment extends Fragment {

    private FragmentChatBinding binding;
    private ChatViewModel viewModel;
    private ChatAdapter adapter;

    private int senderId;
    private int receiverId;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            receiverId = getArguments().getInt("receiverId");
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

        viewModel = new ViewModelProvider(this).get(ChatViewModel.class);
        viewModel.initialize(new m3.eventplanner.clients.ClientUtils(requireContext())); // ✅ OBAVEZNO pre korišćenja viewModel-a

        senderId = new TokenManager(requireContext()).getUserId();
        receiverId = requireArguments().getInt("receiverId");

        adapter = new ChatAdapter(new ArrayList<>(), senderId);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerView.setAdapter(adapter);

        setupObservers();
        setupListeners();

        viewModel.loadMessages(senderId, receiverId);
        viewModel.subscribeToSocket(receiverId);
    }


    private void setupObservers() {
        viewModel.getMessages().observe(getViewLifecycleOwner(), messages -> {
            adapter.setMessages(messages);
            binding.recyclerView.scrollToPosition(messages.size() - 1);
        });

        viewModel.getNewMessage().observe(getViewLifecycleOwner(), message -> {
            adapter.addMessage(message);
            binding.recyclerView.scrollToPosition(adapter.getItemCount() - 1);
        });

        viewModel.getError().observe(getViewLifecycleOwner(), error ->
                Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show()
        );
    }

    private void setupListeners() {
        binding.sendButton.setOnClickListener(v -> {
            String text = binding.messageInput.getText().toString().trim();
            if (!TextUtils.isEmpty(text)) {
                viewModel.sendMessage(senderId, receiverId, text);
                binding.messageInput.setText("");
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
