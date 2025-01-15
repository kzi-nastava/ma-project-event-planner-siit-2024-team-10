package m3.eventplanner.clients;

import m3.eventplanner.models.GetEventDTO;
import m3.eventplanner.models.GetUserDTO;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Path;

public interface UserService {
    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json"
    })

    @GET("users/{accountId}")
    Call<GetUserDTO> getUser(@Path("accountId") int accountId);
}
