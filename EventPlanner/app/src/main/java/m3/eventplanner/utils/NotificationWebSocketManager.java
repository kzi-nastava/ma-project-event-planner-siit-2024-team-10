package m3.eventplanner.utils;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;

import java.util.function.Consumer;

import m3.eventplanner.BuildConfig;
import m3.eventplanner.models.GetNotificationDTO;
import ua.naiksoftware.stomp.Stomp;
import ua.naiksoftware.stomp.StompClient;
import ua.naiksoftware.stomp.dto.LifecycleEvent;

public class NotificationWebSocketManager {

    private static StompClient stompClient;
    private static final String SOCKET_URL = "ws://" + BuildConfig.IP_ADDR + ":8080/socket/websocket";
    private static final String TAG = "NotificationWebSocket";

    public static void connect(Context context, int accountId, Consumer<GetNotificationDTO> onMessageReceived) {
        if (stompClient == null || !stompClient.isConnected()) {
            stompClient = Stomp.over(Stomp.ConnectionProvider.OKHTTP, SOCKET_URL);

            stompClient.lifecycle().subscribe(lifecycleEvent -> {
                LifecycleEvent.Type type = lifecycleEvent.getType();
                switch (type) {
                    case OPENED:
                        Log.d(TAG, "Stomp connection opened");

                        String topic = "/socket-publisher/notifications/" + accountId;
                        stompClient.topic(topic).subscribe(topicMessage -> {
                            String payload = topicMessage.getPayload();
                            Log.d(TAG, "Received notification payload: " + payload);

                            Gson gson = new Gson();
                            GetNotificationDTO notification = gson.fromJson(payload, GetNotificationDTO.class);
                            onMessageReceived.accept(notification);
                        }, error -> {
                            Log.e(TAG, "Error subscribing to topic", error);
                        });

                        break;
                    case ERROR:
                        Log.e(TAG, "Stomp connection error", lifecycleEvent.getException());
                        break;
                    case CLOSED:
                        Log.d(TAG, "Stomp connection closed");
                        break;
                }
            });

            stompClient.connect();
        }
    }



    public static void disconnect() {
        if (stompClient != null) {
            stompClient.disconnect();
            stompClient = null;
        }
    }
}

