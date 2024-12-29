package m3.eventplanner.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import m3.eventplanner.clients.ClientUtils;
import m3.eventplanner.models.GetEventDTO;
import m3.eventplanner.models.GetEventTypeDTO;
import m3.eventplanner.models.GetOfferingDTO;
import m3.eventplanner.models.PagedResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeScreenViewModel extends ViewModel {

    private final ClientUtils clientUtils;

    private final MutableLiveData<List<GetEventDTO>> topEvents = new MutableLiveData<>();
    private final MutableLiveData<List<GetOfferingDTO>> topOfferings = new MutableLiveData<>();

    private final MutableLiveData<PagedResponse<GetEventDTO>> pagedEvents = new MutableLiveData<>();
    private final MutableLiveData<PagedResponse<GetOfferingDTO>> pagedOfferings = new MutableLiveData<>();
    private final MutableLiveData<List<GetEventTypeDTO>> eventTypes = new MutableLiveData<>();


    private int currentEventPage = 0;
    private final int eventPageSize = 3;
    private int totalEventPages = 0;
    private int totalEvents = 0;

    private int currentOfferingPage = 0;
    private final int offeringPageSize = 3;
    private int totalOfferingPages = 0;
    private int totalOfferings = 0;
    private enum PaginationContext {
        EVENTS, OFFERINGS
    }

    private PaginationContext currentPaginationContext = PaginationContext.EVENTS;

    private Integer filterEventTypeId;
    private String filterEventLocation;
    private Integer filterEventMaxParticipants;
    private Double filterEventMinRating;
    private String filterEventStartDate;
    private String filterEventEndDate;
    private String eventSearchQuery;
    private String sortEventsBy;
    private String sortEventsDirection;


    public HomeScreenViewModel(ClientUtils clientUtils) {
        this.clientUtils = clientUtils;
    }
    public LiveData<List<GetEventDTO>> getTopEvents() {
        return topEvents;
    }

    public LiveData<List<GetOfferingDTO>> getTopOfferings() {
        return topOfferings;
    }

    public LiveData<PagedResponse<GetEventDTO>> getPagedEvents() {
        return pagedEvents;
    }

    public LiveData<PagedResponse<GetOfferingDTO>> getPagedOfferings() {
        return pagedOfferings;
    }

    public void loadTopEvents() {
        clientUtils.getEventService().getTopEvents().enqueue(new Callback<Collection<GetEventDTO>>() {
            @Override
            public void onResponse(Call<Collection<GetEventDTO>> call, Response<Collection<GetEventDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    topEvents.setValue(new ArrayList<>(response.body()));
                }
            }

            @Override
            public void onFailure(Call<Collection<GetEventDTO>> call, Throwable t) {
                topEvents.setValue(new ArrayList<>());
            }
        });
    }

    public void loadTopOfferings() {
        clientUtils.getOfferingService().getTopOfferings().enqueue(new Callback<Collection<GetOfferingDTO>>() {
            @Override
            public void onResponse(Call<Collection<GetOfferingDTO>> call, Response<Collection<GetOfferingDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    topOfferings.setValue(new ArrayList<>(response.body()));
                }
            }

            @Override
            public void onFailure(Call<Collection<GetOfferingDTO>> call, Throwable t) {
                topOfferings.setValue(new ArrayList<>());
            }
        });
    }
    public void loadPagedEvents(int page) {
        currentEventPage = page;
        currentPaginationContext = PaginationContext.EVENTS;
        clientUtils.getEventService().getEvents(currentEventPage, eventPageSize,
                filterEventTypeId, filterEventLocation, filterEventMaxParticipants, filterEventMinRating,
                filterEventStartDate, filterEventEndDate, null, null, null).enqueue(new Callback<PagedResponse<GetEventDTO>>() {
            @Override
            public void onResponse(Call<PagedResponse<GetEventDTO>> call, Response<PagedResponse<GetEventDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    pagedEvents.setValue(response.body());
                    totalEventPages = response.body().getTotalPages();
                    totalEvents = response.body().getTotalElements();
                }
            }

            @Override
            public void onFailure(Call<PagedResponse<GetEventDTO>> call, Throwable t) {
                pagedEvents.setValue(null);
            }
        });
    }
    public void loadSearchedEvents(int page, String query){
        currentEventPage = page;
        currentPaginationContext = PaginationContext.EVENTS;

        this.eventSearchQuery = query;
        clientUtils.getEventService().getEvents(currentEventPage, eventPageSize,
                null, null, null, null, null, null,
                query, null, null).enqueue(new Callback<PagedResponse<GetEventDTO>>() {
            @Override
            public void onResponse(Call<PagedResponse<GetEventDTO>> call, Response<PagedResponse<GetEventDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    pagedEvents.setValue(response.body());
                    totalEventPages = response.body().getTotalPages();
                    totalEvents = response.body().getTotalElements();
                }
            }

            @Override
            public void onFailure(Call<PagedResponse<GetEventDTO>> call, Throwable t) {
                pagedEvents.setValue(null);
            }
        });
    }
    public void loadPagedEvents(int page, Integer eventTypeId, String location, Integer maxParticipants, Double minRating, String startDate, String endDate) {
        currentEventPage = page;
        currentPaginationContext = PaginationContext.EVENTS;

        this.filterEventTypeId = eventTypeId;
        this.filterEventLocation = location;
        this.filterEventMaxParticipants = maxParticipants;
        this.filterEventMinRating = minRating;
        this.filterEventStartDate = startDate;
        this.filterEventEndDate = endDate;

        clientUtils.getEventService().getEvents(currentEventPage, eventPageSize,
                eventTypeId, location, maxParticipants, minRating, startDate, endDate,
                null, null, null).enqueue(new Callback<PagedResponse<GetEventDTO>>() {
            @Override
            public void onResponse(Call<PagedResponse<GetEventDTO>> call, Response<PagedResponse<GetEventDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    pagedEvents.setValue(response.body());
                    totalEventPages = response.body().getTotalPages();
                    totalEvents = response.body().getTotalElements();
                }
            }

            @Override
            public void onFailure(Call<PagedResponse<GetEventDTO>> call, Throwable t) {
                pagedEvents.setValue(null);
            }
        });
    }

    public void loadPagedOfferings(int page) {
        currentOfferingPage = page;
        currentPaginationContext = PaginationContext.OFFERINGS;
        clientUtils.getOfferingService().getOfferings(currentOfferingPage, offeringPageSize, null, null, null, null, null, null, null, null, null, null, null, null, null).enqueue(new Callback<PagedResponse<GetOfferingDTO>>() {
            @Override
            public void onResponse(Call<PagedResponse<GetOfferingDTO>> call, Response<PagedResponse<GetOfferingDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    pagedOfferings.setValue(response.body());
                    totalOfferingPages = response.body().getTotalPages();
                    totalOfferings = response.body().getTotalElements();
                }
            }

            @Override
            public void onFailure(Call<PagedResponse<GetOfferingDTO>> call, Throwable t) {
                pagedOfferings.setValue(null);
            }
        });
    }

    public void resetFilters(){
        this.filterEventTypeId = null;
        this.filterEventLocation = null;
        this.filterEventMaxParticipants = null;
        this.filterEventMinRating = null;
        this.filterEventStartDate = null;
        this.filterEventEndDate = null;
        this.eventSearchQuery = null;
        this.sortEventsBy = null;
        this.sortEventsDirection = null;
    }

    public void fetchEventTypes() {
        clientUtils.getEventTypeService().getAllEventTypes().enqueue(new Callback<List<GetEventTypeDTO>>() {
            @Override
            public void onResponse(Call<List<GetEventTypeDTO>> call, Response<List<GetEventTypeDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    eventTypes.setValue(response.body());
                }
            }

            @Override
            public void onFailure(Call<List<GetEventTypeDTO>> call, Throwable t) {
                eventTypes.setValue(new ArrayList<>());
            }
        });
    }

    public LiveData<List<GetEventTypeDTO>> getEventTypes() {
        return eventTypes;
    }
    public void loadNextPage() {
        switch (currentPaginationContext){
            case EVENTS:
                if (currentEventPage+1<totalEventPages) {
                    currentEventPage++;
                    loadPagedEvents(currentEventPage);
                }
                break;
            case OFFERINGS:
                if(currentOfferingPage+1 <totalOfferingPages){
                    currentOfferingPage++;
                    loadPagedOfferings(currentOfferingPage);
                }
                break;
        }
    }

    public void loadPreviousPage() {

        switch (currentPaginationContext){
            case EVENTS:
                if (currentEventPage > 0) {
                    currentEventPage--;
                    loadPagedEvents(currentEventPage);
                }
                break;
            case OFFERINGS:
                if (currentOfferingPage > 0) {
                    currentOfferingPage--;
                    loadPagedOfferings(currentOfferingPage);
                }
                break;
        }
    }
    public int getCurrentEventPage(){
        return currentEventPage;
    }
    public int getCurrentOfferingPage(){
        return currentOfferingPage;
    }
    public int getTotalEventPages(){
        return totalEventPages;
    }
    public int getTotalOfferingPages(){
        return totalOfferingPages;
    }
    public int getNumOfEvents(){
        return totalEvents;
    }
    public int getNumOfOfferings(){
        return totalOfferings;
    }
}
