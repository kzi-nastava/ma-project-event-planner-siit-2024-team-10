package m3.eventplanner.fragments.user;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.io.IOException;

import m3.eventplanner.auth.TokenManager;
import m3.eventplanner.clients.ClientUtils;
import m3.eventplanner.models.ChangePasswordDTO;
import m3.eventplanner.models.GetUserDTO;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChangePasswordViewModel extends ViewModel {
    private ClientUtils clientUtils;
    private TokenManager tokenManager;
    private final MutableLiveData<String> error = new MutableLiveData<>();
    private final MutableLiveData<String> successMessage = new MutableLiveData<>();

    public LiveData<String> getError() {
        return error;
    }

    public LiveData<String> getSuccessMessage() {
        return successMessage;
    }

    public void initialize(ClientUtils clientUtils, TokenManager tokenManager) {
        this.clientUtils = clientUtils;
        this.tokenManager=tokenManager;
    }

    public void changePassword(ChangePasswordDTO changePasswordDTO){
        clientUtils.getUserService().changePassword(tokenManager.getAccountId(),changePasswordDTO).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    successMessage.setValue("Password changed successfully!");
                } else {
                    try {
                        error.setValue(response.errorBody().string());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                error.setValue(t.getMessage());
            }
        });
    }

}
