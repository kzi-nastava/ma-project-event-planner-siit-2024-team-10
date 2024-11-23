package m3.eventplanner.fragments;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
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

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

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

        // Spinner setup
        Spinner sortSpinner = view.findViewById(R.id.btnSort);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(requireContext(),
                R.array.sort_options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sortSpinner.setAdapter(adapter);

        // FloatingActionButton setup
        FloatingActionButton fab = view.findViewById(R.id.fab);
        fab.setOnClickListener(view1 -> {
            NavController navController = NavHostFragment.findNavController(ManageOfferingsFragment.this);
            navController.navigate(R.id.createOfferingFragment);
        });

        // Button for opening filters
        Button btnFilters = view.findViewById(R.id.btnFilters);
        btnFilters.setOnClickListener(v -> {
            showFilterBottomSheet();
        });

        // Example button for 'See More'
        MaterialCardView offeringCard = view.findViewById(R.id.offering_card);
        offeringCard.setOnClickListener(v -> {
            NavController navController = NavHostFragment.findNavController(ManageOfferingsFragment.this);
            navController.navigate(R.id.serviceDetailsFragment);
        });
    }

    // Method to display the BottomSheetDialog
    private void showFilterBottomSheet() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(requireContext());
        View bottomSheetView = getLayoutInflater().inflate(R.layout.fragment_offering_search_bar, null);
        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetDialog.show();
    }


}
