package m3.eventplanner.fragments.user;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.Collection;

import m3.eventplanner.auth.TokenManager;
import m3.eventplanner.clients.ClientUtils;
import m3.eventplanner.models.GetEventDTO;
import m3.eventplanner.models.GetUserDTO;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserDetailsViewModel extends ViewModel {
    private ClientUtils clientUtils;
    private TokenManager tokenManager;
    private final MutableLiveData<GetUserDTO> user = new MutableLiveData<>();
    private final MutableLiveData<String> error = new MutableLiveData<>();
    private final MutableLiveData<String> successMessage = new MutableLiveData<>();

    public LiveData<String> getError() {
        return error;
    }

    public LiveData<String> getSuccessMessage() {
        return successMessage;
    }

    public LiveData<GetUserDTO> getUser() {
        return user;
    }
    public void initialize(ClientUtils clientUtils, TokenManager tokenManager) {
        this.clientUtils = clientUtils;
        this.tokenManager=tokenManager;
    }

    public void loadUser(){
        int accountId = tokenManager.getAccountId();
        clientUtils.getUserService().getUser(accountId).enqueue(new Callback<GetUserDTO>() {
            @Override
            public void onResponse(Call<GetUserDTO> call, Response<GetUserDTO> response) {
                if (response.isSuccessful() && response.body() != null) {
                    user.setValue(response.body());
                } else {
                    error.setValue("Failed to fetch user details");
                }
            }

            @Override
            public void onFailure(Call<GetUserDTO> call, Throwable t) {
                error.setValue(t.getMessage() != null ? t.getMessage() : "Error fetching user details");
            }
        });
    }
}
