package m3.eventplanner.fragments.event;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointForward;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.textfield.TextInputLayout;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.stream.IntStream;

import m3.eventplanner.R;
import m3.eventplanner.auth.TokenManager;
import m3.eventplanner.clients.ClientUtils;
import m3.eventplanner.databinding.FragmentCreateEventBinding;
import m3.eventplanner.databinding.FragmentEditEventBinding;
import m3.eventplanner.models.CreateEventDTO;
import m3.eventplanner.models.CreateLocationDTO;
import m3.eventplanner.models.GetEventDTO;
import m3.eventplanner.models.GetEventTypeDTO;

public class EditEventFragment extends Fragment {
    private FragmentEditEventBinding binding;
    private GetEventTypeDTO eventType;
    private List<GetEventTypeDTO> eventTypes;
    private EditEventViewModel viewModel;
    private ClientUtils clientUtils;
    private boolean noEventType=false;
    private boolean isOpen=true;
    private String eventDate;

    private int eventId;

    public EditEventFragment() {
        // Required empty public constructor
    }
    public static EditEventFragment newInstance() {
        EditEventFragment fragment = new EditEventFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentEditEventBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(EditEventViewModel.class);
        clientUtils = new ClientUtils(requireContext());
        viewModel.initialize(clientUtils);

        setupObservers();
        setupListeners();
        setupDatePicker();
        setupValidation();

        if (getArguments() != null) {
            eventId = getArguments().getInt("selectedEventId");
        }

        binding.submit.setOnClickListener(v->{
            if(!isFormValid())
                return;
//            String name=binding.name.getEditText().getText().toString().trim();
//            String description=binding.description.getEditText().getText().toString().trim();
//            int maxParticipants=Integer.parseInt(binding.maxParticipants.getEditText().getText().toString().trim());
//            String country=binding.country.getEditText().getText().toString().trim();
//            String city=binding.city.getEditText().getText().toString().trim();
//            String street=binding.street.getEditText().getText().toString().trim();
//            String houseNumber=binding.houseNumber.getEditText().getText().toString().trim();
//            int organizerId=new TokenManager(requireContext()).getUserId();
//            CreateEventDTO eventDTO = new CreateEventDTO(noEventType?0:eventType.getId(),organizerId,name,description,maxParticipants,isOpen,eventDate,new CreateLocationDTO(country,city,street,houseNumber));
//            this.viewModel.createEvent(eventDTO);
            //TODO: edit event
        });
    }

    private void setupObservers() {
        viewModel.getEventTypes().observe(getViewLifecycleOwner(), this::setUpEventTypeSpinner);
        viewModel.loadEventTypes();

        viewModel.getEvent().observe(getViewLifecycleOwner(), this::populateEventDetails);

        viewModel.getError().observe(getViewLifecycleOwner(), error ->
                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show()
        );

        viewModel.getSuccessMessage().observe(getViewLifecycleOwner(), message ->{
                    Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
//                    NavController navController = NavHostFragment.findNavController(CreateEventFragment.this);
//                    navController.navigate(R.id.homeScreenFragment);
            //TODO:navigate to event details page
                }
        );
    }

    private void populateEventDetails(GetEventDTO event) {
        binding.name.getEditText().setText(event.getName());
        binding.description.getEditText().setText(event.getDescription());
        binding.maxParticipants.getEditText().setText(String.valueOf(event.getMaxParticipants()));
        binding.country.getEditText().setText(event.getLocation().getCountry());
        binding.city.getEditText().setText(event.getLocation().getCity());
        binding.street.getEditText().setText(event.getLocation().getStreet());
        binding.houseNumber.getEditText().setText(event.getLocation().getHouseNumber());
        this.eventDate=event.getDate();
        binding.eventDate.getEditText().setText(event.getDate());
        if(event.isOpen())
            binding.buttonOpen.setChecked(true);
        else
            binding.buttonClosed.setChecked(false);

        if(event.getEventType()==null){
            binding.noEventTypeCheckbox.setChecked(true);
            return;
        }

        if(eventTypes.stream()
                .noneMatch(eventType -> eventType.getId()==event.getEventType().getId())){
            eventTypes.add(event.getEventType());
        }
        binding.eventTypeSpinner.setSelection(
                IntStream.range(0, eventTypes.size())
                .filter(i -> eventTypes.get(i).getId()==event.getEventType().getId())
                .findFirst()
                .orElse(-1));
    }

