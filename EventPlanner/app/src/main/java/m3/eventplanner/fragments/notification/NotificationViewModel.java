package m3.eventplanner.fragments.notification;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import m3.eventplanner.clients.ClientUtils;
import m3.eventplanner.models.GetNotificationDTO;
import m3.eventplanner.models.PagedResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationViewModel extends ViewModel {
    private final MutableLiveData<PagedResponse<GetNotificationDTO>> notifications = new MutableLiveData<>();
    private final MutableLiveData<String> error = new MutableLiveData<>();

    private final MutableLiveData<Boolean> isNotificationsSilenced = new MutableLiveData<>();

    public int currentPage = 0;
    public int totalPages = 0;
    public int totalElements = 0;
    public final int pageSize = 6;
    private Integer accountId;

    private ClientUtils clientUtils;

    public void initialize(ClientUtils clientUtils, Integer accountId) {
        this.clientUtils = clientUtils;
        this.accountId = accountId;
    }

    public LiveData<PagedResponse<GetNotificationDTO>> getNotifications() { return notifications; }

    public LiveData<String> getError() { return error; }

    public LiveData<Boolean> getIsNotificationsSilenced() {
        return isNotificationsSilenced;
    }

    public void fetchPage(int page){
        clientUtils.getNotificationService().getNotifications(this.accountId, page, pageSize).enqueue(new Callback<PagedResponse<GetNotificationDTO>>() {
            @Override
            public void onResponse(Call<PagedResponse<GetNotificationDTO>> call, Response<PagedResponse<GetNotificationDTO>> response) {
                if (response.isSuccessful() && response.body() != null){
                    PagedResponse<GetNotificationDTO> pagedResponse = response.body();
                    notifications.postValue(pagedResponse);
                    currentPage = page;
                    totalPages = pagedResponse.getTotalPages();
                    totalElements = pagedResponse.getTotalElements();
                } else {
                    notifications.setValue(null);
                    error.setValue("Failed to load page: " + page);
                    Log.d("fail", String.valueOf(page));
                }
            }

            @Override
            public void onFailure(Call<PagedResponse<GetNotificationDTO>> call, Throwable t) {
                notifications.setValue(null);
                error.setValue("Failed to load notifications: " + t.getMessage() + t.getStackTrace());
                Log.d("fail","Failed to load notifications: " + t.getMessage() + t.getStackTrace());
            }
        });
    }
    public void fetchNextPage() {
        if (currentPage + 1 < totalPages) {
            fetchPage(currentPage + 1);
        } else {
            error.setValue("No more pages to load.");
        }
    }

    public void fetchPreviousPage() {
        if (currentPage > 0) {
            fetchPage(currentPage - 1);
        } else {
            error.setValue("Already on the first page.");
        }
    }

    public void readAll() {
        clientUtils.getNotificationService().readAll(this.accountId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.d("NotificationViewModel", "All notifications marked as read.");
                } else {
                    error.setValue("Failed to mark notifications as read");
                    Log.d("fail", "Failed to mark notifications as read. Code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                error.setValue("Failed to mark notifications as read: " + t.getMessage());
                Log.d("fail", "Failed to mark notifications as read: " + t.getMessage(), t);
            }
        });
    }

    public void toggleNotifications(){
        clientUtils.getNotificationService().toggleNotifications(this.accountId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.d("NotificationViewModel", "Notifications toggled.");
                } else {
                    error.setValue("Failed to toggle notifications");
                    Log.d("fail", "Failed to toggle notifications. Code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                error.setValue("Failed to toggle notifications");
                Log.d("fail", "Failed to toggle notifications: " + t.getMessage(), t);
            }
        });
    }

    public void fetchNotificationSilenceStatus() {
        clientUtils.getNotificationService().isNotificationSilenced(accountId)
                .enqueue(new Callback<Boolean>() {
                    @Override
                    public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            isNotificationsSilenced.postValue(response.body());
                        } else {
                            error.setValue("Failed to fetch toggle state");
                        }
                    }

                    @Override
                    public void onFailure(Call<Boolean> call, Throwable t) {
                        error.setValue("Failed to fetch toggle state: " + t.getMessage());
                    }
                });
    }
}
