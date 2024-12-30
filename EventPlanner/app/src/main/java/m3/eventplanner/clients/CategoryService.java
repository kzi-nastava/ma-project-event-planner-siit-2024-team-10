package m3.eventplanner.clients;

import java.util.Collection;

import m3.eventplanner.models.GetOfferingCategoryDTO;
import retrofit2.Call;
import retrofit2.http.GET;

public interface CategoryService {
    @GET("categories")
    Call<Collection<GetOfferingCategoryDTO>> getCategories();
}
