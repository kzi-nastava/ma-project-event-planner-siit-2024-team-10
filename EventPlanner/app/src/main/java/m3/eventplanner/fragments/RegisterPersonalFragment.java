package m3.eventplanner.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import m3.eventplanner.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RegisterPersonalFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
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
            //TODO
        });
    }
}