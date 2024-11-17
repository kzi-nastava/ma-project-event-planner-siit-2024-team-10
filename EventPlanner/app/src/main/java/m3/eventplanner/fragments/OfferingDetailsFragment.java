package m3.eventplanner.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import m3.eventplanner.R;
import m3.eventplanner.models.Offering;

public class OfferingDetailsFragment extends Fragment {

    private Offering offering;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            offering = getArguments().getParcelable("offering");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_offering_details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (offering != null) {
            // Find and set your views
            TextView titleView = view.findViewById(R.id.offering_title);
            TextView providerView = view.findViewById(R.id.offering_provider);
            TextView priceView = view.findViewById(R.id.offering_price);
            // Add more views as needed

            // Set the data
            titleView.setText(offering.getTitle());
            providerView.setText(offering.getProvider());
            priceView.setText(String.valueOf(offering.getPrice()));
            // Set more data as needed
        }
    }
}