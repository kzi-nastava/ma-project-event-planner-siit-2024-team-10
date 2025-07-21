package m3.eventplanner.clients;

import java.util.List;

import m3.eventplanner.models.CreatedBudgetItemDTO;
import m3.eventplanner.models.CreateBudgetItemDTO;
import m3.eventplanner.models.GetBudgetItemDTO;
import m3.eventplanner.models.GetEventDTO;
import m3.eventplanner.models.UpdatedBudgetItemDTO;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface BudgetItemService {

    @POST("/{eventId}/budget")
    Call<CreatedBudgetItemDTO> createBudgetItem(
            @Path("eventId") int eventId,
            @Body CreateBudgetItemDTO createBudgetItemDTO
    );

    @PUT("/{eventId}/budget/{budgetItemId}")
    Call<UpdatedBudgetItemDTO> updateBudgetItemAmount(
            @Path("eventId") int eventId,
            @Path("budgetItemId") int budgetItemId,
            @Body Integer amount
    );

    @PUT("/{eventId}/budget/buy/{offeringId}")
    Call<Boolean> buy(
            @Path("eventId") int eventId,
            @Path("offeringId") int offeringId
    );

    @DELETE("/{eventId}/budget/{budgetItemId}")
    Call<Boolean> deleteBudgetItem(
            @Path("eventId") int eventId,
            @Path("budgetItemId") int budgetItemId
    );

    @GET("/budget/{eventId}")
    Call<List<GetBudgetItemDTO>> getBudgetItemsByEvent(
            @Path("eventId") int eventId
    );

    @GET("/{eventId}/budget/total")
    Call<Double> getTotalBudget(
            @Path("eventId") int eventId
    );

    @GET("events/organizers")
    Call<List<GetEventDTO>> findEventsByOrganizer(@Query("accountId") int accountId);
    @GET("/api/budget/{eventId}")
    Call<List<GetBudgetItemDTO>> getBudgetItemsForEvent(@Path("eventId") int eventId);
}
