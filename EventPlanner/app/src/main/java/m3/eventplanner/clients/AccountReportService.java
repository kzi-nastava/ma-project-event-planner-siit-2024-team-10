package m3.eventplanner.clients;

import m3.eventplanner.models.CreateAccountReportDTO;
import m3.eventplanner.models.CreatedAccountReportDTO;
import m3.eventplanner.models.GetAccountReportDTO;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

import java.util.Collection;

public interface AccountReportService {

    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type: application/json"
    })
    @GET("reports")
    Call<Collection<GetAccountReportDTO>> getAllPendingReports();

    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type: application/json"
    })
    @POST("reports")
    Call<CreatedAccountReportDTO> createAccountReport(@Body CreateAccountReportDTO report);

    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type: application/json"
    })
    @PUT("reports/{reportId}/accept")
    Call<Void> acceptReport(@Path("reportId") int reportId);

    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type: application/json"
    })
    @PUT("reports/{reportId}/reject")
    Call<Void> rejectReport(@Path("reportId") int reportId);
}
