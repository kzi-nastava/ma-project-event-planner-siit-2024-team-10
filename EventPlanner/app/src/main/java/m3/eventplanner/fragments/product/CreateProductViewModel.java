package m3.eventplanner.fragments.product;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import m3.eventplanner.clients.ClientUtils;
import m3.eventplanner.models.GetEventTypeDTO;
import m3.eventplanner.models.GetOfferingCategoryDTO;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateProductViewModel extends ViewModel {
    private final MutableLiveData<List<GetOfferingCategoryDTO>> categories = new MutableLiveData<>();
    private final MutableLiveData<String> error = new MutableLiveData<>();
    private final MutableLiveData<String> successMessage = new MutableLiveData<>();

    private ClientUtils clientUtils;

    public LiveData<String> getError() {
        return error;
    }
    public LiveData<String> getSuccessMessage() {
        return successMessage;
    }

    public LiveData<List<GetOfferingCategoryDTO>> getCategories() {
        return categories;
    }

    public void initialize(ClientUtils clientUtils) {
        this.clientUtils = clientUtils;
    }

    public void loadCategories(){
        clientUtils.getCategoryService().getCategories().enqueue(new Callback<Collection<GetOfferingCategoryDTO>>() {
            @Override
            public void onResponse(Call<Collection<GetOfferingCategoryDTO>> call, Response<Collection<GetOfferingCategoryDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    categories.setValue(response.body().stream()
                            .filter(category->!(category.isPending()||category.isDeleted()))
                            .collect(Collectors.toList()));
                } else {
                    error.setValue("Failed to load categories");
                }
            }

            @Override
            public void onFailure(Call<Collection<GetOfferingCategoryDTO>> call, Throwable t) {
                error.setValue(t.getMessage() != null ? t.getMessage() : "Error loading categories");
            }
        });
    }

}
