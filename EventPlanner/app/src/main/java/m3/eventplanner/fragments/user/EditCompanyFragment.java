package m3.eventplanner.fragments.user;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;

import m3.eventplanner.R;
import m3.eventplanner.auth.TokenManager;
import m3.eventplanner.clients.ClientUtils;
import m3.eventplanner.databinding.FragmentEditCompanyBinding;
import m3.eventplanner.databinding.FragmentEditPersonalBinding;
import m3.eventplanner.models.CreateLocationDTO;
import m3.eventplanner.models.GetUserDTO;
import m3.eventplanner.models.UpdateCompanyDTO;
import m3.eventplanner.models.UpdateUserDTO;

public class EditCompanyFragment extends Fragment {
    private FragmentEditCompanyBinding binding;
    private EditCompanyViewModel viewModel;
    private ClientUtils clientUtils;

    public EditCompanyFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentEditCompanyBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(EditCompanyViewModel.class);
        clientUtils = new ClientUtils(requireContext());
        viewModel.initialize(clientUtils,new TokenManager(requireContext()));

        viewModel.getUser().observe(getViewLifecycleOwner(), this::populateUserDetails);
        viewModel.loadUser();
        setupValidation();

        viewModel.getError().observe(getViewLifecycleOwner(), error ->
                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show()
        );

        viewModel.getSuccessMessage().observe(getViewLifecycleOwner(), message ->{
                    Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                    NavController navController = NavHostFragment.findNavController(EditCompanyFragment.this);
                    navController.navigate(R.id.userDetailsFragment);
                }
        );

        binding.submit.setOnClickListener(v->{
            if(!isFormValid())
                return;
            String description=binding.description.getEditText().getText().toString().trim();
            String phone=binding.phone.getEditText().getText().toString().trim();
            String country=binding.country.getEditText().getText().toString().trim();
            String city=binding.city.getEditText().getText().toString().trim();
            String street=binding.street.getEditText().getText().toString().trim();
            String houseNumber=binding.houseNumber.getEditText().getText().toString().trim();
            UpdateCompanyDTO updateCompanyDTO = new UpdateCompanyDTO(phone,description,new CreateLocationDTO(city,country,street,houseNumber));
            viewModel.updateCompany(updateCompanyDTO);
        });
    }

    private void populateUserDetails(GetUserDTO user){
        binding.description.getEditText().setText(user.getCompany().getDescription());
        binding.phone.getEditText().setText(user.getCompany().getPhoneNumber());
        binding.country.getEditText().setText(user.getCompany().getLocation().getCountry());
        binding.city.getEditText().setText(user.getCompany().getLocation().getCity());
        binding.street.getEditText().setText(user.getCompany().getLocation().getStreet());
        binding.houseNumber.getEditText().setText(user.getCompany().getLocation().getHouseNumber());
    }

    private boolean isFormValid(){
        boolean isValid=true;
        if(!validateRequiredField(binding.description))
            isValid=false;
        if(!validateRequiredField(binding.phone))
            isValid=false;
        if(!validateRequiredField(binding.country))
            isValid=false;
        if(!validateRequiredField(binding.city))
            isValid=false;
        if(!validateRequiredField(binding.street))
            isValid=false;
        if(!validateRequiredField(binding.houseNumber))
            isValid=false;
        return isValid;
    }

    private void setupValidation(){
        binding.description.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                validateRequiredField(binding.description);
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });
        binding.phone.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                validateRequiredField(binding.phone);
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });
        binding.country.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                validateRequiredField(binding.country);
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });
        binding.city.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                validateRequiredField(binding.city);
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });
        binding.street.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                validateRequiredField(binding.street);
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });
        binding.houseNumber.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                validateRequiredField(binding.houseNumber);
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });
    }

    private boolean validateRequiredField(TextInputLayout textInputLayout) {
        if (TextUtils.isEmpty(textInputLayout.getEditText().getText())) {
            textInputLayout.setError("Required field");
            return false;
        } else {
            textInputLayout.setError(null);
            return true;
        }
    }
}