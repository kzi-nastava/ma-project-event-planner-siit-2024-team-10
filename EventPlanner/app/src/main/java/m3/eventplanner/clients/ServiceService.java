package m3.eventplanner.clients;

import m3.eventplanner.models.CreateServiceDTO;
import m3.eventplanner.models.CreatedServiceDTO;
import m3.eventplanner.models.GetReservationDTO;
import m3.eventplanner.models.GetServiceDTO;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface ServiceService {
    @POST("services")
    Call<CreatedServiceDTO> addService(@Body CreateServiceDTO service);
    @GET("services/{id}")
    Call<GetServiceDTO> getService(@Path("id") int id);
}
