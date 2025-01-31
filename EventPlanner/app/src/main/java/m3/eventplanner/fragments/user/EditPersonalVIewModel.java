package m3.eventplanner.fragments.user;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import m3.eventplanner.auth.TokenManager;
import m3.eventplanner.clients.ClientUtils;
import m3.eventplanner.models.GetUserDTO;
import m3.eventplanner.models.UpdateUserDTO;
import m3.eventplanner.models.UpdatedUserDTO;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditPersonalVIewModel extends ViewModel {
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

    public void updateUser(UpdateUserDTO updateUserDTO){
        int accountId=tokenManager.getAccountId();
        clientUtils.getUserService().updateUser(accountId,updateUserDTO).enqueue(new Callback<UpdatedUserDTO>() {
            @Override
            public void onResponse(Call<UpdatedUserDTO> call, Response<UpdatedUserDTO> response) {
                if (response.isSuccessful() && response.body() != null) {
                    successMessage.setValue("Successfully updated personal details!");
                } else {
                    error.setValue("Failed to update personal details");
                }
            }

            @Override
            public void onFailure(Call<UpdatedUserDTO> call, Throwable t) {
                error.setValue(t.getMessage() != null ? t.getMessage() : "Error updating personal details");
            }
        });
    }
}
