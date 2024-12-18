package m3.eventplanner.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Arrays;
import java.util.List;

import m3.eventplanner.R;
import m3.eventplanner.models.CreateCompanyDTO;
import m3.eventplanner.models.CreateLocationDTO;
import m3.eventplanner.models.RegisterDTO;

public class RegisterFragment extends Fragment {
    private RadioGroup roleRadioGroup;
    private LinearLayout companyInfoContainer;
    Button submitButton;
    TextInputLayout emailLayout, passwordLayout, passwordAgainLayout, firstNameLayout, lastNameLayout, profilePhotoLayout, countryLayout, cityLayout, streetLayout, houseNumberLayout, phoneLayout, companyEmailLayout, companyNameLayout, companyCountryLayout, companyCityLayout, companyStreetLayout, companyHouseNumberLayout, companyPhoneLayout, companyDescriptionLayout, companyPhotosLayout;
    EditText emailInput, passwordInput, passwordAgainInput, firstNameInput, lastNameInput, profilePhotoInput, countryInput, cityInput, streetInput, houseNumberInput, phoneInput, companyEmailInput, companyNameInput, companyCountryInput, companyCityInput, companyStreetInput, companyHouseNumberInput, companyPhoneInput, companyDescriptionInput, companyPhotosInput;

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
        View view = inflater.inflate(R.layout.fragment_register, container, false);

        initializeForm(view);

        submitButton.setOnClickListener(v -> {
            if(!validateForm())
                return;
            RegisterDTO registerDTO = getFormData();
        });

        return view;
    }

    public void initializeForm(View view) {
        // Initialize buttons and input fields
        submitButton = view.findViewById(R.id.submit);

        emailLayout = view.findViewById(R.id.email);
        emailInput = emailLayout.getEditText();

        passwordLayout = view.findViewById(R.id.password);
        passwordInput = passwordLayout.getEditText();

        passwordAgainLayout = view.findViewById(R.id.password_again);
        passwordAgainInput = passwordAgainLayout.getEditText();

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
        companyEmailLayout = view.findViewById(R.id.companyEmail);
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

    private boolean validateForm(){
        boolean isValid=true;

        // Validate Email
        String email = emailInput.getText().toString();
        if (email.isEmpty()) {
            emailLayout.setError("Email is required");
            isValid = false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailLayout.setError("Invalid email format");
            isValid = false;
        } else {
            emailLayout.setError(null);
        }

        // Validate Password
        String password = passwordInput.getText().toString();
        if (password.isEmpty()) {
            passwordLayout.setError("Password is required");
            isValid = false;
        } else if (password.length() < 8) {
            passwordLayout.setError("Password must be at least 8 characters");
            isValid = false;
        } else {
            passwordLayout.setError(null);
        }

        // Validate Confirm Password
        String passwordAgain = passwordAgainInput.getText().toString();
        if (!passwordAgain.equals(password)) {
            passwordAgainLayout.setError("Passwords do not match");
            isValid = false;
        } else {
            passwordAgainLayout.setError(null);
        }

        // Validate First Name
        String firstName = firstNameInput.getText().toString();
        if (firstName.isEmpty()) {
            firstNameLayout.setError("First name is required");
            isValid = false;
        } else {
            firstNameLayout.setError(null);
        }

        // Validate Last Name
        String lastName = lastNameInput.getText().toString();
        if (lastName.isEmpty()) {
            lastNameLayout.setError("Last name is required");
            isValid = false;
        } else {
            lastNameLayout.setError(null);
        }

        // Validate Profile Photo
        String profilePhoto = profilePhotoInput.getText().toString();
        if (profilePhoto.isEmpty()) {
            profilePhotoLayout.setError("Profile photo is required");
            isValid = false;
        } else {
            profilePhotoLayout.setError(null);
        }

        // Validate Address (Country, City, Street, House Number)
        String country = countryInput.getText().toString();
        if (country.isEmpty()) {
            countryLayout.setError("Country is required");
            isValid = false;
        } else {
            countryLayout.setError(null);
        }

        String city = cityInput.getText().toString();
        if (city.isEmpty()) {
            cityLayout.setError("City is required");
            isValid = false;
        } else {
            cityLayout.setError(null);
        }

        String street = streetInput.getText().toString();
        if (street.isEmpty()) {
            streetLayout.setError("Street is required");
            isValid = false;
        } else {
            streetLayout.setError(null);
        }

        String houseNumber = houseNumberInput.getText().toString();
        if (houseNumber.isEmpty()) {
            houseNumberLayout.setError("House number is required");
            isValid = false;
        } else {
            houseNumberLayout.setError(null);
        }

        // Validate Phone Number
        String phone = phoneInput.getText().toString();
        if (phone.isEmpty()) {
            phoneLayout.setError("Phone number is required");
            isValid = false;
        } else {
            phoneLayout.setError(null);
        }

        int selectedRole = roleRadioGroup.getCheckedRadioButtonId();

        if (selectedRole == R.id.product_service_provider) {
            String companyEmail = companyEmailInput.getText().toString();
            if (companyEmail.isEmpty()) {
                companyEmailLayout.setError("Company email is required");
                isValid = false;
            } else if (!Patterns.EMAIL_ADDRESS.matcher(companyEmail).matches()) {
                companyEmailLayout.setError("Invalid email format");
                isValid = false;
            } else {
                companyEmailLayout.setError(null);
            }

            String companyName = companyNameInput.getText().toString();
            if (companyName.isEmpty()) {
                companyNameLayout.setError("Company name is required");
                isValid = false;
            } else {
                companyNameLayout.setError(null);
            }

            String companyCountry = companyCountryInput.getText().toString();
            if (companyCountry.isEmpty()) {
                companyCountryLayout.setError("Company country is required");
                isValid = false;
            } else {
                companyCountryLayout.setError(null);
            }

            String companyCity = companyCityInput.getText().toString();
            if (companyCity.isEmpty()) {
                companyCityLayout.setError("Company city is required");
                isValid = false;
            } else {
                companyCityLayout.setError(null);
            }

            String companyStreet = companyStreetInput.getText().toString();
            if (companyStreet.isEmpty()) {
                companyStreetLayout.setError("Company street is required");
                isValid = false;
            } else {
                companyStreetLayout.setError(null);
            }

            String companyHouseNumber = companyHouseNumberInput.getText().toString();
            if (companyHouseNumber.isEmpty()) {
                companyHouseNumberLayout.setError("Company house number is required");
                isValid = false;
            } else {
                companyHouseNumberLayout.setError(null);
            }

            String companyPhone = companyPhoneInput.getText().toString();
            if (companyPhone.isEmpty()) {
                companyPhoneLayout.setError("Company phone is required");
                isValid = false;
            }else {
                companyPhoneLayout.setError(null);
            }

            String companyDescription = companyDescriptionInput.getText().toString();
            if (companyDescription.isEmpty()) {
                companyDescriptionLayout.setError("Company description is required");
                isValid = false;
            } else {
                companyDescriptionLayout.setError(null);
            }

            String companyPhotos = companyPhotosInput.getText().toString();
            if (companyPhotos.isEmpty()) {
                companyPhotosLayout.setError("Company photos are required");
                isValid = false;
            } else {
                companyPhotosLayout.setError(null);
            }
        }

        return isValid;
    }

    private RegisterDTO getFormData(){
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();
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
                ? "Event Organizer"
                : "Product Service Provider";

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
        if (role.equals("Product Service Provider")) {
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
}