package m3.eventplanner.clients;

import java.util.List;

import m3.eventplanner.models.GetPricelistItemDTO;
import m3.eventplanner.models.UpdatePricelistItemDTO;
import m3.eventplanner.models.UpdatedPricelistItemDTO;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.*;

public interface PricelistService {

    @GET("pricelists/provider/{providerId}")
    Call<List<GetPricelistItemDTO>> getPricelist(@Path("providerId") int providerId);
    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type: application/json"
    })
    @PUT("pricelists/{offeringId}")
    Call<UpdatedPricelistItemDTO> updatePricing(
            @Path("offeringId") int offeringId,
            @Body UpdatePricelistItemDTO dto
    );

    @GET("pricelists/report")
    Call<ResponseBody> getPricelistReport();
}
