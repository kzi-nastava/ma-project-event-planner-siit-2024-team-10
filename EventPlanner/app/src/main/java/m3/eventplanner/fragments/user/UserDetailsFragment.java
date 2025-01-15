package m3.eventplanner.fragments.user;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.google.android.material.tabs.TabLayoutMediator;
import java.util.List;
import java.util.Arrays;

import m3.eventplanner.R;
import m3.eventplanner.adapters.ImagePagerAdapter;
import m3.eventplanner.databinding.FragmentUserDetailsBinding;

public class UserDetailsFragment extends Fragment {
    private FragmentUserDetailsBinding binding;
    private ImagePagerAdapter imagePagerAdapter;

    public UserDetailsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentUserDetailsBinding.inflate(inflater, container, false);
        setupImageViewPager();
        return binding.getRoot();
    }

    private void setupImageViewPager() {
        // Sample image URLs - replace with your actual image URLs
        List<String> imageUrls = Arrays.asList(
                "http://192.168.54.139:8080/api/images/company1.jpg",
                "http://192.168.54.139:8080/api/images/company3.jpg"
        );

        imagePagerAdapter = new ImagePagerAdapter(imageUrls);
        binding.imageViewPager.setAdapter(imagePagerAdapter);

        // Connect TabLayout with ViewPager2
        new TabLayoutMediator(binding.imageTabLayout, binding.imageViewPager,
                (tab, position) -> {
                    // No title for the tabs
                }
        ).attach();

        // Optional: Add page change callback
        binding.imageViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                // Handle page change if needed
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}