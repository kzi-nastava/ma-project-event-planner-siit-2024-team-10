package m3.eventplanner.fragments.event;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.timepicker.MaterialTimePicker;

import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

import m3.eventplanner.R;
import m3.eventplanner.clients.ClientUtils;
import m3.eventplanner.databinding.FragmentAgendaItemFormBinding;
import m3.eventplanner.fragments.eventtype.EventTypeFormFragment;
import m3.eventplanner.fragments.eventtype.EventTypeFormViewModel;
import m3.eventplanner.models.AgendaItem;
import m3.eventplanner.models.GetAgendaItemDTO;
import m3.eventplanner.models.GetOfferingCategoryDTO;


public class AgendaItemFormFragment extends DialogFragment{

    private OnAgendaItemFormFilledListener listener;
    private GetAgendaItemDTO agendaItem;
    private FragmentAgendaItemFormBinding binding;
    private MaterialTimePicker startTimePicker, endTimePicker;
    private LocalTime startTime, endTime;

    public interface OnAgendaItemFormFilledListener {
        void onAgendaItemFormFilled(int id, String name, String description, LocalTime startTime, LocalTime endTime, String location, boolean edit);
    }

    public static EventTypeFormFragment newInstance() {
        return new EventTypeFormFragment();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listener = (AgendaItemFormFragment.OnAgendaItemFormFilledListener) getParentFragment();
        } catch (ClassCastException e) {
            throw new ClassCastException("Parent fragment must implement FormDialogListener");
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null)
            agendaItem = getArguments().getParcelable("selectedAgendaItem");
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        binding = FragmentAgendaItemFormBinding.inflate(requireActivity().getLayoutInflater());

        initializeForm();
        setupValidation();

        builder.setView(binding.getRoot())
                .setTitle(agendaItem==null?"Create Agenda Item":"Edit Agenda Item")
                .setPositiveButton("Submit", (dialog, id) -> {
                    String name = binding.nameInput.getEditText().getText().toString().trim();
                    String description = binding.descriptionInput.getEditText().getText().toString().trim();
                    String location = binding.locationInput.getEditText().getText().toString().trim();
                    if(isFormValid())
                        listener.onAgendaItemFormFilled(agendaItem==null? 0 : agendaItem.getId(),name, description, startTime, endTime, location, agendaItem!=null);
                })
                .setNegativeButton("Cancel", (dialog, id) -> dialog.dismiss());

        return builder.create();
    }

    private void initializeForm() {
        MaterialTimePicker.Builder timePickerBuilder = new MaterialTimePicker.Builder();

        if(agendaItem!=null) {
            timePickerBuilder.setHour(agendaItem.getStartTime().getHour()).setMinute(agendaItem.getStartTime().getMinute());
            binding.startTimeInput.getEditText().setText(agendaItem.getStartTime().toString());
        }

        startTimePicker = timePickerBuilder.setTitleText("Start time").build();

        startTimePicker.addOnPositiveButtonClickListener(v -> {
            startTime=LocalTime.of(startTimePicker.getHour(), startTimePicker.getMinute());
            binding.startTimeInput.getEditText().setText(startTime.toString());
        });

        binding.startTimeInput.getEditText().setOnClickListener(v->{
            if(!startTimePicker.isAdded())
                startTimePicker.show(getParentFragmentManager(),"startTimePicker");
        });

        timePickerBuilder = new MaterialTimePicker.Builder();

        if(agendaItem!=null) {
            timePickerBuilder.setHour(agendaItem.getEndTime().getHour()).setMinute(agendaItem.getEndTime().getMinute());
            binding.endTimeInput.getEditText().setText(agendaItem.getEndTime().toString());
        }


        endTimePicker = timePickerBuilder.build();

        endTimePicker.addOnPositiveButtonClickListener(v -> {
            endTime=LocalTime.of(endTimePicker.getHour(), endTimePicker.getMinute());
            binding.endTimeInput.getEditText().setText(endTime.toString());
        });

        binding.endTimeInput.getEditText().setOnClickListener(v->{
            if(!endTimePicker.isAdded())
                endTimePicker.show(getParentFragmentManager(),"startTimePicker");
        });

        if(agendaItem!=null){
            binding.nameInput.getEditText().setText(agendaItem.getName());
            binding.descriptionInput.getEditText().setText(agendaItem.getDescription());
            binding.locationInput.getEditText().setText(agendaItem.getLocation());
        }
    }

    private void setupValidation(){
        binding.nameInput.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                validateRequiredField(binding.nameInput);
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        binding.descriptionInput.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                validateRequiredField(binding.descriptionInput);
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        binding.startTimeInput.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                validateRequiredField(binding.startTimeInput);
                validateTimeInputs();
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        binding.endTimeInput.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                validateRequiredField(binding.endTimeInput);
                validateTimeInputs();
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        binding.locationInput.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                validateRequiredField(binding.locationInput);
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

    private boolean validateTimeInputs(){
        if (startTime==null || endTime==null) {
            return true;
        }
        if(startTime.compareTo(endTime)>0){
            binding.endTimeInput.setError("End time must be after start time");
            return false;
        }
        binding.endTimeInput.setError(null);
        return true;
    }

    private boolean isFormValid(){
        boolean isValid=true;
        if(!validateRequiredField(binding.nameInput))
            isValid = false;
        if(!validateRequiredField(binding.descriptionInput))
            isValid = false;
        if(!validateRequiredField(binding.locationInput))
            isValid = false;
        if(!validateRequiredField(binding.startTimeInput))
            isValid = false;
        if(!validateRequiredField(binding.endTimeInput))
            isValid = false;
        if(!validateTimeInputs())
            isValid = false;
        return isValid;
    }
}