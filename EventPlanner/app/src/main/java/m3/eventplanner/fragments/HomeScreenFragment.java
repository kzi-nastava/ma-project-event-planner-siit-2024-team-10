package m3.eventplanner.fragments;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import m3.eventplanner.R;

public class HomeScreenFragment extends Fragment {
    private TabLayout tabLayout;
    private ViewPager2 viewPager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_homescreen, container, false);

        tabLayout = rootView.findViewById(R.id.homeScreenTab);
        viewPager = rootView.findViewById(R.id.homeScreenViewPager);

        // Set up the ViewPager with the adapter
        FragmentStateAdapter pagerAdapter = new FragmentStateAdapter(getChildFragmentManager(), getLifecycle()) {
            @Override
            public Fragment createFragment(int position) {
                if (position == 0) {
                    return null;
                    //return new HomeScreenEvents();
                } else {
                    return null;
                    //return new HomeScreenOfferings();
                }
            }

            @Override
            public int getItemCount() {
                return 2;
            }
        };
        viewPager.setAdapter(pagerAdapter);

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            if (position == 0) {
                tab.setText("Events");
            } else {
                tab.setText("Offerings");
            }
        }).attach();

        return rootView;
    }
}

