package m3.eventplanner.fragments.comment;

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

import m3.eventplanner.adapters.CommentApprovalListAdapter;
import m3.eventplanner.clients.ClientUtils;
import m3.eventplanner.databinding.FragmentCommentApprovalBinding;

public class CommentApprovalFragment extends Fragment {
    private CommentApprovalViewModel viewModel;
    private ClientUtils clientUtils;
    private FragmentCommentApprovalBinding binding;

    public CommentApprovalFragment(){

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentCommentApprovalBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(CommentApprovalViewModel.class);
        clientUtils = new ClientUtils(requireContext());
        viewModel.initialize(clientUtils);

        setupObservers();
        viewModel.loadPendingComments();
    }

    private void setupObservers() {
        viewModel.getComments().observe(getViewLifecycleOwner(), comments -> {
            if (comments == null || comments.isEmpty()) {
                binding.noCommentsText.setVisibility(View.VISIBLE);
                binding.commentsRecyclerView.setVisibility(View.GONE);
            } else {
                binding.noCommentsText.setVisibility(View.GONE);
                binding.commentsRecyclerView.setVisibility(View.VISIBLE);
                binding.commentsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                binding.commentsRecyclerView.setAdapter(new CommentApprovalListAdapter(comments, this));
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
