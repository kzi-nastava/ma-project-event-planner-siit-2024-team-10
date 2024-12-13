package m3.eventplanner.fragments;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import m3.eventplanner.R;

public class EditOfferingFragment extends Fragment {

    private Button addNowButton;
    private Button minMaxButton;
    private Button setTimeButton;
    private View rootView;

    public EditOfferingFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_edit_offering, container, false);
        return rootView;  // Set rootView here
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupDropdowns();
        setupButtons();
    }

    private void setupDropdowns() {
        // dropdown - reservation deadline
        String[] reservation = new String[]{"1 month", "2 months", "3 months"};
        AutoCompleteTextView autoCompleteTextView2 = rootView.findViewById(R.id.autoCompleteTextView2);

        ArrayAdapter<String> adapter2 = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_dropdown_item_1line, reservation);
        autoCompleteTextView2.setAdapter(adapter2);

        // dropdown - cancellation deadline
        AutoCompleteTextView autoCompleteTextView3 = rootView.findViewById(R.id.autoCompleteTextView3);

        ArrayAdapter<String> adapter3 = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_dropdown_item_1line, reservation);
        autoCompleteTextView3.setAdapter(adapter3);
    }

    private void setupButtons() {
        // dialog - min max input
        minMaxButton = rootView.findViewById(R.id.min_max_time);
        minMaxButton.setOnClickListener(v -> showMinMaxDialog());

        // dialog - set time
        setTimeButton = rootView.findViewById(R.id.set_time);
        setTimeButton.setOnClickListener(v -> showSetTimeInputDialog());
    }


    private void showSetTimeInputDialog() {
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.fragment_set_time, null);

        RadioGroup radioGroup = dialogView.findViewById(R.id.radio_group);
        Button btnCancel = dialogView.findViewById(R.id.btn_cancel);
        Button btnOk = dialogView.findViewById(R.id.btn_ok);

        String[] values = {"1", "2", "3", "4"};

        for (String value : values) {
            RadioButton radioButton = new RadioButton(requireContext());
            radioButton.setText(value);
            radioButton.setId(View.generateViewId());
            radioGroup.addView(radioButton);
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setView(dialogView);

        AlertDialog dialog = builder.create();
        dialog.show();

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        btnOk.setOnClickListener(v -> {
            int selectedId = radioGroup.getCheckedRadioButtonId();
            RadioButton selectedRadioButton = dialogView.findViewById(selectedId);

            if (selectedRadioButton != null) {
                String selectedValue = selectedRadioButton.getText().toString();
                Toast.makeText(requireContext(), "Selected: " + selectedValue, Toast.LENGTH_SHORT).show();
            }

            dialog.dismiss();
        });
    }

    private void showMinMaxDialog() {
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.min_max_dialog, null);

        // EditText min = dialogView.findViewById(R.id.edit_text_min);
        // EditText max = dialogView.findViewById(R.id.edit_text_max);

        Button btnCancel = dialogView.findViewById(R.id.btn_cancel);
        Button btnOk = dialogView.findViewById(R.id.btn_ok);

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setView(dialogView);

        AlertDialog dialog = builder.create();
        dialog.show();

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        btnOk.setOnClickListener(v -> dialog.dismiss());
    }
}