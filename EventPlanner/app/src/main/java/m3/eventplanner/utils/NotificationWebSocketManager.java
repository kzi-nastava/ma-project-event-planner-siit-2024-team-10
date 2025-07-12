package m3.eventplanner.utils;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.gson.Gson;

import java.util.function.Consumer;

import m3.eventplanner.BuildConfig;
import m3.eventplanner.R;
import m3.eventplanner.auth.TokenManager;
import m3.eventplanner.clients.ClientUtils;
import m3.eventplanner.models.GetNotificationDTO;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
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

    public static void showNotification(Context context, GetNotificationDTO notification) {
        Integer accountId = new TokenManager(context).getAccountId();
        ClientUtils clientUtils = new ClientUtils(context);

        clientUtils.getNotificationService().isNotificationSilenced(accountId).enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(@NonNull Call<Boolean> call, @NonNull Response<Boolean> response) {
                if (response.isSuccessful() && Boolean.FALSE.equals(response.body())) {
                    displayLocalNotification(context, notification);
                }
            }

            @Override
            public void onFailure(@NonNull Call<Boolean> call, @NonNull Throwable t) {
                t.printStackTrace();
            }
        });
    }

    private static void displayLocalNotification(Context context, GetNotificationDTO notification) {
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        String channelId = "notifications_channel";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    channelId,
                    "Notifications",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            notificationManager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.drawable.ic_bell)
                .setContentTitle(notification.getTitle())
                .setContentText(notification.getContent())
                .setAutoCancel(true);

        notificationManager.notify(notification.getId(), builder.build());
    }



    public static void disconnect() {
        if (stompClient != null) {
            stompClient.disconnect();
            stompClient = null;
        }
    }
}

