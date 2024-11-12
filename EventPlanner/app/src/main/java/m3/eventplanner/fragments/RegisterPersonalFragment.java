package m3.eventplanner.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;

import m3.eventplanner.R;

public class RegisterPersonalFragment extends Fragment {

    public RegisterPersonalFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_register_personal, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Button submitButton = view.findViewById(R.id.submit);
        submitButton.setOnClickListener(v -> {
            RadioButton provider_role=view.findViewById(R.id.product_service_provider);
            NavController navController = NavHostFragment.findNavController(RegisterPersonalFragment.this);
            if(provider_role.isChecked()){
                navController.navigate(R.id.registerCompanyFragment);
            }else{
                navController.navigate(R.id.homeScreenFragment);
            }
        });
    }
}