package m3.eventplanner.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import m3.eventplanner.R;
import m3.eventplanner.models.Event;

public class EventDetailsFragment extends Fragment {
    public EventDetailsFragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_event_details, container, false);

        if (getArguments() != null) {
            Event event = getArguments().getParcelable("selectedEvent");
        }

        return view;
    }
}