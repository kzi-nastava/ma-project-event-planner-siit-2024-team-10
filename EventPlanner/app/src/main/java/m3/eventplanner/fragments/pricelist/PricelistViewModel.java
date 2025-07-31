package m3.eventplanner.fragments.pricelist;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.io.File;
import java.io.IOException;
import java.util.List;

import m3.eventplanner.clients.ClientUtils;
import m3.eventplanner.clients.PricelistService;
import m3.eventplanner.models.GetPricelistItemDTO;
import m3.eventplanner.models.UpdatePricelistItemDTO;
import m3.eventplanner.models.UpdatedPricelistItemDTO;
import m3.eventplanner.utils.PdfUtils;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PricelistViewModel extends ViewModel {
    private MutableLiveData<List<GetPricelistItemDTO>> items = new MutableLiveData<>();
    private ClientUtils clientUtils;
    private PdfUtils pdfUtils;
    public MutableLiveData<String> error=new MutableLiveData<>();

    public PricelistViewModel() {
    }

    public LiveData<List<GetPricelistItemDTO>> getItems() {
        return items;
    }
    public void initialize(Context context, ClientUtils clientUtils) {
        if (clientUtils == null) throw new IllegalArgumentException("ClientUtils cannot be null");
        this.clientUtils = clientUtils;
        this.pdfUtils = new PdfUtils(context);
    }
    public void fetchItems() {
        clientUtils.getPricelistService().getPricelist().enqueue(new Callback<List<GetPricelistItemDTO>>() {
            @Override
            public void onResponse(Call<List<GetPricelistItemDTO>> call, Response<List<GetPricelistItemDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<GetPricelistItemDTO> sortedList = response.body();
                    sortedList.sort((item1, item2) -> item1.getName().compareToIgnoreCase(item2.getName()));
                    items.postValue(sortedList);
                }
            }

            @Override
            public void onFailure(Call<List<GetPricelistItemDTO>> call, Throwable t) {
                Log.e("PricelistViewModel", "Get fail: " + t.getMessage());
            }
        });
    }

    public void updateItem(int offeringId, double price, double discount) {
        UpdatePricelistItemDTO dto = new UpdatePricelistItemDTO(price, discount);
        clientUtils.getPricelistService().updatePricing(offeringId, dto).enqueue(new Callback<UpdatedPricelistItemDTO>() {
            @Override
            public void onResponse(Call<UpdatedPricelistItemDTO> call, Response<UpdatedPricelistItemDTO> response) {
                if (response.isSuccessful()) {
                    fetchItems();
                }
            }

            @Override
            public void onFailure(Call<UpdatedPricelistItemDTO> call, Throwable t) {
                Log.e("PricelistViewModel", "Update fail: " + t.getMessage());
            }
        });
    }
    public void exportToPdf(){
        clientUtils.getPricelistService().getPricelistReport().enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    new Thread(() -> {
                        try {
                            // Read bytes from ResponseBody
                            byte[] pdfBytes = response.body().bytes();

                            // Save the PDF file
                            File file = pdfUtils.savePdfFile(pdfBytes, "pricelist_report");

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
