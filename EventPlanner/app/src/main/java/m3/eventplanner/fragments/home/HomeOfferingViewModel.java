package m3.eventplanner.fragments.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import m3.eventplanner.clients.ClientUtils;
import m3.eventplanner.models.GetEventTypeDTO;
import m3.eventplanner.models.GetOfferingCategoryDTO;
import m3.eventplanner.models.GetOfferingDTO;
import m3.eventplanner.models.PagedResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeOfferingViewModel extends ViewModel {
    private ClientUtils clientUtils;

    public final MutableLiveData<Collection<GetOfferingDTO>> topData = new MutableLiveData<>();
    public final MutableLiveData<PagedResponse<GetOfferingDTO>> pagedData = new MutableLiveData<>();
    public final MutableLiveData<String> error = new MutableLiveData<>();
    public int currentPage = 0;
    public int totalPages = 0;
    public int totalElements = 0;
    public final int pageSize = 3;

    private final MutableLiveData<List<GetOfferingCategoryDTO>> categories = new MutableLiveData<>();
    private Boolean isService;
    private Integer filterEventTypeId;
    private String filterEventLocation;
    private Integer filterEventMaxParticipants;
    private Double filterEventMinRating;
    private String filterEventStartDate;
    private String filterEventEndDate;
    private String eventSearchQuery;
    private String sortEventsBy;
    private String sortEventsDirection;
    private MutableLiveData<Double> highestPrice = new MutableLiveData<>();
    public LiveData<Double> getHighestPrice() {
        return highestPrice;
    }

    public void initialize(ClientUtils clientUtils) {
        this.clientUtils = clientUtils;
    }

    public LiveData<String> getError() {
        return error;
    }

    public LiveData<Collection<GetOfferingDTO>> getTopData() {
        return topData;
    }

    public LiveData<PagedResponse<GetOfferingDTO>> getPagedData() {
        return pagedData;
    }

    public LiveData<List<GetOfferingCategoryDTO>> getCategories() {
        return categories;
    }

    public void loadTopOfferings() {
        clientUtils.getOfferingService().getTopOfferings().enqueue(new Callback<Collection<GetOfferingDTO>>() {
            @Override
            public void onResponse(Call<Collection<GetOfferingDTO>> call, Response<Collection<GetOfferingDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    topData.setValue(new ArrayList<>(response.body()));
                } else {
                    topData.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<Collection<GetOfferingDTO>> call, Throwable t) {
                topData.setValue(null);
                error.setValue("Failed to load offerings");
            }
        });
    }

    public void fetchPage(int page) {
        clientUtils.getOfferingService().getOfferings(
                page,
                pageSize,
                isService,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null
        ).enqueue(new Callback<PagedResponse<GetOfferingDTO>>() {
            @Override
            public void onResponse(Call<PagedResponse<GetOfferingDTO>> call, Response<PagedResponse<GetOfferingDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    PagedResponse<GetOfferingDTO> pagedResponse = response.body();
                    pagedData.postValue(pagedResponse);
                    currentPage = page;
                    totalPages = pagedResponse.getTotalPages();
                    totalElements = pagedResponse.getTotalElements();
                } else {
                    pagedData.setValue(null);
                    error.setValue("Failed to load page: " + page);
                }
            }

            @Override
            public void onFailure(Call<PagedResponse<GetOfferingDTO>> call, Throwable t) {
                pagedData.setValue(null);
                error.setValue("Failed to load offerings: " + t.getMessage());
            }
        });
    }

    public void fetchNextPage() {
        if (currentPage + 1 < totalPages) {
            fetchPage(currentPage + 1);
        } else {
            error.setValue("No more pages to load.");
        }
    }

    public void fetchPreviousPage() {
        if (currentPage > 0) {
            fetchPage(currentPage - 1);
        } else {
            error.setValue("Already on the first page.");
        }
    }

    // TO-DO: change from event logic to offering logic
    // 1. Move error string to be shared between view models
    // 2. Change the search bar, move the search bar id

    public void loadFilteredEvents(int page, Integer eventTypeId, String location, Integer maxParticipants, Double minRating, String startDate, String endDate) {
        this.filterEventTypeId = eventTypeId;
        this.filterEventLocation = location;
        this.filterEventMaxParticipants = maxParticipants;
        this.filterEventMinRating = minRating;
        this.filterEventStartDate = startDate;
        this.filterEventEndDate = endDate;
        fetchPage(page);
    }

    public void loadSearchedEvents(int page, String query) {
        this.eventSearchQuery = query;
        fetchPage(page);
    }

    public void loadSortedEvents(int page, String sortBy, String sortDirection) {
        this.sortEventsBy = sortBy;
        this.sortEventsDirection = sortDirection;
        fetchPage(page);
    }

    public void loadOfferings(int page, Boolean isService){
        this.isService = isService;
        fetchPage(page);
    }

    public void resetFilters() {
        this.filterEventTypeId = null;
        this.filterEventLocation = null;
        this.filterEventMaxParticipants = null;
        this.filterEventMinRating = null;
        this.filterEventStartDate = null;
        this.filterEventEndDate = null;
        this.eventSearchQuery = null;
        this.sortEventsBy = null;
        this.sortEventsDirection = null;

        fetchPage(0);
    }

    public void fetchCategories() {
        clientUtils.getCategoryService().getCategories().enqueue(new Callback<Collection<GetOfferingCategoryDTO>>() {
            @Override
            public void onResponse(Call<Collection<GetOfferingCategoryDTO>> call, Response<Collection<GetOfferingCategoryDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    categories.setValue((List<GetOfferingCategoryDTO>) response.body());
                }
            }

            @Override
            public void onFailure(Call<Collection<GetOfferingCategoryDTO>> call, Throwable t) {
                categories.setValue(new ArrayList<>());
            }
        });
    }
    public void fetchHighestPrice(Boolean isServiceFilter) {
        clientUtils.getOfferingService().getHighestPrice(isServiceFilter).enqueue(new Callback<Double>() {
            @Override
            public void onResponse(Call<Double> call, Response<Double> response) {
                if (response.isSuccessful() && response.body() != null) {
                    highestPrice.setValue(response.body());
                } else {
                    highestPrice.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<Double> call, Throwable t) {
                highestPrice.setValue(null);
            }
        });
    }
}
