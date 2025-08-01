package m3.eventplanner.fragments.offering;

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
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import m3.eventplanner.R;
import m3.eventplanner.adapters.ProviderOfferingsAdapter;
import m3.eventplanner.databinding.FragmentProviderOfferingsBinding;
import m3.eventplanner.models.GetOfferingDTO;
import m3.eventplanner.models.GetProviderDTO;

public class ProviderOfferingsFragment extends Fragment {

    private RecyclerView recyclerView;
    private FragmentProviderOfferingsBinding binding;
    private ProviderOfferingsAdapter adapter;
    private ProviderOfferingsViewModel viewModel;
    private List<GetOfferingDTO> offerings = new ArrayList<>();
    private GetProviderDTO provider;
    private TextView textCompanyName;
    private TextView textCompanyPhone;
    private TextView textCompanyDescription;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            provider = getArguments().getParcelable("provider");
        }
        viewModel = new ViewModelProvider(this).get(ProviderOfferingsViewModel.class);
        viewModel.initialize(requireContext());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentProviderOfferingsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews(view);
        setupRecyclerView();
        setupObservers();

        // Postavi podatke kompanije ako postoje
        if (provider != null && provider.getCompany() != null) {
            textCompanyName.setText(provider.getCompany().getName());
            textCompanyPhone.setText("Phone: " + provider.getCompany().getPhoneNumber());
            textCompanyDescription.setText(provider.getCompany().getDescription());
        }

        viewModel.loadOfferings();
    }

    private void initViews(View view) {
        recyclerView = view.findViewById(R.id.recycler_view_offerings);
        recyclerView = view.findViewById(R.id.recycler_view_offerings);
        textCompanyName = view.findViewById(R.id.text_company_name);
        textCompanyPhone = view.findViewById(R.id.text_company_phone);
        textCompanyDescription = view.findViewById(R.id.text_company_description);
    }

    private void setupRecyclerView() {
        adapter = new ProviderOfferingsAdapter(offerings, getContext(), viewModel);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
    }

    private void setupObservers() {
        viewModel.getOfferingsLiveData().observe(getViewLifecycleOwner(), offeringsList -> {
            if (offeringsList != null) {
                offerings.clear();
                offerings.addAll(offeringsList);
                adapter.notifyDataSetChanged();
            }
        });

        viewModel.getErrorLiveData().observe(getViewLifecycleOwner(), error -> {
            if (error != null && !error.isEmpty()) {
                Toast.makeText(getContext(), "Error loading: " + error, Toast.LENGTH_SHORT).show();
            }
        });

        viewModel.getLoadingLiveData().observe(getViewLifecycleOwner(), isLoading -> {
        });
    }
}
