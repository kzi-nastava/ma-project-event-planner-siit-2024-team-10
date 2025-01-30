package m3.eventplanner.fragments.service;

import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.List;

import m3.eventplanner.R;
import m3.eventplanner.adapters.SelectedImagesAdapter;
import m3.eventplanner.auth.TokenManager;
import m3.eventplanner.clients.ClientUtils;
import m3.eventplanner.databinding.FragmentCreateServiceBinding;
import m3.eventplanner.models.CreateServiceDTO;
import m3.eventplanner.models.GetOfferingCategoryDTO;

public class CreateServiceFragment extends Fragment {
    private static final int PICK_IMAGES_REQUEST = 1;
    private FragmentCreateServiceBinding binding;
    private ClientUtils clientUtils;
    private CreateServiceViewModel viewModel;
    private boolean createCategory = false;
    private boolean isAvailable = false;
    private boolean isVisible = false;
    private boolean autoConfirm = false;
    private GetOfferingCategoryDTO category;
    private List<Uri> selectedImageUris = new ArrayList<>();
    private SelectedImagesAdapter imagesAdapter;

    public CreateServiceFragment() {
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

        setupRecyclerView();
        setupObservers();
        setupListeners();
        setupValidation();
        setupImageSelection();

        binding.submit.setOnClickListener(v -> onSubmitButtonClick());
    }

