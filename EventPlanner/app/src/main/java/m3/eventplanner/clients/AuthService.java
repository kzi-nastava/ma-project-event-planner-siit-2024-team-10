package m3.eventplanner.clients;

import static android.view.View.GONE;

import android.view.Menu;
import android.view.MenuInflater;

import m3.eventplanner.R;
import m3.eventplanner.auth.TokenManager;
import m3.eventplanner.models.LoginRequestDTO;
import m3.eventplanner.models.LoginResponseDTO;
import m3.eventplanner.models.RegisterDTO;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Query;

public interface AuthService {
    @POST("auth/login")
    Call<LoginResponseDTO> login(@Body LoginRequestDTO loginRequest);

    @PUT("auth/activate")
    Call<String> activateAccount(@Query("token") String token);

    @POST("auth/register")
    Call<RegisterDTO> register(@Body RegisterDTO registerDTO, @Query("roleUpgrade") boolean roleUpgrade);
}
