package m3.eventplanner.clients;

import java.util.Collection;

import m3.eventplanner.models.CreatedEventTypeDTO;
import m3.eventplanner.models.GetOfferingCategoryDTO;
import m3.eventplanner.models.UpdateCategoryDTO;
import m3.eventplanner.models.UpdatedEventTypeDTO;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface CategoryService {
    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json"
    })
    @GET("categories")
    Call<Collection<GetOfferingCategoryDTO>> getCategories();
    @POST("categories")
    Call<CreatedEventTypeDTO> addCategory(@Body CreateCategoryDTO categoryDTO);

    @PUT("categories/{id}")
    Call<UpdatedEventTypeDTO> editCategory(@Path("id") int id, @Body UpdateCategoryDTO categoryDTO);

    @DELETE("categories/{id}")
    Call<Void> deleteCategory(@Path("id") int id);
    @PUT("categories/{id}/approve")
    Call<Void> approve(@Path("id") int id);

}
