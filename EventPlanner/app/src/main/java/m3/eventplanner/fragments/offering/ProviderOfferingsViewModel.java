package m3.eventplanner.fragments.offering;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.Collection;
import java.util.List;

import m3.eventplanner.clients.ClientUtils;
import m3.eventplanner.clients.OfferingService;
import m3.eventplanner.models.GetCommentDTO;
import m3.eventplanner.models.GetOfferingDTO;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProviderOfferingsViewModel extends ViewModel {

    private MutableLiveData<List<GetOfferingDTO>> offeringsLiveData = new MutableLiveData<>();
    private MutableLiveData<String> errorLiveData = new MutableLiveData<>();
    private MutableLiveData<Boolean> loadingLiveData = new MutableLiveData<>();

    private ClientUtils clientUtils;

    public ProviderOfferingsViewModel() {
    }

    public MutableLiveData<List<GetOfferingDTO>> getOfferingsLiveData() {
        return offeringsLiveData;
    }

    public MutableLiveData<String> getErrorLiveData() {
        return errorLiveData;
    }

    public MutableLiveData<Boolean> getLoadingLiveData() {
        return loadingLiveData;
    }

    // Obavezno pozvati pre korišćenja clientUtils!
    public void initialize(Context context) {
        this.clientUtils = new ClientUtils(context);
    }

    public void loadOfferings() {
        if (clientUtils == null) {
            errorLiveData.setValue("ClientUtils not initialized");
            return;
        }
        loadingLiveData.setValue(true);

        clientUtils.getOfferingService().getNonPaged().enqueue(new Callback<Collection<GetOfferingDTO>>() {
            @Override
            public void onResponse(Call<Collection<GetOfferingDTO>> call, Response<Collection<GetOfferingDTO>> response) {
                loadingLiveData.setValue(false);

                if (response.isSuccessful() && response.body() != null) {
                    List<GetOfferingDTO> offerings = (List<GetOfferingDTO>) response.body();
                    offeringsLiveData.setValue(offerings);
                } else {
                    String error = "Network error: " + response.code();
                    errorLiveData.setValue(error);
                }
            }

            @Override
            public void onFailure(Call<Collection<GetOfferingDTO>> call, Throwable t) {
                loadingLiveData.setValue(false);
                String error = "Network error: " + t.getMessage();
                errorLiveData.setValue(error);
            }
        });
    }

    public void loadCommentsForOffering(int offeringId, CommentsCallback callback) {
        if (clientUtils == null) {
            callback.onError("ClientUtils not initialized");
            return;
        }

        clientUtils.getOfferingService().getComments(offeringId).enqueue(new Callback<Collection<GetCommentDTO>>() {
            @Override
            public void onResponse(Call<Collection<GetCommentDTO>> call, Response<Collection<GetCommentDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<GetCommentDTO> comments = (List<GetCommentDTO>) response.body();
                    callback.onSuccess(comments);
                } else {
                    callback.onError("Failed to load comments for offering");
                }
            }

            @Override
            public void onFailure(Call<Collection<GetCommentDTO>> call, Throwable t) {
                callback.onError("Network error loading comments: " + t.getMessage());
            }
        });
    }

    public interface CommentsCallback {
        void onSuccess(List<GetCommentDTO> comments);
        void onError(String error);
    }
}
