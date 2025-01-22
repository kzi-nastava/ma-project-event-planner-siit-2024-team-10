package m3.eventplanner.fragments.user;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;

import m3.eventplanner.R;
import m3.eventplanner.activities.MainActivity;
import m3.eventplanner.auth.TokenManager;
import m3.eventplanner.clients.ClientUtils;
import m3.eventplanner.databinding.FragmentChangePasswordBinding;
import m3.eventplanner.fragments.event.CreateEventFragment;
import m3.eventplanner.models.ChangePasswordDTO;

public class ChangePasswordFragment extends DialogFragment {
    private FragmentChangePasswordBinding binding;
    private ClientUtils clientUtils;
    private ChangePasswordViewModel viewModel;
    private AlertDialog dialog;

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

        setupValidation();


        builder.setView(binding.getRoot())
                .setTitle("Change password")
                .setPositiveButton("Submit", null)
                .setNegativeButton("Cancel", (dialog, id) -> dialog.dismiss());
        dialog = builder.create();

        dialog.setOnShowListener(d->{
            Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            positiveButton.setOnClickListener(view -> {
                if(!isFormValid())
                    return;
                String oldPassword = binding.oldPassword.getEditText().getText().toString().trim();
                String newPassword = binding.newPassword.getEditText().getText().toString().trim();
                ChangePasswordDTO changePasswordDTO = new ChangePasswordDTO(oldPassword,newPassword);
                viewModel.changePassword(changePasswordDTO);
            });
        });

        viewModel.getError().observe(this, error ->{
                    binding.oldPassword.setError(error);
                }
        );

        viewModel.getSuccessMessage().observe(this, message ->{
                    Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    ((MainActivity)requireActivity()).logout();
                }
        );

        return dialog;
    }

    private boolean isFormValid(){
        boolean isValid=true;
        if(!validatePasswordField(binding.oldPassword))
            isValid=false;
        if(!validatePasswordField(binding.newPassword))
            isValid=false;
        if(!validatePasswordField(binding.confirmPassword))
            isValid=false;
        if(!validateMatchingPasswords())
            isValid=false;
        return isValid;
    }


    private void setupValidation(){
        binding.oldPassword.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                validatePasswordField(binding.oldPassword);
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });
        binding.newPassword.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                validateMatchingPasswords();
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });
        binding.confirmPassword.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                validateMatchingPasswords();
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });
    }
    private boolean validatePasswordField(TextInputLayout textInputLayout) {
        if (TextUtils.isEmpty(textInputLayout.getEditText().getText())) {
            textInputLayout.setError("Required field");
            return false;
        }
        if(textInputLayout.getEditText().getText().toString().trim().length()<8){
            textInputLayout.setError("Password must be at least 8 characters long");
            return false;
        }
        textInputLayout.setError(null);
        return true;
    }
    private boolean validateMatchingPasswords(){
        if (!validatePasswordField(binding.newPassword) || !validatePasswordField(binding.confirmPassword))
            return true;
        if(binding.newPassword.getEditText().getText().toString().trim().equals(binding.confirmPassword.getEditText().getText().toString().trim())){
            binding.confirmPassword.setError(null);
            return true;
        }
        binding.confirmPassword.setError("Passwords don't match");
        return false;
    }
}