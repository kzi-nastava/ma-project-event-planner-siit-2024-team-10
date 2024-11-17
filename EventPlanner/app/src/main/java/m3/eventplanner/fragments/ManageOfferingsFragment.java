package m3.eventplanner.fragments;

import android.os.Bundle;
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_manage_offerings, container, false);

        FloatingActionButton fab = rootView.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Here's a Snackbar", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        Button button = rootView.findViewById(R.id.offering_see_more);
        button.setOnClickListener(v -> {
            NavController navController = NavHostFragment.findNavController(ManageOfferingsFragment.this);;
            navController.navigate(R.id.serviceDetailsFragment);
        });
        return rootView;
    }
}