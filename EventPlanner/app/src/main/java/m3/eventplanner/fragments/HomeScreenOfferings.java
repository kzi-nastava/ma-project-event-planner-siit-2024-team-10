package m3.eventplanner.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import m3.eventplanner.R;
import m3.eventplanner.adapters.OfferingListAdapter;
import m3.eventplanner.models.Event;
import m3.eventplanner.models.Offering;
import m3.eventplanner.models.Product;
import m3.eventplanner.models.Service;

public class HomeScreenOfferings extends Fragment {

    private RecyclerView topOfferingRecyclerView;
    private OfferingListAdapter topOfferingListAdapter;
    private ArrayList<Offering> topOfferingList;

    private RecyclerView allOfferingRecyclerView;
    private OfferingListAdapter allOfferingListAdapter;
    private ArrayList<Offering> allOfferingList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home_screen_offerings, container, false);

        topOfferingRecyclerView = rootView.findViewById(R.id.recyclerTopOfferings);

        topOfferingList = new ArrayList<>();

        prepareTopOfferingList(topOfferingList);

        topOfferingListAdapter = new OfferingListAdapter(topOfferingList);
        topOfferingRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        topOfferingRecyclerView.setAdapter(topOfferingListAdapter);

        allOfferingRecyclerView = rootView.findViewById(R.id.recyclerAllOfferings);

        allOfferingList = new ArrayList<>();

        prepareAllOfferingList(allOfferingList);

        allOfferingListAdapter = new OfferingListAdapter(allOfferingList);
        allOfferingRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        allOfferingRecyclerView.setAdapter(allOfferingListAdapter);

        return rootView;
    }

    private void prepareTopOfferingList(ArrayList<Offering> topOfferingList){
        topOfferingList.clear();
        topOfferingList.add(new Product(1L, "Wedding Flowers", 2.5, "Provider 1", 90));
        topOfferingList.add(new Service(2L, "Wedding Makeup", 4, "Provider 2", 2500));
        topOfferingList.add(new Product(3L, "Wedding Flowers", 5, "Provider 3", 90));
        topOfferingList.add(new Service(4L, "Wedding Makeup", 3.5, "Provider 4", 2500));
        topOfferingList.add(new Service(5L, "Wedding Makeup", 1.2, "Provider 2", 2500));
    }

    private void prepareAllOfferingList(ArrayList<Offering> allOfferingList){
        allOfferingList.clear();
        allOfferingList.add(new Product(1L, "Wedding Flowers", 2.5, "Provider 1", 90));
        allOfferingList.add(new Service(2L, "Wedding Makeup", 4, "Provider 2", 2500));
        allOfferingList.add(new Product(3L, "Wedding Flowers", 5, "Provider 3", 90));
        allOfferingList.add(new Service(4L, "Wedding Makeup", 3.5, "Provider 4", 2500));
        allOfferingList.add(new Service(5L, "Wedding Makeup", 1.2, "Provider 2", 2500));
        allOfferingList.add(new Product(6L, "Wedding Flowers", 2.5, "Provider 1", 90));
        allOfferingList.add(new Service(7L, "Wedding Makeup", 4, "Provider 2", 2500));
        allOfferingList.add(new Product(8L, "Wedding Flowers", 5, "Provider 3", 90));
        allOfferingList.add(new Service(9L, "Wedding Makeup", 3.5, "Provider 4", 2500));
        allOfferingList.add(new Service(10L, "Wedding Makeup", 1.2, "Provider 2", 2500));
    }
}