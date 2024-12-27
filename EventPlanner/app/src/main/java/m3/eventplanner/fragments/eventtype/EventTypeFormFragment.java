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

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputLayout;

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
    private TextInputLayout nameInput;
    private TextInputLayout descriptionInput;
    private TextView recommendedCategoriesTextView;
    private OnEventTypeFormFilledListener listener;
    private GetEventTypeDTO eventType;
    private ClientUtils clientUtils;
    private List<GetOfferingCategoryDTO> recommendedCategories=new ArrayList<>();
    private EventTypeFormViewModel viewModel;
    private View view;

    public interface OnEventTypeFormFilledListener {
        void onEventTypeFormFilled(int id, String name, String description, List<Integer> recommendedCategoryIds, boolean edit);
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
            nameInput.getEditText().setText(eventType.getName());
            descriptionInput.getEditText().setText(eventType.getDescription());
            nameInput.setEnabled(false);
        }

        setupValidation();

        builder.setView(view)
                .setTitle(eventType==null?"Create Event Type":"Edit Event Type")
                .setPositiveButton("Submit", (dialog, id) -> {
                    String name = nameInput.getEditText().getText().toString().trim();
                    String description = descriptionInput.getEditText().getText().toString().trim();
                    List<Integer> recommendedCategoryIds = recommendedCategories.stream()
                            .map(GetOfferingCategoryDTO::getId)
                            .collect(Collectors.toList());
                    if (isFormValid()) {
                        listener.onEventTypeFormFilled(eventType==null?0:eventType.getId(), name, description, recommendedCategoryIds, eventType!=null);
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

    private void setupValidation(){
        nameInput.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                validateRequiredField(nameInput);
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        descriptionInput.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                validateRequiredField(descriptionInput);
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

    private boolean isFormValid(){
        if(!validateRequiredField(nameInput))
            return false;
        if(!validateRequiredField(descriptionInput))
            return false;
        return true;
    }
}