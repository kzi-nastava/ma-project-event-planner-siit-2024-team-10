package m3.eventplanner.clients;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

import m3.eventplanner.models.ChangeCategoryDTO;
import m3.eventplanner.models.CreateCommentDTO;
import m3.eventplanner.models.CreatedCommentDTO;
import m3.eventplanner.models.GetCommentDTO;
import m3.eventplanner.models.GetEventDTO;
import m3.eventplanner.models.GetOfferingDTO;
import m3.eventplanner.models.PagedResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface OfferingService {
    @GET("offerings/top")
    Call<Collection<GetOfferingDTO>> getTopOfferings(
            @Query("accountId") Integer accountId
    );
    Call<Collection<GetOfferingDTO>> getTopOfferings();
    @GET("provider/{providerId}")
    Call<List<GetOfferingDTO>> getOfferingsByProviderId(@Path("providerId") int providerId);
    @GET("offerings/all")
    Call<Collection<GetOfferingDTO>> getOfferings(
            @Query("isServiceFilter") Boolean isServiceFilter,
            @Query("name") String name,
            @Query("eventTypeId") Integer eventTypeId,
            @Query("categoryId") Integer categoryId,
            @Query("location") String location,
            @Query("minPrice") Integer minPrice,
            @Query("maxPrice") Integer maxPrice,
            @Query("minDiscount") Integer minDiscount,
            @Query("duration") Integer duration,
            @Query("minRating") Double minRating,
            @Query("startDate") LocalDate startDate,
            @Query("endDate") LocalDate endDate,
            @Query("isAvailable") Boolean isAvailable);

    @GET("offerings")
    Call<PagedResponse<GetOfferingDTO>> getOfferings(
            @Query("page") int page,
            @Query("size") int size,
            @Query("isServiceFilter") Boolean isServiceFilter,
            @Query("name") String name,
            @Query("categoryId") Integer categoryId,
            @Query("location") String location,
            @Query("startPrice") Integer startPrice,
            @Query("endPrice") Integer endPrice,
            @Query("minDiscount") Integer minDiscount,
            @Query("duration") Integer duration,
            @Query("minRating") Double minRating,
            @Query("isAvailable") Boolean isAvailable,
            @Query("sortBy") String sortBy,
            @Query("sortDirection") String sortDirection,
            @Query("accountId") Integer accountId,
            @Query("providerId") Integer providerId
            );
    @GET("offerings/highest-prices")
    Call<Double> getHighestPrice(@Query("isService") Boolean isService);
    @GET("offerings/all-non-paged")
    Call<Collection<GetOfferingDTO>> getNonPaged();
    @PUT("offerings/{offeringId}/category")
    Call<Void> changeOfferingCategory(@Path("offeringId") int offeringId,@Body ChangeCategoryDTO changeCategoryDTO);

    @POST("offerings/{offeringId}/comments")
    Call<CreatedCommentDTO> createComment(
            @Path("offeringId") int offeringId,
            @Body CreateCommentDTO comment
    );
    @PUT("offerings/comments/{commentId}/reject")
    Call<Void> rejectComment(
            @Path("commentId") int commentId
    );

    @PUT("offerings/comments/{commentId}/approve")
    Call<Void> approveComment(
            @Path("commentId") int commentId
    );

    @GET("offerings/comments/pending")
    Call<Collection<GetCommentDTO>> getPendingComments();
    @GET("offerings/{offeringId}/comments")
    Call<Collection<GetCommentDTO>> getComments(@Path("offeringId") int offeringId);
    @GET("offerings/{userId}/purchased/{offeringId}")
    Call<Boolean> hasUserPurchasedOffering(
            @Path("userId") int userId,
            @Path("offeringId") int offeringId
    );
}
