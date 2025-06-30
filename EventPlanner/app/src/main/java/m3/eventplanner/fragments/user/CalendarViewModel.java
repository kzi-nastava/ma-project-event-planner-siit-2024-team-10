package m3.eventplanner.fragments.user;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import m3.eventplanner.clients.ClientUtils;
import m3.eventplanner.models.GetCalendarItemDTO;
import m3.eventplanner.models.GetEventDTO;
import m3.eventplanner.models.GetEventTypeDTO;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CalendarViewModel extends ViewModel {
    private final MutableLiveData<Collection<GetCalendarItemDTO>> calendarItems = new MutableLiveData<>();
    private final MutableLiveData<String> error = new MutableLiveData<>();

    public LiveData<Collection<GetCalendarItemDTO>> getCalendarItems() {
        return calendarItems;
    }

    public LiveData<String> getError() {
        return error;
    }

    private ClientUtils clientUtils;

    public void initialize(ClientUtils clientUtils) {
        this.clientUtils = clientUtils;
    }

    public void loadCalendar(int accountId){
        clientUtils.getAccountService().getCalendar(accountId).enqueue(new Callback<Collection<GetCalendarItemDTO>>() {
            @Override
            public void onResponse(Call<Collection<GetCalendarItemDTO>> call, Response<Collection<GetCalendarItemDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    calendarItems.setValue(response.body());
                } else {
                    error.setValue("Failed to load calendar");
                }
            }

            @Override
            public void onFailure(Call<Collection<GetCalendarItemDTO>> call, Throwable t) {
                error.setValue(t.getMessage() != null ? t.getMessage() : "Error loading calendar");
            }
        });
    }
}
