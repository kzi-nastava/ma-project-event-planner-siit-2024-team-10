package m3.eventplanner.clients;

import m3.eventplanner.models.ChangePasswordDTO;
import m3.eventplanner.models.GetEventDTO;
import m3.eventplanner.models.GetUserDTO;
import m3.eventplanner.models.UpdateAgendaItemDTO;
import m3.eventplanner.models.UpdateCompanyDTO;
import m3.eventplanner.models.UpdateUserDTO;
import m3.eventplanner.models.UpdatedAgendaItemDTO;
import m3.eventplanner.models.UpdatedCompanyDTO;
import m3.eventplanner.models.UpdatedUserDTO;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface UserService {
    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json"
    })

    @GET("users/{accountId}")
    Call<GetUserDTO> getUser(@Path("accountId") int accountId);

    @PUT("users/{accountId}")
    Call<UpdatedUserDTO> updateUser(@Path("accountId") int accountId, @Body UpdateUserDTO updateUserDTO);

    @PUT("users/{accountId}/company")
    Call<UpdatedCompanyDTO> updateCompany(@Path("accountId") int accountId, @Body UpdateCompanyDTO updateCompanyDTO);

    @PUT("users/{accountId}/password")
    Call<Void> changePassword(@Path("accountId") int accountId, @Body ChangePasswordDTO changePasswordDTO);
}
