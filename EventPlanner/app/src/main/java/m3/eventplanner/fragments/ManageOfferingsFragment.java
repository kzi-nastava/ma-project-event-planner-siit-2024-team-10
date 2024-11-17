package m3.eventplanner.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

import m3.eventplanner.R;
import m3.eventplanner.adapters.OfferingListAdapter;
import m3.eventplanner.models.Offering;

public class ManageOfferingsFragment extends Fragment {

    private static final String TAG = "ManageOfferingsFragment";
    private RecyclerView allOfferingRecyclerView;
    private OfferingListAdapter allOfferingListAdapter;
    private ArrayList<Offering> allOfferingList;

    public ManageOfferingsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_manage_offerings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        FloatingActionButton fab = view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavController navController = NavHostFragment.findNavController(ManageOfferingsFragment.this);
                navController.navigate(R.id.createOfferingFragment);
            }
        });

        // Example button for 'See More' - you can replace this logic with another appropriate one
        Button button = view.findViewById(R.id.offering_see_more);
        button.setOnClickListener(v -> {
            NavController navController = NavHostFragment.findNavController(ManageOfferingsFragment.this);
            navController.navigate(R.id.serviceDetailsFragment);
        });

    }

}
