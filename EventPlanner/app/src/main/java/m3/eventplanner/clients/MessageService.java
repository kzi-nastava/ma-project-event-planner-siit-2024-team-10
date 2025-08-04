package m3.eventplanner.clients;

import android.content.Context;

import java.util.List;

import m3.eventplanner.models.CreateMessageDTO;
import m3.eventplanner.models.CreatedMessageDTO;
import m3.eventplanner.models.GetChatContact;
import m3.eventplanner.models.GetMessageDTO;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.*;

public interface MessageService {
    @GET("messages/{senderId}/{receiverId}")
    Call<List<GetMessageDTO>> getBySenderIdAndReceiverId(@Path("senderId") int senderId, @Path("receiverId") int receiverId);
    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type: application/json"
    })
    @POST("messages")
    Call<CreatedMessageDTO> createMessage(@Body CreateMessageDTO dto);
    @GET("messages/{userId}")
    Call<List<GetChatContact>> getChatContacts(@Path("userId") int userId);
}
