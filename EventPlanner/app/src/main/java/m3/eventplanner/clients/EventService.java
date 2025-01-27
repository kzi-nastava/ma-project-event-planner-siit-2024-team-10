package m3.eventplanner.clients;

import java.time.LocalDate;
import java.util.Collection;

import m3.eventplanner.models.AddFavouriteEventDTO;
import m3.eventplanner.models.CreateAgendaItemDTO;
import m3.eventplanner.models.CreateEventDTO;
import m3.eventplanner.models.CreateEventRatingDTO;
import m3.eventplanner.models.CreateEventTypeDTO;
import m3.eventplanner.models.CreatedAgendaItemDTO;
import m3.eventplanner.models.CreatedEventDTO;
import m3.eventplanner.models.CreatedEventRatingDTO;
import m3.eventplanner.models.CreatedEventTypeDTO;
import m3.eventplanner.models.GetAgendaItemDTO;
import m3.eventplanner.models.GetEventDTO;
import m3.eventplanner.models.GetEventStatsDTO;
import m3.eventplanner.models.PagedResponse;
import m3.eventplanner.models.UpdateAgendaItemDTO;
import m3.eventplanner.models.UpdatedAgendaItemDTO;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Streaming;

public interface EventService {
    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json"
    })
    @GET("events/top")
    Call<Collection<GetEventDTO>> getTopEvents(
            @Query("accountId") Integer accountId
    );

    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type: application/json"
    })
    @GET("events")
    Call<PagedResponse<GetEventDTO>> getEvents(
            @Query("page") int page,
            @Query("size") int size,
            @Query("eventTypeId") Integer eventTypeId,
            @Query("location") String location,
            @Query("maxParticipants") Integer maxParticipants,
            @Query("minRating") Double minRating,
            @Query("startDate") String startDate,
            @Query("endDate") String endDate,
            @Query("name") String name,
            @Query("sortBy") String sortBy,
            @Query("sortDirection") String sortDirection,
            @Query("accountId") Integer accountId
    );

    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json"
    })

    @GET("events/{eventId}")
    Call<GetEventDTO> getEvent(@Path("eventId") int eventId);

    @GET("events/{eventId}/agenda")
    Call<Collection<GetAgendaItemDTO>> getEventAgenda(@Path("eventId") int eventId);

    @POST("events")
    Call<CreatedEventDTO> addEvent(@Body CreateEventDTO event);

    @POST("events/{eventId}/ratings")
    Call<CreatedEventRatingDTO> addRating(@Path("eventId") int eventId, @Body CreateEventRatingDTO createEventRatingDTO);

    @POST("events/{eventId}/agenda")
    Call<CreatedAgendaItemDTO> addAgendaItem(@Path("eventId") int eventId, @Body CreateAgendaItemDTO createAgendaItemDTO);

    @PUT("events/{eventId}/agenda/{agendaItemId}")
    Call<UpdatedAgendaItemDTO> updateAgendaItem(@Path("eventId") int eventId, @Path("agendaItemId") int agendaItemId, @Body UpdateAgendaItemDTO updateAgendaItemDTO);

    @DELETE("events/{eventId}/agenda/{agendaItemId}")
    Call<Void> deleteAgendaItem(@Path("eventId") int eventId, @Path("agendaItemId") int agendaItemId);

    @GET("events/{eventId}/stats")
    Call<GetEventStatsDTO> getEventStats(@Path("eventId") int eventId);

    @POST("events/{eventId}/stats/participants")
    Call<GetEventStatsDTO> addParticipant(@Path("eventId") int eventId);

    @GET("events/{eventId}/reports/open-event")
    @Headers({
            "Accept: application/pdf"
    })
    @Streaming
    Call<ResponseBody> getOpenEventReport(@Path("eventId") int eventId);

}
