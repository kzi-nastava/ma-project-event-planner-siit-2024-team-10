package m3.eventplanner.fragments.product;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.List;

import m3.eventplanner.R;
import m3.eventplanner.auth.TokenManager;
import m3.eventplanner.clients.ClientUtils;
import m3.eventplanner.databinding.FragmentCreateProductBinding;
import m3.eventplanner.databinding.FragmentEditProductBinding;
import m3.eventplanner.models.CreateProductDTO;
import m3.eventplanner.models.GetOfferingCategoryDTO;
import m3.eventplanner.models.GetProductDTO;
import m3.eventplanner.models.UpdateProductDTO;

public class EditProductFragment extends Fragment {
    private FragmentEditProductBinding binding;
    private ClientUtils clientUtils;
    private EditProductViewModel viewModel;
    private boolean isAvailable = false;
    private boolean isVisible = false;
    private int productId;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentEditProductBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(EditProductViewModel.class);
        clientUtils = new ClientUtils(requireContext());
        viewModel.initialize(clientUtils);

        if (getArguments() != null) {
            productId = getArguments().getInt("selectedProductId");
        }

        setupObservers();
        setupListeners();
        setupValidation();

        binding.submit.setOnClickListener(v->{
            if(!isFormValid())
                return;
            String name = binding.name.getEditText().getText().toString().trim();
            String description = binding.description.getEditText().getText().toString().trim();
            double price = Double.parseDouble(binding.price.getEditText().getText().toString().trim());
            double discount = Double.parseDouble(binding.discount.getEditText().getText().toString().trim());
            UpdateProductDTO product = new UpdateProductDTO(name,description,price,discount,isVisible,isAvailable);
            viewModel.updateProduct(productId, product);
        });
    }

    private void setupListeners(){
        binding.productAvailabilityGroup.addOnButtonCheckedListener(new MaterialButtonToggleGroup.OnButtonCheckedListener() {
            @Override
            public void onButtonChecked(MaterialButtonToggleGroup group, int checkedId, boolean isChecked) {
                if (checkedId == R.id.buttonAvailable) {
                    isAvailable=isChecked;
                } else if (checkedId == R.id.buttonVisible) {
                    isVisible=isChecked;
                }
            }
        });
    }

    private void setupObservers(){
        viewModel.getError().observe(getViewLifecycleOwner(), error ->
                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show()
        );

        viewModel.getSuccessMessage().observe(getViewLifecycleOwner(), message ->{
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
            Bundle bundle = new Bundle();
            bundle.putInt("selectedServiceId", this.productId);

            NavOptions navOptions = new NavOptions.Builder()
                    .setPopUpTo(R.id.serviceDetailsFragment, true)
                    .build();

            Navigation.findNavController(binding.getRoot()).navigate(R.id.serviceDetailsFragment, bundle, navOptions);
                }
        );

        viewModel.getProduct().observe(getViewLifecycleOwner(), this::populateProductDetails);
        viewModel.fetchProductDetails(productId);
    }

    private boolean isFormValid(){
        boolean isValid=true;
        if(!validateRequiredField(binding.name))
            isValid=false;
        if(!validateRequiredField(binding.description))
            isValid=false;
        if(!validateRequiredField(binding.price))
            isValid=false;
        if(!validateRequiredField(binding.discount))
            isValid=false;
        return isValid;
    }

    private void setupValidation(){

        binding.name.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                validateRequiredField(binding.name);
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

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

        binding.price.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                validateRequiredField(binding.price);
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        binding.discount.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                validateRequiredField(binding.discount);
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

    private void populateProductDetails(GetProductDTO product){
        binding.name.getEditText().setText(product.getName());
        binding.description.getEditText().setText(product.getDescription());
        binding.discount.getEditText().setText(String.valueOf(product.getDiscount()));
        binding.price.getEditText().setText(String.valueOf(product.getPrice()));
        binding.buttonVisible.setChecked(product.isVisible());
        binding.buttonAvailable.setChecked(product.isAvailable());
        isAvailable=product.isAvailable();
        isVisible=product.isVisible();
    }
}