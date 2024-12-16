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
    Call<Collection<GetOfferingDTO>> getTopOfferings();

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
}
