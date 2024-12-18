package m3.eventplanner.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioGroup;

import m3.eventplanner.R;

public class RegisterFragment extends Fragment {
    private RadioGroup roleRadioGroup;
    private LinearLayout companyInfoContainer;

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

        roleRadioGroup = view.findViewById(R.id.role_radio_group);
        companyInfoContainer = view.findViewById(R.id.company_info);

        roleRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.product_service_provider) {
                companyInfoContainer.setVisibility(View.VISIBLE);
            } else if (checkedId == R.id.event_organizer) {
                companyInfoContainer.setVisibility(View.GONE);
            }
        });

        return view;
    }
}