package m3.eventplanner.fragments.service;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.textfield.TextInputLayout;

import m3.eventplanner.R;
import m3.eventplanner.adapters.PhotoAdapter;
import m3.eventplanner.clients.ClientUtils;
import m3.eventplanner.databinding.FragmentEditServiceBinding;
import m3.eventplanner.models.GetServiceDTO;
import m3.eventplanner.models.UpdateServiceDTO;

import java.util.ArrayList;
import java.util.List;

public class EditServiceFragment extends Fragment {

    private FragmentEditServiceBinding binding;
    private EditServiceViewModel viewModel;
    private ClientUtils clientUtils;
    private int serviceId;
    private List<String> photoPaths = new ArrayList<>();
    private PhotoAdapter photoAdapter;

    public EditServiceFragment() {}

    public static EditServiceFragment newInstance() {
        return new EditServiceFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentEditServiceBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(EditServiceViewModel.class);
        clientUtils = new ClientUtils(requireContext());
        viewModel.initialize(clientUtils);

        setupObservers();
        setupValidation();
        setupDurationToggle();
        setupClickListeners();

        // Učitaj podatke servisa
        if (getArguments() != null) {
            serviceId = getArguments().getInt("selectedServiceId");
            viewModel.loadService(serviceId);
        }
    }

    private void setupClickListeners() {
        // add for photos here
        // Submit button
        binding.submit.setOnClickListener(v -> {
            if (!isFormValid()) return;
            updateService();
        });
    }

