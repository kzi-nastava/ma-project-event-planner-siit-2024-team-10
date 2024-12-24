package m3.eventplanner.clients;

import m3.eventplanner.models.AddFavouriteEventDTO;
import m3.eventplanner.models.Event;
import m3.eventplanner.models.GetEventDTO;
import m3.eventplanner.models.PagedResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

import java.util.Collection;

public interface AccountService {

    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type: application/json"
    })
    @GET("accounts/{accountId}/favourite-events")
    Call<Collection<GetEventDTO>> getFavouriteEvents(@Path("accountId") int accountId);

    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type: application/json"
    })
    @POST("accounts/{accountId}/favourite-events")
    Call<Void> addEventToFavourites(@Path("accountId") int accountId, @Body AddFavouriteEventDTO addFavouriteEventDTO);

    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type: application/json"
    })
    @DELETE("accounts/{accountId}/favourite-events/{eventId}")
    Call<Void> removeEventFromFavourites(@Path("accountId") int accountId, @Path("eventId") int eventId);
}

