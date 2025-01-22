package m3.eventplanner.fragments.user;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import m3.eventplanner.auth.TokenManager;
import m3.eventplanner.clients.ClientUtils;
import m3.eventplanner.models.GetUserDTO;

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


}
