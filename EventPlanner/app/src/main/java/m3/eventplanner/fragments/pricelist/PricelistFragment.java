package m3.eventplanner.fragments.pricelist;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import m3.eventplanner.R;
import m3.eventplanner.adapters.PricelistItemAdapter;
import m3.eventplanner.clients.ClientUtils;
import m3.eventplanner.databinding.FragmentOpenEventReportBinding;
import m3.eventplanner.databinding.FragmentPricelistBinding;

public class PricelistFragment extends Fragment {
    private FragmentPricelistBinding binding;
    private PricelistViewModel viewModel;
    private PricelistItemAdapter adapter;
    private ClientUtils clientUtils;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentPricelistBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        RecyclerView recyclerView = view.findViewById(R.id.pricelistRecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new PricelistItemAdapter(new ArrayList<>(), (offeringId, newPrice, newDiscount) -> {
            viewModel.updateItem(offeringId, newPrice, newDiscount);
        });

        recyclerView.setAdapter(adapter);

        viewModel = new ViewModelProvider(this).get(PricelistViewModel.class);
        binding.exportToPdfButton.setOnClickListener(v->{
            viewModel.exportToPdf();
        });
        clientUtils = new ClientUtils(requireContext());
        viewModel.initialize(requireContext(),clientUtils);

        viewModel.getItems().observe(getViewLifecycleOwner(), adapter::updateItems);
        viewModel.fetchItems();
    }
}
