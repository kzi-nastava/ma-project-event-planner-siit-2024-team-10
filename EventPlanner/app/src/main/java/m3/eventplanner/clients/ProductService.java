package m3.eventplanner.clients;

import m3.eventplanner.models.CreateEventDTO;
import m3.eventplanner.models.CreateProductDTO;
import m3.eventplanner.models.CreatedEventDTO;
import m3.eventplanner.models.CreatedProductDTO;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ProductService {
    @POST("products")
    Call<CreatedProductDTO> addProduct(@Body CreateProductDTO product);
}
