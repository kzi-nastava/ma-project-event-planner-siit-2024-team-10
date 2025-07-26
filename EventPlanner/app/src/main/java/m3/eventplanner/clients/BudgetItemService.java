package m3.eventplanner.clients;

import java.util.List;

import m3.eventplanner.models.BuyRequestDTO;
import m3.eventplanner.models.CreatedBudgetItemDTO;
import m3.eventplanner.models.CreateBudgetItemDTO;
import m3.eventplanner.models.GetBudgetItemDTO;
import m3.eventplanner.models.GetEventDTO;
import m3.eventplanner.models.UpdateBudgetItemDTO;
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


    @POST("/api/events/{eventId}/budget")
    Call<CreatedBudgetItemDTO> createBudgetItem(
            @Path("eventId") int eventId,
            @Body CreateBudgetItemDTO createBudgetItemDTO
    );

    @PUT("/api/events/{eventId}/budget/{budgetItemId}")
    Call<UpdatedBudgetItemDTO> updateBudgetItemAmount(
            @Path("eventId") int eventId,
            @Path("budgetItemId") int budgetItemId,
            @Body UpdateBudgetItemDTO amount
    );

    @DELETE("/api/events/{eventId}/budget/{budgetItemId}")
    Call<Boolean> deleteBudgetItem(
            @Path("eventId") int eventId,
            @Path("budgetItemId") int budgetItemId
    );

    @GET("/api/events/{eventId}/budget/total")
    Call<Double> getTotalBudget(
            @Path("eventId") int eventId
    );

    @GET("/api/events/organizers")
    Call<List<GetEventDTO>> findEventsByOrganizer(@Query("accountId") int accountId);

    @PUT("/api/events/{eventId}/budget/buy/{offeringId}")
    Call<Boolean> buyOffering(
            @Path("eventId") int eventId,
            @Path("offeringId") int offeringId
    );
    @GET("/api/events/budget/{eventId}")
    Call<List<GetBudgetItemDTO>> getBudgetItemsForEvent(@Path("eventId") int eventId);
}
