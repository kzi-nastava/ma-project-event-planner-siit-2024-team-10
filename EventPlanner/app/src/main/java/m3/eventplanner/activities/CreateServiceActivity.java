package m3.eventplanner.activities;
import m3.eventplanner.R;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import androidx.appcompat.app.AppCompatActivity;

public class CreateServiceActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_service); // Adjust this if your layout file has a different name

        // Find the AutoCompleteTextView by its ID
        AutoCompleteTextView autoCompleteTextView = findViewById(R.id.menu);

        // Array of values to display in the dropdown
        String[] dropdownValues = new String[]{"12", "3"};

        // Creating an ArrayAdapter with the string values
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                dropdownValues
        );

        // Setting the adapter to the AutoCompleteTextView
        autoCompleteTextView.setAdapter(adapter);
    }
}
