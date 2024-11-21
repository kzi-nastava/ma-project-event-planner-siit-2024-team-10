package m3.eventplanner.fragments;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.google.android.material.button.MaterialButtonToggleGroup;
import m3.eventplanner.R;

public class HomeScreenFragment extends Fragment {

    private MaterialButtonToggleGroup toggleGroup;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_homescreen, container, false);

        // Initialize toggle group
        toggleGroup = rootView.findViewById(R.id.toggleButton);

        // Load the default fragment (e.g., Top Events)
        if (savedInstanceState == null) {
            loadFragment(new TopEventsFragment());
        }

        // Set up the button checked listener for toggle group
        toggleGroup.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (isChecked) {
                Fragment fragment = null;
                if (checkedId == R.id.tabTopEvents) {
                    fragment = new TopEventsFragment();
                }
                else if (checkedId == R.id.tabAllEvents) {
                    fragment = new AllEventsFragment();
                } else if (checkedId == R.id.tabTopOfferings) {
                    fragment = new TopOfferingsFragment();
                } else if (checkedId == R.id.tabAllOfferings) {
                    fragment = new AllOfferingsFragment();
                }
                if (fragment != null) {
                    loadFragment(fragment);
                }
            }
        });

        return rootView;
    }

    // Helper method to replace the current fragment
    private void loadFragment(Fragment fragment) {
        getChildFragmentManager().beginTransaction()
                .replace(R.id.homeScreenViewPager, fragment)
                .commit();
    }
}
