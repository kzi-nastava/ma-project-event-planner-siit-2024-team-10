package m3.eventplanner.fragments.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import m3.eventplanner.clients.ClientUtils;
import m3.eventplanner.models.GetEventDTO;
import m3.eventplanner.models.GetEventTypeDTO;
import m3.eventplanner.models.PagedResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeEventsViewModel extends ViewModel {
    private ClientUtils clientUtils;

    public final MutableLiveData<Collection<GetEventDTO>> topData = new MutableLiveData<>();
    public final MutableLiveData<PagedResponse<GetEventDTO>> pagedData = new MutableLiveData<>();
    public final MutableLiveData<String> error = new MutableLiveData<>();
    public int currentPage = 0;
    public int totalPages = 0;
    public int totalElements = 0;
    public final int pageSize = 3;

    private final MutableLiveData<List<GetEventTypeDTO>> eventTypes = new MutableLiveData<>();
    private Integer filterTypeId;
    private String filterLocation;
    private Integer filterMaxParticipants;
    private Double filterMinRating;
    private String filterStartDate;
    private String filterEndDate;
    private String searchQuery;
    private String sortBy;
    private String sortDirection;
    private Boolean initLoad;
    private Integer accountId;

    public void initialize(ClientUtils clientUtils) {
        this.clientUtils = clientUtils;
    }

    public LiveData<String> getError() {
        return error;
    }

    public LiveData<Collection<GetEventDTO>> getTopData() {
        return topData;
    }

    public LiveData<List<GetEventTypeDTO>> getEventTypes() {
        return eventTypes;
    }

    public LiveData<PagedResponse<GetEventDTO>> getPagedData() {
        return pagedData;
    }

    public void loadTopEvents(Integer loggedInID) {
        clientUtils.getEventService().getTopEvents(loggedInID).enqueue(new Callback<Collection<GetEventDTO>>() {
            @Override
            public void onResponse(Call<Collection<GetEventDTO>> call, Response<Collection<GetEventDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    topData.setValue(new ArrayList<>(response.body()));
                } else {
                    topData.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<Collection<GetEventDTO>> call, Throwable t) {
                topData.setValue(null);
                error.setValue("Failed to load events");
            }
        });
    }

    public void fetchPage(int page) {
        clientUtils.getEventService().getEvents(
                page,
                pageSize,
                filterTypeId,
                filterLocation,
                filterMaxParticipants,
                filterMinRating,
                filterStartDate,
                filterEndDate,
                searchQuery,
                sortBy,
                sortDirection,
                accountId,
                initLoad
        ).enqueue(new Callback<PagedResponse<GetEventDTO>>() {
            @Override
            public void onResponse(Call<PagedResponse<GetEventDTO>> call, Response<PagedResponse<GetEventDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    PagedResponse<GetEventDTO> pagedResponse = response.body();
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
            public void onFailure(Call<PagedResponse<GetEventDTO>> call, Throwable t) {
                pagedData.setValue(null);
                error.setValue("Failed to load events: " + t.getMessage() + t.getStackTrace());
            }
        });
    }

    public void loadEvents(int page, Boolean initialLoad, Integer accountId){
        this.initLoad = initialLoad;
        this.accountId = accountId;
        fetchPage(page);
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

    public void loadFilteredEvents(int page, Integer eventTypeId, String location, Integer maxParticipants, Double minRating, String startDate, String endDate, Boolean initialLoad, Integer accountId) {
        this.filterTypeId = eventTypeId;
        this.filterLocation = location;
        this.filterMaxParticipants = maxParticipants;
        this.filterMinRating = minRating;
        this.filterStartDate = startDate;
        this.filterEndDate = endDate;
        this.initLoad = initialLoad;
        this.accountId = accountId;
        fetchPage(page);
    }

    public void loadSearchedEvents(int page, String query) {
        this.searchQuery = query;
        fetchPage(page);
    }

    public void loadSortedEvents(int page, String sortBy, String sortDirection) {
        this.sortBy = sortBy;
        this.sortDirection = sortDirection;
        fetchPage(page);
    }

    public void seeAll(){
        this.initLoad = false;
        resetFilters();
    }

    public void resetFilters() {
        this.filterTypeId = null;
        this.filterLocation = null;
        this.filterMaxParticipants = null;
        this.filterMinRating = null;
        this.filterStartDate = null;
        this.filterEndDate = null;
        this.searchQuery = null;
        this.sortBy = null;
        this.sortDirection = null;

        fetchPage(0);
    }

    public void fetchEventTypes() {
        clientUtils.getEventTypeService().getEventTypes().enqueue(new Callback<Collection<GetEventTypeDTO>>() {
            @Override
            public void onResponse(Call<Collection<GetEventTypeDTO>> call, Response<Collection<GetEventTypeDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    eventTypes.setValue((List<GetEventTypeDTO>) response.body());
                }
            }

            @Override
            public void onFailure(Call<Collection<GetEventTypeDTO>> call, Throwable t) {
                eventTypes.setValue(new ArrayList<>());
            }
        });
    }
}
