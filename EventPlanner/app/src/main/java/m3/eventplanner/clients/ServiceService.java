package m3.eventplanner.clients;

import m3.eventplanner.models.CreateServiceDTO;
import m3.eventplanner.models.CreatedServiceDTO;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ServiceService {
    @POST("services")
    Call<CreatedServiceDTO> addService(@Body CreateServiceDTO service);
}