    private void setupListeners() {
        binding.noEventTypeCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
                noEventType=isChecked;
                if(isChecked){
                    binding.eventTypeSpinner.setVisibility(View.GONE);
                    binding.eventTypeText.setVisibility(View.GONE);
                }
                else{
                    binding.eventTypeSpinner.setVisibility(View.VISIBLE);
                    binding.eventTypeText.setVisibility(View.VISIBLE);
                }

            }
        });

        binding.eventPublicityGroup.addOnButtonCheckedListener(new MaterialButtonToggleGroup.OnButtonCheckedListener() {
            @Override
            public void onButtonChecked(MaterialButtonToggleGroup group, int checkedId, boolean isChecked) {
                if (isChecked) {
                    if (checkedId == R.id.buttonOpen) {
                        isOpen=true;
                    } else if (checkedId == R.id.buttonClosed) {
                        isOpen=false;
                    }
                }
            }
        });
    }

    private void setUpEventTypeSpinner(List<GetEventTypeDTO> eventTypes) {
        this.eventTypes=eventTypes;
        GetEventTypeDTO defaultOption=new GetEventTypeDTO();
        defaultOption.setId(-1);
        defaultOption.setName("Select event type");
        eventTypes.add(0,defaultOption);
        Spinner eventTypeSpinner = binding.eventTypeSpinner;
        ArrayAdapter<GetEventTypeDTO> adapter = getGetEventTypeDTOArrayAdapter(eventTypes);
        eventTypeSpinner.setAdapter(adapter);
        eventTypeSpinner.setTag(eventTypes);

        eventTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!eventTypes.isEmpty()) {
                    GetEventTypeDTO selectedEventType = eventTypes.get(position);
                    if (selectedEventType != null) {
                        eventType=selectedEventType;
                    }
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        viewModel.loadEventDetails(eventId);
    }

    @NonNull
    private ArrayAdapter<GetEventTypeDTO> getGetEventTypeDTOArrayAdapter(List<GetEventTypeDTO> eventTypes) {
        ArrayAdapter<GetEventTypeDTO> adapter = new ArrayAdapter<GetEventTypeDTO>(requireContext(), android.R.layout.simple_spinner_item, eventTypes) {
            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView textView = (TextView) view;
                textView.setText(eventTypes.get(position).getName());
                return view;
            }

            @NonNull
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                TextView textView = (TextView) super.getView(position, convertView, parent);
                textView.setText(eventTypes.get(position).getName());
                return textView;
            }
        };

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        return adapter;
    }

    private void setupDatePicker() {
        MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select event date")
                .build();

        binding.eventDateEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!datePicker.isAdded())
                    datePicker.show(getParentFragmentManager(), "event_date_picker");
            }
        });

        datePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Long>() {
            @Override
            public void onPositiveButtonClick(Long selection) {
                eventDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(selection);

                SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
                String date = dateFormat.format(selection);
                binding.eventDateEditText.setText(date);
            }
        });
    }

    private void setupValidation(){
        binding.name.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                validateRequiredField(binding.name);
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        binding.description.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                validateRequiredField(binding.description);
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        binding.maxParticipants.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                validateRequiredField(binding.maxParticipants);
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        binding.country.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                validateRequiredField(binding.country);
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        binding.city.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                validateRequiredField(binding.city);
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        binding.street.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                validateRequiredField(binding.street);
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        binding.houseNumber.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                validateRequiredField(binding.houseNumber);
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        binding.eventDate.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                validateRequiredField(binding.eventDate);
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });
    }

    private boolean validateRequiredField(TextInputLayout textInputLayout) {
        if (TextUtils.isEmpty(textInputLayout.getEditText().getText())) {
            textInputLayout.setError("Required field");
            return false;
        } else {
            textInputLayout.setError(null);
            return true;
        }
    }

    private boolean validateEventType(){
        if(noEventType)
            return true;
        if(eventType.getId()==-1){
            binding.eventTypeError.setVisibility(View.VISIBLE);
            return false;
        }
        else{
            binding.eventTypeError.setVisibility(View.GONE);
            return true;
        }
    }

    private boolean isFormValid(){
        boolean isValid=true;
        if(!validateRequiredField(binding.name))
            isValid=false;
        if(!validateRequiredField(binding.description))
            isValid=false;
        if(!validateRequiredField(binding.maxParticipants))
            isValid=false;
        if(!validateRequiredField(binding.country))
            isValid=false;
        if(!validateRequiredField(binding.city))
            isValid=false;
        if(!validateRequiredField(binding.street))
            isValid=false;
        if(!validateRequiredField(binding.houseNumber))
            isValid=false;
        if(!validateRequiredField(binding.eventDate))
            isValid=false;
        if(!validateEventType())
            isValid=false;
        return isValid;
    }
}