package m3.eventplanner.fragments.user;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.io.IOException;

import m3.eventplanner.clients.ClientUtils;
import m3.eventplanner.models.GetGuestDTO;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ActivateAccountViewModel extends ViewModel {
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

    public void activate(String token) {
        clientUtils.getAuthService().activateAccount(token).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {
                    successMessage.postValue(response.body());
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
            public void onFailure(Call<String> call, Throwable t) {
                error.postValue(t.getMessage() != null ? t.getMessage() : "Error processing activation");
            }
        });
    }
}
