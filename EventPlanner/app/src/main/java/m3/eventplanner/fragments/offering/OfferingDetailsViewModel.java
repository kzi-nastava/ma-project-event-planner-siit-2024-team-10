package m3.eventplanner.fragments.offering;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.io.IOException;
import java.util.List;

import m3.eventplanner.clients.ClientUtils;
import m3.eventplanner.models.AddFavouriteEventDTO;
import m3.eventplanner.models.AddFavouriteOfferingDTO;
import m3.eventplanner.models.BuyRequestDTO;
import m3.eventplanner.models.GetAgendaItemDTO;
import m3.eventplanner.models.GetEventDTO;
import m3.eventplanner.models.GetOfferingDTO;
import m3.eventplanner.models.GetProductDTO;
import m3.eventplanner.models.GetServiceDTO;
import m3.eventplanner.utils.PdfUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OfferingDetailsViewModel extends ViewModel {
    private final MutableLiveData<GetOfferingDTO> offering = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isFavourite = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isOwner = new MutableLiveData<>();
    private final MutableLiveData<Boolean> navigateHome = new MutableLiveData<>();
    private final MutableLiveData<String> error = new MutableLiveData<>();
    private final MutableLiveData<String> successMessage = new MutableLiveData<>();
    private final MutableLiveData<List<GetEventDTO>> events = new MutableLiveData<>();
    private ClientUtils clientUtils;
    private PdfUtils pdfUtils;

    public void initialize(Context context) {
        navigateHome.setValue(false);
        this.clientUtils = new ClientUtils(context);
        this.pdfUtils = new PdfUtils(context);
    }

    public LiveData<GetOfferingDTO> getOffering() {
        return offering;
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
    public LiveData<List<GetEventDTO>> getEvents() {
        return events;
    }
    public LiveData<Boolean> getNavigateHome() {
        return navigateHome;
    }

    public LiveData<String> getSuccessMessage() {
        return successMessage;
    }
    public void loadEventsForOrganizer(int organizerId) {
        clientUtils.getBudgetItemService().findEventsByOrganizer(organizerId).enqueue(new Callback<List<GetEventDTO>>() {
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

    public void loadOfferingDetails(int offeringId, int accountId, int userId) {
        // Prvo pokušaj da učitaš kao Service
        clientUtils.getServiceService().getService(offeringId).enqueue(new Callback<GetServiceDTO>() {
            @Override
            public void onResponse(Call<GetServiceDTO> call, Response<GetServiceDTO> response) {
                if (response.isSuccessful() && response.body() != null) {
                    offering.setValue(response.body());
                    isOwner.setValue(response.body().getProvider().getId() == userId);
                } else if (response.code() == 404) {
                    // Ako nije Service, probaj kao Product
                    loadProductOffering(offeringId, userId);
                } else {
                    error.setValue("Failed to load service details");
                }
            }

            @Override
            public void onFailure(Call<GetServiceDTO> call, Throwable t) {
                error.setValue(t.getMessage() != null ? t.getMessage() : "Error loading service");
            }
        });

        // Ako nije ulogovan korisnik, ne proveravaj favorite
        if (accountId == 0)
            return;

        // Proveri da li je favorit
        clientUtils.getAccountService().getFavouriteOffering(accountId, offeringId).enqueue(new Callback<GetOfferingDTO>() {
            @Override
            public void onResponse(Call<GetOfferingDTO> call, Response<GetOfferingDTO> response) {
                if (response.isSuccessful() && response.body() != null) {
                    isFavourite.setValue(true);
                } else {
                    if (response.code() == 404)
                        isFavourite.setValue(false);
                    else
                        error.setValue("Failed to check favourite status");
                }
            }

            @Override
            public void onFailure(Call<GetOfferingDTO> call, Throwable t) {
                error.setValue(t.getMessage() != null ? t.getMessage() : "Error checking favourite status");
            }
        });
    }

    private void loadProductOffering(int offeringId, int userId) {
        clientUtils.getProductService().getProduct(offeringId).enqueue(new Callback<GetProductDTO>() {
            @Override
            public void onResponse(Call<GetProductDTO> call, Response<GetProductDTO> response) {
                if (response.isSuccessful() && response.body() != null) {
                    offering.setValue(response.body());
                    isOwner.setValue(response.body().getProvider().getId() == userId);
                } else {
                    error.setValue("Offering not found");
                }
            }

            @Override
            public void onFailure(Call<GetProductDTO> call, Throwable t) {
                error.setValue(t.getMessage() != null ? t.getMessage() : "Error loading product");
            }
        });
    }


    public void toggleFavourite(int accountId) {
        Boolean currentFavStatus = isFavourite.getValue();
        if (currentFavStatus == null) return;

        if (currentFavStatus) {
            clientUtils.getAccountService().removeOfferingFromFavourites(accountId, this.offering.getValue().getId()).enqueue(new Callback<Void>() {
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
            clientUtils.getAccountService().addOfferingToFavourites(accountId, new AddFavouriteOfferingDTO(this.offering.getValue().getId())).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        isFavourite.setValue(true);
                        successMessage.setValue("Offering added to favourites");
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
    public void deleteOffering(){
        clientUtils.getServiceService().deleteService(this.offering.getValue().getId()).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    successMessage.setValue("Offering deleted successfully");
                    navigateHome.setValue(true);
                } else {
                    try {
                        String errorBody = response.errorBody().string();
                        error.setValue(errorBody);
                    } catch (IOException e) {
                        e.printStackTrace();
                        error.setValue("An error occurred while parsing the error message.");
                    }
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                error.setValue(t.getMessage() != null ? t.getMessage() : "Error deleting offering");
            }
        });
    }
    public void buyOffering(int eventId) {
        clientUtils.getBudgetItemService().buyOffering(eventId, offering.getValue().getId()).enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                if (response.isSuccessful()) {
                    successMessage.setValue("Product successfully purchased.");
                } else {
                    error.setValue("Failed to purchase product.");
                }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                error.setValue("Error: " + t.getMessage());
            }
        });
    }

}
