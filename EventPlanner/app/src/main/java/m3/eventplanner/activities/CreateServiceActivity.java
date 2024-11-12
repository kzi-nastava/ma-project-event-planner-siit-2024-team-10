package m3.eventplanner.activities;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;

import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.appcompat.app.AppCompatActivity;

import m3.eventplanner.R;
import android.app.AlertDialog;
import android.content.DialogInterface;

import android.text.InputType;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class CreateServiceActivity extends AppCompatActivity {
    private Button addNowButton;
    private Button minMaxButton;
    private Button setTimeButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_service);

        // dropdown - service categories
        String[] categories = new String[]{"Beauty", "Music", "Transportation"};
        AutoCompleteTextView autoCompleteTextView = findViewById(R.id.autoCompleteTextView);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, categories);
        autoCompleteTextView.setAdapter(adapter);

        // dropdown - reservation deadline
        String[] reservation = new String[]{"1 month", "2 months", "3 months"};
        AutoCompleteTextView autoCompleteTextView2 = findViewById(R.id.autoCompleteTextView2);

        ArrayAdapter<String> adapter2 = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, reservation);
        autoCompleteTextView2.setAdapter(adapter2);

        // dropdown - cancellation deadline
        AutoCompleteTextView autoCompleteTextView3 = findViewById(R.id.autoCompleteTextView3);

        ArrayAdapter<String> adapter3 = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, reservation);
        autoCompleteTextView3.setAdapter(adapter3);

        // dialog - new catagory
        addNowButton = findViewById(R.id.add_now);
        addNowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTextInputDialog();
            }
        });

        // dialog - min max input
        minMaxButton = findViewById(R.id.min_max_time);
        minMaxButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMinMaxDialog();
            }
        });

        // dialog - set time
        setTimeButton = findViewById(R.id.set_time);
        setTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSetTimeInputDialog();
            }
        });

    }

    private void showSetTimeInputDialog() {
        android.view.LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.fragment_set_time, null);

        RadioGroup radioGroup = dialogView.findViewById(R.id.radio_group);
        Button btnCancel = dialogView.findViewById(R.id.btn_cancel);
        Button btnOk = dialogView.findViewById(R.id.btn_ok);

        String[] values = {"1", "2", "3", "4"};

        for (String value : values) {
            RadioButton radioButton = new RadioButton(this);
            radioButton.setText(value);
            radioButton.setId(View.generateViewId());  // Ensure each RadioButton has a unique ID
            radioGroup.addView(radioButton);
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);

        AlertDialog dialog = builder.create();
        dialog.show();

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selectedId = radioGroup.getCheckedRadioButtonId();
                RadioButton selectedRadioButton = dialogView.findViewById(selectedId);

                if (selectedRadioButton != null) {
                    String selectedValue = selectedRadioButton.getText().toString();
                    Toast.makeText(CreateServiceActivity.this, "Selected: " + selectedValue, Toast.LENGTH_SHORT).show();
                }

                dialog.dismiss();
            }
        });
    }

    private void showTextInputDialog() {
        android.view.LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.text_dialog, null);

        EditText editTextInput = dialogView.findViewById(R.id.edit_text_input);
        Button btnCancel = dialogView.findViewById(R.id.btn_cancel);
        Button btnOk = dialogView.findViewById(R.id.btn_ok);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);

        AlertDialog dialog = builder.create();
        dialog.show();

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String inputText = editTextInput.getText().toString();
                dialog.dismiss();
            }
        });
    }
    private void showMinMaxDialog() {
        android.view.LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.min_max_dialog, null);

        EditText min = dialogView.findViewById(R.id.edit_text_min);
        EditText max = dialogView.findViewById(R.id.edit_text_max);

        Button btnCancel = dialogView.findViewById(R.id.btn_cancel);
        Button btnOk = dialogView.findViewById(R.id.btn_ok);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);

        AlertDialog dialog = builder.create();
        dialog.show();

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

}
