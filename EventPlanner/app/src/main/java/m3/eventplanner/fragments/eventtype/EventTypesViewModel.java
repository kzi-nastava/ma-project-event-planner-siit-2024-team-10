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
}
