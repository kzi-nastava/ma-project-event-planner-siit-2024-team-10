package m3.eventplanner.clients;

import m3.eventplanner.models.LoginRequestDTO;
import m3.eventplanner.models.LoginResponseDTO;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface AuthService {
    @POST("auth/login")
    Call<LoginResponseDTO> login(@Body LoginRequestDTO loginRequest);
}
