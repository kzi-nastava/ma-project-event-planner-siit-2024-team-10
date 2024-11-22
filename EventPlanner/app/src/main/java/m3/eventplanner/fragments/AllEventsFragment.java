package m3.eventplanner.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import m3.eventplanner.R;
import m3.eventplanner.adapters.EventListAdapter;
import m3.eventplanner.models.Event;

public class AllEventsFragment extends Fragment {
    private RecyclerView recyclerView;
    private EventListAdapter adapter;
    private List<Event> eventList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_all_events, container, false);

        // Find views
        TextView noEventsTextView = rootView.findViewById(R.id.noEventsTextView);
        RecyclerView recyclerView = rootView.findViewById(R.id.allEventsRecyclerView);

        // Initialize eventList (replace with actual data-fetching logic)
        eventList = getTopEvents();

        if (eventList.isEmpty()) {
            // Show "No events found" message
            noEventsTextView.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            // Show the RecyclerView
            noEventsTextView.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);

            // Initialize the adapter and set it to RecyclerView
            EventListAdapter adapter = new EventListAdapter(eventList);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            recyclerView.setAdapter(adapter);
        }

        return rootView;
    }

    // Method to simulate getting top event data
    private List<Event> getTopEvents() {
        List<Event> list = new ArrayList<>();

        // Example top events data
//        list.add(new Event(1L, "Mary And Josh's Wedding", 2.5, "Organizer 1", "Beograd, Serbia","12.12.2024. 12:03"));
//        list.add(new Event(2L, "Mary And Josh's Wedding", 5, "Organizer 1", "Novi Sad, Serbia","12.12.2024. 12:03"));
//        list.add(new Event(3L, "Mary And Josh's Wedding", 1.5, "Organizer 1", "Arilje, Serbia","12.12.2024. 12:03"));
//        list.add(new Event(4L, "Mary And Josh's Wedding", 2.5, "Organizer 1", "Beograd, Serbia","12.12.2024. 12:03"));
//        list.add(new Event(5L, "Mary And Josh's Wedding", 5, "Organizer 1", "Novi Sad, Serbia","12.12.2024. 12:03"));
//        list.add(new Event(6L, "Mary And Josh's Wedding", 1.5, "Organizer 1", "Arilje, Serbia","12.12.2024. 12:03"));
//        list.add(new Event(7L, "Mary And Josh's Wedding", 2.5, "Organizer 1", "Beograd, Serbia","12.12.2024. 12:03"));
//        list.add(new Event(8L, "Mary And Josh's Wedding", 5, "Organizer 1", "Novi Sad, Serbia","12.12.2024. 12:03"));
//        list.add(new Event(9L, "Mary And Josh's Wedding", 1.5, "Organizer 1", "Arilje, Serbia","12.12.2024. 12:03"));
        return list;
    }
}
