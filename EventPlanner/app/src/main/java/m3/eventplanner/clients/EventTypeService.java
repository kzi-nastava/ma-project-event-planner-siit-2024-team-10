package m3.eventplanner.clients;

import java.util.Collection;

import m3.eventplanner.models.CreateEventTypeDTO;
import m3.eventplanner.models.CreatedEventTypeDTO;
import m3.eventplanner.models.GetEventTypeDTO;
import m3.eventplanner.models.UpdateEventTypeDTO;
import m3.eventplanner.models.UpdatedEventTypeDTO;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface EventTypeService {
    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json"
    })
    @GET("event-types")
    Call<Collection<GetEventTypeDTO>> getEventTypes();

    @POST("event-types")
    Call<CreatedEventTypeDTO> addEventType(@Body CreateEventTypeDTO eventType);

    @PUT("event-types/{id}")
    Call<UpdatedEventTypeDTO> editEventType(@Path("id") int id, @Body UpdateEventTypeDTO eventType);

    @DELETE("event-types/{id}")
    Call<Void> deactivateEventType(@Path("id") int id);

    @PUT("event-types/{id}/activate")
    Call<Void> activateEventType(@Path("id") int id);
}
