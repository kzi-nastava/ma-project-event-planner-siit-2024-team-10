package m3.eventplanner.fragments.category;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import m3.eventplanner.clients.ClientUtils;
import m3.eventplanner.models.CreateCategoryDTO;
import m3.eventplanner.models.CreatedCategoryDTO;
import m3.eventplanner.models.GetOfferingCategoryDTO;
import m3.eventplanner.models.UpdateCategoryDTO;
import m3.eventplanner.models.UpdatedCategoryDTO;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CategoryViewModel extends ViewModel {
    private final MutableLiveData<List<GetOfferingCategoryDTO>> categories = new MutableLiveData<>();
    private final MutableLiveData<String> error = new MutableLiveData<>();
    private final MutableLiveData<String> successMessage = new MutableLiveData<>();

    private ClientUtils clientUtils;
    public void initialize(ClientUtils clientUtils){
        this.clientUtils = clientUtils;
    }

    public LiveData<List<GetOfferingCategoryDTO>> getCategories(){
        return categories;
    }

    public LiveData<String> getError() {
        return error;
    }

    public LiveData<String> getSuccessMessage() {
        return successMessage;
    }

    public void loadCategories() {
        clientUtils.getCategoryService().getCategories().enqueue(new Callback<Collection<GetOfferingCategoryDTO>>() {
            @Override
            public void onResponse(Call<Collection<GetOfferingCategoryDTO>> call, Response<Collection<GetOfferingCategoryDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Filter out deleted categories
                    List<GetOfferingCategoryDTO> activeCategories = ((List<GetOfferingCategoryDTO>) response.body())
                            .stream()
                            .filter(category -> !category.isDeleted())
                            .collect(Collectors.toList());
                    categories.setValue(activeCategories);
                } else {
                    error.setValue("Failed to load category");
                }
            }

            @Override
            public void onFailure(Call<Collection<GetOfferingCategoryDTO>> call, Throwable t) {
                error.setValue(t.getMessage() != null ? t.getMessage() : "Error loading category");
            }
        });
    }
    public void approveCategory(int categoryId) {
        clientUtils.getCategoryService().approve(categoryId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    successMessage.setValue("Category approved successfully");
                    loadCategories();
                } else {
                    error.setValue("Failed to approve category");
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                error.setValue("Network error: " + t.getMessage());
            }
        });
    }

    public void deleteCategory(int categoryId) {
        clientUtils.getCategoryService().deleteCategory(categoryId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    successMessage.setValue("Category deleted successfully");
                    loadCategories();
                } else {
                    error.setValue("Failed to delete category");
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                error.setValue("Network error: " + t.getMessage());
            }
        });
        loadCategories();
    }

    public void createCategory(String name, String description) {
        CreateCategoryDTO categoryDTO= new CreateCategoryDTO(name,description);
        clientUtils.getCategoryService().addCategory(categoryDTO).enqueue(new Callback<CreatedCategoryDTO>() {
            @Override
            public void onResponse(Call<CreatedCategoryDTO> call, Response<CreatedCategoryDTO> response) {
                if (response.isSuccessful()) {
                    successMessage.setValue("Category created successfully");
                    loadCategories();
                } else {
                    error.setValue("Failed to create category");
                }
            }

            @Override
            public void onFailure(Call<CreatedCategoryDTO> call, Throwable t) {
                error.setValue(t.getMessage() != null ? t.getMessage() : "Error creating category");
            }
        });
    }

    public void editCategory(int id, String name, String description) {
        UpdateCategoryDTO category = new UpdateCategoryDTO(name, description);
        clientUtils.getCategoryService().editCategory(id,category).enqueue(new Callback<UpdatedCategoryDTO>() {
            @Override
            public void onResponse(Call<UpdatedCategoryDTO> call, Response<UpdatedCategoryDTO> response) {
                if (response.isSuccessful()) {
                    successMessage.setValue("Category edited successfully");
                    loadCategories();
                } else {
                    error.setValue("Failed to edit category");
                }
            }

            @Override
            public void onFailure(Call<UpdatedCategoryDTO> call, Throwable t) {
                error.setValue(t.getMessage() != null ? t.getMessage() : "Error editing category");
            }
        });
    }
}
