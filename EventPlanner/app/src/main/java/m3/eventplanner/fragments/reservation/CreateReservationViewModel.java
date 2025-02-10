package m3.eventplanner.fragments.reservation;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import m3.eventplanner.clients.ClientUtils;
import m3.eventplanner.models.CreateReservationDTO;
import m3.eventplanner.models.CreatedReservationDTO;
import m3.eventplanner.models.GetEventDTO;
import m3.eventplanner.models.GetServiceDTO;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateReservationViewModel extends ViewModel {
    private final MutableLiveData<GetServiceDTO> service = new MutableLiveData<>();
    private final MutableLiveData<List<GetEventDTO>> events = new MutableLiveData<>();
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
    public LiveData<List<GetEventDTO>> getEvents(){ return this.events; }
    public LiveData<GetServiceDTO> getService() { return this.service; }

    public void loadEvents(int organizerId){
        clientUtils.getReservationService().findEventsByOrganizer(organizerId).enqueue(new Callback<List<GetEventDTO>>() {
            @Override
            public void onResponse(Call<List<GetEventDTO>> call, Response<List<GetEventDTO>> response) {
                if (response.isSuccessful() && response.body()!=null){
                    events.postValue(response.body());
                } else{
                    error.postValue("Failed to load events.");
                }
            }

            @Override
            public void onFailure(Call<List<GetEventDTO>> call, Throwable t) {
                error.postValue(t.getMessage() != null ? t.getMessage() : "Error loading events");
            }
        });
    }

    public void createReservation(CreateReservationDTO reservationDTO){
        clientUtils.getReservationService().createReservation(reservationDTO).enqueue(new Callback<CreatedReservationDTO>() {
            @Override
            public void onResponse(Call<CreatedReservationDTO> call, Response<CreatedReservationDTO> response) {
                if (response.isSuccessful()){
                    successMessage.postValue("Reservation created successfully. Email confirmation has been sent.");
                } else {
                    try {
                        String errorMessage = response.errorBody() != null ? response.errorBody().string() : "Unknown error";
                        error.postValue(errorMessage);
                    } catch (IOException e) {
                        error.postValue("Error parsing server response");
                    }
                }
            }

            @Override
            public void onFailure(Call<CreatedReservationDTO> call, Throwable t) {
                error.postValue(t.getMessage() != null ? t.getMessage() : "Error creating reservation");
            }
        });
    }
    public void getServiceById(int id){
        clientUtils.getServiceService().getService(id).enqueue(new Callback<GetServiceDTO>() {
            @Override
            public void onResponse(Call<GetServiceDTO> call, Response<GetServiceDTO> response) {
                if (response.isSuccessful() && response.body()!=null){
                    service.postValue(response.body());
                } else{
                    error.postValue("Failed to load service.");
                }
            }

            @Override
            public void onFailure(Call<GetServiceDTO> call, Throwable t) {
                error.postValue(t.getMessage() != null ? t.getMessage() : "Error loading service.");
            }
        });
    }
}
