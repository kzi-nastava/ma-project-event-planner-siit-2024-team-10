package m3.eventplanner.fragments.guestlist;

import android.os.Handler;
import android.os.Looper;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.io.File;
import java.io.IOException;
import java.util.List;

import m3.eventplanner.clients.ClientUtils;
import m3.eventplanner.models.CreateGuestListDTO;
import m3.eventplanner.models.GetEventDTO;
import m3.eventplanner.models.GetGuestsDTO;
import m3.eventplanner.utils.PdfUtils;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GuestListViewModel extends ViewModel {
    private final MutableLiveData<GetEventDTO> event = new MutableLiveData<>();
    private MutableLiveData<GetGuestsDTO> currentGuestList = new MutableLiveData<>();
    private final MutableLiveData<String> error = new MutableLiveData<>();
    private final MutableLiveData<String> successMessage = new MutableLiveData<>();
    private ClientUtils clientUtils;
    private PdfUtils pdfUtils;
    private int eventId;

    public LiveData<GetEventDTO> getEvent() { return event; }
    public LiveData<GetGuestsDTO> getCurrentGuestList() { return currentGuestList; }
    public LiveData<String> getError() {
        return error;
    }
    public LiveData<String> getSuccessMessage() {
        return successMessage;
    }

    public void initialize(ClientUtils clientUtils, PdfUtils pdfUtils, int eventId) {
        this.pdfUtils=pdfUtils;
        this.clientUtils = clientUtils;
        this.eventId = eventId;
    }

    public void loadGuestList() {
        clientUtils.getEventService().getGuests(this.eventId).enqueue(new Callback<GetGuestsDTO>() {
            @Override
            public void onResponse(Call<GetGuestsDTO> call, Response<GetGuestsDTO> response) {
                if (response.isSuccessful()){
                    currentGuestList.postValue(response.body());
                } else{
                    error.postValue("Failed to load guests.");
                }
            }

            @Override
            public void onFailure(Call<GetGuestsDTO> call, Throwable t) {
                error.postValue(t.getMessage() != null ? t.getMessage() : "Error loading guests");
            }
        });
    }

    public void getEventById(int id){
        clientUtils.getEventService().getEvent(id).enqueue(new Callback<GetEventDTO>() {
            @Override
            public void onResponse(Call<GetEventDTO> call, Response<GetEventDTO> response) {
                if(response.isSuccessful() && response.body()!=null){
                    event.postValue(response.body());
                } else{
                    error.postValue("Failed to load event.");
                }
            }

            @Override
            public void onFailure(Call<GetEventDTO> call, Throwable t) {
                error.postValue(t.getMessage() != null ? t.getMessage() : "Error loading event.");
            }
        });
    }

    public void sendGuestInvites(int eventId, List<String> emails) {
        CreateGuestListDTO dto = new CreateGuestListDTO(emails);

        clientUtils.getEventService().sendInvitations(eventId, dto).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    successMessage.postValue("Invitations sent!");
                } else {
                    error.postValue("Failed to send invitations.");
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                error.postValue(t.getMessage() != null ? t.getMessage() : "Error sending invitations.");
            }
        });
    }

    public void exportToPdf(){
        clientUtils.getEventService().getGuestlistReport(this.eventId).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    new Thread(() -> {
                        try {
                            // Read bytes from ResponseBody
                            byte[] pdfBytes = response.body().bytes();

                            // Save the PDF file
                            File file = pdfUtils.savePdfFile(pdfBytes, "guestlist_report");

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
