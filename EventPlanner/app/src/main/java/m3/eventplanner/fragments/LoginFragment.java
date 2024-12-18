package m3.eventplanner.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Patterns;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import m3.eventplanner.R;
import m3.eventplanner.auth.TokenManager;
import m3.eventplanner.clients.AuthService;
import m3.eventplanner.clients.ClientUtils;
import m3.eventplanner.models.LoginRequestDTO;
import m3.eventplanner.models.LoginResponseDTO;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginFragment extends Fragment {
    private TextInputEditText emailInput, passwordInput;
    private TextInputLayout emailLayout,passwordLayout;
    private ClientUtils clientUtils;
    private NavigationView navigationView;
    private TextView loginError;

    public LoginFragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        clientUtils=new ClientUtils(requireContext());
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initializeRegisterMessage(view);
        emailInput=view.findViewById(R.id.email);
        passwordInput=view.findViewById(R.id.password);
        navigationView=requireActivity().findViewById(R.id.nav_view);
        loginError=view.findViewById(R.id.loginError);
        emailLayout=view.findViewById(R.id.email_layout);
        passwordLayout=view.findViewById(R.id.password_layout);
        Button loginButton = view.findViewById(R.id.loginButton);
        loginButton.setOnClickListener(v -> {
            login();
        });

        emailInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                validateEmail(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        passwordInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // No action needed here
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                validatePassword(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                // No action needed here
            }
        });
    }

    private void login(){
        String email=emailInput.getText().toString().trim();
        String password=passwordInput.getText().toString().trim();

        if(!validatePassword(password)||!validateEmail(email))
            return;

        LoginRequestDTO loginRequest = new LoginRequestDTO(email, password);
        AuthService authService = clientUtils.getAuthService();

        authService.login(loginRequest).enqueue(new Callback<LoginResponseDTO>() {
            @Override
            public void onResponse(Call<LoginResponseDTO> call, Response<LoginResponseDTO> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String token = response.body().getAccessToken();
                    TokenManager tokenManager=new TokenManager(requireContext());
                    tokenManager.saveToken(token);
                    updateNavigation(tokenManager.getRole());
                    NavController navController = NavHostFragment.findNavController(LoginFragment.this);;
                    navController.navigate(R.id.homeScreenFragment);
                } else {
                    loginError.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<LoginResponseDTO> call, Throwable t) {
                loginError.setVisibility(View.VISIBLE);
            }
        });
    }


    private void initializeRegisterMessage(View view) {
        TextView textView = view.findViewById(R.id.register);

        // Create a SpannableStringBuilder to concatenate the text
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();

        // Add the first part of the text
        spannableStringBuilder.append(getString(R.string.not_registered_message));

        // Create a Spannable for the "Register now" part
        String registerText = getString(R.string.register_message);
        Spannable registerSpannable = new SpannableString(registerText);

        //Get the primary color
        TypedValue typedValue = new TypedValue();
        requireActivity().getTheme().resolveAttribute(androidx.appcompat.R.attr.colorPrimary, typedValue, true);
        int color = typedValue.data;

        // Set the color to primary for the "Register now" text
        registerSpannable.setSpan(new ForegroundColorSpan(color), 0, registerText.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        // Make the "Register now" text clickable
        registerSpannable.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                NavController navController = NavHostFragment.findNavController(LoginFragment.this);;
                navController.navigate(R.id.registerFragment);
            }
        }, 0, registerText.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        // Append the "Register now" text to the builder
        spannableStringBuilder.append(" ");
        spannableStringBuilder.append(registerSpannable);

        // Set the final SpannableStringBuilder to the TextView and make it clickable
        textView.setText(spannableStringBuilder);
        textView.setMovementMethod(LinkMovementMethod.getInstance());
    }

    private void updateNavigation(String role){
        MenuInflater inflater = getActivity().getMenuInflater();
        Menu newMenu = navigationView.getMenu();
        newMenu.clear();
        switch (role){
            case "ADMIN":
                inflater.inflate(R.menu.admin_nav_menu, newMenu);
                break;
            case "EVENT_ORGANIZER":
                inflater.inflate(R.menu.organizer_nav_menu, newMenu);
                break;
            case "PROVIDER":
                inflater.inflate(R.menu.provider_nav_menu, newMenu);
                break;
            case "AUTHENTICATED_USER":
                inflater.inflate(R.menu.authenticated_user_nav_menu, newMenu);
                break;
        }
        navigationView.findViewById(R.id.logout_button).setVisibility(View.VISIBLE);
    }

    private boolean validateEmail(String email) {
        loginError.setVisibility(View.GONE);
        if (Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailLayout.setError(null);// Clear error if valid
            return true;
        } else {
            emailLayout.setError("Invalid email address"); // Set error message
            return false;
        }
    }

    private boolean validatePassword(String password){
        loginError.setVisibility(View.GONE);
        if (password.length() < 8) {
            passwordLayout.setError("Password must be at least 8 characters long");
            return false;
        }else{
            passwordLayout.setError(null);
            return true;
        }
    }
}