package m3.eventplanner.fragments.offering;

import android.os.Bundle;
import android.util.Log;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.slider.RangeSlider;
import com.google.android.material.slider.Slider;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import m3.eventplanner.R;
import m3.eventplanner.adapters.OfferingListAdapter;
import m3.eventplanner.auth.TokenManager;
import m3.eventplanner.clients.ClientUtils;
import m3.eventplanner.databinding.FragmentFavouritesBinding;
import m3.eventplanner.databinding.FragmentManageOfferingsBinding;
import m3.eventplanner.fragments.user.FavouritesViewModel;
import m3.eventplanner.models.GetOfferingCategoryDTO;
import m3.eventplanner.models.Offering;

public class ManageOfferingsFragment extends Fragment {
    private FragmentManageOfferingsBinding binding;
    private ManageOfferingsViewModel viewModel;
    private ClientUtils clientUtils;
    private Integer accountId;
    private Boolean initialLoad = true;
    private OfferingListAdapter offeringAdapter;

    public ManageOfferingsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentManageOfferingsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(ManageOfferingsViewModel.class);
        TokenManager tokenManager = new TokenManager(requireContext());
        this.accountId= tokenManager.getAccountId();
        if(accountId==0)
            accountId=null;
        this.clientUtils = new ClientUtils(requireContext());
        viewModel.initialize(clientUtils,tokenManager.getUserId());
        binding.contentRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        viewModel.fetchCategories();

        viewModel.getPagedData().observe(getViewLifecycleOwner(), pagedOfferings -> {
            if (pagedOfferings != null && !pagedOfferings.getContent().equals(new ArrayList<>())) {
                offeringAdapter = new OfferingListAdapter(pagedOfferings.getContent());
                binding.contentRecyclerView.setAdapter(offeringAdapter);
                offeringAdapter.notifyDataSetChanged();
                binding.noCardsFoundTextView.setVisibility(View.GONE);
                binding.paginationBar.paginationCurrentPage.setText(String.valueOf("Page "+ (viewModel.currentPage + 1) +" of "+ viewModel.totalPages));
                binding.paginationBar.totalNumberOfElements.setText(String.format("Total Elements: %d", viewModel.totalElements));
            } else {
                binding.noCardsFoundTextView.setVisibility(View.VISIBLE);
                binding.contentRecyclerView.setVisibility(View.GONE);
                binding.paginationBar.getRoot().setVisibility(View.GONE);
            }
        });

        viewModel.getError().observe(getViewLifecycleOwner(), errorMessage -> {
            if (errorMessage != null && !errorMessage.isEmpty()) {
                Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
            }
        });

