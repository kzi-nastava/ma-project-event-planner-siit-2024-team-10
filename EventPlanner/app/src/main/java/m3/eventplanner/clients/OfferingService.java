package m3.eventplanner.clients;

import java.time.LocalDate;
import java.util.Collection;

import m3.eventplanner.models.GetOfferingDTO;
import m3.eventplanner.models.PagedResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface OfferingService {
    @GET("offerings/top")
    Call<Collection<GetOfferingDTO>> getTopOfferings(
            @Query("accountId") Integer accountId
    );
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
            @Query("accountId") Integer accountId
            );
    @GET("offerings/highest-prices")
    Call<Double> getHighestPrice(@Query("isService") Boolean isService);
}
