package m3.eventplanner.fragments.reservation;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.io.IOException;
import java.util.List;

import m3.eventplanner.clients.ClientUtils;
import m3.eventplanner.models.CreateReservationDTO;
import m3.eventplanner.models.CreatedReservationDTO;
import m3.eventplanner.models.GetEventDTO;
import m3.eventplanner.models.GetServiceDTO;
import m3.eventplanner.models.Status;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateReservationViewModel extends ViewModel {

    public interface ReservationActionHandler {
        void onReservationAccepted(int eventId);
    }

    private ReservationActionHandler actionHandler;
    public void setReservationActionHandler(ReservationActionHandler handler) {
        this.actionHandler = handler;
    }

    private final MutableLiveData<GetServiceDTO> service = new MutableLiveData<>();
    private final MutableLiveData<List<GetEventDTO>> events = new MutableLiveData<>();
    private final MutableLiveData<String> error = new MutableLiveData<>();
    private final MutableLiveData<String> successMessage = new MutableLiveData<>();
    private final MutableLiveData<String> notification = new MutableLiveData<>();

    private ClientUtils clientUtils;

    public void initialize(ClientUtils clientUtils) {
        this.clientUtils = clientUtils;
    }

    public LiveData<GetServiceDTO> getService() { return this.service; }
    public LiveData<List<GetEventDTO>> getEvents(){ return this.events; }
    public LiveData<String> getError() { return error; }
    public LiveData<String> getSuccessMessage() { return successMessage; }
    public LiveData<String> getNotification() { return notification; }

    public void loadEvents(int organizerId){
        clientUtils.getReservationService().findEventsByOrganizer(organizerId).enqueue(new Callback<List<GetEventDTO>>() {
            @Override
            public void onResponse(Call<List<GetEventDTO>> call, Response<List<GetEventDTO>> response) {
                if (response.isSuccessful() && response.body() != null){
                    events.postValue(response.body());
                } else {
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
        notification.postValue("Pending reservation...");
        clientUtils.getReservationService().createReservation(reservationDTO).enqueue(new Callback<CreatedReservationDTO>() {
            @Override
            public void onResponse(Call<CreatedReservationDTO> call, Response<CreatedReservationDTO> response) {
                if (response.isSuccessful() && response.body() != null){
                    CreatedReservationDTO created = response.body();

                    if (created.getStatus() == Status.PENDING) {
                        successMessage.postValue("Reservation is pending. You will get confirmation once it's accepted or denied.");
                    } else {
                        successMessage.postValue("Reservation created and accepted. Proceeding with purchase...");
                    }
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
                if (response.isSuccessful() && response.body() != null){
                    service.postValue(response.body());
                } else {
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
