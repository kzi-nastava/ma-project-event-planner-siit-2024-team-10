package m3.eventplanner.fragments.report;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import m3.eventplanner.clients.ClientUtils;
import m3.eventplanner.models.GetAccountReportDTO;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReportConfirmationViewModel extends ViewModel {

    private final MutableLiveData<List<GetAccountReportDTO>> reports = new MutableLiveData<>();
    private final MutableLiveData<String> error = new MutableLiveData<>();
    private final MutableLiveData<String> successMessage = new MutableLiveData<>();
    private int accountId;
    private ClientUtils clientUtils;

    public void initialize(ClientUtils clientUtils, int accountId) {
        this.clientUtils = clientUtils;
        this.accountId = accountId;
    }

    public LiveData<List<GetAccountReportDTO>> getReports() {
        return reports;
    }

    public LiveData<String> getError() {
        return error;
    }

    public LiveData<String> getSuccessMessage() {
        return successMessage;
    }

    public void loadPendingReports() {
        clientUtils.getAccountReportService().getAllPendingReports().enqueue(new Callback<Collection<GetAccountReportDTO>>() {
            @Override
            public void onResponse(Call<Collection<GetAccountReportDTO>> call, Response<Collection<GetAccountReportDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    reports.setValue((List<GetAccountReportDTO>) response.body());
                } else {
                    try {
                        String errorMsg = response.errorBody() != null ? response.errorBody().string() : "Unknown error";
                        error.setValue(errorMsg);
                    } catch (IOException e) {
                        error.setValue("Error parsing server response");
                    }
                }
            }

            @Override
            public void onFailure(Call<Collection<GetAccountReportDTO>> call, Throwable t) {
                error.setValue(t.getMessage() != null ? t.getMessage() : "Error loading reports");
            }
        });
    }

    public void acceptReport(int reportId) {
        clientUtils.getAccountReportService().acceptReport(reportId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    successMessage.setValue("Report accepted");
                    loadPendingReports();
                } else {
                    try {
                        String errorMsg = response.errorBody() != null ? response.errorBody().string() : "Unknown error";
                        error.setValue(errorMsg);
                    } catch (IOException e) {
                        error.setValue("Error parsing server response");
                    }
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                error.setValue(t.getMessage() != null ? t.getMessage() : "Error accepting report");
            }
        });
    }

    public void denyReport(int reportId) {
        clientUtils.getAccountReportService().rejectReport(reportId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    successMessage.setValue("Report denied");
                    loadPendingReports();
                } else {
                    try {
                        String errorMsg = response.errorBody() != null ? response.errorBody().string() : "Unknown error";
                        error.setValue(errorMsg);
                    } catch (IOException e) {
                        error.setValue("Error parsing server response");
                    }
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                error.setValue(t.getMessage() != null ? t.getMessage() : "Error denying report");
            }
        });
    }
}
