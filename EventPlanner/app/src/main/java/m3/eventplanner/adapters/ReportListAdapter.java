package m3.eventplanner.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;

import java.util.List;

import m3.eventplanner.R;
import m3.eventplanner.fragments.report.ReportConfirmationFragment;
import m3.eventplanner.fragments.report.ReportConfirmationViewModel;
import m3.eventplanner.models.GetAccountReportDTO;

public class ReportListAdapter extends RecyclerView.Adapter<ReportListAdapter.ReportViewHolder> {

    private List<GetAccountReportDTO> reportList;
    private ReportConfirmationViewModel reportViewModel;

    public ReportListAdapter(List<GetAccountReportDTO> reportList, ReportConfirmationFragment fragment) {
        this.reportList = reportList;
        this.reportViewModel = new ViewModelProvider(fragment).get(ReportConfirmationViewModel.class);
    }

    public static class ReportViewHolder extends RecyclerView.ViewHolder {
        MaterialCardView reportCard;
        TextView reporterEmailTextView;
        TextView reporteeEmailTextView;
        TextView descriptionTextView;
        ImageButton acceptButton;
        ImageButton denyButton;

        public ReportViewHolder(View view) {
            super(view);
            reportCard = view.findViewById(R.id.reportCard);
            reporterEmailTextView = view.findViewById(R.id.reporterEmail);
            reporteeEmailTextView = view.findViewById(R.id.reporteeEmail);
            descriptionTextView = view.findViewById(R.id.description);
            acceptButton = view.findViewById(R.id.acceptButton);
            denyButton = view.findViewById(R.id.denyButton);
        }
    }

    @NonNull
    @Override
    public ReportViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_report, parent, false);
        return new ReportViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReportViewHolder holder, int position) {
        GetAccountReportDTO report = reportList.get(position);

        holder.reporterEmailTextView.setText("Reporter: " + report.getReporterEmail());
        holder.reporteeEmailTextView.setText("Reported User: " + report.getReporteeEmail());
        holder.descriptionTextView.setText(report.getDescription());

        holder.acceptButton.setOnClickListener(v -> {
            reportViewModel.acceptReport(report.getId());
        });

        holder.denyButton.setOnClickListener(v -> {
            reportViewModel.denyReport(report.getId());
        });
    }

    @Override
    public int getItemCount() {
        return reportList.size();
    }
}
