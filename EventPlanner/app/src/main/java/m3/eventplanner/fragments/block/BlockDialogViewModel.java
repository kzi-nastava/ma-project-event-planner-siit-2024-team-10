package m3.eventplanner.fragments.block;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.io.IOException;

import m3.eventplanner.clients.ClientUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BlockDialogViewModel extends ViewModel {

    private final MutableLiveData<String> successMessage = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    private ClientUtils clientUtils;

    public void initialize(ClientUtils clientUtils) {
        this.clientUtils = clientUtils;
    }

    public LiveData<String> getSuccessMessage() {
        return successMessage;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public void sendRequest(boolean blocked, Integer blockerId, Integer toBeBlockedId){
        if(blocked){
            unblock(blockerId, toBeBlockedId);
        } else {
            block(blockerId, toBeBlockedId);
        }
    }

    private void block(Integer blockerId, Integer toBeBlockedId) {
        clientUtils.getAccountService().blockAccount(blockerId, toBeBlockedId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    successMessage.postValue("User blocked successfully.");
                } else {
                    try {
                        String error = response.errorBody() != null ? response.errorBody().string() : "Unknown error";
                        errorMessage.postValue(error);
                    } catch (IOException e) {
                        errorMessage.postValue("Error parsing server response");
                    }
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                errorMessage.postValue(t.getMessage() != null ? t.getMessage() : "Error blocking user");
            }
        });
    }

    private void unblock(Integer blockerId, Integer toBeBlockedId) {
        clientUtils.getAccountService().unblockAccount(blockerId, toBeBlockedId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    successMessage.postValue("User unblocked successfully.");
                } else {
                    try {
                        String error = response.errorBody() != null ? response.errorBody().string() : "Unknown error";
                        errorMessage.postValue(error);
                    } catch (IOException e) {
                        errorMessage.postValue("Error parsing server response");
                    }
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                errorMessage.postValue(t.getMessage() != null ? t.getMessage() : "Error unblocking user");
            }
        });
    }
}