        setUpOfferingSortSpinners();
        setUpFilterButtons();
        setUpPaginationButtons();
        setUpSearchBar();
        setUpOfferingRadioGroup();
    }

    private void setUpOfferingSortSpinners() {
        Spinner offeringSortBySpinner = binding.offeringSearchBar.btnSortOfferingsBy;
        Spinner offeringSortDirectionSpinner = binding.offeringSearchBar.btnSortOfferingDirection;

        offeringSortBySpinner.setAdapter(createSpinnerAdapter(R.array.offering_sort_criteria));
        offeringSortDirectionSpinner.setAdapter(createSpinnerAdapter(R.array.sort_directions));

        Map<String, String> sortCriteriaMapping = new HashMap<>();
        sortCriteriaMapping.put("Any", null);
        sortCriteriaMapping.put("Name", "name");
        sortCriteriaMapping.put("Price", "price");
        sortCriteriaMapping.put("Average Rating", "averageRating");
        sortCriteriaMapping.put("City", "location.city");

        Map<String, String> sortDirectionMapping = new HashMap<>();
        sortDirectionMapping.put("Ascending", "ASC");
        sortDirectionMapping.put("Descending", "DESC");

        AdapterView.OnItemSelectedListener sortListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedSortBy = offeringSortBySpinner.getSelectedItem().toString();
                String selectedSortDirection = offeringSortDirectionSpinner.getSelectedItem().toString();

                String sortBy = sortCriteriaMapping.get(selectedSortBy);
                String sortDirection = sortDirectionMapping.get(selectedSortDirection);

                viewModel.loadSortedOfferings(0, sortBy, sortDirection);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        };

        offeringSortBySpinner.setOnItemSelectedListener(sortListener);
        offeringSortDirectionSpinner.setOnItemSelectedListener(sortListener);
    }

    private ArrayAdapter<String> createSpinnerAdapter(int arrayResId) {
        Collection<String> data = new ArrayList<>();
        String[] array = getResources().getStringArray(arrayResId);
        for (String item : array) {
            data.add(item);
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, new ArrayList<>(data));
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        return adapter;
    }

    private void setUpFilterButtons() {
        Button btnOfferingFilters = binding.offeringSearchBar.filterOfferingsButton;
        btnOfferingFilters.setOnClickListener(v -> {
            Boolean isService = getSelectedOfferingType();
            showOfferingFilterBottomSheet(isService);
        });
    }

    private Boolean getSelectedOfferingType() {
        RadioGroup offeringRadioGroup = binding.offeringSearchBar.offeringRadioGroup;
        int selectedId = offeringRadioGroup.getCheckedRadioButtonId();
        if (selectedId != -1) {
            String selectedText = ((RadioButton) binding.offeringSearchBar.getRoot().findViewById(selectedId)).getText().toString();
            return "Service".equalsIgnoreCase(selectedText);
        } else {
            Log.e("HomeScreenFragment", "No radio button selected.");
            return null;
        }
    }

    private void showOfferingFilterBottomSheet(Boolean isService) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(requireContext());
        View bottomSheetView = getLayoutInflater().inflate(R.layout.homescreen_offering_filter, null);
        bottomSheetDialog.setContentView(bottomSheetView);

        RangeSlider priceRangeSlider = bottomSheetView.findViewById(R.id.price_range_slider);

        viewModel.getCategories().observe(getViewLifecycleOwner(), categories -> {
            if (categories != null && !categories.isEmpty()) {
                setUpCategorySpinner(bottomSheetView, categories);
            } else {
                Toast.makeText(getContext(),R.string.category_error, Toast.LENGTH_SHORT);
            }
        });

        viewModel.getHighestPrice().observe(getViewLifecycleOwner(), highestPrice -> {
            if (highestPrice != null) {
                priceRangeSlider.setValueTo(highestPrice.floatValue());

                List<Float> currentValues = priceRangeSlider.getValues();
                List<Float> adjustedValues = new ArrayList<>();
                for (Float value : currentValues) {
                    adjustedValues.add(Math.min(value, highestPrice.floatValue()));
                }
                priceRangeSlider.setValues(priceRangeSlider.getValueFrom(), highestPrice.floatValue());
            } else {
                Toast.makeText(getContext(), R.string.highest_price_error, Toast.LENGTH_SHORT);
            }
        });
        viewModel.fetchHighestPrice(isService);

        setUpVisibilityForOfferingType(bottomSheetView, isService);

        Spinner categorySpinner = bottomSheetView.findViewById(R.id.category_spinner);

        Button offeringFilterSendButton = bottomSheetView.findViewById(R.id.send_offering_filter);
        Button resetFiltersButton = bottomSheetView.findViewById(R.id.restart_offering_filter);

        offeringFilterSendButton.setOnClickListener(v ->{
            GetOfferingCategoryDTO selectedCategory = (GetOfferingCategoryDTO) categorySpinner.getSelectedItem();

            Integer categoryId = null;
            if (selectedCategory != null && selectedCategory.getId() != -1){
                categoryId = selectedCategory.getId();
            }

            String location = ((TextInputEditText) bottomSheetView.findViewById(R.id.offering_location_text_field)).getText().toString();
            if (location.isEmpty()){
                location = null;
            }

            Integer duration = null;
            if (isService){
                TextInputEditText durationEditText = bottomSheetView.findViewById(R.id.service_duration);
                String durationText = durationEditText.getText().toString();
                if (!durationText.isEmpty()){
                    try{
                        duration = Integer.parseInt(durationText);
                    }catch (NumberFormatException e){
                        Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT);
                    }
                }
            }

            Double minRating = (double) ((Slider) bottomSheetView.findViewById(R.id.min_rating_slider)).getValue();
            if (minRating == 0.0){
                minRating = null;
            }
            Integer minDiscount = (int) ((Slider) bottomSheetView.findViewById(R.id.min_discount_slider)).getValue();
            if (minDiscount == 0.0){
                minDiscount = null;
            }

            List<Float> rangeValues = priceRangeSlider.getValues();
            Integer priceFrom = rangeValues.size() > 0 ? rangeValues.get(0).intValue() : null;
            Integer priceTo = rangeValues.size() > 1 ? rangeValues.get(1).intValue() : null;

            if(priceTo == 0){
                priceTo = null;
            }

            SwitchCompat switchAvailable = bottomSheetView.findViewById(R.id.switch_available);
            Boolean isAvailable = switchAvailable.isChecked() ? true : null;

            viewModel.loadFilteredOfferings(0, categoryId,location,priceFrom,priceTo,duration,minDiscount,minRating,isAvailable, this.initialLoad, this.accountId);

            bottomSheetDialog.dismiss();
            initialLoad = false;
        });

        resetFiltersButton.setOnClickListener(v->{
            viewModel.resetFilters();
            bottomSheetDialog.dismiss();
        });

        bottomSheetDialog.show();
    }

    private void setUpCategorySpinner(View bottomSheetView, List<GetOfferingCategoryDTO> categories) {
        Spinner categorySpinner = bottomSheetView.findViewById(R.id.category_spinner);
        if (categorySpinner != null) {
            List<GetOfferingCategoryDTO> spinnerList = new ArrayList<>();
            GetOfferingCategoryDTO anyOption = new GetOfferingCategoryDTO();
            anyOption.setName("Any");
            anyOption.setId(-1);
            spinnerList.add(anyOption);
            spinnerList.addAll(categories);

            ArrayAdapter<GetOfferingCategoryDTO> adapter = new ArrayAdapter<GetOfferingCategoryDTO>(requireContext(), android.R.layout.simple_spinner_item, spinnerList) {
                @Override
                public View getDropDownView(int position, View convertView, ViewGroup parent) {
                    View view = super.getDropDownView(position, convertView, parent);
                    TextView textView = (TextView) view;
                    textView.setText(spinnerList.get(position).getName());
                    return view;
                }

                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    TextView textView = (TextView) super.getView(position, convertView, parent);
                    textView.setText(spinnerList.get(position).getName());
                    return textView;
                }
            };

            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            categorySpinner.setAdapter(adapter);
            categorySpinner.setTag(spinnerList);
        } else {
            Log.e("HomeScreenFragment", String.valueOf(R.string.spinner_initialization_error));
        }
    }

    private void setUpVisibilityForOfferingType(View bottomSheetView, Boolean isService) {
        View serviceDurationView = bottomSheetView.findViewById(R.id.service_duration);
        if (isService) {
            serviceDurationView.setVisibility(View.VISIBLE);
        } else {
            serviceDurationView.setVisibility(View.GONE);
        }
    }

    private void setUpPaginationButtons() {
        binding.paginationBar.paginationForwardButton.setOnClickListener(v -> {
            viewModel.fetchNextPage();
        });
        binding.paginationBar.paginationBackButton.setOnClickListener(v -> {
            viewModel.fetchPreviousPage();
        });
    }

    private void setUpSearchBar() {
        binding.offeringSearchBar.title.setText("Your Offerings");
        binding.offeringSearchBar.offeringSearchText.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                viewModel.loadSearchedOfferings(0,query);
                return true;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }

        });

    }

    private void setUpOfferingRadioGroup() {
        binding.offeringSearchBar.offeringRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            Boolean isService = getSelectedOfferingType();
            if (isService != null) {
                viewModel.loadOfferings(0, isService, this.initialLoad, this.accountId);
                viewModel.resetFilters();
            } else {
                Log.e("ManageOfferingsFragment", "Invalid offering type selection.");
            }
        });
    }
}
