package m3.eventplanner.fragments.eventtype;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import m3.eventplanner.R;
import m3.eventplanner.clients.ClientUtils;
import m3.eventplanner.models.GetEventTypeDTO;
import m3.eventplanner.models.GetOfferingCategoryDTO;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class EventTypeFormFragment extends DialogFragment {
    private EditText nameInput;
    private EditText descriptionInput;
    private TextView recommendedCategoriesTextView;
    private OnEventTypeFormFilledListener listener;
    private GetEventTypeDTO eventType;
    private ClientUtils clientUtils;
    private List<GetOfferingCategoryDTO> recommendedCategories=new ArrayList<>();
    private EventTypeFormViewModel viewModel;
    private View view;

    public interface OnEventTypeFormFilledListener {
        void onEventTypeFormFilled(String name, String description, List<Integer> recommendedCategoryIds);
    }

    public static EventTypeFormFragment newInstance() {
        return new EventTypeFormFragment();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listener = (OnEventTypeFormFilledListener) getParentFragment();
        } catch (ClassCastException e) {
            throw new ClassCastException("Parent fragment must implement FormDialogListener");
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(EventTypeFormViewModel.class);
        clientUtils = new ClientUtils(requireContext());
        viewModel.initialize(clientUtils);

        if (getArguments() != null) {
            eventType = getArguments().getParcelable("selectedEventType");
            if(eventType!=null)
                recommendedCategories=eventType.getRecommendedCategories();
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel.getCategories().observe(getViewLifecycleOwner(), this::setupCategorySelector);
        viewModel.loadCategories();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        view = inflater.inflate(R.layout.fragment_event_type_form, null);

        // Initialize views
        nameInput = view.findViewById(R.id.event_name_input);
        descriptionInput = view.findViewById(R.id.event_description_input);
        recommendedCategoriesTextView = view.findViewById(R.id.recommended_categories_textview);

        if(eventType!=null){
            nameInput.setText(eventType.getName());
            descriptionInput.setText(eventType.getDescription());
            nameInput.setEnabled(false);
        }

        builder.setView(view)
                .setTitle(eventType==null?"Create Event Type":"Edit Event Type")
                .setPositiveButton("Submit", (dialog, id) -> {
                    String name = nameInput.getText().toString().trim();
                    String description = descriptionInput.getText().toString().trim();
                    List<Integer> recommendedCategoryIds = recommendedCategories.stream()
                            .map(GetOfferingCategoryDTO::getId)
                            .collect(Collectors.toList());
                    if (!name.isEmpty()) {
                        listener.onEventTypeFormFilled(name, description, recommendedCategoryIds);
                    }
                })
                .setNegativeButton("Cancel", (dialog, id) -> dialog.dismiss());

        return builder.create();
    }

    private void setupCategorySelector(List<GetOfferingCategoryDTO> categories){
        String[] categoryNames = categories.stream()
                .map(GetOfferingCategoryDTO::getName)
                .toArray(String[]::new);
        boolean[] selectedCategories = new boolean[categories.size()];
        for (int i = 0; i < selectedCategories.length; i++) {
            final GetOfferingCategoryDTO currentCategory = categories.get(i);
            selectedCategories[i] = recommendedCategories.stream()
                    .anyMatch(recommended -> recommended.getId()==currentCategory.getId());
        }

        recommendedCategoriesTextView.setText(
                recommendedCategories.stream()
                        .map(GetOfferingCategoryDTO::getName)
                        .collect(Collectors.joining(", "))
        );

        recommendedCategoriesTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Initialize alert dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());

                // set title
                builder.setTitle("Select Recommended Categories");

                // set dialog non cancelable
                builder.setCancelable(false);

                builder.setMultiChoiceItems(categoryNames, selectedCategories, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i, boolean b) {
                        if (b) {
                            recommendedCategories.add(categories.get(i));
                        } else {
                            recommendedCategories.remove(categories.get(i));
                        }
                    }
                });

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        recommendedCategoriesTextView.setText(
                                recommendedCategories.stream()
                                .map(GetOfferingCategoryDTO::getName)
                                .collect(Collectors.joining(", "))
                        );
                    }
                });

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // dismiss dialog
                        dialogInterface.dismiss();
                    }
                });
                // show dialog
                builder.show();
            }
        });
    }
}