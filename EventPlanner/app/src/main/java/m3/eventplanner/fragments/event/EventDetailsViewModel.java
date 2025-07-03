package m3.eventplanner.fragments.event;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import m3.eventplanner.clients.ClientUtils;
import m3.eventplanner.models.AddFavouriteEventDTO;
import m3.eventplanner.models.CreateAgendaItemDTO;
import m3.eventplanner.models.CreateEventRatingDTO;
import m3.eventplanner.models.CreatedAgendaItemDTO;
import m3.eventplanner.models.CreatedEventRatingDTO;
import m3.eventplanner.models.GetAgendaItemDTO;
import m3.eventplanner.models.GetEventDTO;
import m3.eventplanner.models.GetEventStatsDTO;
import m3.eventplanner.models.UpdateAgendaItemDTO;
import m3.eventplanner.models.UpdatedAgendaItemDTO;
import m3.eventplanner.utils.PdfUtils;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.util.stream.Collectors;


public class EventDetailsViewModel extends ViewModel {
    private final MutableLiveData<GetEventDTO> event = new MutableLiveData<>();
    private final MutableLiveData<List<GetAgendaItemDTO>> agenda = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isFavourite = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isOwner = new MutableLiveData<>();
    private final MutableLiveData<String> error = new MutableLiveData<>();
    private final MutableLiveData<String> successMessage = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isParticipating = new MutableLiveData<>();

    private ClientUtils clientUtils;
    private PdfUtils pdfUtils;

    public void initialize(Context context) {
        this.clientUtils = new ClientUtils(context);
        this.pdfUtils = new PdfUtils(context);
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

    public LiveData<Boolean> getIsOwner() {
        return isOwner;
    }

    public LiveData<String> getError() {
        return error;
    }

    public LiveData<String> getSuccessMessage() {
        return successMessage;
    }

    public LiveData<Boolean> getIsParticipating() {
        return isParticipating;
    }

    public void loadEventDetails(int eventId, int accountId, int userId) {
        // Load event details
        clientUtils.getEventService().getEvent(eventId).enqueue(new Callback<GetEventDTO>() {
            @Override
            public void onResponse(Call<GetEventDTO> call, Response<GetEventDTO> response) {
                if (response.isSuccessful() && response.body() != null) {
                    event.setValue(response.body());
                    isOwner.setValue(event.getValue().getOrganizer().getId()==userId);
                    loadAgenda(eventId);
                } else {
                    error.setValue("Failed to load event details");
                }
            }

            @Override
            public void onFailure(Call<GetEventDTO> call, Throwable t) {
                error.setValue(t.getMessage() != null ? t.getMessage() : "Error loading event details");
            }
        });

        if(accountId==0)
            return;

        // Check if event is favourite
        clientUtils.getAccountService().getFavouriteEvent(accountId,eventId).enqueue(new Callback<GetEventDTO>() {
            @Override
            public void onResponse(Call<GetEventDTO> call, Response<GetEventDTO> response) {
                if (response.isSuccessful() && response.body() != null) {
                    isFavourite.setValue(true);
                } else {
                    if(response.code()==404)
                        isFavourite.setValue(false);
                    else
                        error.setValue("Failed to check favourite status");
                }
            }

            @Override
            public void onFailure(Call<GetEventDTO> call, Throwable t) {
                error.setValue(t.getMessage() != null ? t.getMessage() : "Error checking favourite status");
            }
        });
    }

    private void loadAgenda(int eventId) {
        clientUtils.getEventService().getEventAgenda(eventId).enqueue(new Callback<Collection<GetAgendaItemDTO>>() {
            @Override
            public void onResponse(Call<Collection<GetAgendaItemDTO>> call, Response<Collection<GetAgendaItemDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    agenda.setValue(response.body().stream().filter(item -> !item.isDeleted()).collect(Collectors.toList()));
                } else {
                    error.setValue("Failed to load agenda");
                }
            }

            @Override
            public void onFailure(Call<Collection<GetAgendaItemDTO>> call, Throwable t) {
                error.setValue(t.getMessage() != null ? t.getMessage() : "Error loading agenda");
            }
        });
    }

