package m3.eventplanner.clients;

import java.util.List;

import m3.eventplanner.models.GetEventTypeDTO;
import retrofit2.Call;
import retrofit2.http.GET;

public interface EventTypeService {
    @GET("event-types")
    Call<List<GetEventTypeDTO>> getAllEventTypes();
}

