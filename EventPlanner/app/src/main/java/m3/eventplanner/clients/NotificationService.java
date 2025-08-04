package m3.eventplanner.clients;

import m3.eventplanner.models.GetNotificationDTO;
import m3.eventplanner.models.PagedResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface NotificationService {
    @GET("notifications/{accountId}")
    Call<PagedResponse<GetNotificationDTO>> getNotifications(
            @Path("accountId") Integer accountId,
            @Query("page") int page,
            @Query("size") int size
    );

    @PUT("notifications/read/{notificationId}")
    Call<Void> markAsRead(
            @Path("notificationId") Integer notificationId
    );

    @PUT("notifications/{accountId}/read-all")
    Call<Void> readAll(
            @Path("accountId") Integer accountId
    );

    @GET("notifications/{accountId}/toggle")
    Call<Boolean> isNotificationSilenced(
            @Path("accountId") Integer accountId
    );

    @PUT("notifications/{accountId}/change-toggle")
    Call<Void> toggleNotifications(
            @Path("accountId") Integer accountId
    );
}
