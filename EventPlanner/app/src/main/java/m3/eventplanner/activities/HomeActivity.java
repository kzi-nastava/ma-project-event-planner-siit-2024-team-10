package m3.eventplanner.activities;

import android.os.Bundle;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Collections;

import m3.eventplanner.R;
import m3.eventplanner.adapters.EventCardAdapter;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homescreen);

        ListView eventListView = findViewById(android.R.id.list);

        ArrayList<String> events = new ArrayList<>(Collections.nCopies(5, "Event"));

        EventCardAdapter adapter = new EventCardAdapter(this, R.layout.event_card, events);
        eventListView.setAdapter(adapter);
    }
}