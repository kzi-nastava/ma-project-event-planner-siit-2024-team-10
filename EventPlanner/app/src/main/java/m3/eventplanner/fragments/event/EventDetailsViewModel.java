package m3.eventplanner.fragments.event;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;


import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import m3.eventplanner.clients.ClientUtils;
import m3.eventplanner.models.AddFavouriteEventDTO;
import m3.eventplanner.models.GetAgendaItemDTO;
import m3.eventplanner.models.GetEventDTO;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.util.stream.Collectors;


public class EventDetailsViewModel extends ViewModel {
    private final MutableLiveData<GetEventDTO> event = new MutableLiveData<>();
    private final MutableLiveData<List<GetAgendaItemDTO>> agenda = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isFavourite = new MutableLiveData<>();
    private final MutableLiveData<String> error = new MutableLiveData<>();

    private ClientUtils clientUtils;

    public void initialize(ClientUtils clientUtils) {
        this.clientUtils = clientUtils;
    }

    public LiveData<GetEventDTO> getEvent() {
        return event;
    }

    public LiveData<List<GetAgendaItemDTO>> getAgenda() {
        return agenda;
    }

    public LiveData<Boolean> getIsFavourite() {
        return isFavourite;
    }

    public LiveData<String> getError() {
        return error;
    }

    public void loadEventDetails(int eventId, int accountId) {
        // Load event details
        clientUtils.getEventService().getEvent(eventId).enqueue(new Callback<GetEventDTO>() {
            @Override
            public void onResponse(Call<GetEventDTO> call, Response<GetEventDTO> response) {
                if (response.isSuccessful() && response.body() != null) {
                    event.setValue(response.body());
                } else {
                    error.setValue("Failed to load event details");
                }
            }

            @Override
            public void onFailure(Call<GetEventDTO> call, Throwable t) {
                error.setValue(t.getMessage() != null ? t.getMessage() : "Error loading event details");
            }
        });

        // Load agenda
        clientUtils.getEventService().getEventAgenda(eventId).enqueue(new Callback<Collection<GetAgendaItemDTO>>() {
            @Override
            public void onResponse(Call<Collection<GetAgendaItemDTO>> call, Response<Collection<GetAgendaItemDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    agenda.setValue(response.body().stream().collect(Collectors.toList()));
                } else {
                    error.setValue("Failed to load agenda");
                }
            }

            @Override
            public void onFailure(Call<Collection<GetAgendaItemDTO>> call, Throwable t) {
                error.setValue(t.getMessage() != null ? t.getMessage() : "Error loading agenda");
            }
        });

        // Check if event is favourite
        clientUtils.getAccountService().getFavouriteEvents(accountId).enqueue(new Callback<Collection<GetEventDTO>>() {
            @Override
            public void onResponse(Call<Collection<GetEventDTO>> call, Response<Collection<GetEventDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    boolean isFav = response.body().stream().anyMatch(e -> e.getId() == eventId);
                    isFavourite.setValue(isFav);
                } else {
                    error.setValue("Failed to check favourite status");
                }
            }

            @Override
            public void onFailure(Call<Collection<GetEventDTO>> call, Throwable t) {
                error.setValue(t.getMessage() != null ? t.getMessage() : "Error checking favourite status");
            }
        });
    }

    public void toggleFavourite(int accountId, int eventId) {
        Boolean currentFavStatus = isFavourite.getValue();
        if (currentFavStatus == null) return;

        if (currentFavStatus) {
            clientUtils.getAccountService().removeEventFromFavourites(accountId, eventId).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        isFavourite.setValue(false);
                    } else {
                        error.setValue("Failed to remove from favourites");
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    error.setValue(t.getMessage() != null ? t.getMessage() : "Error removing from favourites");
                }
            });
        } else {
            clientUtils.getAccountService().addEventToFavourites(accountId, new AddFavouriteEventDTO(eventId)).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        isFavourite.setValue(true);
                    } else {
                        error.setValue("Failed to add to favourites");
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    error.setValue(t.getMessage() != null ? t.getMessage() : "Error adding to favourites");
                }
            });
        }
    }
}