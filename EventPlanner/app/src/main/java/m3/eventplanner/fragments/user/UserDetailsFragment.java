package m3.eventplanner.fragments.user;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.viewpager2.widget.ViewPager2;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayoutMediator;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

import m3.eventplanner.BuildConfig;
import m3.eventplanner.R;
import m3.eventplanner.activities.MainActivity;
import m3.eventplanner.adapters.ImagePagerAdapter;
import m3.eventplanner.auth.TokenManager;
import m3.eventplanner.clients.ClientUtils;
import m3.eventplanner.databinding.FragmentUserDetailsBinding;
import m3.eventplanner.fragments.event.AgendaItemFormFragment;
import m3.eventplanner.fragments.event.EventDetailsViewModel;
import m3.eventplanner.models.GetUserDTO;

public class UserDetailsFragment extends Fragment {
    private FragmentUserDetailsBinding binding;
    private ClientUtils clientUtils;
    private UserDetailsViewModel viewModel;
    private ImagePagerAdapter imagePagerAdapter;

    public UserDetailsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentUserDetailsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(UserDetailsViewModel.class);
        clientUtils = new ClientUtils(requireContext());
        viewModel.initialize(clientUtils,new TokenManager(requireContext()));

        viewModel.getUser().observe(getViewLifecycleOwner(), this::populateUserDetails);
        viewModel.loadUser();

        viewModel.getError().observe(getViewLifecycleOwner(), error ->{
            Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
                }
        );

        viewModel.getSuccessMessage().observe(getViewLifecycleOwner(), message ->{
                    Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                    ((MainActivity)requireActivity()).logout();
                }
        );

        binding.editPersonalButton.setOnClickListener(v->{
            NavController navController = NavHostFragment.findNavController(UserDetailsFragment.this);
            navController.navigate(R.id.editPersonalFragment);
        });

        binding.editCompanyButton.setOnClickListener(v->{
            NavController navController = NavHostFragment.findNavController(UserDetailsFragment.this);
            navController.navigate(R.id.editCompanyFragment);
        });


        binding.changePasswordButton.setOnClickListener(v->{
            ChangePasswordFragment dialog = new ChangePasswordFragment();
            dialog.show(getChildFragmentManager(), "ChangePasswordFragment");
        });

        binding.noPersonalDetailsChangePasswordButton.setOnClickListener(v->{
            ChangePasswordFragment dialog = new ChangePasswordFragment();
            dialog.show(getChildFragmentManager(), "ChangePasswordFragment");
        });

        binding.noPersonalDetailsDeacitvateAccountButton.setOnClickListener(v->deactivateAccount());

        binding.deacitvateAccountButton.setOnClickListener(v->deactivateAccount());
    }

    private void setupImageViewPager(List<String> imageUrls) {
        imagePagerAdapter = new ImagePagerAdapter(imageUrls);
        binding.imageViewPager.setAdapter(imagePagerAdapter);

        // Connect TabLayout with ViewPager2
        new TabLayoutMediator(binding.imageTabLayout, binding.imageViewPager,
                (tab, position) -> {
                    // No title for the tabs
                }
        ).attach();
    }

    private void populateUserDetails(GetUserDTO user){
        if(Objects.equals(user.getRole(), "ADMIN") || Objects.equals(user.getRole(), "AUTHENTICATED_USER")){
            binding.noPersonalDetailsLayout.setVisibility(View.VISIBLE);
            binding.noPersonalDetailsEmail.setText(binding.noPersonalDetailsEmail.getText()+user.getEmail());
        }

        if(Objects.equals(user.getRole(), "EVENT_ORGANIZER") || Objects.equals(user.getRole(), "PROVIDER")) {
            binding.userHeader.setVisibility(View.VISIBLE);
            binding.personalDetailsCard.setVisibility(View.VISIBLE);
            binding.userName.setText(user.getFirstName()+" "+user.getLastName());
            binding.userEmail.setText(user.getEmail());
            binding.userPhone.setText(user.getPhoneNumber());
            binding.userLocation.setText(user.getLocation().toString());
            if(user.getProfilePhoto()!=null)
                Picasso.get().load(parsePhotoFilename(user.getProfilePhoto()))
                        .into(binding.profilePhoto);
        }
        if(Objects.equals(user.getRole(), "PROVIDER")) {
            binding.companyDetailsCard.setVisibility(View.VISIBLE);
            binding.companyImagesCard.setVisibility(View.VISIBLE);
            binding.companyName.setText(user.getCompany().getName());
            binding.companyDescription.setText(user.getCompany().getDescription());
            binding.companyPhone.setText(user.getCompany().getPhoneNumber());
            binding.companyLocation.setText(user.getCompany().getLocation().toString());
            setupImageViewPager(user.getCompany().getPhotos().stream()
                    .map(this::parsePhotoFilename)
                    .collect(Collectors.toList()));
        }
    }

    private String parsePhotoFilename(String photo){
        String fileName = photo;
        if (photo.contains("\\")) {
            fileName = photo.substring(photo.lastIndexOf("\\") + 1);
        }
        if (fileName.contains("/")) {
            fileName = fileName.substring(fileName.lastIndexOf("/") + 1);
        }
        return "http://"+BuildConfig.IP_ADDR + ":8080/api/images/" + fileName;
    }

    private void deactivateAccount(){
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());

        builder.setTitle("Confirm deactivation")
                .setMessage("Are you sure you want to deactivate your account?")
                .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        viewModel.deactivateAccount();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        builder.create().show();
    }
}