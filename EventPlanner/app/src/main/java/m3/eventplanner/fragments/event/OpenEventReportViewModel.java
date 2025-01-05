package m3.eventplanner.fragments.event;

import static androidx.core.content.ContentProviderCompat.requireContext;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.io.File;
import java.io.IOException;

import m3.eventplanner.clients.ClientUtils;
import m3.eventplanner.utils.PdfUtils;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OpenEventReportViewModel extends ViewModel {
    private Context context;
    private ClientUtils clientUtils;
    private PdfUtils pdfUtils;
    private int eventId;
    public MutableLiveData<String> error=new MutableLiveData<>();
    public MutableLiveData<String> successMessage=new MutableLiveData<>();

    public void initialize(Context context, int eventId){
        this.eventId = eventId;
        this.context = context;
        this.clientUtils = new ClientUtils(context);
        this.pdfUtils = new PdfUtils(context);
    }

    public LiveData<String> getError() {
        return error;
    }

    public LiveData<String> getSuccessMessage() {
        return successMessage;
    }

    public void exportToPdf(){
        clientUtils.getEventService().getOpenEventReport(eventId).enqueue(new Callback<ResponseBody>() {
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
