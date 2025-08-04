package m3.eventplanner.fragments.budget;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.Collection;
import java.util.List;
import java.util.ArrayList;
import m3.eventplanner.clients.ClientUtils;
import m3.eventplanner.models.CreateBudgetItemDTO;
import m3.eventplanner.models.CreatedBudgetItemDTO;
import m3.eventplanner.models.GetBudgetItemDTO;
import m3.eventplanner.models.GetEventDTO;
import m3.eventplanner.models.GetOfferingCategoryDTO;
import m3.eventplanner.models.UpdateBudgetItemDTO;
import m3.eventplanner.models.UpdatedBudgetItemDTO;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BudgetManagerViewModel extends ViewModel {
    private final MutableLiveData<List<GetBudgetItemDTO>> budgetItems = new MutableLiveData<>();
    private final MutableLiveData<List<GetEventDTO>> events = new MutableLiveData<>();
    private final MutableLiveData<List<GetOfferingCategoryDTO>> categories = new MutableLiveData<>();
    private final MutableLiveData<String> error = new MutableLiveData<>();
    private final MutableLiveData<String> successMessage = new MutableLiveData<>();
    private ClientUtils clientUtils;

    public void initialize(ClientUtils clientUtils) {
        if (clientUtils == null) throw new IllegalArgumentException("ClientUtils cannot be null");
        this.clientUtils = clientUtils;
    }

    public LiveData<List<GetBudgetItemDTO>> getBudgetItems() {
        return budgetItems;
    }

    public LiveData<List<GetEventDTO>> getEvents() {
        return events;
    }

    public LiveData<List<GetOfferingCategoryDTO>> getCategories() {
        return categories;
    }

    public LiveData<String> getError() {
        return error;
    }

    public LiveData<String> getSuccessMessage() {
        return successMessage;
    }

    public void clearMessages() {
        error.setValue(null);
        successMessage.setValue(null);
    }

    public void loadOrganizersEvents(int accountId) {
        clientUtils.getBudgetItemService().findEventsByOrganizer(accountId).enqueue(new Callback<List<GetEventDTO>>() {
            @Override
            public void onResponse(Call<List<GetEventDTO>> call, Response<List<GetEventDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    events.setValue(response.body());
                } else {
                    error.setValue("Failed to load events");
                }
            }

            @Override
            public void onFailure(Call<List<GetEventDTO>> call, Throwable t) {
                error.setValue(t.getMessage() != null ? t.getMessage() : "Error loading events");
            }
        });
    }

    public void loadCategories() {
        clientUtils.getCategoryService().getCategories().enqueue(new Callback<Collection<GetOfferingCategoryDTO>>() {
            @Override
            public void onResponse(Call<Collection<GetOfferingCategoryDTO>> call, Response<Collection<GetOfferingCategoryDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    categories.setValue(new ArrayList<>(response.body())); // Ako želiš List
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

    public void loadBudgetItemsForEvent(int eventId) {
        clientUtils.getBudgetItemService().getBudgetItemsForEvent(eventId).enqueue(new Callback<List<GetBudgetItemDTO>>() {
            @Override
            public void onResponse(Call<List<GetBudgetItemDTO>> call, Response<List<GetBudgetItemDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    budgetItems.setValue(response.body());
                } else {
                    error.setValue("Failed to load budget items");
                    budgetItems.setValue(new ArrayList<>());
                }
            }

            @Override
            public void onFailure(Call<List<GetBudgetItemDTO>> call, Throwable t) {
                error.setValue(t.getMessage() != null ? t.getMessage() : "Error loading budget items");
                budgetItems.setValue(new ArrayList<>());
            }
        });
    }

    public void addBudgetItem(int eventId, int categoryId, int amount) {
        CreateBudgetItemDTO createDTO = new CreateBudgetItemDTO();
        createDTO.setCategoryId(categoryId);
        createDTO.setAmount(amount);

        clientUtils.getBudgetItemService().createBudgetItem(eventId, createDTO)
                .enqueue(new Callback<CreatedBudgetItemDTO>() {
                    @Override
                    public void onResponse(Call<CreatedBudgetItemDTO> call, Response<CreatedBudgetItemDTO> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            successMessage.setValue("Budget item added successfully");
                            loadBudgetItemsForEvent(eventId);
                        } else {
                            error.setValue("Failed to add budget item");
                        }
                    }

                    @Override
                    public void onFailure(Call<CreatedBudgetItemDTO> call, Throwable t) {
                        error.setValue(t.getMessage() != null ? t.getMessage() : "Error adding budget item");
                    }
                });
    }

    public void deleteBudgetItem(int budgetItemId, int eventId) {
        clientUtils.getBudgetItemService().deleteBudgetItem(eventId, budgetItemId)
                .enqueue(new Callback<Boolean>() {
                    @Override
                    public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            successMessage.setValue("Budget item deleted successfully");
                            loadBudgetItemsForEvent(eventId);
                        } else {
                            error.setValue("Cannot delete budget item, as the are purchased items.");
                        }
                    }

                    @Override
                    public void onFailure(Call<Boolean> call, Throwable t) {
                        error.setValue(t.getMessage() != null ? t.getMessage() : "Error deleting budget item");
                    }
                });
    }

    public void updateBudgetItemAmount(int eventId, int budgetItemId, int newAmount) {
        clientUtils.getBudgetItemService().updateBudgetItemAmount(eventId, budgetItemId, new UpdateBudgetItemDTO(newAmount))
                .enqueue(new Callback<UpdatedBudgetItemDTO>() {
                    @Override
                    public void onResponse(Call<UpdatedBudgetItemDTO> call, Response<UpdatedBudgetItemDTO> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            successMessage.setValue("Budget item updated successfully");
                            loadBudgetItemsForEvent(eventId);
                        } else {
                            error.setValue("Cannot update budget item: amount cannot be less than purchased items.");
                        }
                    }

                    @Override
                    public void onFailure(Call<UpdatedBudgetItemDTO> call, Throwable t) {
                        error.setValue(t.getMessage() != null ? t.getMessage() : "Error updating budget item");
                    }
                });
    }
}
