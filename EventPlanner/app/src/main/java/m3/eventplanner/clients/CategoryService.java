package m3.eventplanner.clients;

import java.util.Collection;

import m3.eventplanner.models.CreateCategoryDTO;
import m3.eventplanner.models.CreatedCategoryDTO;
import m3.eventplanner.models.GetOfferingCategoryDTO;
import m3.eventplanner.models.UpdateCategoryDTO;
import m3.eventplanner.models.UpdatedCategoryDTO;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface CategoryService {
    @GET("categories")
    Call<Collection<GetOfferingCategoryDTO>> getCategories();
    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json"
    })
    @POST("categories")
    Call<CreatedCategoryDTO> addCategory(@Body CreateCategoryDTO categoryDTO);
    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json"
    })

    @PUT("categories/{id}")
    Call<UpdatedCategoryDTO> editCategory(@Path("id") int id, @Body UpdateCategoryDTO categoryDTO);

    @DELETE("categories/{id}")
    Call<Boolean> deleteCategory(@Path("id") int categoryId);

    @PUT("categories/{id}/approve")
    Call<Void> approve(@Path("id") int id);

}
