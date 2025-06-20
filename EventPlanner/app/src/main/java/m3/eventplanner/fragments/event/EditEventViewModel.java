package m3.eventplanner.fragments.event;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import m3.eventplanner.clients.ClientUtils;
import m3.eventplanner.models.GetEventDTO;
import m3.eventplanner.models.GetEventTypeDTO;
import m3.eventplanner.models.UpdateEventDTO;
import m3.eventplanner.models.UpdatedEventDTO;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditEventViewModel extends ViewModel {
    private final MutableLiveData<List<GetEventTypeDTO>> eventTypes = new MutableLiveData<>();
    private final MutableLiveData<GetEventDTO> event = new MutableLiveData<>();
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

    public LiveData<List<GetEventTypeDTO>> getEventTypes() {
        return this.eventTypes;
    }

    public LiveData<GetEventDTO> getEvent() { return event; }

    public void loadEventTypes(){
        clientUtils.getEventTypeService().getEventTypes().enqueue(new Callback<Collection<GetEventTypeDTO>>() {
            @Override
            public void onResponse(Call<Collection<GetEventTypeDTO>> call, Response<Collection<GetEventTypeDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    eventTypes.setValue(response.body().stream()
                            .filter(eventType->eventType.isActive())
                            .collect(Collectors.toList()));
                } else {
                    error.setValue("Failed to load event types");
                }
            }

            @Override
            public void onFailure(Call<Collection<GetEventTypeDTO>> call, Throwable t) {
                error.setValue(t.getMessage() != null ? t.getMessage() : "Error loading event details");
            }
        });
    }

    public void loadEventDetails(int eventId) {
        clientUtils.getEventService().getEvent(eventId).enqueue(new Callback<GetEventDTO>() {
            @Override
            public void onResponse(Call<GetEventDTO> call, Response<GetEventDTO> response) {
                if (response.isSuccessful() && response.body() != null) {
                    event.setValue(response.body());
                } else {
                    error.setValue("Failed to load event details");
                }
            }

            @Override
            public void onFailure(Call<GetEventDTO> call, Throwable t) {
                error.setValue(t.getMessage() != null ? t.getMessage() : "Error loading event details");
            }
        });
    }

    public void updateEvent(UpdateEventDTO updateEventDTO, int eventId) {
        clientUtils.getEventService().updateEvent(eventId,updateEventDTO).enqueue(new Callback<UpdatedEventDTO>() {
            @Override
            public void onResponse(Call<UpdatedEventDTO> call, Response<UpdatedEventDTO> response) {
                if (response.isSuccessful()) {
                    successMessage.setValue("Event edited successfully");
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
            public void onFailure(Call<UpdatedEventDTO> call, Throwable t) {
                error.setValue(t.getMessage() != null ? t.getMessage() : "Error ");
            }
        });
    }
}
