package m3.eventplanner.clients;

import m3.eventplanner.models.CreateProductDTO;
import m3.eventplanner.models.CreatedProductDTO;
import m3.eventplanner.models.GetProductDTO;
import m3.eventplanner.models.UpdateProductDTO;
import m3.eventplanner.models.UpdatedProductDTO;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ProductService {
    @GET("products/{id}")
    Call<GetProductDTO> getProduct(@Path("id") int id);
    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type: application/json"
    })
    @POST("products")
    Call<CreatedProductDTO> addProduct(@Body CreateProductDTO product);

    @PUT("products/{id}")
    Call<UpdatedProductDTO> updateProduct(@Path("id") int id, @Body UpdateProductDTO product);

    @DELETE("products/{id}")
    Call<Void> deleteProduct(@Path("id") int id);
}
