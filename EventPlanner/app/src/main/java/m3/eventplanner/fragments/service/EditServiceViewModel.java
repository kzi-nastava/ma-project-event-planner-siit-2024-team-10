package m3.eventplanner.fragments.service;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.io.IOException;
import java.util.List;

import m3.eventplanner.clients.ClientUtils;
import m3.eventplanner.models.GetEventDTO;
import m3.eventplanner.models.GetEventTypeDTO;
import m3.eventplanner.models.GetServiceDTO;
import m3.eventplanner.models.UpdateEventDTO;
import m3.eventplanner.models.UpdateServiceDTO;
import m3.eventplanner.models.UpdatedEventDTO;
import m3.eventplanner.models.UpdatedServiceDTO;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditServiceViewModel extends ViewModel {
    private final MutableLiveData<GetServiceDTO> service = new MutableLiveData<>();
    private final MutableLiveData<String> error = new MutableLiveData<>();
    private final MutableLiveData<String> successMessage = new MutableLiveData<>();
    public LiveData<String> getError() {
        return error;
    }
    public LiveData<String> getSuccessMessage() {
        return successMessage;
    }

    private ClientUtils clientUtils;

    public void initialize(ClientUtils clientUtils) {
        this.clientUtils = clientUtils;
    }
    public LiveData<GetServiceDTO> getService() { return service; }
    public void loadService(int serviceId) {
        clientUtils.getServiceService().getService(serviceId).enqueue(new Callback<GetServiceDTO>() {
            @Override
            public void onResponse(Call<GetServiceDTO> call, Response<GetServiceDTO> response) {
                if (response.isSuccessful() && response.body() != null) {
                    service.setValue(response.body());
                } else {
                    error.setValue("Failed to load service details");
                }
            }

            @Override
            public void onFailure(Call<GetServiceDTO> call, Throwable t) {
                error.setValue(t.getMessage() != null ? t.getMessage() : "Error loading service details");
            }
        });
    }
    public void updateService(UpdateServiceDTO updateServiceDTO, int serviceId) {
        clientUtils.getServiceService().updateService(serviceId,updateServiceDTO).enqueue(new Callback<UpdatedServiceDTO>() {
            @Override
            public void onResponse(Call<UpdatedServiceDTO> call, Response<UpdatedServiceDTO> response) {
                if (response.isSuccessful()) {
                    successMessage.setValue("Service edited successfully");
                } else {
                    try {
                        String errorBody = response.errorBody().string();
                        error.setValue(errorBody);
                    } catch (IOException e) {
                        e.printStackTrace();
                        error.setValue("An error occurred while parsing the error message.");
                    }
                }
            }

            @Override
            public void onFailure(Call<UpdatedServiceDTO> call, Throwable t) {
                error.setValue(t.getMessage() != null ? t.getMessage() : "Error ");
            }
        });
    }
}
