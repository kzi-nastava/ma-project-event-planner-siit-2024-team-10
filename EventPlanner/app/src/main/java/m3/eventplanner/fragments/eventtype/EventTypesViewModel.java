package m3.eventplanner.fragments.eventtype;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.Collection;
import java.util.List;

import m3.eventplanner.clients.ClientUtils;
import m3.eventplanner.models.GetEventTypeDTO;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EventTypesViewModel extends ViewModel {
    private final MutableLiveData<List<GetEventTypeDTO>> eventTypes = new MutableLiveData<>();
    private final MutableLiveData<String> error = new MutableLiveData<>();
    private final MutableLiveData<String> successMessage = new MutableLiveData<>();

    private ClientUtils clientUtils;
    public void initialize(ClientUtils clientUtils){
        this.clientUtils = clientUtils;
    }

    public LiveData<List<GetEventTypeDTO>> getEventTypes(){
        return eventTypes;
    }

    public LiveData<String> getError() {
        return error;
    }

    public LiveData<String> getSuccessMessage() {
        return successMessage;
    }

    public void loadEventTypes() {
        clientUtils.getEventTypeService().getEventTypes().enqueue(new Callback<Collection<GetEventTypeDTO>>() {
            @Override
            public void onResponse(Call<Collection<GetEventTypeDTO>> call, Response<Collection<GetEventTypeDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    eventTypes.setValue((List<GetEventTypeDTO>) response.body());
                } else {
                    error.setValue("Failed to load event details");
                }
            }

            @Override
            public void onFailure(Call<Collection<GetEventTypeDTO>> call, Throwable t) {
                error.setValue(t.getMessage() != null ? t.getMessage() : "Error loading event details");
            }
        });
    }

    public void activateEventType(int id) {
        clientUtils.getEventTypeService().activateEventType(id).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    successMessage.setValue("Event type activated successfully");
                    loadEventTypes();
                } else {
                    error.setValue("Failed to activate event type");
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                error.setValue(t.getMessage() != null ? t.getMessage() : "Error activating event type");
            }
        });
    }

    public void deactivateEventType(int id) {
        clientUtils.getEventTypeService().deactivateEventType(id).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    successMessage.setValue("Event type deactivated successfully");
                    loadEventTypes();
                } else {
                    error.setValue("Failed to deactivate event type");
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                error.setValue(t.getMessage() != null ? t.getMessage() : "Error deactivating event type");
            }
        });
    }
}
