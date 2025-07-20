package m3.eventplanner.fragments.category;

import android.view.View;
import android.widget.TextView;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import m3.eventplanner.adapters.OfferingItemAdapter;
import m3.eventplanner.clients.ClientUtils;
import m3.eventplanner.models.CreateCategoryDTO;
import m3.eventplanner.models.CreatedCategoryDTO;
import m3.eventplanner.models.GetOfferingCategoryDTO;
import m3.eventplanner.models.GetOfferingDTO;
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
                    error.setValue("Failed to load categories");
                }
            }

            @Override
            public void onFailure(Call<Collection<GetOfferingCategoryDTO>> call, Throwable t) {
                error.setValue(t.getMessage() != null ? t.getMessage() : "Error loading categories");
            }
        });
    }

    public void loadOfferingsForCategory(int categoryId, OfferingItemAdapter adapter, TextView noOfferingsText) {
        clientUtils.getOfferingService().getNonPaged().enqueue(new Callback<Collection<GetOfferingDTO>>() {
            @Override
            public void onResponse(Call<Collection<GetOfferingDTO>> call, Response<Collection<GetOfferingDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Filter offerings by category
                    List<GetOfferingDTO> categoryOfferings = ((List<GetOfferingDTO>) response.body())
                            .stream()
                            .filter(offering -> offering.getCategory().getId() == categoryId)
                            .collect(Collectors.toList());

                    if (categoryOfferings.isEmpty()) {
                        noOfferingsText.setVisibility(View.VISIBLE);
                        adapter.updateOfferings(null);
                    } else {
                        noOfferingsText.setVisibility(View.GONE);
                        adapter.updateOfferings(categoryOfferings);
                    }
                } else {
                    noOfferingsText.setVisibility(View.VISIBLE);
                    noOfferingsText.setText("Failed to load offerings");
                }
            }

            @Override
            public void onFailure(Call<Collection<GetOfferingDTO>> call, Throwable t) {
                noOfferingsText.setVisibility(View.VISIBLE);
                noOfferingsText.setText("Error loading offerings");
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
                error.setValue(t.getMessage());
            }
        });
    }

    public void deleteCategory(int categoryId) {
        clientUtils.getCategoryService().deleteCategory(categoryId).enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                if (response.isSuccessful()) {
                    Boolean deleted = response.body();
                    if (deleted != null && deleted) {
                        successMessage.setValue("Category deleted successfully");
                        loadCategories();
                    } else {
                        error.setValue("Category cannot be deleted because it has associated services.");
                    }
                } else if (response.code() == 404) {
                    error.setValue("Category not found");
                } else {
                    error.setValue("Failed to delete category");
                }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                error.setValue("Network error: " + t.getMessage());
            }
        });
    }



    public void createCategory(String name, String description) {
        CreateCategoryDTO categoryDTO = new CreateCategoryDTO(name, description);
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
        clientUtils.getCategoryService().editCategory(id, category).enqueue(new Callback<UpdatedCategoryDTO>() {
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