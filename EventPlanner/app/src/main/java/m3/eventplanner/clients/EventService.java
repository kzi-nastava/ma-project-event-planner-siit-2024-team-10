package m3.eventplanner.clients;

import java.time.LocalDate;
import java.util.Collection;

import m3.eventplanner.models.GetEventDTO;
import m3.eventplanner.models.PagedResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;

public interface EventService {
    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json"
    })
    @GET("events/top")
    Call<Collection<GetEventDTO>> getTopEvents();

    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type: application/json"
    })
    @GET("events/all")
    Call<Collection<GetEventDTO>> getEvents(
            @Query("eventTypeId") Integer eventTypeId,
            @Query("location") String location,
            @Query("maxParticipants") Integer maxParticipants,
            @Query("minRating") Double minRating,
            @Query("startDate") String startDate,
            @Query("endDate") String endDate,
            @Query("name") String name
    );
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
            @Query("sortDirection") String sortDirection
    );

}
