package m3.eventplanner.fragments.block;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import m3.eventplanner.R;
import m3.eventplanner.clients.ClientUtils;

public class BlockDialogFragment extends DialogFragment {
    private TextView blockedText, blockTitle;
    private Button cancelButton, blockButton;
    private int blockerId, toBeBlockedId;
    private boolean blocked;
    private BlockDialogViewModel viewModel;
    private ClientUtils clientUtils;

    public static BlockDialogFragment newInstance(int blockerId, int toBeBlockedId, boolean blocked) {
        BlockDialogFragment fragment = new BlockDialogFragment();
        Bundle args = new Bundle();
        args.putInt("blockerId", blockerId);
        args.putInt("toBeBlockedId", toBeBlockedId);
        args.putBoolean("blocked", blocked);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_block_user, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        viewModel = new ViewModelProvider(this).get(BlockDialogViewModel.class);
        clientUtils = new ClientUtils(requireContext());
        viewModel.initialize(clientUtils);

        blockTitle = view.findViewById(R.id.blockTitle);
        blockedText = view.findViewById(R.id.blockText);
        cancelButton = view.findViewById(R.id.cancelBtn);
        blockButton = view.findViewById(R.id.blockBtn);

        if (getArguments() != null) {
            blockerId = getArguments().getInt("blockerId");
            toBeBlockedId = getArguments().getInt("toBeBlockedId");
            blocked = getArguments().getBoolean("blocked");
        }

        viewModel.getSuccessMessage().observe(getViewLifecycleOwner(), message -> {
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
            Bundle result = new Bundle();
            result.putBoolean("changed", true);
            getParentFragmentManager().setFragmentResult("block_result", result);

            dismiss();
        });

        viewModel.getErrorMessage().observe(getViewLifecycleOwner(), message -> {
            Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show();
            dismiss();
        });

        cancelButton.setOnClickListener(v -> dismiss());

        blockButton.setOnClickListener(v -> {
            viewModel.sendRequest(blocked, blockerId, toBeBlockedId);
        });

        checkBlockStatus();
    }

    private void checkBlockStatus(){
        if(blocked){
            blockTitle.setText(R.string.unblock_user);
            blockedText.setText(R.string.are_you_sure_you_want_to_unblock_this_user);
            blockButton.setText(R.string.unblock);
        } else{
            blockTitle.setText(R.string.block_user);
            blockedText.setText(R.string.are_you_sure_you_want_to_block_this_user);
            blockButton.setText(R.string.block);
        }
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
