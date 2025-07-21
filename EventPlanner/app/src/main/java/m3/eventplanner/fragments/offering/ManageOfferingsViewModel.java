package m3.eventplanner.fragments.offering;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import m3.eventplanner.clients.ClientUtils;
import m3.eventplanner.models.GetOfferingCategoryDTO;
import m3.eventplanner.models.GetOfferingDTO;
import m3.eventplanner.models.PagedResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ManageOfferingsViewModel extends ViewModel {
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
    private Integer filterCategoryId;
    private String filterOfferingLocation;
    private Integer filterMinPrice;
    private Integer filterMaxPrice;
    private Integer filterMinDiscount;
    private Integer filterMinDuration;
    private Double filterMinRating;
    private Boolean filterIsAvailable;
    private String searchQuery;
    private String sortBy;
    private String sortDirection;
    private Boolean initLoad = true;
    private Integer accountId;
    private Integer userId;
    private MutableLiveData<Double> highestPrice = new MutableLiveData<>();
    public LiveData<Double> getHighestPrice() {
        return highestPrice;
    }

    public void initialize(ClientUtils clientUtils, Integer userId) {
        this.userId=userId;
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
        if(!initLoad && this.filterOfferingLocation != null){
            accountId=null;
        }
        clientUtils.getOfferingService().getOfferings(
                page,
                pageSize,
                isService,
                searchQuery,
                filterCategoryId,
                filterOfferingLocation,
                filterMinPrice,
                filterMaxPrice,
                filterMinDiscount,
                filterMinDuration,
                filterMinRating,
                filterIsAvailable,
                sortBy,
                sortDirection,
                accountId,
                userId

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

    public void loadFilteredOfferings(int page, Integer categoryId, String location, Integer minPrice, Integer maxPrice, Integer minDuration, Integer minDiscount, Double minRating, Boolean isAvailable, Boolean initialLoad, Integer accountId) {
        this.filterCategoryId = categoryId;
        this.filterOfferingLocation = location;
        this.filterMinPrice = minPrice;
        this.filterMaxPrice = maxPrice;
        this.filterMinDiscount = minDiscount;
        this.filterMinDuration = minDuration;
        this.filterMinRating = minRating;
        this.filterIsAvailable = isAvailable;
        this.initLoad = initialLoad;
        if (!initialLoad && this.filterOfferingLocation!=null){
            this.accountId=null;
        }else{
            this.accountId=accountId;
        }
        fetchPage(page);
    }

    public void loadSearchedOfferings(int page, String query) {
        this.searchQuery = query;
        fetchPage(page);
    }

    public void loadSortedOfferings(int page, String sortBy, String sortDirection) {
        this.sortBy = sortBy;
        this.sortDirection = sortDirection;

        fetchPage(page);
    }

    public void loadOfferings(int page, Boolean isService, Boolean initialLoad, Integer accountId){
        this.isService = isService;
        this.initLoad = initialLoad;
        if (!initialLoad && this.filterOfferingLocation!=null){
            this.accountId=null;
        }else{
            this.accountId=accountId;
        }
        fetchPage(page);
    }

    public void resetFilters() {
        this.filterCategoryId = null;
        this.filterOfferingLocation = null;
        this.filterMinPrice = null;
        this.filterMaxPrice = null;
        this.filterMinDiscount = null;
        this.filterMinDuration = null;
        this.filterMinRating = null;
        this.filterIsAvailable = null;
        this.searchQuery = null;
        this.sortBy = null;
        this.sortDirection = null;

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
