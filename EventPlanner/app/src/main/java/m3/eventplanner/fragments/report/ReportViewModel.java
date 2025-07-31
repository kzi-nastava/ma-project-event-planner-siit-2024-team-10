package m3.eventplanner.fragments.report;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.io.IOException;

import m3.eventplanner.clients.ClientUtils;
import m3.eventplanner.models.CreateAccountReportDTO;
import m3.eventplanner.models.CreatedAccountReportDTO;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReportViewModel extends ViewModel {

    private final MutableLiveData<String> successMessage = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    private ClientUtils clientUtils;

    public void initialize(ClientUtils clientUtils) {
        this.clientUtils = clientUtils;
    }

    public LiveData<String> getSuccessMessage() {
        return successMessage;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public void sendReport(CreateAccountReportDTO reportDTO) {
        clientUtils.getAccountReportService().createAccountReport(reportDTO).enqueue(new Callback<CreatedAccountReportDTO>() {
            @Override
            public void onResponse(Call<CreatedAccountReportDTO> call, Response<CreatedAccountReportDTO> response) {
                if (response.isSuccessful()) {
                    successMessage.postValue("Report sent successfully.");
                } else {
                    try {
                        String error = response.errorBody() != null ? response.errorBody().string() : "Unknown error";
                        errorMessage.postValue(error);
                    } catch (IOException e) {
                        errorMessage.postValue("Error parsing server response");
                    }
                }
            }

            @Override
            public void onFailure(Call<CreatedAccountReportDTO> call, Throwable t) {
                errorMessage.postValue(t.getMessage() != null ? t.getMessage() : "Error sending report");
            }
        });
    }
}
