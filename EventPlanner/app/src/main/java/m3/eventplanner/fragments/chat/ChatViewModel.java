package m3.eventplanner.fragments.chat;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import m3.eventplanner.clients.ClientUtils;
import m3.eventplanner.clients.MessageService;
import m3.eventplanner.models.CreateMessageDTO;
import m3.eventplanner.models.CreatedMessageDTO;
import m3.eventplanner.models.GetMessageDTO;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ua.naiksoftware.stomp.Stomp;
import ua.naiksoftware.stomp.StompClient;


public class ChatViewModel extends ViewModel {

    private final MutableLiveData<List<GetMessageDTO>> messages = new MutableLiveData<>();
    private final MutableLiveData<GetMessageDTO> newMessage = new MutableLiveData<>();
    private final MutableLiveData<String> error = new MutableLiveData<>();
    private final MutableLiveData<String> successMessage = new MutableLiveData<>();

    private ClientUtils clientUtils;
    private StompClient stompClient;

    public void initialize(ClientUtils clientUtils) {
        this.clientUtils = clientUtils;

        // Init STOMP client
        OkHttpClient okHttpClient = new OkHttpClient();
        stompClient = Stomp.over(Stomp.ConnectionProvider.OKHTTP, "ws://192.168.1.16:8080/socket/websocket", null, okHttpClient);

        stompClient.lifecycle().subscribe(lifecycleEvent -> {
            switch (lifecycleEvent.getType()) {
                case OPENED:
                    Log.d("STOMP", "Connection opened");
                    break;
                case ERROR:
                    Log.e("STOMP", "Error", lifecycleEvent.getException());
                    error.postValue("WebSocket error: " + lifecycleEvent.getException().getMessage());
                    break;
                case CLOSED:
                    Log.d("STOMP", "Connection closed");
                    break;
            }
        });

        stompClient.connect();
    }

    public LiveData<String> getSuccessMessage() {
        return successMessage;
    }

    public LiveData<List<GetMessageDTO>> getMessages() {
        return messages;
    }

    public LiveData<GetMessageDTO> getNewMessage() {
        return newMessage;
    }

    public LiveData<String> getError() {
        return error;
    }

    public void loadMessages(int senderId, int receiverId) {
        Log.d("ChatViewModel", "=== loadMessages STARTED ===");
        Log.d("ChatViewModel", "senderId: " + senderId + ", receiverId: " + receiverId);
        clientUtils.getMessageService().getBySenderIdAndReceiverId(senderId, receiverId).enqueue(new Callback<List<GetMessageDTO>>() {
            @Override
            public void onResponse(@NotNull Call<List<GetMessageDTO>> call, @NotNull Response<List<GetMessageDTO>> response) {
                if (response.isSuccessful()) {
                    messages.setValue(response.body());
                    if (response.body() != null) {
                        for (GetMessageDTO message : response.body()) {
                            Log.d("ChatViewModel", "Message from " + message.getSenderId() +
                                    " to " + message.getReceiverId() + ": " + message.getContent());
                        }
                    }else{
                        Log.d("nema","nema");
                    }
                } else {
                    error.setValue("Failed to load messages");
                }
            }

            @Override
            public void onFailure(@NotNull Call<List<GetMessageDTO>> call, @NotNull Throwable t) {
                error.setValue("Connection error: " + t.getMessage());
            }
        });
    }

    public void sendMessage(int senderId, int receiverId, String content) {
        CreateMessageDTO dto = new CreateMessageDTO(senderId, receiverId, content);

        clientUtils.getMessageService().createMessage(dto).enqueue(new Callback<CreatedMessageDTO>() {
            @Override
            public void onResponse(@NotNull Call<CreatedMessageDTO> call, @NotNull Response<CreatedMessageDTO> response) {
                if (!response.isSuccessful()) {
                    error.setValue("Failed to send message via REST");
                }
            }

            @Override
            public void onFailure(@NotNull Call<CreatedMessageDTO> call, @NotNull Throwable t) {
                error.setValue("Send error (REST): " + t.getMessage());
            }
        });

        // Send via STOMP
        try {
            JSONObject obj = new JSONObject();
            obj.put("fromId", senderId);
            obj.put("toId", receiverId);
            obj.put("message", content);

            stompClient.send("/app/send/message", obj.toString()).subscribe(
                    () -> Log.d("STOMP", "Message sent"),
                    throwable -> error.postValue("STOMP send error: " + throwable.getMessage())
            );
        } catch (JSONException e) {
            error.setValue("JSON error: " + e.getMessage());
        }
    }

    public void subscribeToSocket(int userId) {
        String topic = "/socket-publisher/" + userId;

        stompClient.topic(topic).subscribe(topicMessage -> {
            try {
                JSONObject json = new JSONObject(topicMessage.getPayload());

                GetMessageDTO msg = new GetMessageDTO(
                        json.getInt("senderId"),
                        json.getInt("receiverId"),
                        json.getString("content"),
                        LocalDateTime.parse(json.getString("timestamp"), DateTimeFormatter.ISO_DATE_TIME)
                );
                newMessage.postValue(msg);
            } catch (JSONException e) {
                error.postValue("Receive parse error: " + e.getMessage());
            }
        }, throwable -> {
            error.postValue("Subscribe error: " + throwable.getMessage());
        });
    }

    @Override
    protected void onCleared() {
        if (stompClient != null) {
            stompClient.disconnect();
        }
        super.onCleared();
    }
}
