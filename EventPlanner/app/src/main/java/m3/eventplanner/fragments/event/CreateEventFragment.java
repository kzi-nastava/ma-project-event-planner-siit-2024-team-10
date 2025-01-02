package m3.eventplanner.fragments.event;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.material.button.MaterialButtonToggleGroup;

import java.util.List;

import m3.eventplanner.R;
import m3.eventplanner.clients.ClientUtils;
import m3.eventplanner.databinding.FragmentCreateEventBinding;
import m3.eventplanner.databinding.FragmentEventDetailsBinding;
import m3.eventplanner.models.GetEventDTO;
import m3.eventplanner.models.GetEventTypeDTO;

public class CreateEventFragment extends Fragment {
    private FragmentCreateEventBinding binding;
    private GetEventTypeDTO eventType;
    private CreateEventViewModel viewModel;
    private ClientUtils clientUtils;
    private boolean noEventType=false;
    private boolean isOpen=true;

    public CreateEventFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentCreateEventBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(CreateEventViewModel.class);
        clientUtils = new ClientUtils(requireContext());
        viewModel.initialize(clientUtils);

        viewModel.getEventTypes().observe(getViewLifecycleOwner(), this::setUpEventTypeSpinner);
        viewModel.loadEventTypes();

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
                    } else {
                    }
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
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
}