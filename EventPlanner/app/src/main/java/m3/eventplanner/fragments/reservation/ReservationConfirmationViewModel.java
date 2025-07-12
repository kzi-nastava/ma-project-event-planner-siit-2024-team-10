package m3.eventplanner.fragments.reservation;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import m3.eventplanner.clients.ClientUtils;
import m3.eventplanner.models.GetReservationDTO;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReservationConfirmationViewModel extends ViewModel {

    private final MutableLiveData<List<GetReservationDTO>> reservations = new MutableLiveData<>();
    private final MutableLiveData<String> error = new MutableLiveData<>();
    private final MutableLiveData<String> successMessage = new MutableLiveData<>();
    private int accountId;
    private ClientUtils clientUtils;

    public void initialize(ClientUtils clientUtils, int accountId) {
        this.clientUtils = clientUtils;
        this.accountId = accountId;
    }

    public LiveData<List<GetReservationDTO>> getReservations() {
        return reservations;
    }

    public LiveData<String> getError() {
        return error;
    }

    public LiveData<String> getSuccessMessage() {
        return successMessage;
    }

    public void loadPendingReservations() {
        clientUtils.getReservationService().getPendingReservationsByProvider(this.accountId).enqueue(new Callback<List<GetReservationDTO>>() {
            @Override
            public void onResponse(Call<List<GetReservationDTO>> call, Response<List<GetReservationDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    reservations.setValue(response.body());
                } else {
                    error.setValue("Failed to load pending reservations");
                }
            }

            @Override
            public void onFailure(Call<List<GetReservationDTO>> call, Throwable t) {
                error.setValue(t.getMessage() != null ? t.getMessage() : "Error loading reservations");
            }
        });
    }

    public void acceptReservation(int reservationId, Runnable onComplete) {
        clientUtils.getReservationService().acceptReservation(reservationId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    successMessage.setValue("Reservation accepted");
                    if (onComplete != null) onComplete.run();
                    loadPendingReservations();
                } else {
                    error.setValue("Failed to accept reservation");
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                error.setValue(t.getMessage() != null ? t.getMessage() : "Error accepting reservation");
            }
        });
    }

    public void denyReservation(int reservationId, Runnable onComplete) {
        clientUtils.getReservationService().rejectReservation(reservationId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    successMessage.setValue("Reservation denied");
                    if (onComplete != null) onComplete.run();
                    loadPendingReservations();
                } else {
                    error.setValue("Failed to deny reservation");
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                error.setValue(t.getMessage() != null ? t.getMessage() : "Error denying reservation");
            }
        });
    }
}
