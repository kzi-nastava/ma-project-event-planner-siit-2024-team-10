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
import m3.eventplanner.models.GetOfferingDTO;
import m3.eventplanner.models.PagedResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeScreenViewModel extends ViewModel {

    private ClientUtils clientUtils;

    private final MutableLiveData<List<GetEventDTO>> topEvents = new MutableLiveData<>();
    private final MutableLiveData<List<GetOfferingDTO>> topOfferings = new MutableLiveData<>();

    private final MutableLiveData<PagedResponse<GetEventDTO>> pagedEvents = new MutableLiveData<>();
    private final MutableLiveData<PagedResponse<GetOfferingDTO>> pagedOfferings = new MutableLiveData<>();
    private final MutableLiveData<List<GetEventTypeDTO>> eventTypes = new MutableLiveData<>();

    private final MutableLiveData<String> error = new MutableLiveData<>();

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

    private Integer filterOfferingTypeId;
    private String filterOfferingLocation;
    private Integer filterOfferingMaxParticipants;
    private Double filterOfferingMinRating;
    private String filterOfferingStartDate;
    private String filterOfferingEndDate;
    private String offeringSearchQuery;
    private String sortOfferingsBy;
    private String sortOfferingsDirection;


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

    public void initialize(ClientUtils clientUtils) {
        this.clientUtils = clientUtils;
    }

    public void loadTopEvents() {
        clientUtils.getEventService().getTopEvents().enqueue(new Callback<Collection<GetEventDTO>>() {
            @Override
            public void onResponse(Call<Collection<GetEventDTO>> call, Response<Collection<GetEventDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    topEvents.setValue(new ArrayList<>(response.body()));

                } else {
                    topEvents.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<Collection<GetEventDTO>> call, Throwable t) {
                topEvents.setValue(null);
                error.setValue("Failed to load events");
            }
        });
    }

    public void loadTopOfferings() {
        clientUtils.getOfferingService().getTopOfferings().enqueue(new Callback<Collection<GetOfferingDTO>>() {
            @Override
            public void onResponse(Call<Collection<GetOfferingDTO>> call, Response<Collection<GetOfferingDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    topOfferings.setValue(new ArrayList<>(response.body()));
                } else{
                    topOfferings.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<Collection<GetOfferingDTO>> call, Throwable t) {
                topOfferings.setValue(new ArrayList<>());
                error.setValue("Failed to load offerings");
            }
        });
    }
    public void loadPagedEvents(int page) {
        fetchPagedEvents(page);
    }
    public void loadFilteredEvents(int page, Integer eventTypeId, String location, Integer maxParticipants, Double minRating, String startDate, String endDate) {
        this.filterEventTypeId = eventTypeId;
        this.filterEventLocation = location;
        this.filterEventMaxParticipants = maxParticipants;
        this.filterEventMinRating = minRating;
        this.filterEventStartDate = startDate;
        this.filterEventEndDate = endDate;

        fetchPagedEvents(page);
    }
    public void loadSearchedEvents(int page, String query){
        this.eventSearchQuery = query;

        fetchPagedEvents(page);
    }
    public void loadSortedEvents(int page, String sortBy, String sortDirection){
        this.sortEventsBy = sortBy;
        this.sortEventsDirection = sortDirection;

        fetchPagedEvents(page);
    }

    private void fetchPagedEvents(int page){
        currentEventPage = page;
        currentPaginationContext = PaginationContext.EVENTS;

        clientUtils.getEventService().getEvents(currentEventPage, eventPageSize,
                filterEventTypeId, filterEventLocation, filterEventMaxParticipants, filterEventMinRating, filterEventStartDate, filterEventEndDate,
                eventSearchQuery, sortEventsBy, sortEventsDirection).enqueue(new Callback<PagedResponse<GetEventDTO>>() {
            @Override
            public void onResponse(Call<PagedResponse<GetEventDTO>> call, Response<PagedResponse<GetEventDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    PagedResponse<GetEventDTO> pagedResponse = response.body();
                    Collection<GetEventDTO> eventList = pagedResponse.getContent();

                    if (eventList != null && !eventList.isEmpty()) {
                        pagedEvents.setValue(pagedResponse);
                        totalEventPages = pagedResponse.getTotalPages();
                        totalEvents = pagedResponse.getTotalElements();
                    } else {
                        pagedEvents.setValue(null);
                    }
                } else {
                    pagedEvents.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<PagedResponse<GetEventDTO>> call, Throwable t) {
                pagedEvents.setValue(null);
                error.setValue("Failed to load events");
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
                error.setValue("Failed to load offerings");
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

        fetchPagedEvents(0);
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

    public LiveData<String> getError() {
        return error;
    }
}