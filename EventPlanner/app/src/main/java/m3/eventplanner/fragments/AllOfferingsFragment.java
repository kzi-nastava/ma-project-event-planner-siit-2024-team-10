package m3.eventplanner.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import m3.eventplanner.R;
import m3.eventplanner.adapters.OfferingListAdapter;
import m3.eventplanner.models.Offering;
import m3.eventplanner.models.Product;
import m3.eventplanner.models.Service;

public class AllOfferingsFragment extends Fragment {

    private RecyclerView recyclerView;
    private OfferingListAdapter adapter;
    private List<Offering> offeringList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_all_offerings, container, false);

        // Initialize RecyclerView
        recyclerView = rootView.findViewById(R.id.allOfferingsRecyclerView);

        // Initialize offeringList (you will replace this with actual data fetching logic)
        offeringList = getOfferingList();  // Ensure this method provides data

        // Initialize the adapter and set it to RecyclerView
        adapter = new OfferingListAdapter(offeringList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        return rootView;
    }

    // Method to simulate getting offering data
    private List<Offering> getOfferingList() {
        List<Offering> list = new ArrayList<>();

        // Example offerings data
        list.add(new Product(1L, "Wedding Flowers", 2.5, "Provider 1", 90));
        list.add(new Service(2L, "Wedding Makeup", 4, "Provider 2", 2500));
        list.add(new Product(3L, "Wedding Flowers", 5, "Provider 3", 90));
        list.add(new Service(4L, "Wedding Makeup", 3.5, "Provider 4", 2500));
        list.add(new Service(5L, "Wedding Makeup", 1.2, "Provider 2", 2500));
        list.add(new Product(6L, "Wedding Flowers", 2.5, "Provider 1", 90));
        list.add(new Service(7L, "Wedding Makeup", 4, "Provider 2", 2500));
        list.add(new Product(8L, "Wedding Flowers", 5, "Provider 3", 90));
        list.add(new Service(9L, "Wedding Makeup", 3.5, "Provider 4", 2500));
        list.add(new Service(10L, "Wedding Makeup", 1.2, "Provider 2", 2500));

        return list;
    }
}

