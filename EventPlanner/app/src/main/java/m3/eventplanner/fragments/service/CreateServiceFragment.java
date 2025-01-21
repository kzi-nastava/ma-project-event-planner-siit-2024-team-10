package m3.eventplanner.fragments.service;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.app.AlertDialog;

import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.List;

import m3.eventplanner.R;
import m3.eventplanner.auth.TokenManager;
import m3.eventplanner.clients.ClientUtils;
import m3.eventplanner.databinding.FragmentCreateProductBinding;
import m3.eventplanner.databinding.FragmentCreateServiceBinding;
import m3.eventplanner.models.CreateServiceDTO;
import m3.eventplanner.models.GetOfferingCategoryDTO;

public class CreateServiceFragment extends Fragment {
    private View rootView;
    private FragmentCreateServiceBinding binding;
    private ClientUtils clientUtils;
    private CreateServiceViewModel viewModel;
    private boolean createCategory=false;
    private boolean isAvailable = false;
    private boolean isVisible = false;
    private boolean autoConfirm = false;
    private GetOfferingCategoryDTO category;
    public CreateServiceFragment(){

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentCreateServiceBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(CreateServiceViewModel.class);
        clientUtils = new ClientUtils(requireContext());
        viewModel.initialize(clientUtils);

        setupObservers();
        setupListeners();
        setupValidation();

        binding.submit.setOnClickListener(v->{
            if(!isFormValid())
                return;
            String name = binding.name.getEditText().getText().toString().trim();
            String description = binding.description.getEditText().getText().toString().trim();
            String categoryName = createCategory ? binding.categoryName.getEditText().getText().toString().trim() : null;
            String categoryDescription = createCategory ? binding.categoryDescription.getEditText().getText().toString().trim() : null;
            double price = Double.parseDouble(binding.price.getEditText().getText().toString().trim());
            double discount = Double.parseDouble(binding.discount.getEditText().getText().toString().trim());
            int providerId=new TokenManager(requireContext()).getUserId();
            int minDuration = Integer.parseInt(binding.minDuration.getEditText().getText().toString().trim());
            int maxDuration = Integer.parseInt(binding.maxDuration.getEditText().getText().toString().trim());
            int reservationDeadline = Integer.parseInt(binding.reservationDeadline.getEditText().getText().toString().trim());
            int cancellationDeadline = Integer.parseInt(binding.cancellationDeadline.getEditText().getText().toString().trim());
            if(minDuration == maxDuration)
                autoConfirm = true;
            //TODO: implement photo upload
            CreateServiceDTO service = new CreateServiceDTO(createCategory?0:category.getId(),categoryName,categoryDescription,providerId,name,description,price,discount, new ArrayList<String>(),isVisible,isAvailable,minDuration,maxDuration,reservationDeadline,cancellationDeadline,autoConfirm);
            viewModel.createService(service);
        });
    }
    private void setupListeners(){
        binding.createCategoryCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
                createCategory=isChecked;
                if(isChecked){
                    binding.categorySpinner.setVisibility(View.GONE);
                    binding.categoryError.setVisibility(View.GONE);
                    binding.categoryText.setVisibility(View.GONE);
                    binding.categoryName.setVisibility(View.VISIBLE);
                    binding.categoryDescription.setVisibility(View.VISIBLE);
                }
                else{
                    binding.categorySpinner.setVisibility(View.VISIBLE);
                    binding.categoryText.setVisibility(View.VISIBLE);
                    binding.categoryName.setVisibility(View.GONE);
                    binding.categoryDescription.setVisibility(View.GONE);
                }

            }
        });

        binding.fixedDurationCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
                createCategory=isChecked;
                if(isChecked){
                    binding.fixedDuration.setVisibility(View.VISIBLE);
                    binding.minDuration.setVisibility(View.GONE);
                    binding.maxDuration.setVisibility(View.GONE);
                }
                else{
                    binding.fixedDuration.setVisibility(View.GONE);
                    binding.minDuration.setVisibility(View.VISIBLE);
                    binding.maxDuration.setVisibility(View.VISIBLE);
                }
            }
        });

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
        viewModel.getCategories().observe(getViewLifecycleOwner(), this::setUpCategorySpinner);
        viewModel.loadCategories();

        viewModel.getError().observe(getViewLifecycleOwner(), error ->
                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show()
        );

        viewModel.getSuccessMessage().observe(getViewLifecycleOwner(), message ->{
                    Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                    NavController navController = NavHostFragment.findNavController(CreateServiceFragment.this);
                    navController.navigate(R.id.homeScreenFragment);
                }
        );
    }

    private void setUpCategorySpinner(List<GetOfferingCategoryDTO> categories) {
        GetOfferingCategoryDTO defaultOption=new GetOfferingCategoryDTO();
        defaultOption.setId(-1);
        defaultOption.setName("Select category");
        categories.add(0,defaultOption);
        Spinner categorySpinner = binding.categorySpinner;
        ArrayAdapter<GetOfferingCategoryDTO> adapter = getGetOfferingCategoryDTOArrayAdapter(categories);
        categorySpinner.setAdapter(adapter);
        categorySpinner.setTag(categories);

        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!categories.isEmpty()) {
                    GetOfferingCategoryDTO selectedCategory = categories.get(position);
                    if (selectedCategory != null) {
                        category=selectedCategory;
                    }
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @NonNull
    private ArrayAdapter<GetOfferingCategoryDTO> getGetOfferingCategoryDTOArrayAdapter(List<GetOfferingCategoryDTO> categories) {
        ArrayAdapter<GetOfferingCategoryDTO> adapter = new ArrayAdapter<GetOfferingCategoryDTO>(requireContext(), android.R.layout.simple_spinner_item, categories) {
            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView textView = (TextView) view;
                textView.setText(categories.get(position).getName());
                return view;
            }

            @NonNull
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                TextView textView = (TextView) super.getView(position, convertView, parent);
                textView.setText(categories.get(position).getName());
                return textView;
            }
        };

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        return adapter;
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
        if(createCategory){
            if(!validateRequiredField(binding.categoryName))
                isValid=false;
            if(!validateRequiredField(binding.categoryDescription))
                isValid=false;
        }
        else{
            if(!validateCategory())
                isValid=false;
        }
        return isValid;
    }

    private void setupValidation(){
        binding.categoryName.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                validateRequiredField(binding.categoryName);
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        binding.categoryDescription.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                validateRequiredField(binding.categoryDescription);
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

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

    private boolean validateCategory(){
        if(createCategory)
            return true;
        if(category.getId()==-1){
            binding.categoryError.setVisibility(View.VISIBLE);
            return false;
        }
        else{
            binding.categoryError.setVisibility(View.GONE);
            return true;
        }
    }
}