package m3.eventplanner.fragments.product;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import m3.eventplanner.clients.ClientUtils;
import m3.eventplanner.models.CreateProductDTO;
import m3.eventplanner.models.CreatedProductDTO;
import m3.eventplanner.models.GetOfferingCategoryDTO;
import m3.eventplanner.models.GetProductDTO;
import m3.eventplanner.models.UpdateProductDTO;
import m3.eventplanner.models.UpdatedProductDTO;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditProductViewModel extends ViewModel {
    private final MutableLiveData<String> error = new MutableLiveData<>();
    private final MutableLiveData<String> successMessage = new MutableLiveData<>();
    private final MutableLiveData<GetProductDTO> product = new MutableLiveData<>();

    private ClientUtils clientUtils;

    public LiveData<String> getError() {
        return error;
    }
    public LiveData<String> getSuccessMessage() {
        return successMessage;
    }

    public LiveData<GetProductDTO> getProduct() {
        return product;
    }

    public void initialize(ClientUtils clientUtils) {
        this.clientUtils = clientUtils;
    }


    public void updateProduct(int productId, UpdateProductDTO product) {
        clientUtils.getProductService().updateProduct(productId,product).enqueue(new Callback<UpdatedProductDTO>() {
            @Override
            public void onResponse(Call<UpdatedProductDTO> call, Response<UpdatedProductDTO> response) {
                if (response.isSuccessful()) {
                    successMessage.setValue("Product edited successfully");
                } else {
                    error.setValue("Failed to edited product");
                }
            }

            @Override
            public void onFailure(Call<UpdatedProductDTO> call, Throwable t) {
                error.setValue(t.getMessage() != null ? t.getMessage() : "Error editing product");
            }
        });
    }

    public void fetchProductDetails(int productId) {
        clientUtils.getProductService().getProduct(productId).enqueue(new Callback<GetProductDTO>() {
            @Override
            public void onResponse(Call<GetProductDTO> call, Response<GetProductDTO> response) {
                product.setValue(response.body());
            }

            @Override
            public void onFailure(Call<GetProductDTO> call, Throwable t) {
                error.setValue(t.getMessage() != null ? t.getMessage() : "Error fetching product details");
            }
        });
    }

}
