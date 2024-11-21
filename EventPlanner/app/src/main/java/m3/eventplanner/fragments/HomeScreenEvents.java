package m3.eventplanner.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import m3.eventplanner.R;
import m3.eventplanner.adapters.EventListAdapter;
import m3.eventplanner.models.Event;
import java.util.ArrayList;
import java.util.List;

public class HomeScreenEvents extends Fragment {

    private RecyclerView recyclerTopEventsView;
    private RecyclerView recyclerAllEventsView;
    private EventListAdapter topEventsListAdapter;
    private EventListAdapter allEventsListAdapter;
    private ArrayList<Event> topEventItemList;
    private ArrayList<Event> allEventItemList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home_screen_events, container, false);

        recyclerTopEventsView = rootView.findViewById(R.id.topEventsRecyclerView);

        topEventItemList = new ArrayList<Event>();
        prepareTopEventList(topEventItemList);
        topEventsListAdapter = new EventListAdapter(topEventItemList);
        recyclerTopEventsView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerTopEventsView.setAdapter(topEventsListAdapter);

        recyclerAllEventsView = rootView.findViewById(R.id.allEventsRecyclerView);

        allEventItemList = new ArrayList<Event>();
        prepareAllEventList(allEventItemList);
        allEventsListAdapter = new EventListAdapter(allEventItemList);
        recyclerAllEventsView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerAllEventsView.setAdapter(allEventsListAdapter);

        return rootView;
    }

    private void prepareTopEventList(ArrayList<Event> eventItemList){
        eventItemList.clear();
        eventItemList.add(new Event(1L, "Mary And Josh's Wedding", 2.5, "Organizer 1", "Beograd, Serbia","12.12.2024. 12:03"));
        eventItemList.add(new Event(2L, "Mary And Josh's Wedding", 5, "Organizer 1", "Novi Sad, Serbia","12.12.2024. 12:03"));
        eventItemList.add(new Event(3L, "Mary And Josh's Wedding", 1.5, "Organizer 1", "Arilje, Serbia","12.12.2024. 12:03"));
        eventItemList.add(new Event(4L, "Mary And Josh's Wedding", 4, "Organizer 1", "Pozega, Serbia","12.12.2024. 12:03"));
        eventItemList.add(new Event(5L, "Mary And Josh's Wedding", 2, "Organizer 1", "Novi Sad, Serbia","12.12.2024. 12:03"));
    }

    private void prepareAllEventList(ArrayList<Event> eventItemList){
        eventItemList.clear();
        eventItemList.add(new Event(1L, "Mary And Josh's Wedding", 2.5, "Organizer 1", "Beograd, Serbia","12.12.2024. 12:03"));
        eventItemList.add(new Event(2L, "Mary And Josh's Wedding", 5, "Organizer 1", "Novi Sad, Serbia","12.12.2024. 12:03"));
        eventItemList.add(new Event(3L, "Mary And Josh's Wedding", 1.5, "Organizer 1", "Arilje, Serbia","12.12.2024. 12:03"));
        eventItemList.add(new Event(4L, "Mary And Josh's Wedding", 4, "Organizer 1", "Pozega, Serbia","12.12.2024. 12:03"));
        eventItemList.add(new Event(5L, "Mary And Josh's Wedding", 2, "Organizer 1", "Novi Sad, Serbia","12.12.2024. 12:03"));
        eventItemList.add(new Event(6L, "Mary And Josh's Wedding", 2.5, "Organizer 1", "Novi Sad, Serbia","12.12.2024. 12:03"));
        eventItemList.add(new Event(7L, "Mary And Josh's Wedding", 5, "Organizer 1", "Novi Sad, Serbia","12.12.2024. 12:03"));
        eventItemList.add(new Event(8L, "Mary And Josh's Wedding", 1.5, "Organizer 1", "Novi Sad, Serbia","12.12.2024. 12:03"));
        eventItemList.add(new Event(9L, "Mary And Josh's Wedding", 4, "Organizer 1", "Novi Sad, Serbia","12.12.2024. 12:03"));
        eventItemList.add(new Event(10L, "Mary And Josh's Wedding", 2, "Organizer 1", "Novi Sad, Serbia","12.12.2024. 12:03"));
    }
}
