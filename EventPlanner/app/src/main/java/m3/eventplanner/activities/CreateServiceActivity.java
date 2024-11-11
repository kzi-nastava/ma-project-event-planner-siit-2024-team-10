package m3.eventplanner.activities;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;

import m3.eventplanner.R;

public class CreateServiceActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_service);

        // Pronađite dugme za otvaranje dijaloga
        MaterialButton button = findViewById(R.id.add_now);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Inflating layout za dijalog
                View dialogView = LayoutInflater.from(CreateServiceActivity.this).inflate(R.layout.text_dialog, null);

                // Pronađite TextInputEditText unutar dijaloga
                TextInputEditText editText = dialogView.findViewById(R.id.edit_text_input);

                // Kreirajte dijalog koristeći MaterialAlertDialogBuilder
                MaterialAlertDialogBuilder dialogBuilder = new MaterialAlertDialogBuilder(CreateServiceActivity.this)
                        .setTitle("Enter Text")  // Naslov dijaloga
                        .setView(dialogView)  // Postavljanje prilagođenog layout-a dijaloga
                        .setPositiveButton("OK", (dialog, which) -> {
                            // Uzimanje unetog teksta
                            String userInput = editText.getText().toString();
                            if (!userInput.isEmpty()) {
                                // Prikazivanje unetog teksta u Toast-u
                                Toast.makeText(CreateServiceActivity.this, "You entered: " + userInput, Toast.LENGTH_SHORT).show();
                            } else {
                                // Ako je polje prazno, obavestite korisnika
                                Toast.makeText(CreateServiceActivity.this, "Please enter some text!", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());  // Dugme za odustajanje

                // Prikazivanje dijaloga
                dialogBuilder.show();
            }
        });
    }
}
