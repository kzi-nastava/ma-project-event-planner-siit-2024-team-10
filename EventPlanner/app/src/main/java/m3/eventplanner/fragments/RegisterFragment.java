package m3.eventplanner.fragments;

import static android.view.View.GONE;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;

import java.util.Arrays;
import java.util.List;

import m3.eventplanner.R;
import m3.eventplanner.auth.TokenManager;
import m3.eventplanner.clients.AuthService;
import m3.eventplanner.clients.ClientUtils;
import m3.eventplanner.models.CreateCompanyDTO;
import m3.eventplanner.models.CreateLocationDTO;
import m3.eventplanner.models.RegisterDTO;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterFragment extends Fragment {
    private RadioGroup roleRadioGroup;
    private LinearLayout companyInfoContainer;
    private Button submitButton;
    private TextInputLayout emailLayout, passwordLayout, passwordAgainLayout, firstNameLayout, lastNameLayout, profilePhotoLayout, countryLayout, cityLayout, streetLayout, houseNumberLayout, phoneLayout, companyEmailLayout, companyNameLayout, companyCountryLayout, companyCityLayout, companyStreetLayout, companyHouseNumberLayout, companyPhoneLayout, companyDescriptionLayout, companyPhotosLayout;
    private EditText emailInput, passwordInput, passwordAgainInput, firstNameInput, lastNameInput, profilePhotoInput, countryInput, cityInput, streetInput, houseNumberInput, phoneInput, companyEmailInput, companyNameInput, companyCountryInput, companyCityInput, companyStreetInput, companyHouseNumberInput, companyPhoneInput, companyDescriptionInput, companyPhotosInput;
    private ClientUtils clientUtils;
    private boolean roleUpgrade;
    private TokenManager tokenManager;

    public RegisterFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        clientUtils = new ClientUtils(requireContext());
        tokenManager = new TokenManager(requireContext());
        roleUpgrade = tokenManager.getEmail()!=null;

        View view = inflater.inflate(R.layout.fragment_register, container, false);
        initializeForm(view);
        setValidationListeners();

        submitButton.setOnClickListener(v -> {
            register();
        });

        return view;
    }

    public void initializeForm(View view) {
        // Initialize buttons and input fields
        submitButton = view.findViewById(R.id.submit);

        emailLayout = view.findViewById(R.id.email);
        emailInput = emailLayout.getEditText();
        if(roleUpgrade)
        {
            emailInput.setEnabled(false);
            emailInput.setFocusable(false);
            emailInput.setText(tokenManager.getEmail());
        }

        passwordLayout = view.findViewById(R.id.password);
        passwordInput = passwordLayout.getEditText();

        passwordAgainLayout = view.findViewById(R.id.password_again);
        passwordAgainInput = passwordAgainLayout.getEditText();

        if(roleUpgrade){
            passwordLayout.setVisibility(View.GONE);
            passwordAgainLayout.setVisibility(View.GONE);
        }

        firstNameLayout = view.findViewById(R.id.first_name);
        firstNameInput = firstNameLayout.getEditText();

        lastNameLayout = view.findViewById(R.id.last_name);
        lastNameInput = lastNameLayout.getEditText();

        profilePhotoLayout = view.findViewById(R.id.profile_photo);
        profilePhotoInput = profilePhotoLayout.getEditText();

        countryLayout = view.findViewById(R.id.country);
        countryInput = countryLayout.getEditText();

        cityLayout = view.findViewById(R.id.city);
        cityInput = cityLayout.getEditText();

        streetLayout = view.findViewById(R.id.street);
        streetInput = streetLayout.getEditText();

        houseNumberLayout = view.findViewById(R.id.house_number);
        houseNumberInput = houseNumberLayout.getEditText();

        phoneLayout = view.findViewById(R.id.phone);
        phoneInput = phoneLayout.getEditText();

        // Initialize company fields
        companyEmailLayout = view.findViewById(R.id.company_email);
        companyEmailInput = companyEmailLayout.getEditText();

        companyNameLayout = view.findViewById(R.id.name);
        companyNameInput = companyNameLayout.getEditText();

        companyCountryLayout = view.findViewById(R.id.company_country);
        companyCountryInput = companyCountryLayout.getEditText();

        companyCityLayout = view.findViewById(R.id.company_city);
        companyCityInput = companyCityLayout.getEditText();

        companyStreetLayout = view.findViewById(R.id.company_street);
        companyStreetInput = companyStreetLayout.getEditText();

        companyHouseNumberLayout = view.findViewById(R.id.company_house_number);
        companyHouseNumberInput = companyHouseNumberLayout.getEditText();

        companyPhoneLayout = view.findViewById(R.id.company_phone);
        companyPhoneInput = companyPhoneLayout.getEditText();

        companyDescriptionLayout = view.findViewById(R.id.company_description);
        companyDescriptionInput = companyDescriptionLayout.getEditText();

        companyPhotosLayout = view.findViewById(R.id.company_photos);
        companyPhotosInput = companyPhotosLayout.getEditText();

        roleRadioGroup = view.findViewById(R.id.role_radio_group);
        companyInfoContainer = view.findViewById(R.id.company_info);

        roleRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.product_service_provider) {
                companyInfoContainer.setVisibility(View.VISIBLE);
            } else if (checkedId == R.id.event_organizer) {
                companyInfoContainer.setVisibility(View.GONE);
            }
        });
    }

    private void setValidationListeners() {
        // Email validation
        emailInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                validateEmail(emailLayout);
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        if(!roleUpgrade){
            // Password validation
            passwordInput.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                    validatePassword();
                }

                @Override
                public void afterTextChanged(Editable editable) {}
            });

            // Confirm password validation
            passwordAgainInput.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                    validateConfirmPassword();
                }

                @Override
                public void afterTextChanged(Editable editable) {}
            });
        }

        // First name validation
        firstNameInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                validateRequiredField(firstNameLayout);
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        // Last name validation
        lastNameInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                validateRequiredField(lastNameLayout);
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        // Country validation
        countryInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                validateRequiredField(countryLayout);
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        // City validation
        cityInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                validateRequiredField(cityLayout);
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        // Street validation
        streetInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                validateRequiredField(streetLayout);
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        // House number validation
        houseNumberInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                validateRequiredField(houseNumberLayout);
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        // Phone validation
        phoneInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                validateRequiredField(phoneLayout);
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        // Company email validation
        companyEmailInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                validateEmail(companyEmailLayout);
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        // Company name validation
        companyNameInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                validateRequiredField(companyNameLayout);
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        // Company country validation
        companyCountryInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                validateRequiredField(companyCountryLayout);
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        // Company city validation
        companyCityInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                validateRequiredField(companyCityLayout);
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        // Company street validation
        companyStreetInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                validateRequiredField(companyStreetLayout);
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        // Company house number validation
        companyHouseNumberInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                validateRequiredField(companyHouseNumberLayout);
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        // Company phone validation
        companyPhoneInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                validateRequiredField(companyPhoneLayout);
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        // Company description validation
        companyDescriptionInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                validateRequiredField(companyDescriptionLayout);
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });
    }

    private boolean validateEmail(TextInputLayout textInputLayout) {
        if (TextUtils.isEmpty(textInputLayout.getEditText().getText())) {
            textInputLayout.setError("Required field");
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(textInputLayout.getEditText().getText()).matches()) {
            textInputLayout.setError("Invalid email format");
            return false;
        } else {
            textInputLayout.setError(null);
            return true;
        }
    }

    private boolean validatePassword() {
        if (TextUtils.isEmpty(passwordInput.getText())) {
            passwordLayout.setError("Required field");
            return false;
        } else if (passwordInput.getText().length() < 8) {
            passwordLayout.setError("Password should be at least 8 characters");
            return false;
        } else {
            passwordLayout.setError(null);
            return true;
        }
    }

    private boolean validateConfirmPassword() {
        if (TextUtils.isEmpty(passwordAgainInput.getText()) || !passwordAgainInput.getText().toString().equals(passwordInput.getText().toString())) {
            passwordAgainLayout.setError("Passwords do not match");
            return false;
        } else {
            passwordAgainLayout.setError(null);
            return true;
        }
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

    private RegisterDTO getFormData(){
        String email = emailInput.getText().toString().trim();
        String password = roleUpgrade? null: passwordInput.getText().toString().trim();
        String firstName = firstNameInput.getText().toString().trim();
        String lastName = lastNameInput.getText().toString().trim();
        String phone = phoneInput.getText().toString().trim();
        String profilePhoto = profilePhotoInput.getText().toString().trim();
        String country = countryInput.getText().toString().trim();
        String city = cityInput.getText().toString().trim();
        String street = streetInput.getText().toString().trim();
        String houseNumber = houseNumberInput.getText().toString().trim();

        // Determine role
        String role = (roleRadioGroup.getCheckedRadioButtonId() == R.id.event_organizer)
                ? "EVENT_ORGANIZER"
                : "PROVIDER";

        // Create location DTO
        CreateLocationDTO location = new CreateLocationDTO();
        location.setCity(city);
        location.setCountry(country);
        location.setStreet(street);
        location.setHouseNumber(houseNumber);

        // Create RegisterDTO
        RegisterDTO registerDTO = new RegisterDTO();
        registerDTO.setEmail(email);
        registerDTO.setPassword(password);
        registerDTO.setRole(role);
        registerDTO.setFirstName(firstName);
        registerDTO.setLastName(lastName);
        registerDTO.setPhoneNumber(phone);
        registerDTO.setProfilePhoto(profilePhoto);
        registerDTO.setLocation(location);

        // Check if role is Product Service Provider
        if (role.equals("PROVIDER")) {
            String companyEmail = companyEmailInput.getText().toString().trim();
            String companyName = companyNameInput.getText().toString().trim();
            String companyPhone = companyPhoneInput.getText().toString().trim();
            String companyCountry = companyCountryInput.getText().toString().trim();
            String companyCity = companyCityInput.getText().toString().trim();
            String companyStreet = companyStreetInput.getText().toString().trim();
            String companyHouseNumber = companyHouseNumberInput.getText().toString().trim();
            String companyDescription = companyDescriptionInput.getText().toString().trim();
            List<String> companyPhotos = Arrays.asList(companyPhotosInput.getText().toString().trim().split(" "));

            // Create company location DTO
            CreateLocationDTO companyLocation = new CreateLocationDTO();
            companyLocation.setCity(companyCity);
            companyLocation.setCountry(companyCountry);
            companyLocation.setStreet(companyStreet);
            companyLocation.setHouseNumber(companyHouseNumber);

            // Create company DTO
            CreateCompanyDTO companyDTO = new CreateCompanyDTO();
            companyDTO.setEmail(companyEmail);
            companyDTO.setName(companyName);
            companyDTO.setPhoneNumber(companyPhone);
            companyDTO.setDescription(companyDescription);
            companyDTO.setPhotos(companyPhotos);
            companyDTO.setLocation(companyLocation);

            // Set company in registerDTO
            registerDTO.setCompany(companyDTO);
        }
        return registerDTO;
    }

    private boolean isFormValid() {
        boolean isValid = true;

        // Validate email
        if (!validateEmail(emailLayout)) {
            isValid = false;
        }

        // Validate password
        if (!roleUpgrade && !validatePassword()) {
            isValid = false;
        }

        // Validate confirm password
        if (!roleUpgrade && !validateConfirmPassword()) {
            isValid = false;
        }

        // Validate first name
        if (!validateRequiredField(firstNameLayout)) {
            isValid = false;
        }

        // Validate last name
        if (!validateRequiredField(lastNameLayout)) {
            isValid = false;
        }

        // Validate country
        if (!validateRequiredField(countryLayout)) {
            isValid = false;
        }

        // Validate city
        if (!validateRequiredField(cityLayout)) {
            isValid = false;
        }

        // Validate street
        if (!validateRequiredField(streetLayout)) {
            isValid = false;
        }

        // Validate house number
        if (!validateRequiredField(houseNumberLayout)) {
            isValid = false;
        }

        // Validate phone
        if (!validateRequiredField(phoneLayout)) {
            isValid = false;
        }

        if (roleRadioGroup.getCheckedRadioButtonId() == R.id.product_service_provider) {
            // Validate company email
            if (!validateEmail(companyEmailLayout)) {
                isValid = false;
            }

            // Validate company name
            if (!validateRequiredField(companyNameLayout)) {
                isValid = false;
            }

            // Validate company country
            if (!validateRequiredField(companyCountryLayout)) {
                isValid = false;
            }

            // Validate company city
            if (!validateRequiredField(companyCityLayout)) {
                isValid = false;
            }

            // Validate company street
            if (!validateRequiredField(companyStreetLayout)) {
                isValid = false;
            }

            // Validate company house number
            if (!validateRequiredField(companyHouseNumberLayout)) {
                isValid = false;
            }

            // Validate company phone
            if (!validateRequiredField(companyPhoneLayout)) {
                isValid = false;
            }

            // Validate company description
            if (!validateRequiredField(companyDescriptionLayout)) {
                isValid = false;
            }
        }

        return isValid;
    }

    private void register(){
        if(!isFormValid())
            return;
        RegisterDTO registerDTO = getFormData();
        AuthService authService = clientUtils.getAuthService();
        authService.register(registerDTO,roleUpgrade).enqueue(new Callback<RegisterDTO>() {
            @Override
            public void onResponse(Call<RegisterDTO> call, Response<RegisterDTO> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(requireContext(), "Success! Please confirm your email", Toast.LENGTH_LONG).show();
                    NavController navController = NavHostFragment.findNavController(RegisterFragment.this);
                    navController.navigate(R.id.homeScreenFragment);
                } else if(response.code()==409){
                    emailLayout.setError("Account with given email already exists");
                }
            }
            @Override
            public void onFailure(Call<RegisterDTO> call, Throwable t) {
                String a="aaaa";
                //loginError.setVisibility(View.VISIBLE);
            }
        });
    }
}