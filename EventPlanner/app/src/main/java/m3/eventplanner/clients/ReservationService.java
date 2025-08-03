package m3.eventplanner.clients;

import java.util.Collection;
import java.util.List;

import m3.eventplanner.models.CreateReservationDTO;
import m3.eventplanner.models.CreatedReservationDTO;
import m3.eventplanner.models.GetEventDTO;
import m3.eventplanner.models.GetReservationDTO;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ReservationService {
    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json"
    })
    @GET("reservations")
    Call<Collection<GetReservationDTO>> getReservations();

    @GET("reservations/{id}")
    Call<GetReservationDTO> getReservation(@Path("id") int id);

    @GET("reservations/{serviceId}")
    Call<Collection<GetReservationDTO>> getReservationsByService(@Path("serviceId") int serviceId);

    @GET("reservations/events/{organizerId}")
    Call<List<GetEventDTO>> findEventsByOrganizer(@Path("organizerId") int organizerId);
    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type: application/json"
    })
    @POST("reservations")
    Call<CreatedReservationDTO> createReservation(@Body CreateReservationDTO reservation);

    @DELETE("reservations/{id}")
    Call<Void> cancelReservation(@Path("id") int id);

    @GET("reservations/{providerId}/pending")
    Call<List<GetReservationDTO>> getPendingReservationsByProvider(@Path("providerId") int providerId);

    @PUT("reservations/{reservationId}/accept")
    Call<Void> acceptReservation(@Path("reservationId") int reservationId);

    @PUT("reservations/{reservationId}/reject")
    Call<Void> rejectReservation(@Path("reservationId") int reservationId);

}