    private void setupRecyclerView() {
        RecyclerView recyclerView = binding.selectedImagesRecycler;
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext(),
                LinearLayoutManager.HORIZONTAL, false));
        imagesAdapter = new SelectedImagesAdapter();
        recyclerView.setAdapter(imagesAdapter);

        imagesAdapter.setOnImageRemoveListener(position -> {
            selectedImageUris.remove(position);
            imagesAdapter.setImages(selectedImageUris);
        });
    }

    private void setupImageSelection() {
        binding.addPhoto.setOnClickListener(v -> {
            if (selectedImageUris.size() >= 10) {
                Toast.makeText(requireContext(), "Maximum 10 images allowed",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            startActivityForResult(Intent.createChooser(intent, "Select Images"),
                    PICK_IMAGES_REQUEST);
        });
    }

    private void setupListeners() {
        binding.createCategoryCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            createCategory = isChecked;
            if (isChecked) {
                binding.categorySpinner.setVisibility(View.GONE);
                binding.categoryError.setVisibility(View.GONE);
                binding.categoryText.setVisibility(View.GONE);
                binding.categoryName.setVisibility(View.VISIBLE);
                binding.categoryDescription.setVisibility(View.VISIBLE);
            } else {
                binding.categorySpinner.setVisibility(View.VISIBLE);
                binding.categoryText.setVisibility(View.VISIBLE);
                binding.categoryName.setVisibility(View.GONE);
                binding.categoryDescription.setVisibility(View.GONE);
            }
        });

        binding.fixedDurationCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                binding.fixedDuration.setVisibility(View.VISIBLE);
                binding.minDuration.setVisibility(View.GONE);
                binding.maxDuration.setVisibility(View.GONE);
            } else {
                binding.fixedDuration.setVisibility(View.GONE);
                binding.minDuration.setVisibility(View.VISIBLE);
                binding.maxDuration.setVisibility(View.VISIBLE);
            }
        });

        binding.serviceAvailabilityGroup.addOnButtonCheckedListener(
                (group, checkedId, isChecked) -> {
                    if (checkedId == R.id.buttonAvailable) {
                        isAvailable = isChecked;
                    } else if (checkedId == R.id.buttonVisible) {
                        isVisible = isChecked;
                    }
                });
    }

    private void setupObservers() {
        viewModel.getCategories().observe(getViewLifecycleOwner(), this::setUpCategorySpinner);
        viewModel.loadCategories();

        viewModel.getError().observe(getViewLifecycleOwner(), error ->
                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show()
        );

        viewModel.getSuccessMessage().observe(getViewLifecycleOwner(), message -> {
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
            NavController navController = NavHostFragment.findNavController(CreateServiceFragment.this);
            navController.navigate(R.id.homeScreenFragment);
        });

        viewModel.getUploadedImagePaths().observe(getViewLifecycleOwner(), paths -> {
            if (paths != null && !paths.isEmpty()) {
                CreateServiceDTO service = createServiceDTO();
                service.setPhotos(paths);
                viewModel.createService(service);
            }
        });
    }

    private void setUpCategorySpinner(List<GetOfferingCategoryDTO> categories) {
        GetOfferingCategoryDTO defaultOption = new GetOfferingCategoryDTO();
        defaultOption.setId(-1);
        defaultOption.setName("Select category");
        categories.add(0, defaultOption);

        ArrayAdapter<GetOfferingCategoryDTO> adapter = new ArrayAdapter<GetOfferingCategoryDTO>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                categories
        ) {
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
        binding.categorySpinner.setAdapter(adapter);
        binding.categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!categories.isEmpty()) {
                    category = categories.get(position);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void setupValidation() {
        setupTextValidator(binding.categoryName);
        setupTextValidator(binding.categoryDescription);
        setupTextValidator(binding.name);
        setupTextValidator(binding.description);
        setupTextValidator(binding.price);
        setupTextValidator(binding.discount);
    }

    private void setupTextValidator(TextInputLayout textInputLayout) {
        textInputLayout.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                validateRequiredField(textInputLayout);
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGES_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            if (data.getClipData() != null) {
                ClipData clipData = data.getClipData();
                int count = Math.min(clipData.getItemCount(), 10 - selectedImageUris.size());
                for (int i = 0; i < count; i++) {
                    Uri imageUri = clipData.getItemAt(i).getUri();
                    if (!selectedImageUris.contains(imageUri)) {
                        selectedImageUris.add(imageUri);
                    }
                }
            } else if (data.getData() != null) {
                Uri imageUri = data.getData();
                if (!selectedImageUris.contains(imageUri)) {
                    selectedImageUris.add(imageUri);
                }
            }
            imagesAdapter.setImages(selectedImageUris);
        }
    }

    private void onSubmitButtonClick() {
        if (!isFormValid())
            return;

        if (!selectedImageUris.isEmpty()) {
            viewModel.uploadImages(selectedImageUris, requireContext());
        } else {
            CreateServiceDTO service = createServiceDTO();
            service.setPhotos(new ArrayList<>());
            viewModel.createService(service);
        }
    }

    private CreateServiceDTO createServiceDTO() {
        String name = binding.name.getEditText().getText().toString().trim();
        String description = binding.description.getEditText().getText().toString().trim();
        String specification = binding.specification.getEditText().getText().toString().trim();
        String categoryName = createCategory ? binding.categoryName.getEditText().getText().toString().trim() : null;
        String categoryDescription = createCategory ? binding.categoryDescription.getEditText().getText().toString().trim() : null;
        double price = Double.parseDouble(binding.price.getEditText().getText().toString().trim());
        double discount = Double.parseDouble(binding.discount.getEditText().getText().toString().trim());
        int providerId = new TokenManager(requireContext()).getUserId();

        int minDuration, maxDuration;
        if (binding.fixedDurationCheckbox.isChecked()) {
            minDuration = maxDuration = Integer.parseInt(binding.fixedDuration.getEditText().getText().toString().trim());
            autoConfirm = true;
        } else {
            minDuration = Integer.parseInt(binding.minDuration.getEditText().getText().toString().trim());
            maxDuration = Integer.parseInt(binding.maxDuration.getEditText().getText().toString().trim());
        }

        int reservationDeadline = Integer.parseInt(binding.reservationDeadline.getEditText().getText().toString().trim());
        int cancellationDeadline = Integer.parseInt(binding.cancellationDeadline.getEditText().getText().toString().trim());

        return new CreateServiceDTO(
                createCategory ? 0 : category.getId(),
                categoryName,
                categoryDescription,
                providerId,
                name,
                description,
                specification,
                price,
                discount,
                new ArrayList<>(),
                isVisible,
                isAvailable,
                minDuration,
                maxDuration,
                reservationDeadline,
                cancellationDeadline,
                autoConfirm
        );
    }

    private boolean validateDuration() {
        if (binding.fixedDurationCheckbox.isChecked()) {
            if (!validateRequiredField(binding.fixedDuration)) return false;

            try {
                int duration = Integer.parseInt(binding.fixedDuration.getEditText().getText().toString().trim());
                if (duration <= 0) {
                    binding.fixedDuration.setError("Duration must be greater than 0");
                    return false;
                }
            } catch (NumberFormatException e) {
                binding.fixedDuration.setError("Please enter a valid number");
                return false;
            }
        } else {
            if (!validateRequiredField(binding.minDuration) || !validateRequiredField(binding.maxDuration))
                return false;

            try {
                int minDuration = Integer.parseInt(binding.minDuration.getEditText().getText().toString().trim());
                int maxDuration = Integer.parseInt(binding.maxDuration.getEditText().getText().toString().trim());

                if (minDuration <= 0) {
                    binding.minDuration.setError("Minimum duration must be greater than 0");
                    return false;
                }
                if (maxDuration <= 0) {
                    binding.maxDuration.setError("Maximum duration must be greater than 0");
                    return false;
                }
                if (minDuration > maxDuration) {
                    binding.minDuration.setError("Minimum duration cannot be greater than maximum duration");
                    return false;
                }
            } catch (NumberFormatException e) {
                binding.minDuration.setError("Please enter valid numbers");
                binding.maxDuration.setError("Please enter valid numbers");
                return false;
            }
        }
        return true;
    }

    private boolean validateDeadlines() {
        if (!validateRequiredField(binding.reservationDeadline) ||
                !validateRequiredField(binding.cancellationDeadline))
            return false;

        try {
            int reservationDeadline = Integer.parseInt(binding.reservationDeadline.getEditText().getText().toString().trim());
            int cancellationDeadline = Integer.parseInt(binding.cancellationDeadline.getEditText().getText().toString().trim());

            if (reservationDeadline < 0) {
                binding.reservationDeadline.setError("Reservation deadline must be non-negative");
                return false;
            }
            if (cancellationDeadline < 0) {
                binding.cancellationDeadline.setError("Cancellation deadline must be non-negative");
                return false;
            }
        } catch (NumberFormatException e) {
            binding.reservationDeadline.setError("Please enter valid numbers");
            binding.cancellationDeadline.setError("Please enter valid numbers");
            return false;
        }
        return true;
    }

    private boolean isFormValid() {
        boolean isValid = true;

        if (!validateRequiredField(binding.name)) isValid = false;
        if (!validateRequiredField(binding.description)) isValid = false;
        if (!validateRequiredField(binding.price)) isValid = false;
        if (!validateRequiredField(binding.discount)) isValid = false;

        if (createCategory) {
            if (!validateRequiredField(binding.categoryName)) isValid = false;
            if (!validateRequiredField(binding.categoryDescription)) isValid = false;
        } else {
            if (!validateCategory()) isValid = false;
        }

        if (!validateDuration()) isValid = false;
        if (!validateDeadlines()) isValid = false;

        return isValid;
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