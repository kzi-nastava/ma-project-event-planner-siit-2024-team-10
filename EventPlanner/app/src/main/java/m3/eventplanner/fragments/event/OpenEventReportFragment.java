package m3.eventplanner.fragments.event;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;

import m3.eventplanner.R;
import m3.eventplanner.databinding.FragmentEventDetailsBinding;
import m3.eventplanner.databinding.FragmentOpenEventReportBinding;
import m3.eventplanner.models.GetEventStatsDTO;

public class OpenEventReportFragment extends Fragment {
    private FragmentOpenEventReportBinding binding;
    private OpenEventReportViewModel viewModel;

    public OpenEventReportFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentOpenEventReportBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getArguments() != null) {
            int eventId = getArguments().getInt("selectedEventId");
            viewModel = new ViewModelProvider(this).get(OpenEventReportViewModel.class);
            viewModel.initialize(requireContext(),eventId);
        }

        binding.exportToPdfButton.setOnClickListener(v->{
            viewModel.exportToPdf();
        });

        viewModel.getEventStats().observe(getViewLifecycleOwner(), this::populateChart);

        viewModel.getError().observe(getViewLifecycleOwner(), error ->
                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show()
        );

        viewModel.getSuccessMessage().observe(getViewLifecycleOwner(), message ->
                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show()
        );

        viewModel.loadEventStats();
    }


    private void populateChart(GetEventStatsDTO eventStats){
        binding.eventNameTextView.setText(eventStats.getEventName());
        binding.participantsCountTextView.setText(" "+String.valueOf(eventStats.getParticipantsCount()));
        binding.averageRatingTextView.setText(" "+String.valueOf(eventStats.getAverageRating()));

        ArrayList<BarEntry> barEntries = new ArrayList<>();
        barEntries.add(new BarEntry(1,eventStats.getOneStarCount()));
        barEntries.add(new BarEntry(2,eventStats.getTwoStarCount()));
        barEntries.add(new BarEntry(3,eventStats.getThreeStarCount()));
        barEntries.add(new BarEntry(4,eventStats.getFourStarCount()));
        barEntries.add(new BarEntry(5,eventStats.getFiveStarCount()));
        BarDataSet dataSet = new BarDataSet(barEntries,"Ratings");
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        dataSet.setValueTextColor(Color.BLACK);
        dataSet.setValueTextSize(16f);
        BarData barData = new BarData(dataSet);

        BarChart barChart = binding.barChart;

        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return String.valueOf((int) value);
            }
        });
        xAxis.setGranularity(1f);
        xAxis.setTextSize(12f);
        xAxis.setTextColor(Color.BLACK);
        barChart.setFitBars(true);
        barChart.setData(barData);
        barChart.getDescription().setText(eventStats.getEventName());
        barChart.setDrawValueAboveBar(true);
        barChart.animateY(2000);
    }
}