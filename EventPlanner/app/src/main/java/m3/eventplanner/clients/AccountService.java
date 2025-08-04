package m3.eventplanner.clients;

import m3.eventplanner.models.AddFavouriteEventDTO;
import m3.eventplanner.models.AddFavouriteOfferingDTO;
import m3.eventplanner.models.BlockStatusDTO;
import m3.eventplanner.models.GetCalendarItemDTO;
import m3.eventplanner.models.GetEventDTO;
import m3.eventplanner.models.GetOfferingDTO;
import m3.eventplanner.models.PagedResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

import java.util.Collection;

public interface AccountService {
    @GET("accounts/{accountId}/favourite-events")
    Call<PagedResponse<GetEventDTO>> getFavouriteEvents(@Path("accountId") int accountId,
                                                        @Query("page") int page,
                                                        @Query("size") int size);

    @GET("accounts/{accountId}/favourite-events/{eventId}")
    Call<GetEventDTO> getFavouriteEvent(@Path("accountId") int accountId, @Path("eventId") int eventId);
    @GET("accounts/{accountId}/favourite-offerings/{offeringId}")
    Call<GetOfferingDTO> getFavouriteOffering(@Path("accountId") int accountId, @Path("offeringId") int offeringId);

    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type: application/json"
    })
    @POST("accounts/{accountId}/favourite-events")
    Call<Void> addEventToFavourites(@Path("accountId") int accountId, @Body AddFavouriteEventDTO addFavouriteEventDTO);
    @POST("accounts/{accountId}/favourite-offerings/{offeringId}")
    Call<Void> addOfferingToFavourites(@Path("accountId") int accountId, @Path("offeringId") int offeringId);

    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type: application/json"
    })
    @DELETE("accounts/{accountId}/favourite-events/{eventId}")
    Call<Void> removeEventFromFavourites(@Path("accountId") int accountId, @Path("eventId") int eventId);
    @DELETE("accounts/{accountId}/favourite-offerings/{offeringId}")
    Call<Void> removeOfferingFromFavourites(@Path("accountId") int accountId, @Path("offeringId") int offeringId);

    @GET("accounts/{accountId}/favourite-offerings")
    Call<PagedResponse<GetOfferingDTO>> getFavouriteOfferings(@Path("accountId") int accountId,
                                                              @Query("page") int page,
                                                              @Query("size") int size);

    @GET("accounts/{accountId}/calendar")
    Call<Collection<GetCalendarItemDTO>> getCalendar(@Path("accountId") int accountId);

    @GET("accounts/{loggedInId}/blocked-accounts/{accountToBlockId}")
    Call<BlockStatusDTO> isAccountBlocked(@Path("loggedInId") int loggedInId, @Path("accountToBlockId") int accountToBlockId);

    @PUT("accounts/{loggedInId}/block/{accountToBlockId}")
    Call<Void> blockAccount(@Path("loggedInId") int loggedInId, @Path("accountToBlockId") int accountToBlockId);

    @DELETE("accounts/{loggedInId}/unblock/{accountToUnblockId}")
    Call<Void> unblockAccount(@Path("loggedInId") int loggedInId, @Path("accountToUnblockId") int accountToUnblockId);
}

