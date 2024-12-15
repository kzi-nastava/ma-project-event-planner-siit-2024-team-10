package m3.eventplanner.clients;

import m3.eventplanner.BuildConfig;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ClientUtils {

    // FOR THIS TO WORK, YOU NEED TO CHANGE THE IP_ADDR IN THE local.properties FILE!!!
    // CHECK BY OPENING CMD PROMPT AND TYPE ipconfig, LOOK FOR WIRELESS CONNECTION
    public static final String SERVICE_API_PATH = "http://" + BuildConfig.IP_ADDR + ":8080/api/";
}
