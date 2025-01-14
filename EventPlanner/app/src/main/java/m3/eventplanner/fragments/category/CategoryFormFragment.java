package m3.eventplanner.fragments.category;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import androidx.lifecycle.ViewModelProvider;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputLayout;


import m3.eventplanner.clients.ClientUtils;
import m3.eventplanner.models.GetOfferingCategoryDTO;
import m3.eventplanner.R;


public class CategoryFormFragment extends DialogFragment {
    private TextInputLayout nameInput;
    private TextInputLayout descriptionInput;
    private OnCategoryFormFilledListener listener;
    private GetOfferingCategoryDTO category;
    private ClientUtils clientUtils;
    private CategoryFormViewModel viewModel;
    private View view;

    public interface OnCategoryFormFilledListener {
        void onCategoryFormFilled(int id, String name, String description, boolean edit);
    }

    public static CategoryFormFragment newInstance() {
        return new CategoryFormFragment();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listener = (OnCategoryFormFilledListener) getParentFragment();
        } catch (ClassCastException e) {
            throw new ClassCastException("Parent fragment must implement FormDialogListener");
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(CategoryFormViewModel.class);
        clientUtils = new ClientUtils(requireContext());
        viewModel.initialize(clientUtils);

        if (getArguments() != null) {
            category = getArguments().getParcelable("selectedCategory");
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

    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        view = inflater.inflate(R.layout.fragment_category_form, null);

        nameInput = view.findViewById(R.id.category_input);
        descriptionInput = view.findViewById(R.id.category_description_input);

        if(category!=null){
            nameInput.getEditText().setText(category.getName());
            descriptionInput.getEditText().setText(category.getDescription());
        }

        setupValidation();

        builder.setView(view)
                .setTitle(category==null?"Create category":"Edit category")
                .setPositiveButton("Submit", (dialog, id) -> {
                    String name = nameInput.getEditText().getText().toString().trim();
                    String description = descriptionInput.getEditText().getText().toString().trim();
                    if (isFormValid()) {
                        listener.onCategoryFormFilled(category==null?0:category.getId(), name, description, category!=null);
                    }
                })
                .setNegativeButton("Cancel", (dialog, id) -> dialog.dismiss());

        return builder.create();
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