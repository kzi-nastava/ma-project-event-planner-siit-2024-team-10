package m3.eventplanner.fragments.category;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import m3.eventplanner.clients.ClientUtils;
import m3.eventplanner.models.GetOfferingCategoryDTO;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CategoryFormViewModel extends ViewModel {
    private final MutableLiveData<List<GetOfferingCategoryDTO>> categories = new MutableLiveData<>();
    private final MutableLiveData<String> error = new MutableLiveData<>();
    private ClientUtils clientUtils;
    public void initialize(ClientUtils clientUtils){
        this.clientUtils = clientUtils;
    }
    public LiveData<List<GetOfferingCategoryDTO>> getCategories(){
        return categories;
    }


}
