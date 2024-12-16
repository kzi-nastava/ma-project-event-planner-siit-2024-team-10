package m3.eventplanner.clients;

import android.content.Context;

import m3.eventplanner.BuildConfig;

import java.util.concurrent.TimeUnit;

import m3.eventplanner.auth.AuthInterceptor;
import m3.eventplanner.auth.TokenManager;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ClientUtils {
    private static final String SERVICE_API_PATH = "http://" + BuildConfig.IP_ADDR + ":8080/api/";

    private Retrofit retrofit;
    private EventService eventService;
    private AuthService authService;

    public ClientUtils(Context context){
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.HEADERS);
        OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder()
                .connectTimeout(120, TimeUnit.SECONDS)
                .readTimeout(120, TimeUnit.SECONDS)
                .writeTimeout(120, TimeUnit.SECONDS)
                .addInterceptor(loggingInterceptor);
        String token = new TokenManager(context).getToken();
        if(token!=null){
            clientBuilder.addInterceptor(new AuthInterceptor(token));
        }

        retrofit = new Retrofit.Builder()
                .baseUrl(SERVICE_API_PATH)
                .addConverterFactory(GsonConverterFactory.create())
                .client(clientBuilder.build())
                .build();

        eventService = retrofit.create(EventService.class);
        authService = retrofit.create(AuthService.class);
    }

    public EventService getEventService() {
        return eventService;
    }

    public AuthService getAuthService() {
        return authService;
    }
}
