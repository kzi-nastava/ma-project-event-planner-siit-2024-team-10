package m3.eventplanner.fragments.invitation;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.io.IOException;

import m3.eventplanner.clients.ClientUtils;
import m3.eventplanner.models.GetGuestDTO;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InvitationViewModel extends ViewModel {
    private final MutableLiveData<String> error = new MutableLiveData<>();
    private final MutableLiveData<String> successMessage = new MutableLiveData<>();
    private ClientUtils clientUtils;

    public LiveData<String> getError() {
        return error;
    }
    public LiveData<String> getSuccessMessage() {
        return successMessage;
    }

    public void initialize(ClientUtils clientUtils) {
        this.clientUtils = clientUtils;
    }

    public void processInvitation(String token, String email) {
        GetGuestDTO dto = new GetGuestDTO(email);
        clientUtils.getEventService().processInvitation(token, dto).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    successMessage.postValue("Event was added to your schedule");
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
            public void onFailure(Call<Void> call, Throwable t) {
                error.postValue(t.getMessage() != null ? t.getMessage() : "Error processing invitation");
            }
        });
    }
}
