package m3.eventplanner.fragments.event;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import m3.eventplanner.clients.ClientUtils;
import m3.eventplanner.models.CreateEventDTO;
import m3.eventplanner.models.CreatedEventDTO;
import m3.eventplanner.models.GetEventTypeDTO;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditEventViewModel extends ViewModel {
    private final MutableLiveData<List<GetEventTypeDTO>> eventTypes = new MutableLiveData<>();
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

//    public void createEvent(CreateEventDTO createEventDTO) {
//        clientUtils.getEventService().addEvent(createEventDTO).enqueue(new Callback<CreatedEventDTO>() {
//            @Override
//            public void onResponse(Call<CreatedEventDTO> call, Response<CreatedEventDTO> response) {
//                if (response.isSuccessful()) {
//                    successMessage.setValue("Event type created successfully");
//                } else {
//                    error.setValue("Failed to create event type");
//                }
//            }
//
//            @Override
//            public void onFailure(Call<CreatedEventDTO> call, Throwable t) {
//                error.setValue(t.getMessage() != null ? t.getMessage() : "Error creating event type");
//            }
//        });
//    }
}
