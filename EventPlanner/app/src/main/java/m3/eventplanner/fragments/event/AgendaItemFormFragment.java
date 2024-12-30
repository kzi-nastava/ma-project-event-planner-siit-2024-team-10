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

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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

        builder.setView(binding.getRoot())
                .setTitle(agendaItem==null?"Create Agenda Item":"Edit Agenda Item")
                .setPositiveButton("Submit", (dialog, id) -> {
//                    String name = nameInput.getEditText().getText().toString().trim();
//                    String description = descriptionInput.getEditText().getText().toString().trim();
//                    List<Integer> recommendedCategoryIds = recommendedCategories.stream()
//                            .map(GetOfferingCategoryDTO::getId)
//                            .collect(Collectors.toList());
//                    if (isFormValid()) {
//                        listener.onEventTypeFormFilled(eventType==null?0:eventType.getId(), name, description, recommendedCategoryIds, eventType!=null);
//                    }
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
            endTimePicker.show(getParentFragmentManager(),"startTimePicker");
        });

        if(agendaItem!=null){
            binding.nameInput.getEditText().setText(agendaItem.getName());
            binding.descriptionInput.getEditText().setText(agendaItem.getDescription());
            binding.locationInput.getEditText().setText(agendaItem.getLocation());
        }
    }
}