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
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class ClientUtils {
    private static final String SERVICE_API_PATH = "http://" + BuildConfig.IP_ADDR + ":8080/api/";

    private Retrofit retrofit;
    private EventService eventService;
    private AuthService authService;
    private OfferingService offeringService;
    private EventTypeService eventTypeService;
    private AccountService accountService;
    private CategoryService categoryService;
    private ProductService productService;
    private ReservationService reservationService;
    private ServiceService serviceService;
    private UserService userService;
    private NotificationService notificationService;
    private ImageUploadService imageUploadService;
    private CommentService commentService;
    private MessageService messageService;
    private BudgetItemService budgetItemService;
    private PricelistService pricelistService;
    private AccountReportService accountReportService;

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
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .client(clientBuilder.build())
                .build();

        eventService = retrofit.create(EventService.class);
        authService = retrofit.create(AuthService.class);
        offeringService = retrofit.create(OfferingService.class);
        eventTypeService = retrofit.create(EventTypeService.class);
        accountService = retrofit.create(AccountService.class);
        categoryService = retrofit.create(CategoryService.class);
        productService = retrofit.create(ProductService.class);
        reservationService = retrofit.create(ReservationService.class);
        serviceService = retrofit.create(ServiceService.class);
        imageUploadService = retrofit.create(ImageUploadService.class);
        userService = retrofit.create(UserService.class);
        notificationService = retrofit.create(NotificationService.class);
        commentService = retrofit.create(CommentService.class);
        budgetItemService=retrofit.create(BudgetItemService.class);
        messageService = retrofit.create(MessageService.class);
    }

    public EventService getEventService() {
        return eventService;
    }

    public AuthService getAuthService() {
        return authService;
    }

    public OfferingService getOfferingService(){
        return offeringService;
    }

    public EventTypeService getEventTypeService() {return  eventTypeService;}
    public AccountService getAccountService(){
        return accountService;
    }
    public CategoryService getCategoryService() {return categoryService;}
    public ProductService getProductService() {
        return productService;
    }
    public ImageUploadService getImageUploadService() { return imageUploadService; }
    public ReservationService getReservationService() { return reservationService; }

    public ServiceService getServiceService() { return serviceService; }
    public BudgetItemService getBudgetItemService() { return budgetItemService; }

    public UserService getUserService() {
        return userService;
    }
    public NotificationService getNotificationService() { return notificationService; }
    public CommentService getCommentService() { return commentService; }

    public MessageService getMessageService() { return messageService; }
    public PricelistService getPricelistService() { return pricelistService; }
    public AccountReportService getAccountReportService() { return accountReportService; }
}
