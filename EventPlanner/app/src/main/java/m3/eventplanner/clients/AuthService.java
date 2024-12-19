package m3.eventplanner.clients;

import m3.eventplanner.models.LoginRequestDTO;
import m3.eventplanner.models.LoginResponseDTO;
import m3.eventplanner.models.RegisterDTO;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface AuthService {
    @POST("auth/login")
    Call<LoginResponseDTO> login(@Body LoginRequestDTO loginRequest);

    @POST("auth/register")
    Call<RegisterDTO> register(@Body RegisterDTO registerDTO, @Query("roleUpgrade") boolean roleUpgrade);
}
