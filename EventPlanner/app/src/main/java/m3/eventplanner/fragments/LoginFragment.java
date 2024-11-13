package m3.eventplanner.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import m3.eventplanner.R;

public class LoginFragment extends Fragment {
    public LoginFragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initializeRegisterMessage(view);

        Button loginButton = view.findViewById(R.id.loginButton);
        loginButton.setOnClickListener(v -> {
            NavController navController = NavHostFragment.findNavController(LoginFragment.this);;
            navController.navigate(R.id.homeScreenFragment);
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
                navController.navigate(R.id.registerPersonalFragment);
            }
        }, 0, registerText.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        // Append the "Register now" text to the builder
        spannableStringBuilder.append(" ");
        spannableStringBuilder.append(registerSpannable);

        // Set the final SpannableStringBuilder to the TextView and make it clickable
        textView.setText(spannableStringBuilder);
        textView.setMovementMethod(LinkMovementMethod.getInstance());
    }
}