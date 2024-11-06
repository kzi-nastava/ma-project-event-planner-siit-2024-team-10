package m3.eventplanner;

import android.content.Intent;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initializeRegisterMessage();
    }

    private void initializeRegisterMessage() {
        TextView textView = findViewById(R.id.register);

        // Create a SpannableStringBuilder to concatenate the text
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();

        // Add the first part of the text
        spannableStringBuilder.append(getString(R.string.not_registered_message));

        // Create a Spannable for the "Register now" part
        String registerText = getString(R.string.register_message);
        Spannable registerSpannable = new SpannableString(registerText);

        //Get the primary color
        TypedValue typedValue = new TypedValue();
        getTheme().resolveAttribute(androidx.appcompat.R.attr.colorPrimary, typedValue, true);
        int color = typedValue.data;

        // Set the color to primary for the "Register now" text
        registerSpannable.setSpan(new ForegroundColorSpan(color), 0, registerText.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        // Make the "Register now" text clickable
        registerSpannable.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                // Handle the click action here, for example, open registration activity
                Toast.makeText(widget.getContext(), "Register clicked!", Toast.LENGTH_SHORT).show();
            }
        }, 0, registerText.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        // Append the "Register now" text to the builder
        spannableStringBuilder.append(" ");
        spannableStringBuilder.append(registerSpannable);

        // Set the final SpannableStringBuilder to the TextView and make it clickable
        textView.setText(spannableStringBuilder);
        textView.setMovementMethod(LinkMovementMethod.getInstance());
    }
}