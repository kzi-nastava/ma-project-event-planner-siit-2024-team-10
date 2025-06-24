package m3.eventplanner.fragments.user;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import m3.eventplanner.clients.ClientUtils;
import m3.eventplanner.models.GetEventDTO;
import m3.eventplanner.models.PagedResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FavouritesViewModel extends ViewModel {
    private ClientUtils clientUtils;
    public final MutableLiveData<PagedResponse<GetEventDTO>> favouriteEvents = new MutableLiveData<>();
    public final MutableLiveData<String> error = new MutableLiveData<>();
    public int currentEventPage = 0;
    public int totalEventPages = 0;
    public int totalEventElements = 0;
    public final int pageSize = 3;
    private int accountId = -1;

    public void initialize(ClientUtils clientUtils,int accountId) {
        this.clientUtils = clientUtils;
        this.accountId = accountId;
    }

    public LiveData<String> getError() {
        return error;
    }

    public LiveData<PagedResponse<GetEventDTO>> getFavouriteEvents() {
        return favouriteEvents;
    }

    public void fetchEventPage(int page) {
        clientUtils.getAccountService().getFavouriteEvents(accountId,page,pageSize)
                .enqueue(new Callback<PagedResponse<GetEventDTO>>() {
            @Override
            public void onResponse(Call<PagedResponse<GetEventDTO>> call, Response<PagedResponse<GetEventDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    PagedResponse<GetEventDTO> pagedResponse = response.body();
                    favouriteEvents.postValue(pagedResponse);
                    currentEventPage = page;
                    totalEventPages = pagedResponse.getTotalPages();
                    totalEventElements = pagedResponse.getTotalElements();
                } else {
                    favouriteEvents.setValue(null);
                    error.setValue("Failed to load page: " + page);
                }
            }

            @Override
            public void onFailure(Call<PagedResponse<GetEventDTO>> call, Throwable t) {
                favouriteEvents.setValue(null);
                error.setValue("Failed to load events: " + t.getMessage() + t.getStackTrace());
            }
        });
    }

    public void fetchNextEventPage() {
        if (currentEventPage + 1 < totalEventPages) {
            fetchEventPage(currentEventPage + 1);
        } else {
            error.setValue("No more pages to load.");
        }
    }

    public void fetchPreviousEventPage() {
        if (currentEventPage > 0) {
            fetchEventPage(currentEventPage - 1);
        } else {
            error.setValue("Already on the first page.");
        }
    }
}