    public void toggleFavourite(int accountId) {
        Boolean currentFavStatus = isFavourite.getValue();
        if (currentFavStatus == null) return;

        if (currentFavStatus) {
            clientUtils.getAccountService().removeEventFromFavourites(accountId, this.event.getValue().getId()).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        isFavourite.setValue(false);
                        successMessage.setValue("Event removed from favourites");
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
            clientUtils.getAccountService().addEventToFavourites(accountId, new AddFavouriteEventDTO(this.event.getValue().getId())).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        isFavourite.setValue(true);
                        successMessage.setValue("Event added to favourites");
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

    public void submitRating(int rating) {
        CreateEventRatingDTO ratingDTO = new CreateEventRatingDTO(rating);

        clientUtils.getEventService().addRating(this.event.getValue().getId(), ratingDTO).enqueue(new Callback<CreatedEventRatingDTO>() {
            @Override
            public void onResponse(Call<CreatedEventRatingDTO> call, Response<CreatedEventRatingDTO> response) {
                if (response.isSuccessful() && response.body() != null) {
                    GetEventDTO currentEvent = event.getValue();
                    if (currentEvent != null) {
                        currentEvent.setAverageRating(response.body().getAverageRating());
                        event.setValue(currentEvent);
                        successMessage.setValue("Rating submitted successfully");
                    }
                } else {
                    error.setValue("Failed to submit rating");
                }
            }

            @Override
            public void onFailure(Call<CreatedEventRatingDTO> call, Throwable t) {
                error.setValue(t.getMessage() != null ? t.getMessage() : "Error submitting rating");
            }
        });
    }

    public void addAgendaItem(CreateAgendaItemDTO createAgendaItemDTO) {
        clientUtils.getEventService().addAgendaItem(this.event.getValue().getId(), createAgendaItemDTO).enqueue(new Callback<CreatedAgendaItemDTO>() {
            @Override
            public void onResponse(Call<CreatedAgendaItemDTO> call, Response<CreatedAgendaItemDTO> response) {
                if (response.isSuccessful() && response.body() != null) {
                    loadAgenda(event.getValue().getId());
                    successMessage.setValue("Agenda item added successfully");
                } else {
                    error.setValue("Failed to add agenda item");
                }
            }

            @Override
            public void onFailure(Call<CreatedAgendaItemDTO> call, Throwable t) {
                error.setValue(t.getMessage() != null ? t.getMessage() : "Error adding agenda item");
            }
        });
    }

    public void updateAgendaItem(int agendaItemId, UpdateAgendaItemDTO updateAgendaItemDTO) {
        clientUtils.getEventService().updateAgendaItem(this.event.getValue().getId(), agendaItemId, updateAgendaItemDTO).enqueue(new Callback<UpdatedAgendaItemDTO>() {
            @Override
            public void onResponse(Call<UpdatedAgendaItemDTO> call, Response<UpdatedAgendaItemDTO> response) {
                if (response.isSuccessful() && response.body() != null) {
                    loadAgenda(event.getValue().getId());
                    successMessage.setValue("Agenda item updated successfully");
                } else {
                    error.setValue("Failed to update agenda item");
                }
            }

            @Override
            public void onFailure(Call<UpdatedAgendaItemDTO> call, Throwable t) {
                error.setValue(t.getMessage() != null ? t.getMessage() : "Error updating agenda item");
            }
        });
    }

    public void deleteAgendaItem(int agendaItemId) {
        clientUtils.getEventService().deleteAgendaItem(this.event.getValue().getId(), agendaItemId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    loadAgenda(event.getValue().getId());
                    successMessage.setValue("Agenda item deleted successfully");
                } else {
                    error.setValue("Failed to delete agenda item");
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                error.setValue(t.getMessage() != null ? t.getMessage() : "Error deleting agenda item");
            }
        });
    }

    public void addParticipant(){
        clientUtils.getEventService().addParticipant(this.event.getValue().getId()).enqueue(new Callback<GetEventStatsDTO>() {
            @Override
            public void onResponse(Call<GetEventStatsDTO> call, Response<GetEventStatsDTO> response) {
                if (response.isSuccessful() && response.body() != null) {
                    GetEventDTO currentEvent = event.getValue();
                    if (currentEvent != null) {
                        currentEvent.setParticipantsCount(response.body().getParticipantsCount());
                        event.setValue(currentEvent);
                        isParticipating.setValue(true);
                        successMessage.setValue("Participation submitted successfully");
                    }
                } else {
                    error.setValue("Failed to submit participation");
                }
            }

            @Override
            public void onFailure(Call<GetEventStatsDTO> call, Throwable t) {
                error.setValue(t.getMessage() != null ? t.getMessage() : "Error submitting participation");
            }
        });
    }

    public void exportToPdf(){
        clientUtils.getEventService().getEventInfoReport(event.getValue().getId()).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    new Thread(() -> {
                        try {
                            // Read bytes from ResponseBody
                            byte[] pdfBytes = response.body().bytes();

                            // Save the PDF file
                            File file = pdfUtils.savePdfFile(pdfBytes, "open_event_report");

                            // Open the saved PDF file on the main thread
                            new Handler(Looper.getMainLooper()).post(() -> {
                                pdfUtils.openPdfFile(file);
                            });
                        } catch (IOException e) {
                            error.setValue("Error saving PDF report");
                        }
                    }).start();
                } else {
                    error.setValue("Error generating PDF report");
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                error.setValue("Error generating PDF report");
            }
        });
    }
}