    private void setupDurationToggle() {
        binding.fixedDurationCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                // Prikaži fiksno trajanje, sakrij min/max
                binding.fixedDuration.setVisibility(View.VISIBLE);
                binding.minMaxDurationLayout.setVisibility(View.GONE);

                // Dodaj hint
                binding.fixedDuration.setHint("Fixed Duration (hours)");
            } else {
                // Sakrij fiksno, prikaži min/max
                binding.fixedDuration.setVisibility(View.GONE);
                binding.minMaxDurationLayout.setVisibility(View.VISIBLE);

                // Dodaj hint-ove za min/max
                binding.minDuration.setHint("Min Duration (hours)");
                binding.maxDuration.setHint("Max Duration (hours)");
            }
        });
    }

    private void setupObservers() {
        viewModel.getService().observe(getViewLifecycleOwner(), this::populateServiceDetails);

        viewModel.getError().observe(getViewLifecycleOwner(),
                error -> Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show()
        );

        viewModel.getSuccessMessage().observe(getViewLifecycleOwner(), message -> {
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
            navigateBackToDetails();
        });
    }

    private void populateServiceDetails(GetServiceDTO service) {
        if (service == null) return;

        // Popuni osnovne informacije
        binding.name.getEditText().setText(service.getName());
        binding.description.getEditText().setText(service.getDescription());
        binding.specification.getEditText().setText(service.getSpecification());
        binding.price.getEditText().setText(String.valueOf(service.getPrice()));
        binding.discount.getEditText().setText(String.valueOf(service.getDiscount()));

        // Checkboxes
        binding.checkboxVisible.setChecked(service.isVisible());
        binding.checkboxAvailable.setChecked(service.isAvailable());

        // Trajanje - proveri da li je fiksno
        boolean isFixedDuration = service.getMaxDuration() > 0 &&
                service.getMinDuration() > 0 &&
                service.getMaxDuration() == service.getMinDuration();

        binding.fixedDurationCheckbox.setChecked(isFixedDuration);

        if (isFixedDuration) {
            binding.fixedDuration.getEditText().setText(String.valueOf(service.getMaxDuration()));
            binding.fixedDuration.setVisibility(View.VISIBLE);
            binding.minMaxDurationLayout.setVisibility(View.GONE);
        } else {
            binding.minDuration.getEditText().setText(String.valueOf(service.getMinDuration()));
            binding.maxDuration.getEditText().setText(String.valueOf(service.getMaxDuration()));
            binding.fixedDuration.setVisibility(View.GONE);
            binding.minMaxDurationLayout.setVisibility(View.VISIBLE);
        }

        // Deadlines
        binding.reservationDeadline.getEditText().setText(String.valueOf(service.getReservationPeriod()));
        binding.cancellationDeadline.getEditText().setText(String.valueOf(service.getCancellationPeriod()));

        // Slike - popuni postojeće
        if (service.getPhotos() != null && !service.getPhotos().isEmpty()) {
            photoPaths.clear();
            photoPaths.addAll(service.getPhotos());
            photoAdapter.notifyDataSetChanged();
        }
    }

    private void updateService() {
        UpdateServiceDTO dto = new UpdateServiceDTO();

        // Osnovni podaci
        dto.setName(binding.name.getEditText().getText().toString().trim());
        dto.setDescription(binding.description.getEditText().getText().toString().trim());
        dto.setSpecification(binding.specification.getEditText().getText().toString().trim());
        dto.setPrice(Double.parseDouble(binding.price.getEditText().getText().toString().trim()));
        dto.setDiscount(Double.parseDouble(binding.discount.getEditText().getText().toString().trim()));

        // Potvrda i dostupnost
        dto.setVisible(binding.checkboxVisible.isChecked());
        dto.setAvailable(binding.checkboxAvailable.isChecked());

        // Trajanje
        if (binding.fixedDurationCheckbox.isChecked()) {
            int fixedDuration = Integer.parseInt(binding.fixedDuration.getEditText().getText().toString().trim());
            dto.setMaxDuration(fixedDuration);
            dto.setMinDuration(fixedDuration);
        } else {
            dto.setMinDuration(Integer.parseInt(binding.minDuration.getEditText().getText().toString().trim()));
            dto.setMaxDuration(Integer.parseInt(binding.maxDuration.getEditText().getText().toString().trim()));
        }

        // Deadlines
        dto.setReservationPeriod(Integer.parseInt(binding.reservationDeadline.getEditText().getText().toString().trim()));
        dto.setCancellationPeriod(Integer.parseInt(binding.cancellationDeadline.getEditText().getText().toString().trim()));

        // Slike
        dto.setPhotos(new ArrayList<>(photoPaths));

        viewModel.updateService(dto, serviceId);
    }

    private void navigateBackToDetails() {
        Bundle bundle = new Bundle();
        bundle.putInt("selectedServiceId", serviceId);

        NavOptions navOptions = new NavOptions.Builder()
                .setPopUpTo(R.id.serviceDetailsFragment, true)
                .build();

        Navigation.findNavController(binding.getRoot())
                .navigate(R.id.serviceDetailsFragment, bundle, navOptions);
    }

    private void setupValidation() {
        setupTextValidation(binding.name);
        setupTextValidation(binding.description);
        setupPriceValidation(binding.price);
        setupPriceValidation(binding.discount);
        setupNumberValidation(binding.reservationDeadline);
        setupNumberValidation(binding.cancellationDeadline);

        // Dodaj validaciju za trajanje
        setupDurationValidation();
    }

    private void setupDurationValidation() {
        binding.fixedDuration.getEditText().addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                validateDuration();
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        binding.minDuration.getEditText().addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                validateDuration();
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        binding.maxDuration.getEditText().addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                validateDuration();
            }
            @Override public void afterTextChanged(Editable s) {}
        });
    }

    private boolean validateDuration() {
        if (binding.fixedDurationCheckbox.isChecked()) {
            return validateNumberField(binding.fixedDuration, 1);
        } else {
            boolean minValid = validateNumberField(binding.minDuration, 1);
            boolean maxValid = validateNumberField(binding.maxDuration, 1);

            if (minValid && maxValid) {
                int min = Integer.parseInt(binding.minDuration.getEditText().getText().toString().trim());
                int max = Integer.parseInt(binding.maxDuration.getEditText().getText().toString().trim());

                if (min >= max) {
                    binding.maxDuration.setError("Max duration must be greater than min duration");
                    return false;
                } else {
                    binding.maxDuration.setError(null);
                }
            }

            return minValid && maxValid;
        }
    }

    private void setupTextValidation(TextInputLayout field) {
        field.getEditText().addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                validateRequiredField(field);
            }
            @Override public void afterTextChanged(Editable s) {}
        });
    }

    private void setupPriceValidation(TextInputLayout field) {
        field.getEditText().addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                validatePriceField(field);
            }
            @Override public void afterTextChanged(Editable s) {}
        });
    }

    private void setupNumberValidation(TextInputLayout field) {
        field.getEditText().addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                validateNumberField(field, 0);
            }
            @Override public void afterTextChanged(Editable s) {}
        });
    }

    private boolean validateRequiredField(TextInputLayout field) {
        if (TextUtils.isEmpty(field.getEditText().getText())) {
            field.setError("Required field");
            return false;
        } else {
            field.setError(null);
            return true;
        }
    }

    private boolean validatePriceField(TextInputLayout field) {
        String text = field.getEditText().getText().toString().trim();
        if (TextUtils.isEmpty(text)) {
            field.setError("Required field");
            return false;
        }

        try {
            double value = Double.parseDouble(text);
            if (value < 0) {
                field.setError("Must be positive number");
                return false;
            }
        } catch (NumberFormatException e) {
            field.setError("Must be a valid number");
            return false;
        }

        field.setError(null);
        return true;
    }
    private boolean validateDiscountField(TextInputLayout field) {
        String text = field.getEditText().getText().toString().trim();
        if (TextUtils.isEmpty(text)) {
            field.setError("Required field");
            return false;
        }

        try {
            double value = Double.parseDouble(text);
            if (value < 0) {
                field.setError("Must be positive number");
                return false;
            }
            if (value > 100) {
                field.setError("Discount cannot be higher than 100%");
                return false;
            }
        } catch (NumberFormatException e) {
            field.setError("Must be a valid number");
            return false;
        }

        field.setError(null);
        return true;
    }

    private boolean validateNumberField(TextInputLayout field, int minValue) {
        String text = field.getEditText().getText().toString().trim();
        if (TextUtils.isEmpty(text)) {
            field.setError("Required field");
            return false;
        }

        try {
            int value = Integer.parseInt(text);
            if (value < minValue) {
                field.setError("Must be at least " + minValue);
                return false;
            }
        } catch (NumberFormatException e) {
            field.setError("Must be a valid number");
            return false;
        }

        field.setError(null);
        return true;
    }

    private boolean isFormValid() {
        boolean isValid = validateRequiredField(binding.name) &&
                validateRequiredField(binding.description) &&
                validatePriceField(binding.price) &&
                validatePriceField(binding.discount) &&
                validateNumberField(binding.reservationDeadline, 0) &&
                validateNumberField(binding.cancellationDeadline, 0) &&
                validateDuration();

        return isValid;
    }

    // TODO
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1001 && resultCode == Activity.RESULT_OK && data != null) {
            // Handle single image selection
            if (data.getData() != null) {
                Uri imageUri = data.getData();
                String path = getPathFromUri(imageUri);
                if (path != null) {
                    photoPaths.add(path);
                    photoAdapter.notifyItemInserted(photoPaths.size() - 1);
                }
            }
            // Handle multiple image selection
            else if (data.getClipData() != null) {
                int count = data.getClipData().getItemCount();
                for (int i = 0; i < count; i++) {
                    Uri imageUri = data.getClipData().getItemAt(i).getUri();
                    String path = getPathFromUri(imageUri);
                    if (path != null) {
                        photoPaths.add(path);
                        photoAdapter.notifyItemInserted(photoPaths.size() - 1);
                    }
                }
            }
        }
    }

    private String getPathFromUri(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = requireActivity().getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            String path = cursor.getString(column_index);
            cursor.close();
            return path;
        }
        return null;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}