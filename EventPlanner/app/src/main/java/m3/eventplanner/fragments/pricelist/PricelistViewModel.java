package m3.eventplanner.fragments.pricelist;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import m3.eventplanner.clients.ClientUtils;
import m3.eventplanner.clients.PricelistService;
import m3.eventplanner.models.GetPricelistItemDTO;
import m3.eventplanner.models.UpdatePricelistItemDTO;
import m3.eventplanner.models.UpdatedPricelistItemDTO;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PricelistViewModel extends ViewModel {
    private MutableLiveData<List<GetPricelistItemDTO>> items = new MutableLiveData<>();
    private ClientUtils clientUtils;

    public PricelistViewModel() {
    }

    public LiveData<List<GetPricelistItemDTO>> getItems() {
        return items;
    }
    public void initialize(ClientUtils clientUtils) {
        if (clientUtils == null) throw new IllegalArgumentException("ClientUtils cannot be null");
        this.clientUtils = clientUtils;
    }
    public void fetchItems() {
        clientUtils.getPricelistService().getPricelist().enqueue(new Callback<List<GetPricelistItemDTO>>() {
            @Override
            public void onResponse(Call<List<GetPricelistItemDTO>> call, Response<List<GetPricelistItemDTO>> response) {
                if (response.isSuccessful()) {
                    items.postValue(response.body());
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
}
